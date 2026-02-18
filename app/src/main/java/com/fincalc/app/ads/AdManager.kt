package com.fincalc.app.ads

import android.app.Activity
import android.content.Context
import com.fincalc.app.BuildConfig
import com.fincalc.app.core.constants.AppConstants
import com.fincalc.app.data.local.prefs.AppPreferences
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object AdManager {

    private var interstitialAd: InterstitialAd? = null
    private lateinit var prefs: AppPreferences

    fun initialize(context: Context, appPreferences: AppPreferences) {
        prefs = appPreferences
        loadInterstitial(context)
    }

    private fun loadInterstitial(context: Context) {
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    fun onCalculationCompleted(activity: Activity) {
        if (!::prefs.isInitialized || prefs.isPremium) return
        val count = prefs.incrementCalcCount()
        if (count % AppConstants.AD_INTERVAL == 0) {
            showInterstitial(activity)
        }
    }

    private fun showInterstitial(activity: Activity) {
        val ad = interstitialAd ?: run {
            loadInterstitial(activity)
            return
        }
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                loadInterstitial(activity)
            }

            override fun onAdFailedToShowFullScreenContent(p0: com.google.android.gms.ads.AdError) {
                interstitialAd = null
                loadInterstitial(activity)
            }
        }
        ad.show(activity)
    }
}
