package com.fincalc.app.feature.calculator.lumpsum

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class LumpsumViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.LUMPSUM
}
