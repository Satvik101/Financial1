package com.fincalc.app.feature.calculator.compoundinterest

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class CompoundInterestViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.COMPOUND_INTEREST
}
