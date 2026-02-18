package com.fincalc.app.core.formatter

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText

class CurrencyTextWatcher(
    private val editText: TextInputEditText,
    private val indianFormat: Boolean
) : TextWatcher {

    private var selfChange = false

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

    override fun afterTextChanged(editable: Editable?) {
        if (selfChange) return
        val raw = editable?.toString()?.replace(",", "")?.trim().orEmpty()
        if (raw.isEmpty()) return

        // Preserve decimal part if user is typing a decimal number
        val hasDecimal = raw.contains(".")
        val parts = raw.split(".")
        val wholePart = parts[0].toLongOrNull() ?: return
        val decimalPart = if (hasDecimal && parts.size > 1) parts[1] else ""

        val formatted = if (indianFormat) {
            NumberFormatter.formatIndian(wholePart.toDouble())
        } else {
            NumberFormatter.formatInternational(wholePart.toDouble())
        }

        val finalText = if (hasDecimal) "$formatted.$decimalPart" else formatted
        selfChange = true
        editText.setText(finalText)
        editText.setSelection(finalText.length)
        selfChange = false
    }
}
