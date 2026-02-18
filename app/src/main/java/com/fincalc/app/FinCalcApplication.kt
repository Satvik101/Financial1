package com.fincalc.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.fincalc.app.ads.AdManager
import com.fincalc.app.di.AppModule
import com.google.android.gms.ads.MobileAds

class FinCalcApplication : Application() {

    lateinit var appModule: AppModule
        private set

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this)
        AppCompatDelegate.setDefaultNightMode(appModule.preferences.themeMode)
        MobileAds.initialize(this)
        AdManager.initialize(this, appModule.preferences)
    }
}
