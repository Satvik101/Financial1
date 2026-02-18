package com.fincalc.app.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fincalc.app.data.local.db.entity.HistoryEntity
import com.fincalc.app.data.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: HistoryRepository
) : ViewModel() {

    private val selectedFilter = MutableStateFlow("ALL")

    val history: StateFlow<List<HistoryEntity>> = combine(
        repository.getAllHistory(),
        selectedFilter
    ) { list, filter ->
        when (filter) {
            "SIP" -> list.filter {
                it.calculatorType.contains("SIP") || it.calculatorType.contains("SAVINGS_GOAL")
            }
            "EMI" -> list.filter {
                it.calculatorType.contains("EMI") || it.calculatorType.contains("LOAN_COMPARISON")
            }
            "INTEREST" -> list.filter {
                it.calculatorType.contains("COMPOUND") || it.calculatorType.contains("FD") ||
                    it.calculatorType.contains("PPF") || it.calculatorType.contains("CAGR") ||
                    it.calculatorType.contains("LUMPSUM")
            }
            "TAX" -> list.filter { it.calculatorType.contains("TAX") }
            "OTHER" -> list.filter {
                !it.calculatorType.contains("SIP") &&
                    !it.calculatorType.contains("SAVINGS_GOAL") &&
                    !it.calculatorType.contains("EMI") &&
                    !it.calculatorType.contains("LOAN_COMPARISON") &&
                    !it.calculatorType.contains("COMPOUND") &&
                    !it.calculatorType.contains("FD") &&
                    !it.calculatorType.contains("PPF") &&
                    !it.calculatorType.contains("CAGR") &&
                    !it.calculatorType.contains("LUMPSUM") &&
                    !it.calculatorType.contains("TAX")
            }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: String) {
        selectedFilter.value = filter
    }

    fun delete(id: Long) {
        viewModelScope.launch { repository.deleteById(id) }
    }
}
