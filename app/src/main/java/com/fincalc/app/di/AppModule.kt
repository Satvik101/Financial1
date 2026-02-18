package com.fincalc.app.di

import android.content.Context
import androidx.room.Room
import com.fincalc.app.core.constants.AppConstants
import com.fincalc.app.data.local.db.FinCalcDatabase
import com.fincalc.app.data.local.prefs.AppPreferences
import com.fincalc.app.data.repository.GoalRepository
import com.fincalc.app.data.repository.HistoryRepository

class AppModule(context: Context) {

    private val appContext = context.applicationContext

    val preferences: AppPreferences by lazy { AppPreferences(appContext) }

    val database: FinCalcDatabase by lazy {
        Room.databaseBuilder(appContext, FinCalcDatabase::class.java, AppConstants.DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    val historyRepository: HistoryRepository by lazy {
        HistoryRepository(database.historyDao())
    }

    val goalRepository: GoalRepository by lazy {
        GoalRepository(database.goalDao())
    }
}
