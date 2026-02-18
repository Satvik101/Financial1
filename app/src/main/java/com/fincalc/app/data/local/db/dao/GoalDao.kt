package com.fincalc.app.data.local.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fincalc.app.data.local.db.entity.GoalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Query("SELECT * FROM goals ORDER BY deadlineMillis ASC")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GoalEntity): Long

    @Update
    suspend fun update(item: GoalEntity)

    @Delete
    suspend fun delete(item: GoalEntity)

    @Query("SELECT * FROM goals WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): GoalEntity?
}
