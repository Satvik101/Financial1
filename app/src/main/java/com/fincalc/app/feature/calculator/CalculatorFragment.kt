package com.fincalc.app.feature.calculator

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.fincalc.app.FinCalcApplication
import com.fincalc.app.R
import com.fincalc.app.ads.AdManager
import com.fincalc.app.core.base.BaseFragment
import com.fincalc.app.core.base.SimpleViewModelFactory
import com.fincalc.app.core.formatter.CurrencyTextWatcher
import com.fincalc.app.core.utils.PdfExportUtil
import com.fincalc.app.core.utils.ShareUtil
import com.fincalc.app.databinding.FragmentCalculatorBinding
import com.fincalc.app.domain.model.CalculatorType
import com.fincalc.app.feature.calculator.adapter.CalculatorTableAdapter
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.gson.Gson
import kotlinx.coroutines.launch

class CalculatorFragment : BaseFragment(R.layout.fragment_calculator) {

    private var _binding: FragmentCalculatorBinding? = null
    private val binding get() = _binding!!

    private val args by lazy { CalculatorFragmentArgs.fromBundle(requireArguments()) }

    private val viewModel: CalculatorViewModel by viewModels {
        val app = requireActivity().application as FinCalcApplication
        SimpleViewModelFactory { CalculatorViewModel(app.appModule.historyRepository) }
    }

    private lateinit var calculatorType: CalculatorType
    private lateinit var config: CalculatorUiConfig
    private val gson = Gson()
    private var tipPercent = 15.0
    private var peopleCount = 1
    private var compoundingFrequency = 1
    private var ageGroupIndex = 0
    private var lastInputMap: Map<String, Double> = emptyMap()
    private lateinit var tableAdapter: CalculatorTableAdapter

