package com.fincalc.app.feature.calculator.savingsgoal

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class SavingsGoalViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.SAVINGS_GOAL
}
