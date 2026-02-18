package com.fincalc.app.feature.calculator.loancomparison

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class LoanComparisonViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.LOAN_COMPARISON
}
