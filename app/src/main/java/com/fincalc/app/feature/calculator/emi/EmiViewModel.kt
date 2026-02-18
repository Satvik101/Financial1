package com.fincalc.app.feature.calculator.emi

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class EmiViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.EMI
}
