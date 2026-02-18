package com.fincalc.app.feature.calculator.fd

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class FdViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.FD
}
