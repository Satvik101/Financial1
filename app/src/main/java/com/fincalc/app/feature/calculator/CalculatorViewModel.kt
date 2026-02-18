package com.fincalc.app.feature.calculator

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fincalc.app.data.local.db.entity.HistoryEntity
import com.fincalc.app.data.repository.HistoryRepository
import com.fincalc.app.domain.engine.FormulaEngine
import com.fincalc.app.domain.model.AgeGroup
import com.fincalc.app.domain.model.CalculatorType
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CalculatorViewModel(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val gson = Gson()

    private val _resultText = MutableStateFlow("")
    val resultText: StateFlow<String> = _resultText.asStateFlow()

    private val _breakdownText = MutableStateFlow("")
    val breakdownText: StateFlow<String> = _breakdownText.asStateFlow()

    private val _chartPairs = MutableStateFlow<List<Pair<Float, Float>>>(emptyList())
    val chartPairs: StateFlow<List<Pair<Float, Float>>> = _chartPairs.asStateFlow()

    private val _pieParts = MutableStateFlow<List<Pair<String, Float>>>(emptyList())
    val pieParts: StateFlow<List<Pair<String, Float>>> = _pieParts.asStateFlow()

    private val _tableRows = MutableStateFlow<List<List<String>>>(emptyList())
    val tableRows: StateFlow<List<List<String>>> = _tableRows.asStateFlow()

    /** Safely convert Double to Float, clamping Infinity/NaN to 0 */
    private fun Double.safeFloat(): Float {
        if (this.isNaN() || this.isInfinite()) return 0f
        return this.coerceIn(-1e15, 1e15).toFloat()
    }

    fun calculate(
        type: CalculatorType,
        inputs: Map<String, Double>,
        useYears: Boolean,
        frequency: Int,
        tipPercent: Double,
        people: Int,
        ageGroupIndex: Int
    ) {
        val a = inputs["a"] ?: 0.0
        val b = inputs["b"] ?: 0.0
        val c = inputs["c"] ?: 0.0
        val d = inputs["d"] ?: 0.0
        val e = inputs["e"] ?: 0.0
        val f = inputs["f"] ?: 0.0
        val g = inputs["g"] ?: 0.0
        val h = inputs["h"] ?: 0.0

        _tableRows.value = emptyList()
        _pieParts.value = emptyList()

        try {
        when (type) {
            CalculatorType.COMPOUND_INTEREST -> {
                val years = if (useYears) c else c / 12.0
                val r = FormulaEngine.compoundInterest(a, b, years, frequency)
                _resultText.value = "Maturity: ₹${"%.2f".format(r.maturityAmount)}"
                _breakdownText.value = "Principal ₹${"%.2f".format(r.principal)} | Interest ₹${"%.2f".format(r.totalInterest)}"
                _chartPairs.value = r.yearlyGrowth.map { it.year.toFloat() to it.value.safeFloat() }
                _pieParts.value = listOf("Principal" to r.principal.safeFloat(), "Interest" to r.totalInterest.safeFloat())
            }
            CalculatorType.SIP -> {
                val months = if (useYears) (c * 12).toInt() else c.toInt()
                val r = FormulaEngine.sip(a, b, months)
                _resultText.value = "Total Value: ₹${"%.2f".format(r.totalValue)}"
                _breakdownText.value = "Invested ₹${"%.2f".format(r.investedAmount)} | Returns ₹${"%.2f".format(r.estimatedReturns)}"
                _chartPairs.value = r.yearlyGrowth.map { it.year.toFloat() to it.value.safeFloat() }
                _pieParts.value = listOf("Invested" to r.investedAmount.safeFloat(), "Returns" to r.estimatedReturns.safeFloat())
            }
            CalculatorType.STEP_UP_SIP -> {
                val r = FormulaEngine.stepUpSip(a, b, c, d.toInt().coerceAtLeast(1))
                _resultText.value = "Total Value: ₹${"%.2f".format(r.totalValue)}"
                _breakdownText.value = "Invested ₹${"%.2f".format(r.totalInvested)} | Returns ₹${"%.2f".format(r.estimatedReturns)}"
                _chartPairs.value = r.yearlyGrowth.map { it.year.toFloat() to it.value.safeFloat() }
                _pieParts.value = listOf("Invested" to r.totalInvested.safeFloat(), "Returns" to r.estimatedReturns.safeFloat())
            }
            CalculatorType.SIP_LUMPSUM -> {
                val months = if (useYears) (d * 12).toInt() else d.toInt()
                val r = FormulaEngine.sipPlusLumpsum(a, b, c, months)
                _resultText.value = "Total Value: ₹${"%.2f".format(r.totalValue)}"
                _breakdownText.value = "Invested ₹${"%.2f".format(r.totalInvested)} | Returns ₹${"%.2f".format(r.totalReturns)}"
                _chartPairs.value = r.yearlyGrowth.map { it.year.toFloat() to it.value.safeFloat() }
                _pieParts.value = listOf("Invested" to r.totalInvested.safeFloat(), "Returns" to r.totalReturns.safeFloat())
            }
            CalculatorType.STEP_UP_SIP_LUMPSUM -> {
                val r = FormulaEngine.stepUpSipPlusLumpsum(a, b, c, d, e.toInt().coerceAtLeast(1))
                _resultText.value = "Total Value: ₹${"%.2f".format(r.totalValue)}"
                _breakdownText.value = "Invested ₹${"%.2f".format(r.totalInvested)} | Returns ₹${"%.2f".format(r.totalReturns)}"
                _chartPairs.value = r.yearlyGrowth.map { it.year.toFloat() to it.value.safeFloat() }
                _pieParts.value = listOf("Invested" to r.totalInvested.safeFloat(), "Returns" to r.totalReturns.safeFloat())
            }
            CalculatorType.LUMPSUM -> {
                val years = if (useYears) c else c / 12.0
                val r = FormulaEngine.lumpsum(a, b, years)
                _resultText.value = "Maturity: ₹${"%.2f".format(r.maturityValue)}"
                _breakdownText.value = "Invested ₹${"%.2f".format(r.invested)} | Returns ₹${"%.2f".format(r.returns)}"
                _chartPairs.value = r.yearlyGrowth.map { it.year.toFloat() to it.value.safeFloat() }
                _pieParts.value = listOf("Invested" to r.invested.safeFloat(), "Returns" to r.returns.safeFloat())
            }
            CalculatorType.EMI -> {
                val months = if (useYears) (c * 12).toInt() else c.toInt()
                val r = FormulaEngine.emi(a, b, months)
                _resultText.value = "EMI: ₹${"%.2f".format(r.monthlyEmi)}"
                _breakdownText.value = "Total Payment ₹${"%.2f".format(r.totalPayment)} | Interest ₹${"%.2f".format(r.totalInterest)}"
                _chartPairs.value = r.amortizationSchedule.take(24).map { it.month.toFloat() to it.balance.safeFloat() }
                _pieParts.value = listOf("Principal" to a.safeFloat(), "Interest" to r.totalInterest.safeFloat())
                _tableRows.value = r.amortizationSchedule.take(60).map {
                    listOf(it.month.toString(), "%.0f".format(it.emi), "%.0f".format(it.principal), "%.0f".format(it.interest), "%.0f".format(it.balance))
                }
            }
            CalculatorType.LOAN_COMPARISON -> {
                val monthsA = if (useYears) (c * 12).toInt() else c.toInt()
                val monthsB = if (useYears) (f * 12).toInt() else f.toInt()
                val r = FormulaEngine.compareLoans(a, b, monthsA, d, e, monthsB)
                _resultText.value = "Better: ${r.betterDeal}"
                _breakdownText.value = "Loan A: ₹${"%.0f".format(r.totalPaymentA)} | Loan B: ₹${"%.0f".format(r.totalPaymentB)}"
                _chartPairs.value = listOf(1f to r.totalPaymentA.safeFloat(), 2f to r.totalPaymentB.safeFloat())
                _pieParts.value = listOf("Loan A" to r.totalPaymentA.safeFloat(), "Loan B" to r.totalPaymentB.safeFloat())
            }
            CalculatorType.SAVINGS_GOAL -> {
                val months = if (useYears) (c * 12).toInt() else c.toInt()
                val r = FormulaEngine.savingsGoal(a, b, months)
                _resultText.value = "Need ₹${"%.2f".format(r.requiredMonthlyInvestment)}/month"
                _breakdownText.value = "OR Lumpsum Today ₹${"%.2f".format(r.requiredLumpsumToday)}"
                _chartPairs.value = r.yearlyGrowth.map { it.year.toFloat() to it.value.safeFloat() }
            }
            CalculatorType.TIP_SPLIT -> {
                val r = FormulaEngine.tipSplit(a, tipPercent, people)
                _resultText.value = "Per Person: ₹${"%.2f".format(r.perPersonAmount)}"
                _breakdownText.value = "Tip ₹${"%.2f".format(r.tipAmount)} | Total ₹${"%.2f".format(r.totalBill)}"
                _chartPairs.value = emptyList()
                _pieParts.value = listOf("Bill" to a.safeFloat(), "Tip" to r.tipAmount.safeFloat())
            }
            CalculatorType.TAX_ESTIMATOR -> {
                val age = when (ageGroupIndex) {
                    1 -> AgeGroup.BETWEEN_60_80
                    2 -> AgeGroup.ABOVE_80
                    else -> AgeGroup.BELOW_60
                }
                val r = FormulaEngine.taxEstimator(a, b, c, d, age)
                _resultText.value = r.recommendation
                _breakdownText.value = "Old ₹${"%.0f".format(r.taxOldRegime)} | New ₹${"%.0f".format(r.taxNewRegime)}"
                _chartPairs.value = listOf(1f to r.taxOldRegime.safeFloat(), 2f to r.taxNewRegime.safeFloat())
                _pieParts.value = listOf("Old" to r.taxOldRegime.safeFloat(), "New" to r.taxNewRegime.safeFloat())
                _tableRows.value = (listOf(listOf("Regime", "Slab", "Taxable", "Tax", "")) +
                    r.slabBreakdownOld.map { listOf("Old", it.slabLabel, "%.0f".format(it.taxableAmount), "%.0f".format(it.tax), "") } +
                    r.slabBreakdownNew.map { listOf("New", it.slabLabel, "%.0f".format(it.taxableAmount), "%.0f".format(it.tax), "") })
            }
            CalculatorType.RETIREMENT_PLANNER -> {
                val r = FormulaEngine.retirementPlanner(
                    a.toInt(), b.toInt(), c.toInt(), d, e, f, g, h
                )
                _resultText.value = "Corpus: ₹${"%.2f".format(r.corpusNeeded)}"
                _breakdownText.value = "Gap ₹${"%.2f".format(r.gapAmount)} | SIP ₹${"%.2f".format(r.requiredMonthlySip)}/m"
                _chartPairs.value = r.accumulationTimeline.map { it.year.toFloat() to it.value.safeFloat() }
            }
            CalculatorType.FIRE_CALCULATOR -> {
                val r = FormulaEngine.fireCalculator(a, b, c.toInt().coerceAtLeast(1), d, e, f)
                _resultText.value = "FIRE Number: ₹${"%.2f".format(r.fireNumber)}"
                _breakdownText.value = "Gap ₹${"%.2f".format(r.gap)} | Need ₹${"%.2f".format(r.monthlySavingNeeded)}/m"
                _chartPairs.value = r.requiredPath.map { it.year.toFloat() to it.value.safeFloat() }
            }
            CalculatorType.INFLATION_ADJUSTER -> {
                val r = FormulaEngine.inflationAdjuster(a, b, c.toInt().coerceAtLeast(1))
                _resultText.value = "Future Purchasing Power: ₹${"%.2f".format(r.futureValueOfCurrentAmount)}"
                _breakdownText.value = "Need ₹${"%.2f".format(r.requiredFutureAmountForSamePurchasingPower)} for same buying power"
                _chartPairs.value = r.yearlyPurchasingPowerCurve.map { it.year.toFloat() to it.value.safeFloat() }
            }
            CalculatorType.FD -> {
                val years = if (useYears) c else c / 12.0
                val r = FormulaEngine.fdCalculator(a, b, years, frequency)
                _resultText.value = "Maturity: ₹${"%.2f".format(r.maturityAmount)}"
                _breakdownText.value = "Interest ₹${"%.2f".format(r.totalInterest)}"
                _chartPairs.value = r.yearlyGrowth.map { it.year.toFloat() to it.value.safeFloat() }
                _pieParts.value = listOf("Deposit" to a.safeFloat(), "Interest" to r.totalInterest.safeFloat())
            }
            CalculatorType.PPF -> {
                val r = FormulaEngine.ppfCalculator(a, b, c.toInt())
                _resultText.value = "Maturity: ₹${"%.2f".format(r.maturityValue)}"
                _breakdownText.value = "Invested ₹${"%.2f".format(r.totalInvested)} | Interest ₹${"%.2f".format(r.totalInterest)}"
                _chartPairs.value = r.breakdown.map { it.year.toFloat() to it.closingBalance.safeFloat() }
                _tableRows.value = r.breakdown.map {
                    listOf(it.year.toString(), "%.0f".format(it.deposit), "%.0f".format(it.interest), "%.0f".format(it.closingBalance), "")
                }
            }
            CalculatorType.CAGR -> {
                val r = FormulaEngine.cagr(a, b, c.toInt().coerceAtLeast(1))
                _resultText.value = "CAGR: ${"%.2f".format(r.cagr)}%"
                _breakdownText.value = "Absolute Return: ${"%.2f".format(r.absoluteReturnPercent)}%"
                _chartPairs.value = r.yearlyGrowth.map { it.year.toFloat() to it.value.safeFloat() }
            }
        }
        } catch (ex: Exception) {
            _resultText.value = "Calculation error"
            _breakdownText.value = "Please check your inputs and try again"
            _chartPairs.value = emptyList()
            _pieParts.value = emptyList()
            _tableRows.value = emptyList()
        }
    }

    fun saveToHistory(type: CalculatorType, inputMap: Map<String, Double>) {
        val result = _resultText.value
        viewModelScope.launch {
            historyRepository.insert(
                HistoryEntity(
                    calculatorType = type.name,
                    inputJson = gson.toJson(inputMap),
                    resultJson = gson.toJson(mapOf("result" to result)),
                    resultValueLabel = result
                )
            )
        }
    }
}
