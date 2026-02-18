package com.fincalc.app.feature.calculator.tipsplit

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class TipSplitViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.TIP_SPLIT
}
