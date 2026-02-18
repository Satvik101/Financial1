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
        val number = raw.toLongOrNull() ?: return

        val formatted = if (indianFormat) {
            NumberFormatter.formatIndian(number.toDouble())
        } else {
            NumberFormatter.formatInternational(number.toDouble())
        }
        selfChange = true
        editText.setText(formatted)
        editText.setSelection(formatted.length)
        selfChange = false
    }
}
