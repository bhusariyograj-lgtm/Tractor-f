package com.example.data.local

import com.example.data.model.DieselLogEntity
import com.example.data.model.WorkLogEntity
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class OfflineBackupPayload(
    val workLogs: List<WorkLogEntity>,
    val dieselLogs: List<DieselLogEntity>
)

object OfflineBackupManager {

    private const val BACKUP_VERSION = 2

    fun exportToJson(
        workLogs: List<WorkLogEntity>,
        dieselLogs: List<DieselLogEntity>
    ): String {
        val root = JSONObject()
        root.put("app", "TractorNond")
        root.put("version", BACKUP_VERSION)
        root.put("exportedAtMillis", System.currentTimeMillis())

        val workArray = JSONArray()
        workLogs.forEach { log ->
            val obj = JSONObject().apply {
                put("farmerName", log.farmerName)
                put("location", log.location)
                put("implementType", log.implementType)
                put("quantity", log.quantity)
                put("unit", log.unit)
                put("driverName", log.driverName)
                put("dieselLitres", log.dieselLitres)
                put("dieselCost", log.dieselCost)
                put("ratePerUnit", log.ratePerUnit)
                put("totalAmount", log.totalAmount)
                put("isPaid", log.isPaid)
                put("notes", log.notes)
                put("dateMillis", log.dateMillis)
                put("createdAtMillis", log.createdAtMillis)
                if (log.startTimeMinutes != null) put("startTimeMinutes", log.startTimeMinutes)
                if (log.endTimeMinutes != null) put("endTimeMinutes", log.endTimeMinutes)
                put("breakMinutes", log.breakMinutes)
            }
            workArray.put(obj)
        }
        root.put("workLogs", workArray)

        val dieselArray = JSONArray()
        dieselLogs.forEach { log ->
            val obj = JSONObject().apply {
                put("litres", log.litres)
                put("cost", log.cost)
                put("driverName", log.driverName)
                put("pumpOrPlace", log.pumpOrPlace)
                put("notes", log.notes)
                put("dateMillis", log.dateMillis)
                put("createdAtMillis", log.createdAtMillis)
            }
            dieselArray.put(obj)
        }
        root.put("dieselLogs", dieselArray)

        return root.toString(2)
    }

    fun parseFromJson(jsonText: String): OfflineBackupPayload {
        val root = JSONObject(jsonText)
        val workLogs = mutableListOf<WorkLogEntity>()
        val dieselLogs = mutableListOf<DieselLogEntity>()

        val workArray = root.optJSONArray("workLogs") ?: JSONArray()
        for (i in 0 until workArray.length()) {
            val obj = workArray.getJSONObject(i)
            val farmerName = obj.optString("farmerName").trim()
            val location = obj.optString("location").trim()
            val driverName = obj.optString("driverName").trim()
            val quantity = obj.optDouble("quantity", 0.0)
            if (farmerName.isEmpty() || quantity <= 0.0) continue

            workLogs.add(
                WorkLogEntity(
                    id = 0L,
                    farmerName = farmerName,
                    location = location,
                    implementType = obj.optString("implementType", "OTHER"),
                    quantity = quantity,
                    unit = obj.optString("unit", "HOURS"),
                    driverName = driverName,
                    dieselLitres = obj.optDouble("dieselLitres", 0.0).coerceAtLeast(0.0),
                    dieselCost = obj.optDouble("dieselCost", 0.0).coerceAtLeast(0.0),
                    ratePerUnit = obj.optDouble("ratePerUnit", 0.0).coerceAtLeast(0.0),
                    totalAmount = obj.optDouble("totalAmount", 0.0).coerceAtLeast(0.0),
                    isPaid = obj.optBoolean("isPaid", false),
                    notes = obj.optString("notes", ""),
                    dateMillis = obj.optLong("dateMillis", System.currentTimeMillis()),
                    createdAtMillis = obj.optLong("createdAtMillis", System.currentTimeMillis()),
                    startTimeMinutes = if (obj.has("startTimeMinutes") && !obj.isNull("startTimeMinutes")) {
                        obj.optInt("startTimeMinutes")
                    } else null,
                    endTimeMinutes = if (obj.has("endTimeMinutes") && !obj.isNull("endTimeMinutes")) {
                        obj.optInt("endTimeMinutes")
                    } else null,
                    breakMinutes = obj.optInt("breakMinutes", 0).coerceAtLeast(0)
                )
            )
        }

        val dieselArray = root.optJSONArray("dieselLogs") ?: JSONArray()
        for (i in 0 until dieselArray.length()) {
            val obj = dieselArray.getJSONObject(i)
            val litres = obj.optDouble("litres", 0.0)
            if (litres <= 0.0) continue
            dieselLogs.add(
                DieselLogEntity(
                    id = 0L,
                    litres = litres,
                    cost = obj.optDouble("cost", 0.0).coerceAtLeast(0.0),
                    driverName = obj.optString("driverName", "").trim(),
                    pumpOrPlace = obj.optString("pumpOrPlace", "").trim(),
                    notes = obj.optString("notes", ""),
                    dateMillis = obj.optLong("dateMillis", System.currentTimeMillis()),
                    createdAtMillis = obj.optLong("createdAtMillis", System.currentTimeMillis())
                )
            )
        }

        return OfflineBackupPayload(workLogs = workLogs, dieselLogs = dieselLogs)
    }

