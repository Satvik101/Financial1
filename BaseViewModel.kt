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

/**
 * Base ViewModel that every calculator / feature ViewModel extends.
 *
 * Provides:
 *  - A [Resource]-backed [uiState] StateFlow for the primary screen data.
 *  - A [safeLaunch] helper that routes uncaught exceptions into [uiState] as [Resource.Error].
 *  - An overridable [onError] hook for subclasses that need custom error handling.
 *
 * Type parameter [T] is the result model for the screen
 * (e.g., SipResult, EmiResult, CompoundInterestResult).
 */
abstract class BaseViewModel<T> : ViewModel() {

    // ── Primary UI state ────────────────────────────────────

    protected val _uiState = MutableStateFlow<Resource<T>>(Resource.Idle)

    /** Observe this from the Fragment to react to Idle / Loading / Success / Error. */
    val uiState: StateFlow<Resource<T>> = _uiState.asStateFlow()

    // ── Secondary one-shot events (snackbar, navigation, etc.) ──

    protected val _errorEvent = MutableStateFlow<String?>(null)

    /** Collect in Fragment for transient error messages (Snackbar). */
    val errorEvent: StateFlow<String?> = _errorEvent.asStateFlow()

    /** Call from Fragment after the Snackbar has been shown. */
    fun clearError() {
        _errorEvent.value = null
    }

    // ── Coroutine helpers ───────────────────────────────────

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = Resource.Error(
            message = throwable.localizedMessage ?: "An unexpected error occurred",
            throwable = throwable
        )
        onError(throwable)
    }

    /**
     * Launch a coroutine within [viewModelScope] that automatically:
     *  1. Sets [uiState] to [Resource.Loading] if [showLoading] is true.
     *  2. Catches any uncaught exception and pushes [Resource.Error].
     */
    protected fun safeLaunch(
        showLoading: Boolean = true,
        block: suspend CoroutineScope.() -> Unit
    ) {
        if (showLoading) {
            _uiState.value = Resource.Loading
        }
        viewModelScope.launch(exceptionHandler, block = block)
    }

    /**
     * Override in subclasses to react to errors beyond the default
     * [Resource.Error] emission (e.g., analytics logging).
     */
    protected open fun onError(throwable: Throwable) = Unit

    // ── Input-validation helper ─────────────────────────────

    /**
     * Validates that every value in [fields] is positive.
     * Returns `true` if valid, or sets an [Resource.Error] with [errorMessage] and returns `false`.
     */
    protected fun validatePositive(
        vararg fields: Double,
        errorMessage: String = "All input values must be greater than zero"
    ): Boolean {
        return if (fields.all { it > 0.0 }) {
            true
        } else {
            _uiState.value = Resource.Error(errorMessage)
            false
        }
    }

    /**
     * Resets the UI state back to [Resource.Idle].
     * Call this when the user clears the form.
     */
    fun resetState() {
        _uiState.value = Resource.Idle
    }
}
