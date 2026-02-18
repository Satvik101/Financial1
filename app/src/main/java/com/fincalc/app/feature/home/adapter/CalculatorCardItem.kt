package com.fincalc.app.feature.home.adapter

import com.fincalc.app.domain.model.CalculatorType

data class CalculatorCardItem(
    val type: CalculatorType,
    val name: String,
    val description: String,
    val accentColorHex: String
)
