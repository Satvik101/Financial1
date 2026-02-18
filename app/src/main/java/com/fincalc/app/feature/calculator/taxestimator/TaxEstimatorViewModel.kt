package com.fincalc.app.feature.calculator.taxestimator

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType

class TaxEstimatorViewModel : ViewModel() {
    val calculatorType: CalculatorType = CalculatorType.TAX_ESTIMATOR
}
