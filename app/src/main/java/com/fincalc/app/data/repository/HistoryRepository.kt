package com.fincalc.app.data.repository

import com.fincalc.app.data.local.db.dao.HistoryDao
import com.fincalc.app.data.local.db.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

class HistoryRepository(private val historyDao: HistoryDao) {

    fun getAllHistory(): Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    suspend fun insert(item: HistoryEntity) = historyDao.insert(item)

    suspend fun delete(item: HistoryEntity) = historyDao.delete(item)

    suspend fun deleteById(id: Long) = historyDao.deleteById(id)

    suspend fun clearAll() = historyDao.clearAll()
}
