package com.fincalc.app.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fincalc.app.core.result.Resource
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<T> : ViewModel() {

    protected val _uiState = MutableStateFlow<Resource<T>>(Resource.Idle)
    val uiState: StateFlow<Resource<T>> = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = Resource.Error(throwable.message ?: "Something went wrong", throwable)
    }

    protected fun safeLaunch(
        showLoading: Boolean = true,
        block: suspend CoroutineScope.() -> Unit
    ) {
        if (showLoading) _uiState.value = Resource.Loading
        viewModelScope.launch(exceptionHandler, block = block)
    }

    protected fun validatePositive(vararg values: Double): Boolean {
        return values.all { it > 0.0 }
    }
}
