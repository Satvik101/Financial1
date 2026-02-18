package com.fincalc.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val deadlineMillis: Long,
    val currentSavedAmount: Double,
    val createdAt: Long = System.currentTimeMillis()
)
