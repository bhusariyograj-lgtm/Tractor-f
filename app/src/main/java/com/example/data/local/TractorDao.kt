package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DieselLogEntity
import com.example.data.model.WorkLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TractorDao {

    @Query("SELECT * FROM work_logs ORDER BY dateMillis DESC, createdAtMillis DESC")
    fun observeWorkLogs(): Flow<List<WorkLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkLog(log: WorkLogEntity): Long

    @Update
    suspend fun updateWorkLog(log: WorkLogEntity)

    @Query("DELETE FROM work_logs WHERE id = :id")
    suspend fun deleteWorkLogById(id: Long)

    @Query("UPDATE work_logs SET isPaid = :isPaid WHERE id = :id")
    suspend fun updateWorkLogPaidStatus(id: Long, isPaid: Boolean)

    @Query("UPDATE work_logs SET isPaid = 1 WHERE LOWER(TRIM(farmerName)) = LOWER(TRIM(:farmerName))")
    suspend fun markAllFarmerLogsPaid(farmerName: String)

    @Query("SELECT * FROM diesel_logs ORDER BY dateMillis DESC, createdAtMillis DESC")
    fun observeDieselLogs(): Flow<List<DieselLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDieselLog(log: DieselLogEntity): Long

    @Update
    suspend fun updateDieselLog(log: DieselLogEntity)

    @Query("DELETE FROM diesel_logs WHERE id = :id")
    suspend fun deleteDieselLogById(id: Long)

    @Query("SELECT * FROM work_logs")
    suspend fun getAllWorkLogsOnce(): List<WorkLogEntity>

    @Query("SELECT * FROM diesel_logs")
    suspend fun getAllDieselLogsOnce(): List<DieselLogEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkLogs(logs: List<WorkLogEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDieselLogs(logs: List<DieselLogEntity>)
}
