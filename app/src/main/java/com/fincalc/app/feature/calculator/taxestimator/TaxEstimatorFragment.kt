package com.fincalc.app.feature.calculator.taxestimator

import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fincalc.app.FinCalcApplication
import com.fincalc.app.R
import com.fincalc.app.core.base.BaseFragment
import com.fincalc.app.core.utils.PdfExportUtil
import com.fincalc.app.core.utils.ShareUtil
import com.fincalc.app.data.local.db.entity.HistoryEntity
import com.fincalc.app.databinding.FragmentTaxEstimatorBinding
import com.fincalc.app.domain.engine.FormulaEngine
import com.fincalc.app.domain.model.AgeGroup
import com.fincalc.app.domain.model.CalculatorType
import com.fincalc.app.feature.calculator.CalculatorInfoBottomSheet
import com.fincalc.app.feature.calculator.CalculatorInfoProvider
import com.fincalc.app.feature.calculator.adapter.CalculatorTableAdapter
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.gson.Gson
import kotlinx.coroutines.launch

class TaxEstimatorFragment : BaseFragment(R.layout.fragment_tax_estimator) {

    private var _binding: FragmentTaxEstimatorBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()
    private lateinit var tableAdapter: CalculatorTableAdapter
    private var ageGroup: AgeGroup = AgeGroup.BELOW_60
    private var lastInputMap: Map<String, Double> = emptyMap()
    private var lastResultText: String = ""

    override fun setupUi(view: View) {
        _binding = FragmentTaxEstimatorBinding.bind(view)
        tableAdapter = CalculatorTableAdapter()
        binding.rvTable.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTable.adapter = tableAdapter

        val ageList = resources.getStringArray(R.array.age_group_options).toList()
        binding.actAgeGroup.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ageList))
        binding.actAgeGroup.setText(ageList.first(), false)
        binding.actAgeGroup.setOnItemClickListener { _, _, position, _ ->
            ageGroup = when (position) {
                1 -> AgeGroup.BETWEEN_60_80
                2 -> AgeGroup.ABOVE_80
                else -> AgeGroup.BELOW_60
            }
        }

        binding.btnInfo.setOnClickListener {
            CalculatorInfoBottomSheet.newInstance("Tax Estimator", CalculatorInfoProvider.info(CalculatorType.TAX_ESTIMATOR))
                .show(parentFragmentManager, "tax_info")
        }

        prefillInputs(arguments?.getString("prefillInputJson").orEmpty())

        binding.btnCalculate.setOnClickListener { calculate() }

        binding.btnSave.setOnClickListener {
            if (lastResultText.isBlank()) return@setOnClickListener
            val app = requireActivity().application as FinCalcApplication
            viewLifecycleOwner.lifecycleScope.launch {
                app.appModule.historyRepository.insert(
                    HistoryEntity(
                        calculatorType = CalculatorType.TAX_ESTIMATOR.name,
                        inputJson = gson.toJson(lastInputMap),
                        resultJson = gson.toJson(mapOf("result" to lastResultText)),
                        resultValueLabel = lastResultText
                    )
                )
            }
        }

        binding.btnShare.setOnClickListener {
            ShareUtil.shareBitmap(requireContext(), ShareUtil.renderViewToBitmap(binding.root), "Tax_Result")
        }

        binding.btnPdf.setOnClickListener {
            val app = requireActivity().application as FinCalcApplication
            if (!app.appModule.preferences.isPremium) return@setOnClickListener
            PdfExportUtil.generateCalculationPdf(
                context = requireContext(),
                calculatorName = "Tax_Estimation",
                inputLines = lastInputMap.map { "${it.key}: ${it.value}" },
                resultLines = listOf(binding.tvResult.text.toString(), binding.tvBreakdown.text.toString()),
                chartBitmap = ShareUtil.renderViewToBitmap(binding.barChart)
            )
        }
    }

    private fun calculate() {
        binding.etIncome.error = null
        binding.et80C.error = null
        binding.etHra.error = null
        binding.etOther.error = null

        val income = binding.etIncome.text?.toString()?.replace(",", "")?.toDoubleOrNull() ?: 0.0
        val d80c = binding.et80C.text?.toString()?.replace(",", "")?.toDoubleOrNull() ?: 0.0
        val hra = binding.etHra.text?.toString()?.replace(",", "")?.toDoubleOrNull() ?: 0.0
        val other = binding.etOther.text?.toString()?.replace(",", "")?.toDoubleOrNull() ?: 0.0
        var valid = true
        if (income <= 0) {
            binding.etIncome.error = "Enter valid annual income"
            valid = false
        }
        if (d80c < 0 || hra < 0 || other < 0) {
            if (d80c < 0) binding.et80C.error = "Cannot be negative"
            if (hra < 0) binding.etHra.error = "Cannot be negative"
            if (other < 0) binding.etOther.error = "Cannot be negative"
            valid = false
        }
        if (d80c + hra + other > income) {
            binding.etOther.error = "Total deductions cannot exceed income"
            valid = false
        }
        if (!valid) {
            Toast.makeText(requireContext(), "Please correct highlighted fields", Toast.LENGTH_SHORT).show()
            return
        }

        val result = FormulaEngine.taxEstimator(income, d80c, hra, other, ageGroup)
        lastInputMap = mapOf("a" to income, "b" to d80c, "c" to hra, "d" to other)
        lastResultText = result.recommendation

        binding.tvResult.text = result.recommendation
        binding.tvBreakdown.text = "Old: ₹${"%.0f".format(result.taxOldRegime)} | New: ₹${"%.0f".format(result.taxNewRegime)}"

        val entries = listOf(
            BarEntry(1f, result.taxOldRegime.toFloat()),
            BarEntry(2f, result.taxNewRegime.toFloat())
        )
        val dataSet = BarDataSet(entries, "Tax Comparison")
        binding.barChart.data = BarData(dataSet)
        binding.barChart.description.isEnabled = false
        binding.barChart.invalidate()

        val rows = mutableListOf<List<String>>()
        rows += listOf("Regime", "Slab", "Taxable", "Tax", "")
        rows += result.slabBreakdownOld.map { listOf("Old", it.slabLabel, "%.0f".format(it.taxableAmount), "%.0f".format(it.tax), "") }
        rows += result.slabBreakdownNew.map { listOf("New", it.slabLabel, "%.0f".format(it.taxableAmount), "%.0f".format(it.tax), "") }
        tableAdapter.submitRows(rows)
    }

    private fun prefillInputs(inputJson: String) {
        if (inputJson.isBlank()) return
        runCatching {
            val map = gson.fromJson(inputJson, Map::class.java)
            binding.etIncome.setText(map["a"]?.toString()?.replace(".0", "") ?: "")
            binding.et80C.setText(map["b"]?.toString()?.replace(".0", "") ?: "")
            binding.etHra.setText(map["c"]?.toString()?.replace(".0", "") ?: "")
            binding.etOther.setText(map["d"]?.toString()?.replace(".0", "") ?: "")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


