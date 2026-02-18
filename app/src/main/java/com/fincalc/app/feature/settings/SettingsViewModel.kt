package com.fincalc.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.appcompat.app.AppCompatDelegate
import com.fincalc.app.data.local.prefs.AppPreferences

class SettingsViewModel(
    private val prefs: AppPreferences
) : ViewModel() {

    fun setCurrency(symbol: String) {
        prefs.currencySymbol = symbol
    }

    fun getCurrency(): String = prefs.currencySymbol

    fun isPremium(): Boolean = prefs.isPremium

    fun setPremium(value: Boolean) {
        prefs.isPremium = value
    }

    fun setTheme(mode: Int) {
        prefs.themeMode = mode
    }

    fun getTheme(): Int = prefs.themeMode

    fun setIndianFormat(enabled: Boolean) {
        prefs.numberFormatIndian = enabled
    }

    fun isIndianFormat(): Boolean = prefs.numberFormatIndian
}
