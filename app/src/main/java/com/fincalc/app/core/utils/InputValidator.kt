package com.fincalc.app.core.utils

import com.google.android.material.textfield.TextInputLayout

object InputValidator {

    fun requirePositive(layout: TextInputLayout, value: Double?): Boolean {
        val valid = value != null && value > 0.0
        layout.error = if (valid) null else "Enter a valid amount"
        return valid
    }

    fun parseDouble(raw: String?): Double? {
        return raw
            ?.replace(",", "")
            ?.replace("₹", "")
            ?.replace("$", "")
            ?.replace("€", "")
            ?.replace("£", "")
            ?.trim()
            ?.toDoubleOrNull()
    }
}
