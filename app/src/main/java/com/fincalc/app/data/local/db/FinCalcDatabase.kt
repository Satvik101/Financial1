package com.fincalc.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fincalc.app.data.local.db.dao.GoalDao
import com.fincalc.app.data.local.db.dao.HistoryDao
import com.fincalc.app.data.local.db.entity.GoalEntity
import com.fincalc.app.data.local.db.entity.HistoryEntity

@Database(
    entities = [HistoryEntity::class, GoalEntity::class],
    version = 1,
    exportSchema = false
)
abstract class FinCalcDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun goalDao(): GoalDao
}
