package com.fincalc.app.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val calculatorType: String,
    val inputJson: String,
    val resultJson: String,
    val resultValueLabel: String,
    val createdAt: Long = System.currentTimeMillis()
)
