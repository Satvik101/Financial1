package com.fincalc.app.feature.splash

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fincalc.app.R
import com.fincalc.app.core.base.BaseFragment

class SplashFragment : BaseFragment(R.layout.fragment_splash) {
    override fun setupUi(view: View) {
        Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_splash_to_home)
        }, 800)
    }
}
