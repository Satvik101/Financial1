package com.fincalc.app.core.result

/**
 * Generic wrapper for UI state coming from ViewModels.
 *
 * Usage in ViewModel:
 *   _state.value = Resource.Loading
 *   _state.value = Resource.Success(data)
 *   _state.value = Resource.Error("msg")
 *
 * Usage in Fragment (collecting StateFlow):
 *   viewModel.state.collectFlow(viewLifecycleOwner) { resource ->
 *       when (resource) {
 *           is Resource.Idle    -> { /* initial — hide everything */ }
 *           is Resource.Loading -> { /* show shimmer / progress */ }
 *           is Resource.Success -> { /* bind data to views */ }
 *           is Resource.Error   -> { /* show snackbar / inline error */ }
 *       }
 *   }
 */
sealed class Resource<out T> {

    /** No operation has been requested yet (default/initial state). */
    data object Idle : Resource<Nothing>()

    /** Computation is in progress. */
    data object Loading : Resource<Nothing>()

    /** Computation succeeded. */
    data class Success<T>(val data: T) : Resource<T>()

    /** Computation failed. */
    data class Error(
        val message: String,
        val throwable: Throwable? = null
    ) : Resource<Nothing>()

    // ── Convenience helpers ──────────────────────────────────

    val isLoading: Boolean get() = this is Loading

    val isSuccess: Boolean get() = this is Success

    val isError: Boolean get() = this is Error

    /** Returns the data if [Success], or null otherwise. */
    fun dataOrNull(): T? = (this as? Success)?.data

    /** Maps the success data while preserving other states. */
    inline fun <R> map(transform: (T) -> R): Resource<R> = when (this) {
        is Idle    -> Idle
        is Loading -> Loading
        is Success -> Success(transform(data))
        is Error   -> this
    }
}
