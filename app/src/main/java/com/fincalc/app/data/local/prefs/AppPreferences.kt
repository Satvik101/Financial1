package com.fincalc.app.data.local.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.fincalc.app.core.constants.AppConstants

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        AppConstants.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    var calcCount: Int
        get() = prefs.getInt(KEY_CALC_COUNT, 0)
        set(value) = prefs.edit().putInt(KEY_CALC_COUNT, value).apply()

    var isPremium: Boolean
        get() = prefs.getBoolean(KEY_PREMIUM, false)
        set(value) = prefs.edit().putBoolean(KEY_PREMIUM, value).apply()

    var currencySymbol: String
        get() = prefs.getString(KEY_CURRENCY, AppConstants.CURRENCY_INR) ?: AppConstants.CURRENCY_INR
        set(value) = prefs.edit().putString(KEY_CURRENCY, value).apply()

    var numberFormatIndian: Boolean
        get() = prefs.getBoolean(KEY_INDIAN_FORMAT, true)
        set(value) = prefs.edit().putBoolean(KEY_INDIAN_FORMAT, value).apply()

    var themeMode: Int
        get() = prefs.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) = prefs.edit().putInt(KEY_THEME, value).apply()

    fun incrementCalcCount(): Int {
        val next = calcCount + 1
        calcCount = next
        return next
    }

    companion object {
        private const val KEY_CALC_COUNT = "calc_count"
        private const val KEY_PREMIUM = "premium"
        private const val KEY_CURRENCY = "currency"
        private const val KEY_INDIAN_FORMAT = "number_format_indian"
        private const val KEY_THEME = "theme"
    }
}
