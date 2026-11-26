package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppLanguage
import com.example.data.model.DateFilter
import com.example.data.model.DieselLogEntity
import com.example.data.model.ImplementType
import com.example.data.model.WorkLogEntity
import com.example.data.model.WorkUnit
import com.example.data.repository.TractorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class CombinedDieselEntry(
    val uniqueKey: String,
    val sourceWorkLogId: Long?,
    val sourceDieselLogId: Long?,
    val litres: Double,
    val cost: Double,
    val driverName: String,
    val placeOrFarm: String,
    val notes: String,
    val dateMillis: Long,
    val isFromWorkLog: Boolean
)

data class ImplementSummary(
    val implement: ImplementType,
    val jobCount: Int,
    val totalHours: Double,
    val totalTrips: Double,
    val totalAcres: Double,
    val totalGuntha: Double,
    val totalAmount: Double
)

data class DriverSummary(
    val driverName: String,
    val jobCount: Int,
    val totalHours: Double,
    val totalTrips: Double,
    val totalDieselLitres: Double
)

data class FarmerSummary(
    val farmerName: String,
    val locations: List<String>,
    val jobCount: Int,
    val totalHours: Double,
    val totalTrips: Double,
    val totalAcres: Double,
    val totalAmount: Double,
    val pendingAmount: Double,
    val logs: List<WorkLogEntity>
)

data class TractorUiState(
    val language: AppLanguage = AppLanguage.MARATHI,
    val allWorkLogs: List<WorkLogEntity> = emptyList(),
    val filteredWorkLogs: List<WorkLogEntity> = emptyList(),
    val standaloneDieselLogs: List<DieselLogEntity> = emptyList(),
    val combinedDieselEntries: List<CombinedDieselEntry> = emptyList(),
    val searchQuery: String = "",
    val selectedImplement: ImplementType? = null,
    val selectedDateFilter: DateFilter = DateFilter.ALL,
    val totalHours: Double = 0.0,
    val totalTrips: Double = 0.0,
    val totalAcres: Double = 0.0,
    val totalDieselLitres: Double = 0.0,
    val totalDieselCost: Double = 0.0,
    val totalBilledAmount: Double = 0.0,
    val totalPendingAmount: Double = 0.0,
    val totalPaidAmount: Double = 0.0,
    val implementSummaries: List<ImplementSummary> = emptyList(),
    val driverSummaries: List<DriverSummary> = emptyList(),
    val farmerSummaries: List<FarmerSummary> = emptyList(),
    val recentFarmers: List<String> = emptyList(),
    val recentLocations: List<String> = emptyList(),
    val recentDrivers: List<String> = emptyList()
)

