package com.fincalc.app.feature.calculator.stepupsiplumpsum

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.fincalc.app.R
import com.fincalc.app.core.base.BaseFragment

class StepUpSipLumpsumFragment : BaseFragment(R.layout.fragment_step_up_sip_lumpsum) {
    override fun setupUi(view: View) {
        val prefill = arguments?.getString("prefillInputJson").orEmpty()
        val bundle = Bundle().apply {
            putString("calculatorType", "STEP_UP_SIP_LUMPSUM")
            putString("prefillInputJson", prefill)
        }
        findNavController().navigate(R.id.calculatorFragment, bundle, androidx.navigation.NavOptions.Builder().setPopUpTo(findNavController().currentDestination?.id ?: 0, true).build())
    }
}


