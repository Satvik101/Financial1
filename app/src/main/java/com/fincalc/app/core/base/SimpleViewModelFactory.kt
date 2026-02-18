package com.fincalc.app.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SimpleViewModelFactory<T : ViewModel>(
    private val creator: () -> T
) : ViewModelProvider.Factory {
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
        @Suppress("UNCHECKED_CAST")
        return creator.invoke() as VM
    }
}
