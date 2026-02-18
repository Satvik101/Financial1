package com.fincalc.app.feature.calculator.cagr

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class CagrViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.CAGR
}
