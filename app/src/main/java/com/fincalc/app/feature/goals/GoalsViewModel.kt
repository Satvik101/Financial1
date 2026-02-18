package com.fincalc.app.feature.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fincalc.app.data.local.db.entity.GoalEntity
import com.fincalc.app.data.repository.GoalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class GoalsViewModel(
    private val repository: GoalRepository
) : ViewModel() {

    val goals: StateFlow<List<GoalEntity>> = repository.getAllGoals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addGoal(name: String, target: Double, deadlineMillis: Long, saved: Double) {
        viewModelScope.launch {
            repository.insert(
                GoalEntity(
                    name = name,
                    targetAmount = target,
                    deadlineMillis = deadlineMillis,
                    currentSavedAmount = saved
                )
            )
        }
    }

    fun addProgress(goal: GoalEntity, addAmount: Double) {
        viewModelScope.launch {
            repository.update(goal.copy(currentSavedAmount = goal.currentSavedAmount + addAmount))
        }
    }
}
