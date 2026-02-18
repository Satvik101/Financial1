package com.fincalc.app.feature.calculator.emi

import android.graphics.Color
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.fincalc.app.FinCalcApplication
import com.fincalc.app.R
import com.fincalc.app.ads.AdManager
import com.fincalc.app.core.base.BaseFragment
import com.fincalc.app.core.utils.PdfExportUtil
import com.fincalc.app.core.utils.ShareUtil
import com.fincalc.app.data.local.db.entity.HistoryEntity
import com.fincalc.app.databinding.FragmentEmiBinding
import com.fincalc.app.domain.engine.FormulaEngine
import com.fincalc.app.domain.model.CalculatorType
import com.fincalc.app.feature.calculator.CalculatorInfoBottomSheet
import com.fincalc.app.feature.calculator.CalculatorInfoProvider
import com.fincalc.app.feature.calculator.adapter.CalculatorTableAdapter
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import kotlinx.coroutines.launch

class EmiFragment : BaseFragment(R.layout.fragment_emi) {

    private var _binding: FragmentEmiBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()
    private lateinit var tableAdapter: CalculatorTableAdapter

    private var lastInputMap: Map<String, Double> = emptyMap()
    private var lastResultText: String = ""

    override fun setupUi(view: View) {
        _binding = FragmentEmiBinding.bind(view)
        tableAdapter = CalculatorTableAdapter()
        binding.rvSchedule.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSchedule.adapter = tableAdapter

        binding.btnInfo.setOnClickListener {
            CalculatorInfoBottomSheet.newInstance("EMI", CalculatorInfoProvider.info(CalculatorType.EMI))
                .show(parentFragmentManager, "emi_info")
        }

        prefillInputs(arguments?.getString("prefillInputJson").orEmpty())

        binding.btnCalculate.setOnClickListener {
            calculate()
        }

        binding.btnSave.setOnClickListener {
            if (lastResultText.isBlank()) return@setOnClickListener
            val app = requireActivity().application as FinCalcApplication
            viewLifecycleOwner.lifecycleScope.launch {
                app.appModule.historyRepository.insert(
                    HistoryEntity(
                        calculatorType = CalculatorType.EMI.name,
                        inputJson = gson.toJson(lastInputMap),
                        resultJson = gson.toJson(mapOf("result" to lastResultText)),
                        resultValueLabel = lastResultText
                    )
                )
            }
        }

        binding.btnShare.setOnClickListener {
            val bitmap = ShareUtil.renderViewToBitmap(binding.root)
            ShareUtil.shareBitmap(requireContext(), bitmap, "EMI_Result")
        }

        binding.btnPdf.setOnClickListener {
            val app = requireActivity().application as FinCalcApplication
            if (!app.appModule.preferences.isPremium) return@setOnClickListener
            PdfExportUtil.generateCalculationPdf(
                context = requireContext(),
                calculatorName = "EMI_Calculation",
                inputLines = listOf(
                    "Loan: ${binding.etLoan.text}",
                    "Rate: ${binding.etRate.text}",
                    "Tenure: ${binding.etTenure.text} ${if (binding.chipYears.isChecked) "Years" else "Months"}"
                ),
                resultLines = listOf(binding.tvResult.text.toString(), binding.tvBreakdown.text.toString()),
                chartBitmap = ShareUtil.renderViewToBitmap(binding.pieChart)
            )
        }

        binding.btnExportSchedulePdf.setOnClickListener {
            val app = requireActivity().application as FinCalcApplication
            if (!app.appModule.preferences.isPremium) return@setOnClickListener
            PdfExportUtil.generateCalculationPdf(
                context = requireContext(),
                calculatorName = "EMI_Schedule",
                inputLines = listOf("Amortization Schedule"),
                resultLines = listOf(binding.tvResult.text.toString(), binding.tvBreakdown.text.toString()),
                chartBitmap = ShareUtil.renderViewToBitmap(binding.rvSchedule)
            )
        }
    }

    private fun calculate() {
        binding.tilLoan.error = null
        binding.tilRate.error = null
        binding.tilTenure.error = null

        val loan = binding.etLoan.text?.toString()?.replace(",", "")?.toDoubleOrNull() ?: 0.0
        val rate = binding.etRate.text?.toString()?.replace(",", "")?.toDoubleOrNull() ?: 0.0
        val tenure = binding.etTenure.text?.toString()?.replace(",", "")?.toIntOrNull() ?: 0
        var valid = true
        if (loan <= 0) {
            binding.tilLoan.error = "Enter valid loan amount"
            valid = false
        }
        if (rate <= 0 || rate > 60) {
            binding.tilRate.error = "Enter rate between 0 and 60"
            valid = false
        }
        if (tenure <= 0) {
            binding.tilTenure.error = "Enter valid tenure"
            valid = false
        }

        val months = if (binding.chipYears.isChecked) tenure * 12 else tenure
        if (months > 600) {
            binding.tilTenure.error = "Tenure should be 600 months or less"
            valid = false
        }
        if (!valid) {
            Toast.makeText(requireContext(), "Please correct highlighted fields", Toast.LENGTH_SHORT).show()
            return
        }

        val result = FormulaEngine.emi(loan, rate, months)
        lastInputMap = mapOf("a" to loan, "b" to rate, "c" to tenure.toDouble())
        lastResultText = "EMI: ₹${"%.2f".format(result.monthlyEmi)}"

        binding.tvResult.text = lastResultText
        binding.tvBreakdown.text = "Total Interest: ₹${"%.2f".format(result.totalInterest)} | Total Payment: ₹${"%.2f".format(result.totalPayment)}"

        val pieEntries = listOf(
            PieEntry(loan.toFloat(), "Principal"),
            PieEntry(result.totalInterest.toFloat(), "Interest")
        )
        val dataSet = PieDataSet(pieEntries, "EMI Split").apply {
            colors = listOf(Color.parseColor("#1A73E8"), Color.parseColor("#EA4335"))
        }
        binding.pieChart.data = PieData(dataSet)
        binding.pieChart.description.isEnabled = false
        binding.pieChart.invalidate()

        tableAdapter.submitRows(
            result.amortizationSchedule.take(120).map {
                listOf(
                    it.month.toString(),
                    "%.0f".format(it.emi),
                    "%.0f".format(it.principal),
                    "%.0f".format(it.interest),
                    "%.0f".format(it.balance)
                )
            }
        )
        AdManager.onCalculationCompleted(requireActivity())
    }

    private fun prefillInputs(inputJson: String) {
        if (inputJson.isBlank()) return
        runCatching {
            val map = gson.fromJson(inputJson, Map::class.java)
            binding.etLoan.setText(map["a"]?.toString()?.replace(".0", "") ?: "")
            binding.etRate.setText(map["b"]?.toString()?.replace(".0", "") ?: "")
            binding.etTenure.setText(map["c"]?.toString()?.replace(".0", "") ?: "")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