    override fun setupUi(view: View) {
        _binding = FragmentCalculatorBinding.bind(view)
        calculatorType = CalculatorType.valueOf(args.calculatorType)
        binding.tvTitle.text = calculatorType.title
        config = CalculatorUiConfigFactory.forType(calculatorType)

        binding.btnInfo.setOnClickListener {
            CalculatorInfoBottomSheet.newInstance(
                title = calculatorType.title,
                body = CalculatorInfoProvider.info(calculatorType)
            ).show(parentFragmentManager, "calc_info")
        }

        tableAdapter = CalculatorTableAdapter()
        binding.rvTable.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTable.adapter = tableAdapter

        setupDynamicUi()

        val whatIfSupported = calculatorType in setOf(
            CalculatorType.COMPOUND_INTEREST,
            CalculatorType.SIP,
            CalculatorType.STEP_UP_SIP,
            CalculatorType.LUMPSUM,
            CalculatorType.EMI
        )
        binding.switchCompare.visibility = if (whatIfSupported) View.VISIBLE else View.GONE
        binding.switchCompare.setOnCheckedChangeListener { _, isChecked ->
            binding.btnOpenCompare.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
        binding.btnOpenCompare.setOnClickListener {
            val bundle = Bundle().apply { putString("calculatorType", calculatorType.name) }
            findNavController().navigate(com.fincalc.app.R.id.whatIfContainerFragment, bundle)
        }

        prefillFromHistory(args.prefillInputJson)

        binding.btnCalculate.setOnClickListener {
            val inputMap = collectInputs()
            if (!validateInputs(inputMap)) {
                return@setOnClickListener
            }

            viewModel.calculate(
                type = calculatorType,
                inputs = inputMap,
                useYears = binding.chipYears.isChecked,
                frequency = compoundingFrequency,
                tipPercent = tipPercent,
                people = peopleCount,
                ageGroupIndex = ageGroupIndex
            )
            binding.tvResult.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up))
            binding.lineChart.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
            binding.pieChart.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.fade_in))
            lastInputMap = inputMap
            AdManager.onCalculationCompleted(requireActivity())
        }

        binding.btnSave.setOnClickListener {
            if (binding.tvResult.text.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Calculate first", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.saveToHistory(calculatorType, lastInputMap)
                Toast.makeText(requireContext(), "Saved in history", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnShare.setOnClickListener {
            val bitmap = ShareUtil.renderViewToBitmap(binding.root)
            ShareUtil.shareBitmap(requireContext(), bitmap, "${calculatorType.name}_result")
        }

        binding.btnPdf.setOnClickListener {
            val app = requireActivity().application as FinCalcApplication
            if (!app.appModule.preferences.isPremium) {
                Toast.makeText(requireContext(), "Upgrade to Premium to export PDF", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val file = PdfExportUtil.generateCalculationPdf(
                requireContext(),
                calculatorType.title,
                collectInputs().map { "${it.key}: ${it.value}" },
                listOf(binding.tvResult.text.toString(), binding.tvBreakdown.text.toString()),
                ShareUtil.renderViewToBitmap(binding.lineChart)
            )
            Toast.makeText(requireContext(), "PDF saved: ${file.name}", Toast.LENGTH_LONG).show()
        }
    }

    override fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.resultText.collect { binding.tvResult.text = it }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.breakdownText.collect { binding.tvBreakdown.text = it }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.chartPairs.collect { pairs ->
                if (pairs.isEmpty()) {
                    binding.lineChart.visibility = View.GONE
                    binding.lineChart.clear()
                    return@collect
                }
                binding.lineChart.visibility = View.VISIBLE
                val entries = pairs.map { Entry(it.first, it.second) }
                val chartPrimary = Color.parseColor("#6366F1")
                val chartText = Color.parseColor("#94A3B8")
                val dataSet = LineDataSet(entries, calculatorType.title).apply {
                    color = chartPrimary
                    valueTextColor = chartText
                    valueTextSize = 10f
                    lineWidth = 2f
                    setCircleColor(chartPrimary)
                    circleRadius = 3f
                    setDrawCircleHole(true)
                    circleHoleRadius = 1.5f
                    setDrawFilled(true)
                    fillColor = chartPrimary
                    fillAlpha = 20
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawHighlightIndicators(false)
                }
                binding.lineChart.data = LineData(dataSet)
                binding.lineChart.description.isEnabled = false
                binding.lineChart.setPinchZoom(true)
                binding.lineChart.isDoubleTapToZoomEnabled = false
                binding.lineChart.isHighlightPerTapEnabled = true
                binding.lineChart.xAxis.granularity = 1f
                binding.lineChart.xAxis.textColor = chartText
                binding.lineChart.xAxis.textSize = 10f
                binding.lineChart.xAxis.setDrawGridLines(false)
                binding.lineChart.axisLeft.textColor = chartText
                binding.lineChart.axisLeft.textSize = 10f
                binding.lineChart.axisLeft.gridColor = Color.parseColor("#E2E8F0")
                binding.lineChart.axisLeft.gridLineWidth = 0.5f
                binding.lineChart.axisRight.isEnabled = false
                binding.lineChart.legend.textColor = chartText
                binding.lineChart.legend.textSize = 11f
                binding.lineChart.setExtraOffsets(8f, 12f, 8f, 8f)
                binding.lineChart.animateX(600)
                binding.lineChart.invalidate()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pieParts.collect { parts ->
                if (parts.isEmpty()) {
                    binding.pieChart.visibility = View.GONE
                    binding.pieChart.clear()
                    return@collect
                }
                binding.pieChart.visibility = View.VISIBLE
                val entries = parts.map { PieEntry(it.second, it.first) }
                val pieText = Color.parseColor("#94A3B8")
                val dataSet = PieDataSet(entries, "").apply {
                    colors = listOf(Color.parseColor("#6366F1"), Color.parseColor("#10B981"), Color.parseColor("#F43F5E"), Color.parseColor("#F59E0B"))
                    valueTextColor = Color.WHITE
                    valueTextSize = 11f
                    sliceSpace = 3f
                    selectionShift = 6f
                }
                binding.pieChart.data = PieData(dataSet)
                binding.pieChart.description.isEnabled = false
                binding.pieChart.isRotationEnabled = true
                binding.pieChart.isHighlightPerTapEnabled = true
                binding.pieChart.setUsePercentValues(false)
                binding.pieChart.setHoleColor(Color.TRANSPARENT)
                binding.pieChart.holeRadius = 50f
                binding.pieChart.transparentCircleRadius = 53f
                binding.pieChart.setEntryLabelColor(Color.WHITE)
                binding.pieChart.setEntryLabelTextSize(10f)
                binding.pieChart.legend.textColor = pieText
                binding.pieChart.legend.textSize = 11f
                binding.pieChart.setExtraOffsets(4f, 4f, 4f, 4f)
                binding.pieChart.animateY(600)
                binding.pieChart.invalidate()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tableRows.collect { rows ->
                binding.tvTableTitle.visibility = if (rows.isEmpty()) View.GONE else View.VISIBLE
                binding.rvTable.visibility = if (rows.isEmpty()) View.GONE else View.VISIBLE
                tableAdapter.submitRows(rows)
            }
        }
    }

    private fun prefillFromHistory(inputJson: String) {
        if (inputJson.isBlank()) return
        runCatching {
            val map = gson.fromJson(inputJson, Map::class.java)
            binding.etA.setText(map["a"]?.toString()?.replace(".0", "") ?: "")
            binding.etB.setText(map["b"]?.toString()?.replace(".0", "") ?: "")
            binding.etC.setText(map["c"]?.toString()?.replace(".0", "") ?: "")
            binding.etD.setText(map["d"]?.toString()?.replace(".0", "") ?: "")
            binding.etE.setText(map["e"]?.toString()?.replace(".0", "") ?: "")
            binding.etF.setText(map["f"]?.toString()?.replace(".0", "") ?: "")
            binding.etG.setText(map["g"]?.toString()?.replace(".0", "") ?: "")
            binding.etH.setText(map["h"]?.toString()?.replace(".0", "") ?: "")
        }
    }

    private fun setupDynamicUi() {
        val fields = listOf(
            binding.tilA to "etA",
            binding.tilB to "etB",
            binding.tilC to "etC",
            binding.tilD to "etD",
            binding.tilE to "etE",
            binding.tilF to "etF",
            binding.tilG to "etG",
            binding.tilH to "etH"
        )
        fields.forEachIndexed { index, pair ->
            pair.first.visibility = CalculatorUiConfigFactory.fieldVisibility(config.hints.size, index)
            if (index < config.hints.size) pair.first.hint = config.hints[index]
        }

        val app = requireActivity().application as FinCalcApplication
        val useIndian = app.appModule.preferences.numberFormatIndian
        val inputViews = listOf(binding.etA, binding.etB, binding.etC, binding.etD, binding.etE, binding.etF, binding.etG, binding.etH)
        fields.forEachIndexed { index, (til, _) ->
            if (til.visibility == View.VISIBLE) {
                val hint = config.hints.getOrNull(index).orEmpty()
                val isAmountField = hint.contains("₹") || hint.contains("Amount") ||
                    hint.contains("SIP") || hint.contains("Lumpsum") ||
                    hint.contains("Loan") || hint.contains("Deposit") ||
                    hint.contains("Income") || hint.contains("Expense") ||
                    hint.contains("Investment") || hint.contains("Savings") ||
                    hint.contains("Principal") || hint.contains("Bill") ||
                    hint.contains("Value") || hint.contains("Target")
                val isNotSpecialField = !hint.contains("Age") && !hint.contains("Rate") &&
                    !hint.contains("%") && !hint.contains("Year") && !hint.contains("Time") &&
                    !hint.contains("Tenure") && !hint.contains("SWR")
                if (isAmountField || isNotSpecialField) {
                    inputViews[index].addTextChangedListener(CurrencyTextWatcher(inputViews[index], useIndian))
                }
            }
            inputViews[index].addTextChangedListener(object : android.text.TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                override fun afterTextChanged(s: android.text.Editable?) {
                    clearInputErrors()
                    binding.btnCalculate.isEnabled = collectInputs().values.any { it > 0.0 }
                }
            })
        }

        binding.chipPeriod.visibility = if (config.showPeriodToggle) View.VISIBLE else View.GONE
        binding.tilFrequency.visibility = if (config.showFrequency) View.VISIBLE else View.GONE
        binding.tilAgeGroup.visibility = if (config.showAgeGroup) View.VISIBLE else View.GONE
        binding.layoutTipControls.visibility = if (config.showTipControls) View.VISIBLE else View.GONE
        binding.tvTableTitle.visibility = if (config.showTable) View.VISIBLE else View.GONE
        binding.rvTable.visibility = if (config.showTable) View.VISIBLE else View.GONE

        val freqList = resources.getStringArray(com.fincalc.app.R.array.compounding_frequency_options).toList()
        binding.actFrequency.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, freqList))
        binding.actFrequency.setText(freqList.last(), false)
        binding.actFrequency.setOnItemClickListener { _, _, position, _ ->
            compoundingFrequency = when (position) {
                0 -> 12
                1 -> 4
                2 -> 2
                else -> 1
            }
        }

        val ageList = resources.getStringArray(com.fincalc.app.R.array.age_group_options).toList()
        binding.actAgeGroup.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, ageList))
        binding.actAgeGroup.setText(ageList.first(), false)
        binding.actAgeGroup.setOnItemClickListener { _, _, position, _ -> ageGroupIndex = position }

        binding.sliderTip.addOnChangeListener { _, value, _ ->
            tipPercent = value.toDouble()
            binding.tvTipValue.text = "Tip: ${value.toInt()}%"
        }
        binding.btnTip10.setOnClickListener { binding.sliderTip.value = 10f }
        binding.btnTip15.setOnClickListener { binding.sliderTip.value = 15f }
        binding.btnTip18.setOnClickListener { binding.sliderTip.value = 18f }
        binding.btnTip20.setOnClickListener { binding.sliderTip.value = 20f }
        binding.btnPeopleMinus.setOnClickListener {
            peopleCount = (peopleCount - 1).coerceAtLeast(1)
            binding.tvPeople.text = "$peopleCount people"
        }
        binding.btnPeoplePlus.setOnClickListener {
            peopleCount = (peopleCount + 1).coerceAtMost(20)
            binding.tvPeople.text = "$peopleCount people"
        }
        binding.tvPeople.text = "$peopleCount people"
    }

    private fun collectInputs(): Map<String, Double> {
        fun parse(raw: CharSequence?): Double {
            return raw
                ?.toString()
                ?.replace(",", "")
                ?.replace("₹", "")
                ?.replace("$", "")
                ?.replace("€", "")
                ?.replace("£", "")
                ?.trim()
                ?.toDoubleOrNull() ?: 0.0
        }
        return mapOf(
            "a" to parse(binding.etA.text),
            "b" to parse(binding.etB.text),
            "c" to parse(binding.etC.text),
            "d" to parse(binding.etD.text),
            "e" to parse(binding.etE.text),
            "f" to parse(binding.etF.text),
            "g" to parse(binding.etG.text),
            "h" to parse(binding.etH.text)
        )
    }

    private fun validateInputs(inputMap: Map<String, Double>): Boolean {
        clearInputErrors()

        fun markError(key: String, message: String) {
            when (key) {
                "a" -> binding.tilA.error = message
                "b" -> binding.tilB.error = message
                "c" -> binding.tilC.error = message
                "d" -> binding.tilD.error = message
                "e" -> binding.tilE.error = message
                "f" -> binding.tilF.error = message
                "g" -> binding.tilG.error = message
                "h" -> binding.tilH.error = message
            }
        }

        fun messageFor(key: String): String {
            return when (calculatorType) {
                CalculatorType.COMPOUND_INTEREST -> when (key) {
                    "a" -> "Enter principal amount"
                    "b" -> "Enter annual interest rate"
                    "c" -> "Enter time period"
                    else -> "Enter a valid value"
                }
                CalculatorType.SIP -> when (key) {
                    "a" -> "Enter monthly SIP amount"
                    "b" -> "Enter expected return rate"
                    "c" -> "Enter time period"
                    else -> "Enter a valid value"
                }
                CalculatorType.STEP_UP_SIP -> when (key) {
                    "a" -> "Enter starting SIP amount"
                    "b" -> "Enter annual step-up rate"
                    "c" -> "Enter expected return rate"
                    "d" -> "Enter investment period in years"
                    else -> "Enter a valid value"
                }
                CalculatorType.SIP_LUMPSUM -> when (key) {
                    "a" -> "Enter lumpsum amount"
                    "b" -> "Enter monthly SIP amount"
                    "c" -> "Enter expected return rate"
                    "d" -> "Enter time period"
                    else -> "Enter a valid value"
                }
                CalculatorType.STEP_UP_SIP_LUMPSUM -> when (key) {
                    "a" -> "Enter lumpsum amount"
                    "b" -> "Enter starting SIP amount"
                    "c" -> "Enter step-up rate"
                    "d" -> "Enter expected return rate"
                    "e" -> "Enter period in years"
                    else -> "Enter a valid value"
                }
                CalculatorType.LUMPSUM -> when (key) {
                    "a" -> "Enter investment amount"
                    "b" -> "Enter expected return rate"
                    "c" -> "Enter time period"
                    else -> "Enter a valid value"
                }
                CalculatorType.EMI -> when (key) {
                    "a" -> "Enter loan amount"
                    "b" -> "Enter annual interest rate"
                    "c" -> "Enter loan tenure"
                    else -> "Enter a valid value"
                }
                CalculatorType.LOAN_COMPARISON -> when (key) {
                    "a" -> "Enter Loan A amount"
                    "b" -> "Enter Loan A rate"
                    "c" -> "Enter Loan A tenure"
                    "d" -> "Enter Loan B amount"
                    "e" -> "Enter Loan B rate"
                    "f" -> "Enter Loan B tenure"
                    else -> "Enter a valid value"
                }
                CalculatorType.SAVINGS_GOAL -> when (key) {
                    "a" -> "Enter target amount"
                    "b" -> "Enter expected return rate"
                    "c" -> "Enter time period"
                    else -> "Enter a valid value"
                }
                CalculatorType.TAX_ESTIMATOR -> when (key) {
                    "a" -> "Enter annual income"
                    "b" -> "Enter 80C deductions"
                    "c" -> "Enter HRA exemption"
                    "d" -> "Enter other deductions"
                    else -> "Enter a valid value"
                }
                CalculatorType.RETIREMENT_PLANNER -> when (key) {
                    "a" -> "Enter current age"
                    "b" -> "Enter retirement age"
                    "c" -> "Enter life expectancy"
                    "d" -> "Enter current monthly expenses"
                    "e" -> "Enter inflation rate"
                    "f" -> "Enter current savings"
                    "g" -> "Enter pre-retirement return"
                    "h" -> "Enter post-retirement return"
                    else -> "Enter a valid value"
                }
                CalculatorType.FIRE_CALCULATOR -> when (key) {
                    "a" -> "Enter annual expenses"
                    "b" -> "Enter inflation rate"
                    "c" -> "Enter years to FIRE"
                    "d" -> "Enter safe withdrawal rate"
                    "e" -> "Enter current investments"
                    "f" -> "Enter expected return rate"
                    else -> "Enter a valid value"
                }
                CalculatorType.INFLATION_ADJUSTER -> when (key) {
                    "a" -> "Enter current amount"
                    "b" -> "Enter inflation rate"
                    "c" -> "Enter period in years"
                    else -> "Enter a valid value"
                }
                CalculatorType.FD -> when (key) {
                    "a" -> "Enter deposit amount"
                    "b" -> "Enter interest rate"
                    "c" -> "Enter tenure"
                    else -> "Enter a valid value"
                }
                CalculatorType.PPF -> when (key) {
                    "a" -> "Enter yearly deposit"
                    "b" -> "Enter interest rate"
                    "c" -> "Enter tenure"
                    else -> "Enter a valid value"
                }
                CalculatorType.CAGR -> when (key) {
                    "a" -> "Enter initial value"
                    "b" -> "Enter final value"
                    "c" -> "Enter period in years"
                    else -> "Enter a valid value"
                }
                CalculatorType.TIP_SPLIT -> "Enter a valid value"
            }
        }

        val requiredKeys = when (calculatorType) {
            CalculatorType.STEP_UP_SIP -> listOf("a", "b", "c", "d")
            CalculatorType.SIP_LUMPSUM -> listOf("a", "b", "c", "d")
            CalculatorType.STEP_UP_SIP_LUMPSUM -> listOf("a", "b", "c", "d", "e")
            CalculatorType.LOAN_COMPARISON -> listOf("a", "b", "c", "d", "e", "f")
            CalculatorType.RETIREMENT_PLANNER -> listOf("a", "b", "c", "d", "e", "f", "g", "h")
            CalculatorType.FIRE_CALCULATOR -> listOf("a", "b", "c", "d", "e", "f")
            else -> listOf("a", "b", "c")
        }

        var valid = true
        requiredKeys.forEach { key ->
            val value = inputMap[key] ?: 0.0
            if (value <= 0.0) {
                markError(key, messageFor(key))
                valid = false
            }
        }

        fun invalidate(key: String, message: String) {
            markError(key, message)
            valid = false
        }

        if (valid) {
            when (calculatorType) {
                CalculatorType.COMPOUND_INTEREST,
                CalculatorType.SIP,
                CalculatorType.LUMPSUM,
                CalculatorType.SAVINGS_GOAL,
                CalculatorType.FD -> {
                    if ((inputMap["b"] ?: 0.0) > 100.0) invalidate("b", "Rate should be ≤ 100%")
                }
                CalculatorType.STEP_UP_SIP -> {
                    if ((inputMap["b"] ?: 0.0) > 100.0) invalidate("b", "Step-up rate should be ≤ 100%")
                    if ((inputMap["c"] ?: 0.0) > 100.0) invalidate("c", "Return rate should be ≤ 100%")
                }
                CalculatorType.SIP_LUMPSUM -> {
                    if ((inputMap["c"] ?: 0.0) > 100.0) invalidate("c", "Return rate should be ≤ 100%")
                }
                CalculatorType.STEP_UP_SIP_LUMPSUM -> {
                    if ((inputMap["c"] ?: 0.0) > 100.0) invalidate("c", "Step-up rate should be ≤ 100%")
                    if ((inputMap["d"] ?: 0.0) > 100.0) invalidate("d", "Return rate should be ≤ 100%")
                }
                CalculatorType.LOAN_COMPARISON -> {
                    if ((inputMap["b"] ?: 0.0) > 60.0) invalidate("b", "Loan A rate looks too high")
                    if ((inputMap["e"] ?: 0.0) > 60.0) invalidate("e", "Loan B rate looks too high")
                }
                CalculatorType.RETIREMENT_PLANNER -> {
                    val currentAge = inputMap["a"] ?: 0.0
                    val retirementAge = inputMap["b"] ?: 0.0
                    val lifeExpectancy = inputMap["c"] ?: 0.0
                    if (!(currentAge < retirementAge && retirementAge < lifeExpectancy)) {
                        invalidate("b", "Retirement age must be between current age and life expectancy")
                    }
                }
                CalculatorType.FIRE_CALCULATOR -> {
                    if ((inputMap["d"] ?: 0.0) > 20.0) invalidate("d", "SWR should generally be ≤ 20%")
                    if ((inputMap["f"] ?: 0.0) > 100.0) invalidate("f", "Expected return should be ≤ 100%")
                }
                CalculatorType.INFLATION_ADJUSTER -> {
                    if ((inputMap["b"] ?: 0.0) > 50.0) invalidate("b", "Inflation rate should be ≤ 50%")
                }
                CalculatorType.PPF -> {
                    if ((inputMap["a"] ?: 0.0) > 150000.0) invalidate("a", "PPF yearly deposit cannot exceed ₹1,50,000")
                    if ((inputMap["b"] ?: 0.0) > 20.0) invalidate("b", "PPF interest rate should be ≤ 20%")
                }
                CalculatorType.CAGR -> {
                    if ((inputMap["c"] ?: 0.0) > 100.0) invalidate("c", "Years should be ≤ 100")
                }
                else -> Unit
            }
        }

        if (!valid) {
            Toast.makeText(requireContext(), "Enter valid input values", Toast.LENGTH_SHORT).show()
        }
        return valid
    }

    private fun clearInputErrors() {
        binding.tilA.error = null
        binding.tilB.error = null
        binding.tilC.error = null
        binding.tilD.error = null
        binding.tilE.error = null
        binding.tilF.error = null
        binding.tilG.error = null
        binding.tilH.error = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
