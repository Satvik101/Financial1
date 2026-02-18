package com.fincalc.app.feature.calculator.fire

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class FireViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.FIRE_CALCULATOR
}
