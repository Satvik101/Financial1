package com.fincalc.app.feature.calculator.sip

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fincalc.app.R
import com.fincalc.app.core.base.BaseFragment

class SipFragment : BaseFragment(R.layout.fragment_sip) {
    override fun setupUi(view: View) {
        val prefill = arguments?.getString("prefillInputJson").orEmpty()
        val bundle = Bundle().apply {
            putString("calculatorType", "SIP")
            putString("prefillInputJson", prefill)
        }
        findNavController().navigate(R.id.calculatorFragment, bundle, androidx.navigation.NavOptions.Builder().setPopUpTo(findNavController().currentDestination?.id ?: 0, true).build())
    }
}


