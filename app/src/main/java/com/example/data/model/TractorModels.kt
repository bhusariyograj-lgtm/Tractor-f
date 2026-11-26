package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ImplementType(val key: String) {
    ROTAVATOR("ROTAVATOR"),
    CULTIVATOR("CULTIVATOR"),
    TROLLEY("TROLLEY"),
    PLOUGH("PLOUGH"),
    OTHER("OTHER");

    companion object {
        fun fromKey(key: String): ImplementType =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: OTHER
    }
}

enum class WorkUnit(val key: String) {
    HOURS("HOURS"),
    TRIPS("TRIPS"),
    ACRES("ACRES"),
    GUNTHA("GUNTHA");

    companion object {
        fun fromKey(key: String): WorkUnit =
            entries.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: HOURS
    }
}

enum class DateFilter(val key: String) {
    ALL("ALL"),
    TODAY("TODAY"),
    WEEK("WEEK"),
    MONTH("MONTH")
}

enum class AppLanguage(val code: String) {
    MARATHI("mr"),
    ENGLISH("en");

    companion object {
        fun fromCode(code: String?): AppLanguage =
            if (code == ENGLISH.code) ENGLISH else MARATHI
    }
}

@Entity(tableName = "work_logs")
data class WorkLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val farmerName: String,
    val location: String,
    val implementType: String,
    val quantity: Double,
    val unit: String,
    val driverName: String,
    val dieselLitres: Double = 0.0,
    val dieselCost: Double = 0.0,
    val ratePerUnit: Double = 0.0,
    val totalAmount: Double = 0.0,
    val isPaid: Boolean = false,
    val notes: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val createdAtMillis: Long = System.currentTimeMillis(),
    val startTimeMinutes: Int? = null,
    val endTimeMinutes: Int? = null,
    val breakMinutes: Int = 0
) {
    val implement: ImplementType get() = ImplementType.fromKey(implementType)
    val workUnit: WorkUnit get() = WorkUnit.fromKey(unit)

    val calculatedDurationMinutes: Int?
        get() = calculateElapsedMinutes(startTimeMinutes, endTimeMinutes, breakMinutes)
}

fun calculateElapsedMinutes(
    startTimeMinutes: Int?,
    endTimeMinutes: Int?,
    breakMinutes: Int = 0
): Int? {
    if (startTimeMinutes == null || endTimeMinutes == null) return null
    val rawDiff = if (endTimeMinutes > startTimeMinutes) {
        endTimeMinutes - startTimeMinutes
    } else {
        (24 * 60 - startTimeMinutes) + endTimeMinutes
    }
    return (rawDiff - breakMinutes.coerceAtLeast(0)).coerceAtLeast(0)
}

@Entity(tableName = "diesel_logs")
data class DieselLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val litres: Double,
    val cost: Double = 0.0,
    val driverName: String,
    val pumpOrPlace: String = "",
    val notes: String = "",
    val dateMillis: Long = System.currentTimeMillis(),
    val createdAtMillis: Long = System.currentTimeMillis()
)
