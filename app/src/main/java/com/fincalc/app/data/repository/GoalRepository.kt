package com.fincalc.app.data.repository

import com.fincalc.app.data.local.db.dao.GoalDao
import com.fincalc.app.data.local.db.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

class GoalRepository(private val goalDao: GoalDao) {

    fun getAllGoals(): Flow<List<GoalEntity>> = goalDao.getAllGoals()

    suspend fun insert(item: GoalEntity): Long = goalDao.insert(item)

    suspend fun update(item: GoalEntity) = goalDao.update(item)

    suspend fun delete(item: GoalEntity) = goalDao.delete(item)

    suspend fun getById(id: Long): GoalEntity? = goalDao.getById(id)
}