    fun exportToCsv(
        workLogs: List<WorkLogEntity>,
        dieselLogs: List<DieselLogEntity>
    ): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        return buildString {
            // UTF-8 BOM for clean Marathi / Devanagari support in Excel
            append('\uFEFF')
            appendLine("Type,Date,Farmer,Location,Implement,Quantity,Unit,StartTime,EndTime,BreakMin,Driver,DieselLitres,DieselCost,Rate,TotalAmount,PaidStatus,Notes")
            workLogs.forEach { log ->
                val date = dateFormat.format(Date(log.dateMillis))
                val startStr = log.startTimeMinutes?.let { format24h(it) } ?: ""
                val endStr = log.endTimeMinutes?.let { format24h(it) } ?: ""
                val paidStr = if (log.isPaid) "Paid" else "Pending"
                appendLine(
                    listOf(
                        "WORK",
                        date,
                        csvEscape(log.farmerName),
                        csvEscape(log.location),
                        log.implementType,
                        log.quantity.toString(),
                        log.unit,
                        startStr,
                        endStr,
                        log.breakMinutes.toString(),
                        csvEscape(log.driverName),
                        log.dieselLitres.toString(),
                        log.dieselCost.toString(),
                        log.ratePerUnit.toString(),
                        log.totalAmount.toString(),
                        paidStr,
                        csvEscape(log.notes)
                    ).joinToString(",")
                )
            }
            dieselLogs.forEach { log ->
                val date = dateFormat.format(Date(log.dateMillis))
                appendLine(
                    listOf(
                        "DIESEL",
                        date,
                        "",
                        csvEscape(log.pumpOrPlace),
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        csvEscape(log.driverName),
                        log.litres.toString(),
                        log.cost.toString(),
                        "",
                        "",
                        "",
                        csvEscape(log.notes)
                    ).joinToString(",")
                )
            }
        }
    }

    private fun format24h(minutes: Int): String {
        val safe = ((minutes % (24 * 60)) + (24 * 60)) % (24 * 60)
        return String.format(Locale.ENGLISH, "%02d:%02d", safe / 60, safe % 60)
    }

    private fun csvEscape(value: String): String {
        val clean = value.replace("\r", " ").replace("\n", " ")
        return if (clean.contains(",") || clean.contains("\"")) {
            "\"${clean.replace("\"", "\"\"")}\""
        } else {
            clean
        }
    }
}
