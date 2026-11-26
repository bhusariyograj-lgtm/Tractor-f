package com.example.data.repository

import com.example.data.local.PreferencesRepository
import com.example.data.local.TractorDao
import com.example.data.model.AppLanguage
import com.example.data.model.DieselLogEntity
import com.example.data.model.WorkLogEntity
import kotlinx.coroutines.flow.Flow

class TractorRepository(
    private val dao: TractorDao,
    private val preferences: PreferencesRepository
) {
    val workLogs: Flow<List<WorkLogEntity>> = dao.observeWorkLogs()
    val dieselLogs: Flow<List<DieselLogEntity>> = dao.observeDieselLogs()
    val language: Flow<AppLanguage> = preferences.languageFlow

    suspend fun saveWorkLog(log: WorkLogEntity) {
        if (log.id == 0L) {
            dao.insertWorkLog(log)
        } else {
            dao.updateWorkLog(log)
        }
    }

    suspend fun deleteWorkLog(id: Long) {
        dao.deleteWorkLogById(id)
    }

    suspend fun setWorkLogPaid(id: Long, isPaid: Boolean) {
        dao.updateWorkLogPaidStatus(id, isPaid)
    }

    suspend fun markAllFarmerLogsPaid(farmerName: String) {
        dao.markAllFarmerLogsPaid(farmerName)
    }

    suspend fun saveDieselLog(log: DieselLogEntity) {
        if (log.id == 0L) {
            dao.insertDieselLog(log)
        } else {
            dao.updateDieselLog(log)
        }
    }

    suspend fun deleteDieselLog(id: Long) {
        dao.deleteDieselLogById(id)
    }

    suspend fun setLanguage(language: AppLanguage) {
        preferences.setLanguage(language)
    }

    suspend fun importBackup(
        workLogs: List<WorkLogEntity>,
        dieselLogs: List<DieselLogEntity>
    ): Pair<Int, Int> {
        val existingWorkKeys = dao.getAllWorkLogsOnce()
            .map { "${it.createdAtMillis}_${it.dateMillis}_${it.farmerName.trim().lowercase()}_${it.quantity}" }
            .toSet()
        val existingDieselKeys = dao.getAllDieselLogsOnce()
            .map { "${it.createdAtMillis}_${it.dateMillis}_${it.litres}" }
            .toSet()

        val newWork = workLogs.filter {
            val key = "${it.createdAtMillis}_${it.dateMillis}_${it.farmerName.trim().lowercase()}_${it.quantity}"
            key !in existingWorkKeys
        }
        val newDiesel = dieselLogs.filter {
            val key = "${it.createdAtMillis}_${it.dateMillis}_${it.litres}"
            key !in existingDieselKeys
        }

        if (newWork.isNotEmpty()) dao.insertWorkLogs(newWork)
        if (newDiesel.isNotEmpty()) dao.insertDieselLogs(newDiesel)
        return Pair(newWork.size, newDiesel.size)
    }
}
