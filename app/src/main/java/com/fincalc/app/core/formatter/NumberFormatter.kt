package com.fincalc.app.core.formatter

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object NumberFormatter {

    fun formatCurrency(value: Double, currencySymbol: String, indian: Boolean): String {
        val formatted = if (indian) formatIndian(value) else formatInternational(value)
        return "$currencySymbol$formatted"
    }

    fun formatIndian(value: Double): String {
        val abs = kotlin.math.abs(value)
        val whole = abs.toLong().toString()
        if (whole.length <= 3) return prefixSign(value) + whole
        val last3 = whole.takeLast(3)
        var remaining = whole.dropLast(3)
        val chunks = mutableListOf<String>()
        while (remaining.length > 2) {
            chunks.add(0, remaining.takeLast(2))
            remaining = remaining.dropLast(2)
        }
        if (remaining.isNotEmpty()) chunks.add(0, remaining)
        return prefixSign(value) + chunks.joinToString(",") + ",$last3"
    }

    fun formatInternational(value: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val formatter = DecimalFormat("#,###", symbols)
        return formatter.format(value)
    }

    fun abbreviate(value: Double): String {
        return when {
            value >= 10_000_000 -> "%.2f Cr".format(value / 10_000_000)
            value >= 100_000 -> "%.2f L".format(value / 100_000)
            value >= 1_000 -> "%.1f K".format(value / 1_000)
            else -> "%.2f".format(value)
        }
    }

    private fun prefixSign(value: Double): String = if (value < 0) "-" else ""
}
