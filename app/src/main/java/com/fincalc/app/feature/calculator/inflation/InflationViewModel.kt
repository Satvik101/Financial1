package com.fincalc.app.feature.calculator.inflation

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class InflationViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.INFLATION_ADJUSTER
}
