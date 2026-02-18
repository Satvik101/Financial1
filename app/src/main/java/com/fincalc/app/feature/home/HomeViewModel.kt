package com.fincalc.app.feature.home

import androidx.lifecycle.ViewModel
import com.fincalc.app.domain.model.CalculatorType
import com.fincalc.app.feature.home.adapter.CalculatorCardItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {

    private val allCards = CalculatorType.entries.map {
        CalculatorCardItem(
            type = it,
            name = it.title,
            description = it.description,
            accentColorHex = it.accentColorHex
        )
    }

    private val _cards = MutableStateFlow(allCards)
    val cards: StateFlow<List<CalculatorCardItem>> = _cards.asStateFlow()

    fun search(query: String) {
        val q = query.trim().lowercase()
        _cards.value = if (q.isBlank()) {
            allCards
        } else {
            allCards.filter { it.name.lowercase().contains(q) }
        }
    }
}
