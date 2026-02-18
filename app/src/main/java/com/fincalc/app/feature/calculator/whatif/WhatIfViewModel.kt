package com.fincalc.app.feature.calculator.whatif

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.engine.FormulaEngine
import com.fincalc.app.domain.model.CalculatorType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class WhatIfViewModel : ViewModel() {

    private val _comparison = MutableStateFlow("")
    val comparison: StateFlow<String> = _comparison.asStateFlow()

    private val _planValues = MutableStateFlow(0.0 to 0.0)
    val planValues: StateFlow<Pair<Double, Double>> = _planValues.asStateFlow()

    fun compare(type: CalculatorType, a1: Double, b1: Double, c1: Double, a2: Double, b2: Double, c2: Double) {
        val planA = when (type) {
            CalculatorType.COMPOUND_INTEREST -> FormulaEngine.compoundInterest(a1, b1, c1, 1).maturityAmount
            CalculatorType.SIP -> FormulaEngine.sip(a1, b1, c1.toInt()).totalValue
            CalculatorType.STEP_UP_SIP -> FormulaEngine.stepUpSip(a1, b1, 12.0, c1.toInt()).totalValue
            CalculatorType.LUMPSUM -> FormulaEngine.lumpsum(a1, b1, c1).maturityValue
            CalculatorType.EMI -> FormulaEngine.emi(a1, b1, c1.toInt()).totalPayment
            else -> 0.0
        }
        val planB = when (type) {
            CalculatorType.COMPOUND_INTEREST -> FormulaEngine.compoundInterest(a2, b2, c2, 1).maturityAmount
            CalculatorType.SIP -> FormulaEngine.sip(a2, b2, c2.toInt()).totalValue
            CalculatorType.STEP_UP_SIP -> FormulaEngine.stepUpSip(a2, b2, 12.0, c2.toInt()).totalValue
            CalculatorType.LUMPSUM -> FormulaEngine.lumpsum(a2, b2, c2).maturityValue
            CalculatorType.EMI -> FormulaEngine.emi(a2, b2, c2.toInt()).totalPayment
            else -> 0.0
        }
        val diff = planB - planA
        _planValues.value = planA to planB
        _comparison.value = if (diff >= 0) {
            "Plan B gives ₹${"%.2f".format(diff)} more"
        } else {
            "Plan A gives ₹${"%.2f".format(-diff)} more"
        }
    }
}