class TractorViewModel(
    private val repository: TractorRepository
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedImplement = MutableStateFlow<ImplementType?>(null)
    private val selectedDateFilter = MutableStateFlow(DateFilter.ALL)

    private val filtersFlow = combine(
        searchQuery,
        selectedImplement,
        selectedDateFilter
    ) { query, implement, dateFilter ->
        Triple(query, implement, dateFilter)
    }

    val uiState: StateFlow<TractorUiState> = combine(
        repository.workLogs,
        repository.dieselLogs,
        repository.language,
        filtersFlow
    ) { workLogs, dieselLogs, language, (query, implementFilter, dateFilter) ->
        val dateThreshold = computeDateThresholdMillis(dateFilter)

        val dateFilteredWork = if (dateThreshold != null) {
            workLogs.filter { it.dateMillis >= dateThreshold }
        } else {
            workLogs
        }

        val dateFilteredDiesel = if (dateThreshold != null) {
            dieselLogs.filter { it.dateMillis >= dateThreshold }
        } else {
            dieselLogs
        }

        val filteredWork = dateFilteredWork.filter { log ->
            val matchesImpl = implementFilter == null || log.implement == implementFilter
            val q = query.trim()
            val matchesQuery = q.isEmpty() ||
                log.farmerName.contains(q, ignoreCase = true) ||
                log.location.contains(q, ignoreCase = true) ||
                log.driverName.contains(q, ignoreCase = true) ||
                log.notes.contains(q, ignoreCase = true)
            matchesImpl && matchesQuery
        }

        val totalHours = filteredWork.filter { it.workUnit == WorkUnit.HOURS }.sumOf { it.quantity }
        val totalTrips = filteredWork.filter { it.workUnit == WorkUnit.TRIPS }.sumOf { it.quantity }
        val totalAcres = filteredWork.filter { it.workUnit == WorkUnit.ACRES }.sumOf { it.quantity } +
            (filteredWork.filter { it.workUnit == WorkUnit.GUNTHA }.sumOf { it.quantity } / 40.0)

        val combinedDiesel = buildList {
            dieselLogs.forEach { d ->
                add(
                    CombinedDieselEntry(
                        uniqueKey = "diesel_${d.id}",
                        sourceWorkLogId = null,
                        sourceDieselLogId = d.id,
                        litres = d.litres,
                        cost = d.cost,
                        driverName = d.driverName,
                        placeOrFarm = d.pumpOrPlace,
                        notes = d.notes,
                        dateMillis = d.dateMillis,
                        isFromWorkLog = false
                    )
                )
            }
            workLogs.filter { it.dieselLitres > 0.0 }.forEach { w ->
                add(
                    CombinedDieselEntry(
                        uniqueKey = "work_${w.id}",
                        sourceWorkLogId = w.id,
                        sourceDieselLogId = null,
                        litres = w.dieselLitres,
                        cost = w.dieselCost,
                        driverName = w.driverName,
                        placeOrFarm = "${w.farmerName} (${w.location})",
                        notes = w.notes,
                        dateMillis = w.dateMillis,
                        isFromWorkLog = true
                    )
                )
            }
        }.sortedByDescending { it.dateMillis }

        val workDieselLitres = dateFilteredWork.sumOf { it.dieselLitres }
        val standaloneDieselLitres = dateFilteredDiesel.sumOf { it.litres }
        val totalDieselLitres = workDieselLitres + standaloneDieselLitres

        val totalDieselCost = dateFilteredWork.sumOf { it.dieselCost } +
            dateFilteredDiesel.sumOf { it.cost }

        val totalBilled = filteredWork.sumOf { it.totalAmount }
        val totalPaid = filteredWork.filter { it.isPaid }.sumOf { it.totalAmount }
        val totalPending = filteredWork.filter { !it.isPaid }.sumOf { it.totalAmount }

        val implementSummaries = ImplementType.entries.mapNotNull { impl ->
            val group = dateFilteredWork.filter { it.implement == impl }
            if (group.isEmpty()) null else ImplementSummary(
                implement = impl,
                jobCount = group.size,
                totalHours = group.filter { it.workUnit == WorkUnit.HOURS }.sumOf { it.quantity },
                totalTrips = group.filter { it.workUnit == WorkUnit.TRIPS }.sumOf { it.quantity },
                totalAcres = group.filter { it.workUnit == WorkUnit.ACRES }.sumOf { it.quantity },
                totalGuntha = group.filter { it.workUnit == WorkUnit.GUNTHA }.sumOf { it.quantity },
                totalAmount = group.sumOf { it.totalAmount }
            )
        }.sortedByDescending { it.jobCount }

        val driverNamesAll = (dateFilteredWork.map { it.driverName.trim() } +
            dateFilteredDiesel.map { it.driverName.trim() })
            .filter { it.isNotEmpty() }
            .distinctBy { it.lowercase() }

        val driverSummaries = driverNamesAll.map { driver ->
            val jobs = dateFilteredWork.filter { it.driverName.trim().equals(driver, ignoreCase = true) }
            val refills = dateFilteredDiesel.filter { it.driverName.trim().equals(driver, ignoreCase = true) }
            DriverSummary(
                driverName = driver,
                jobCount = jobs.size,
                totalHours = jobs.filter { it.workUnit == WorkUnit.HOURS }.sumOf { it.quantity },
                totalTrips = jobs.filter { it.workUnit == WorkUnit.TRIPS }.sumOf { it.quantity },
                totalDieselLitres = jobs.sumOf { it.dieselLitres } + refills.sumOf { it.litres }
            )
        }.sortedByDescending { it.jobCount }

        val farmerSummaries = dateFilteredWork
            .groupBy { it.farmerName.trim().lowercase() }
            .values
            .map { logs ->
                val displayName = logs.first().farmerName.trim()
                FarmerSummary(
                    farmerName = displayName,
                    locations = logs.map { it.location.trim() }.filter { it.isNotEmpty() }.distinct(),
                    jobCount = logs.size,
                    totalHours = logs.filter { it.workUnit == WorkUnit.HOURS }.sumOf { it.quantity },
                    totalTrips = logs.filter { it.workUnit == WorkUnit.TRIPS }.sumOf { it.quantity },
                    totalAcres = logs.filter { it.workUnit == WorkUnit.ACRES }.sumOf { it.quantity } +
                        (logs.filter { it.workUnit == WorkUnit.GUNTHA }.sumOf { it.quantity } / 40.0),
                    totalAmount = logs.sumOf { it.totalAmount },
                    pendingAmount = logs.filter { !it.isPaid }.sumOf { it.totalAmount },
                    logs = logs
                )
            }
            .sortedWith(compareByDescending<FarmerSummary> { it.pendingAmount }.thenByDescending { it.jobCount })

        val recentFarmers = workLogs.map { it.farmerName.trim() }
            .filter { it.isNotEmpty() }.distinctBy { it.lowercase() }.take(8)
        val recentLocations = workLogs.map { it.location.trim() }
            .filter { it.isNotEmpty() }.distinctBy { it.lowercase() }.take(8)
        val recentDrivers = (workLogs.map { it.driverName.trim() } + dieselLogs.map { it.driverName.trim() })
            .filter { it.isNotEmpty() }.distinctBy { it.lowercase() }.take(8)

        TractorUiState(
            language = language,
            allWorkLogs = workLogs,
            filteredWorkLogs = filteredWork,
            standaloneDieselLogs = dieselLogs,
            combinedDieselEntries = combinedDiesel,
            searchQuery = query,
            selectedImplement = implementFilter,
            selectedDateFilter = dateFilter,
            totalHours = totalHours,
            totalTrips = totalTrips,
            totalAcres = totalAcres,
            totalDieselLitres = totalDieselLitres,
            totalDieselCost = totalDieselCost,
            totalBilledAmount = totalBilled,
            totalPendingAmount = totalPending,
            totalPaidAmount = totalPaid,
            implementSummaries = implementSummaries,
            driverSummaries = driverSummaries,
            farmerSummaries = farmerSummaries,
            recentFarmers = recentFarmers,
            recentLocations = recentLocations,
            recentDrivers = recentDrivers
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TractorUiState()
    )

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setImplementFilter(implement: ImplementType?) {
        selectedImplement.value = implement
    }

    fun setDateFilter(filter: DateFilter) {
        selectedDateFilter.value = filter
    }

    fun toggleLanguage() {
        val next = if (uiState.value.language == AppLanguage.MARATHI) {
            AppLanguage.ENGLISH
        } else {
            AppLanguage.MARATHI
        }
        viewModelScope.launch {
            repository.setLanguage(next)
        }
    }

    fun saveWorkLog(log: WorkLogEntity) {
        viewModelScope.launch {
            repository.saveWorkLog(log)
        }
    }

    fun deleteWorkLog(id: Long) {
        viewModelScope.launch {
            repository.deleteWorkLog(id)
        }
    }

    fun toggleWorkLogPaid(log: WorkLogEntity) {
        viewModelScope.launch {
            repository.setWorkLogPaid(log.id, !log.isPaid)
        }
    }

    fun markFarmerAllPaid(farmerName: String) {
        viewModelScope.launch {
            repository.markAllFarmerLogsPaid(farmerName)
        }
    }

    fun saveDieselLog(log: DieselLogEntity) {
        viewModelScope.launch {
            repository.saveDieselLog(log)
        }
    }

    fun deleteDieselLog(id: Long) {
        viewModelScope.launch {
            repository.deleteDieselLog(id)
        }
    }

    fun buildBackupJson(): String {
        val state = uiState.value
        return com.example.data.local.OfflineBackupManager.exportToJson(
            workLogs = state.allWorkLogs,
            dieselLogs = state.standaloneDieselLogs
        )
    }

    fun buildCsvReport(): String {
        val state = uiState.value
        return com.example.data.local.OfflineBackupManager.exportToCsv(
            workLogs = state.allWorkLogs,
            dieselLogs = state.standaloneDieselLogs
        )
    }

    fun importBackupJson(jsonText: String, onResult: (Boolean, Int) -> Unit) {
        viewModelScope.launch {
            val result = runCatching {
                val payload = com.example.data.local.OfflineBackupManager.parseFromJson(jsonText)
                val (addedWork, addedDiesel) = repository.importBackup(
                    workLogs = payload.workLogs,
                    dieselLogs = payload.dieselLogs
                )
                addedWork + addedDiesel
            }
            result.onSuccess { count -> onResult(true, count) }
                .onFailure { onResult(false, 0) }
        }
    }

    private fun computeDateThresholdMillis(filter: DateFilter): Long? {
        if (filter == DateFilter.ALL) return null
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        when (filter) {
            DateFilter.TODAY -> Unit
            DateFilter.WEEK -> cal.add(Calendar.DAY_OF_YEAR, -6)
            DateFilter.MONTH -> cal.set(Calendar.DAY_OF_MONTH, 1)
            DateFilter.ALL -> return null
        }
        return cal.timeInMillis
    }

    class Factory(private val repository: TractorRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TractorViewModel(repository) as T
        }
    }
}
