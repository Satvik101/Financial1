package com.fincalc.app.feature.calculator.ppf

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class PpfViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.PPF
}
