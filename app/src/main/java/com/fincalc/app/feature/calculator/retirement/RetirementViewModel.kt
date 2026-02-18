package com.fincalc.app.feature.calculator.retirement

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class RetirementViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.RETIREMENT_PLANNER
}
