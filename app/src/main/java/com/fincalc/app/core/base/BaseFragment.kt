package com.fincalc.app.core.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

abstract class BaseFragment(@LayoutRes layoutRes: Int) : Fragment(layoutRes) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi(view)
        observeData()
    }

    open fun setupUi(view: View) = Unit
    open fun observeData() = Unit
}
