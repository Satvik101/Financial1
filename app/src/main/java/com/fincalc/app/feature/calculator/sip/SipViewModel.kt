package com.fincalc.app.feature.calculator.sip

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class SipViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.SIP
}
