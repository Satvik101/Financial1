package com.fincalc.app.feature.calculator.tipsplit

import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.fincalc.app.FinCalcApplication
import com.fincalc.app.R
import com.fincalc.app.core.base.BaseFragment
import com.fincalc.app.core.utils.PdfExportUtil
import com.fincalc.app.core.utils.ShareUtil
import com.fincalc.app.data.local.db.entity.HistoryEntity
import com.fincalc.app.databinding.FragmentTipSplitBinding
import com.fincalc.app.domain.engine.FormulaEngine
import com.fincalc.app.domain.model.CalculatorType
import com.fincalc.app.feature.calculator.CalculatorInfoBottomSheet
import com.fincalc.app.feature.calculator.CalculatorInfoProvider
import com.google.gson.Gson
import kotlinx.coroutines.launch

class TipSplitFragment : BaseFragment(R.layout.fragment_tip_split) {

    private var _binding: FragmentTipSplitBinding? = null
    private val binding get() = _binding!!
    private val gson = Gson()
    private var tipPercent = 15.0
    private var people = 1
    private var lastInputMap: Map<String, Double> = emptyMap()
    private var lastResultText: String = ""

    override fun setupUi(view: View) {
        _binding = FragmentTipSplitBinding.bind(view)

        binding.btnInfo.setOnClickListener {
            CalculatorInfoBottomSheet.newInstance("Tip Split", CalculatorInfoProvider.info(CalculatorType.TIP_SPLIT))
                .show(parentFragmentManager, "tip_info")
        }

        prefillInputs(arguments?.getString("prefillInputJson").orEmpty())

        binding.sliderTip.addOnChangeListener { _, value, _ ->
            tipPercent = value.toDouble()
            binding.tvTipValue.text = "Tip: ${value.toInt()}%"
        }
        binding.btnTip10.setOnClickListener { binding.sliderTip.value = 10f }
        binding.btnTip15.setOnClickListener { binding.sliderTip.value = 15f }
        binding.btnTip18.setOnClickListener { binding.sliderTip.value = 18f }
        binding.btnTip20.setOnClickListener { binding.sliderTip.value = 20f }

        binding.btnPeopleMinus.setOnClickListener {
            people = (people - 1).coerceAtLeast(1)
            binding.tvPeople.text = "$people people"
        }
        binding.btnPeoplePlus.setOnClickListener {
            people = (people + 1).coerceAtMost(20)
            binding.tvPeople.text = "$people people"
        }
        binding.tvPeople.text = "$people people"

        binding.btnCalculate.setOnClickListener { calculate() }

        binding.btnSave.setOnClickListener {
            if (lastResultText.isBlank()) return@setOnClickListener
            val app = requireActivity().application as FinCalcApplication
            viewLifecycleOwner.lifecycleScope.launch {
                app.appModule.historyRepository.insert(
                    HistoryEntity(
                        calculatorType = CalculatorType.TIP_SPLIT.name,
                        inputJson = gson.toJson(lastInputMap),
                        resultJson = gson.toJson(mapOf("result" to lastResultText)),
                        resultValueLabel = lastResultText
                    )
                )
            }
        }

        binding.btnShare.setOnClickListener {
            ShareUtil.shareBitmap(requireContext(), ShareUtil.renderViewToBitmap(binding.root), "Tip_Split")
        }

        binding.btnPdf.setOnClickListener {
            val app = requireActivity().application as FinCalcApplication
            if (!app.appModule.preferences.isPremium) return@setOnClickListener
            PdfExportUtil.generateCalculationPdf(
                context = requireContext(),
                calculatorName = "Tip_Split",
                inputLines = lastInputMap.map { "${it.key}: ${it.value}" },
                resultLines = listOf(binding.tvPerPerson.text.toString(), binding.tvTip.text.toString(), binding.tvTotal.text.toString())
            )
        }
    }

    private fun calculate() {
        val bill = binding.etBill.text?.toString()?.replace(",", "")?.toDoubleOrNull() ?: 0.0
        if (bill <= 0) {
            binding.etBill.error = "Enter valid bill amount"
            Toast.makeText(requireContext(), "Please enter a valid bill amount", Toast.LENGTH_SHORT).show()
            return
        }
        binding.etBill.error = null
        val result = FormulaEngine.tipSplit(bill, tipPercent, people)

        lastInputMap = mapOf("a" to bill, "b" to tipPercent, "c" to people.toDouble())
        lastResultText = "Per Person: ₹${"%.2f".format(result.perPersonAmount)}"

        binding.tvPerPerson.text = lastResultText
        binding.tvTip.text = "Tip Amount: ₹${"%.2f".format(result.tipAmount)}"
        binding.tvTotal.text = "Total Bill: ₹${"%.2f".format(result.totalBill)}"
    }

    private fun prefillInputs(inputJson: String) {
        if (inputJson.isBlank()) return
        runCatching {
            val map = gson.fromJson(inputJson, Map::class.java)
            binding.etBill.setText(map["a"]?.toString()?.replace(".0", "") ?: "")
            val p = (map["b"]?.toString()?.toDoubleOrNull() ?: 15.0).coerceIn(0.0, 30.0)
            binding.sliderTip.value = p.toFloat()
            people = map["c"]?.toString()?.toDoubleOrNull()?.toInt()?.coerceIn(1, 20) ?: 1
            binding.tvPeople.text = "$people people"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


