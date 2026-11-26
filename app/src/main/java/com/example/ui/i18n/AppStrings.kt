package com.example.ui.i18n

import com.example.data.model.AppLanguage
import com.example.data.model.DateFilter
import com.example.data.model.ImplementType
import com.example.data.model.WorkLogEntity
import com.example.data.model.WorkUnit
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UiStrings(
    val appTitle: String,
    val appSubtitle: String,
    val navLogs: String,
    val navDiesel: String,
    val navSummary: String,
    val addWorkLog: String,
    val editWorkLog: String,
    val addDieselLog: String,
    val editDieselLog: String,
    val searchPlaceholder: String,
    val allFilter: String,
    val todayFilter: String,
    val weekFilter: String,
    val monthFilter: String,
    val totalWorkLabel: String,
    val totalDieselLabel: String,
    val totalBilledLabel: String,
    val pendingLabel: String,
    val paidLabel: String,
    val markPaid: String,
    val markPending: String,
    val markAllPaid: String,
    val implementQuestion: String,
    val farmerNameLabel: String,
    val farmerNameHint: String,
    val locationLabel: String,
    val locationHint: String,
    val quantityLabel: String,
    val quantityHint: String,
    val unitLabel: String,
    val driverNameLabel: String,
    val driverNameHint: String,
    val dieselSectionTitle: String,
    val dieselLitresLabel: String,
    val dieselCostLabel: String,
    val billingSectionTitle: String,
    val ratePerUnitLabel: String,
    val totalAmountLabel: String,
    val paymentStatusLabel: String,
    val dateLabel: String,
    val notesLabel: String,
    val notesHint: String,
    val pumpOrPlaceLabel: String,
    val pumpOrPlaceHint: String,
    val saveButton: String,
    val updateButton: String,
    val deleteButton: String,
    val cancelButton: String,
    val deleteConfirmTitle: String,
    val deleteConfirmMessage: String,
    val shareButton: String,
    val emptyLogsTitle: String,
    val emptyLogsSubtitle: String,
    val emptyLogsAction: String,
    val emptyFilteredTitle: String,
    val emptyFilteredSubtitle: String,
    val emptyDieselTitle: String,
    val emptyDieselSubtitle: String,
    val emptyDieselAction: String,
    val emptySummaryTitle: String,
    val emptySummarySubtitle: String,
    val byImplementTab: String,
    val byDriverTab: String,
    val byFarmerTab: String,
    val jobsCountSuffix: String,
    val dieselFilledTag: String,
    val fromWorkLogBadge: String,
    val standaloneRefillBadge: String,
    val recentSuggestionsLabel: String,
    val savedSnackbar: String,
    val deletedSnackbar: String,
    val paidUpdatedSnackbar: String,
    val validationFarmerRequired: String,
    val validationLocationRequired: String,
    val validationQuantityRequired: String,
    val validationDriverRequired: String,
    val validationLitresRequired: String,
    val langToggleLabel: String,
    val litresShort: String,
    val rupeeSymbol: String,
    val driverPrefix: String,
    val shareReportTitle: String,
    val optionalTag: String,
    val selectDateTitle: String,
    val okButton: String,
    val timeCalculatorTitle: String,
    val startTimeLabel: String,
    val endTimeLabel: String,
    val selectTimePlaceholder: String,
    val nowButton: String,
    val clearTimeButton: String,
    val breakTimeLabel: String,
    val totalTimeCalculatedLabel: String,
    val overnightBadge: String,
    val timePeriodLabel: String,
    val offlineBadge: String,
    val offlineBackupTitle: String,
    val offlineBackupSubtitle: String,
    val exportBackupButton: String,
    val restoreBackupButton: String,
    val exportCsvButton: String,
    val backupSavedSnackbar: String,
    val csvSavedSnackbar: String,
    val backupRestoredSnackbar: String,
    val backupErrorSnackbar: String
)

fun stringsFor(language: AppLanguage): UiStrings = when (language) {
    AppLanguage.MARATHI -> MarathiStrings
    AppLanguage.ENGLISH -> EnglishStrings
}

val MarathiStrings = UiStrings(
    appTitle = "अद्विक अवजारे बँक",
    appSubtitle = "काम, अवजार, ड्रायव्हर व डिझेल हिशोब",
    navLogs = "नोंदी",
    navDiesel = "डिझेल",
    navSummary = "हिशोब",
    addWorkLog = "नवीन काम नोंद",
    editWorkLog = "नोंद बदला",
    addDieselLog = "डिझेल नोंद",
    editDieselLog = "डिझेल नोंद बदला",
    searchPlaceholder = "शेतकरी, गाव किंवा ड्रायव्हर शोधा...",
    allFilter = "सर्व",
    todayFilter = "आज",
    weekFilter = "या आठवड्यात",
    monthFilter = "या महिन्यात",
    totalWorkLabel = "एकूण काम",
    totalDieselLabel = "एकूण डिझेल",
    totalBilledLabel = "एकूण बिल",
    pendingLabel = "बाकी",
    paidLabel = "जमा",
    markPaid = "जमा करा",
    markPending = "बाकी करा",
    markAllPaid = "सर्व जमा करा",
    implementQuestion = "कोणत्या अवजारावर चालला?",
    farmerNameLabel = "कोणाच्या शेतात? (शेतकरी नाव)",
    farmerNameHint = "उदा. रामभाऊ पाटील",
    locationLabel = "कुठे चालला? (गाव / शिवार)",
    locationHint = "उदा. पिंपळगाव, मळा नंबर २",
    quantityLabel = "किती चालला?",
    quantityHint = "उदा. 2.5",
    unitLabel = "एकक",
    driverNameLabel = "कोण ड्रायव्हर होता?",
    driverNameHint = "उदा. संतोष काका",
    dieselSectionTitle = "डिझेल माहिती (ऐच्छिक)",
    dieselLitresLabel = "किती डिझेल टाकलं? (लिटर)",
    dieselCostLabel = "डिझेल खर्च (₹)",
    billingSectionTitle = "दर व बिल माहिती (ऐच्छिक)",
    ratePerUnitLabel = "दर प्रति एकक (₹)",
    totalAmountLabel = "एकूण रक्कम (₹)",
    paymentStatusLabel = "पैसे मिळाले का?",
    dateLabel = "तारीख",
    notesLabel = "इतर टीप",
    notesHint = "कामाबद्दल विशेष माहिती...",
    pumpOrPlaceLabel = "पंप / ठिकाण",
    pumpOrPlaceHint = "उदा. भारत पेट्रोलियम, फाटा",
    saveButton = "नोंद सेव्ह करा",
    updateButton = "बदल सेव्ह करा",
    deleteButton = "नोंद काढा",
    cancelButton = "रद्द करा",
    deleteConfirmTitle = "नोंद काढायची का?",
    deleteConfirmMessage = "ही नोंद कायमची काढली जाईल.",
    shareButton = "शेअर करा",
    emptyLogsTitle = "अजून कोणतीही नोंद नाही",
    emptyLogsSubtitle = "ट्रॅक्टर कुठे चालला, किती चालला, ड्रायव्हर व डिझेल यांची पहिली नोंद करा.",
    emptyLogsAction = "पहिली नोंद करा",
    emptyFilteredTitle = "कोणतीही नोंद सापडली नाही",
    emptyFilteredSubtitle = "फिल्टर किंवा शोध शब्द बदलून पहा.",
    emptyDieselTitle = "डिझेलची नोंद नाही",
    emptyDieselSubtitle = "ट्रॅक्टरमध्ये टाकलेल्या डिझेलची नोंद येथे ठेवा.",
    emptyDieselAction = "डिझेल नोंद करा",
    emptySummaryTitle = "हिशोब पाहण्यासाठी नोंद करा",
    emptySummarySubtitle = "नोंदी केल्यानंतर अवजार, ड्रायव्हर व शेतकरीनुसार संपूर्ण हिशोब येथे दिसेल.",
    byImplementTab = "अवजार",
    byDriverTab = "ड्रायव्हर",
    byFarmerTab = "शेतकरी",
    jobsCountSuffix = "कामे",
    dieselFilledTag = "डिझेल",
    fromWorkLogBadge = "कामादरम्यान",
    standaloneRefillBadge = "पंप नोंद",
    recentSuggestionsLabel = "अलीकडील:",
    savedSnackbar = "नोंद सेव्ह झाली",
    deletedSnackbar = "नोंद काढली गेली",
    paidUpdatedSnackbar = "पैसे स्थिती अपडेट झाली",
    validationFarmerRequired = "कृपया शेतकऱ्याचे नाव टाका",
    validationLocationRequired = "कृपया गाव किंवा ठिकाण टाका",
    validationQuantityRequired = "कृपया किती चालला ते टाका",
    validationDriverRequired = "कृपया ड्रायव्हरचे नाव टाका",
    validationLitresRequired = "कृपया डिझेल लिटर टाका",
    langToggleLabel = "EN",
    litresShort = "लि.",
    rupeeSymbol = "₹",
    driverPrefix = "ड्रायव्हर",
    shareReportTitle = "ट्रॅक्टर हिशोब शेअर करा",
    optionalTag = "ऐच्छिक",
    selectDateTitle = "तारीख निवडा",
    okButton = "ठीक आहे",
    timeCalculatorTitle = "वेळेनुसार तास मोजा",
    startTimeLabel = "चालू केलेली वेळ",
    endTimeLabel = "बंद केलेली वेळ",
    selectTimePlaceholder = "वेळ निवडा",
    nowButton = "आत्ता",
    clearTimeButton = "वेळ काढा",
    breakTimeLabel = "थांबा वेळ (मिनिटे)",
    totalTimeCalculatedLabel = "एकूण कामाचा वेळ",
    overnightBadge = "पुढील दिवस",
    timePeriodLabel = "वेळ",
    offlineBadge = "१००% ऑफलाइन",
    offlineBackupTitle = "ऑफलाइन डेटा बॅकअप",
    offlineBackupSubtitle = "हे ॲप इंटरनेटशिवाय पूर्णपणे फोनवर चालते. तुमच्या नोंदी फोनमध्ये बॅकअप किंवा Excel (CSV) म्हणून सेव्ह करा.",
    exportBackupButton = "बॅकअप सेव्ह",
    restoreBackupButton = "रिस्टोअर करा",
    exportCsvButton = "Excel (CSV) सेव्ह करा",
    backupSavedSnackbar = "बॅकअप फाईल फोनवर सेव्ह झाली",
    csvSavedSnackbar = "CSV रिपोर्ट फोनवर सेव्ह झाला",
    backupRestoredSnackbar = "नोंदी रिस्टोअर झाल्या",
    backupErrorSnackbar = "बॅकअप फाईल वाचता आली नाही"
)

val EnglishStrings = UiStrings(
    appTitle = "Advik Awajare Bank",
    appSubtitle = "Work, Implement, Driver & Diesel Log",
    navLogs = "Logs",
    navDiesel = "Diesel",
    navSummary = "Summary",
    addWorkLog = "New Work Entry",
    editWorkLog = "Edit Entry",
    addDieselLog = "Add Diesel",
    editDieselLog = "Edit Diesel",
    searchPlaceholder = "Search farmer, village, or driver...",
    allFilter = "All",
    todayFilter = "Today",
    weekFilter = "This Week",
    monthFilter = "This Month",
    totalWorkLabel = "Total Work",
    totalDieselLabel = "Total Diesel",
    totalBilledLabel = "Total Billed",
    pendingLabel = "Pending",
    paidLabel = "Paid",
    markPaid = "Mark Paid",
    markPending = "Mark Pending",
    markAllPaid = "Mark All Paid",
    implementQuestion = "Which implement was used?",
    farmerNameLabel = "Whose Farm? (Farmer Name)",
    farmerNameHint = "e.g. Rambhau Patil",
    locationLabel = "Where did it run? (Village / Field)",
    locationHint = "e.g. Pimpalgaon, East Plot",
    quantityLabel = "How much did it run?",
    quantityHint = "e.g. 2.5",
    unitLabel = "Unit",
    driverNameLabel = "Who was the Driver?",
    driverNameHint = "e.g. Santosh",
    dieselSectionTitle = "Diesel Filled (Optional)",
    dieselLitresLabel = "Diesel Filled (Litres)",
    dieselCostLabel = "Diesel Cost (₹)",
    billingSectionTitle = "Rate & Billing (Optional)",
    ratePerUnitLabel = "Rate per Unit (₹)",
    totalAmountLabel = "Total Amount (₹)",
    paymentStatusLabel = "Payment Received?",
    dateLabel = "Date",
    notesLabel = "Notes",
    notesHint = "Any extra work details...",
    pumpOrPlaceLabel = "Fuel Pump / Place",
    pumpOrPlaceHint = "e.g. Bharat Petroleum Highway",
    saveButton = "Save Entry",
    updateButton = "Update Entry",
    deleteButton = "Delete",
    cancelButton = "Cancel",
    deleteConfirmTitle = "Delete this entry?",
    deleteConfirmMessage = "This entry will be permanently removed.",
    shareButton = "Share",
    emptyLogsTitle = "No tractor logs yet",
    emptyLogsSubtitle = "Record where the tractor ran, how much, which implement, driver, and diesel.",
    emptyLogsAction = "Add First Entry",
    emptyFilteredTitle = "No matching entries",
    emptyFilteredSubtitle = "Try changing your search or filters.",
    emptyDieselTitle = "No diesel records yet",
    emptyDieselSubtitle = "Track every litre of diesel filled for your tractor here.",
    emptyDieselAction = "Add Diesel Entry",
    emptySummaryTitle = "Add entries to see summary",
    emptySummarySubtitle = "Once you record work, breakdowns by implement, driver, and farmer appear here.",
    byImplementTab = "Implement",
    byDriverTab = "Driver",
    byFarmerTab = "Farmer",
    jobsCountSuffix = "jobs",
    dieselFilledTag = "Diesel",
    fromWorkLogBadge = "With Work",
    standaloneRefillBadge = "Refill",
    recentSuggestionsLabel = "Recent:",
    savedSnackbar = "Entry saved",
    deletedSnackbar = "Entry deleted",
    paidUpdatedSnackbar = "Payment status updated",
    validationFarmerRequired = "Please enter farmer name",
    validationLocationRequired = "Please enter village or location",
    validationQuantityRequired = "Please enter work quantity",
    validationDriverRequired = "Please enter driver name",
    validationLitresRequired = "Please enter diesel litres",
    langToggleLabel = "मराठी",
    litresShort = "L",
    rupeeSymbol = "₹",
    driverPrefix = "Driver",
    shareReportTitle = "Share Tractor Summary",
    optionalTag = "Optional",
    selectDateTitle = "Select Date",
    okButton = "OK",
    timeCalculatorTitle = "Calculate Hours by Time",
    startTimeLabel = "Start Time",
    endTimeLabel = "Stop Time",
    selectTimePlaceholder = "Pick time",
    nowButton = "Now",
    clearTimeButton = "Clear",
    breakTimeLabel = "Break / Pause (min)",
    totalTimeCalculatedLabel = "Calculated Work Time",
    overnightBadge = "Next day",
    timePeriodLabel = "Time",
    offlineBadge = "100% Offline",
    offlineBackupTitle = "Offline Data Backup",
    offlineBackupSubtitle = "This app works completely offline on your phone. Save or restore a local backup file or export CSV anytime.",
    exportBackupButton = "Save Backup",
    restoreBackupButton = "Restore",
    exportCsvButton = "Export Excel (CSV)",
    backupSavedSnackbar = "Backup file saved to phone",
    csvSavedSnackbar = "CSV report saved to phone",
    backupRestoredSnackbar = "Entries restored",
    backupErrorSnackbar = "Could not read backup file"
)

fun implementLabel(type: ImplementType, language: AppLanguage): String = when (language) {
    AppLanguage.MARATHI -> when (type) {
        ImplementType.ROTAVATOR -> "रोटाव्हेटर"
        ImplementType.CULTIVATOR -> "कल्टिव्हेटर"
        ImplementType.TROLLEY -> "ट्रॉली"
        ImplementType.PLOUGH -> "नांगर"
        ImplementType.OTHER -> "इतर"
    }
    AppLanguage.ENGLISH -> when (type) {
        ImplementType.ROTAVATOR -> "Rotavator"
        ImplementType.CULTIVATOR -> "Cultivator"
        ImplementType.TROLLEY -> "Trolley"
        ImplementType.PLOUGH -> "Plough"
        ImplementType.OTHER -> "Other"
    }
}

fun unitLabel(unit: WorkUnit, language: AppLanguage): String = when (language) {
    AppLanguage.MARATHI -> when (unit) {
        WorkUnit.HOURS -> "तास"
        WorkUnit.TRIPS -> "फेऱ्या"
        WorkUnit.ACRES -> "एकर"
        WorkUnit.GUNTHA -> "गुंठे"
    }
    AppLanguage.ENGLISH -> when (unit) {
        WorkUnit.HOURS -> "Hrs"
        WorkUnit.TRIPS -> "Trips"
        WorkUnit.ACRES -> "Acres"
        WorkUnit.GUNTHA -> "Guntha"
    }
}

fun dateFilterLabel(filter: DateFilter, strings: UiStrings): String = when (filter) {
    DateFilter.ALL -> strings.allFilter
    DateFilter.TODAY -> strings.todayFilter
    DateFilter.WEEK -> strings.weekFilter
    DateFilter.MONTH -> strings.monthFilter
}

private val numberFormat = DecimalFormat("#.##")
private val rupeeFormat = DecimalFormat("#,##0.##")

fun formatNumber(value: Double): String = numberFormat.format(value)

fun formatRupees(value: Double): String = "₹${rupeeFormat.format(value)}"

fun formatDateShort(millis: Long, language: AppLanguage): String {
    val locale = if (language == AppLanguage.MARATHI) Locale("mr", "IN") else Locale.ENGLISH
    val sdf = SimpleDateFormat("dd MMM yyyy", locale)
    return sdf.format(Date(millis))
}

fun formatTimeOfDay(minutesSinceMidnight: Int, language: AppLanguage): String {
    val safeMin = ((minutesSinceMidnight % (24 * 60)) + (24 * 60)) % (24 * 60)
    val hour24 = safeMin / 60
    val minute = safeMin % 60
    val isPm = hour24 >= 12
    val hour12 = when {
        hour24 == 0 -> 12
        hour24 > 12 -> hour24 - 12
        else -> hour24
    }
    val period = if (language == AppLanguage.MARATHI) {
        when {
            hour24 < 12 -> "स."
            hour24 < 17 -> "दु."
            hour24 < 20 -> "सायं."
            else -> "रा."
        }
    } else {
        if (isPm) "PM" else "AM"
    }
    val timePart = String.format(Locale.ENGLISH, "%d:%02d", hour12, minute)
    return if (language == AppLanguage.MARATHI) "$period $timePart" else "$timePart $period"
}

fun formatDurationHoursMinutes(totalMinutes: Int, language: AppLanguage): String {
    val clamped = totalMinutes.coerceAtLeast(0)
    val hrs = clamped / 60
    val mins = clamped % 60
    return if (language == AppLanguage.MARATHI) {
        when {
            hrs > 0 && mins > 0 -> "$hrs तास $mins मि."
            hrs > 0 -> "$hrs तास"
            else -> "$mins मि."
        }
    } else {
        when {
            hrs > 0 && mins > 0 -> "${hrs}h ${mins}m"
            hrs > 0 -> "${hrs}h"
            else -> "${mins}m"
        }
    }
}

fun buildWorkLogShareText(log: WorkLogEntity, language: AppLanguage): String {
    val strings = stringsFor(language)
    val dateStr = formatDateShort(log.dateMillis, language)
    val impl = implementLabel(log.implement, language)
    val qty = "${formatNumber(log.quantity)} ${unitLabel(log.workUnit, language)}"
    return buildString {
        appendLine("🚜 ${strings.appTitle} ($dateStr)")
        appendLine("• ${strings.farmerNameLabel}: ${log.farmerName}")
        appendLine("• ${strings.locationLabel}: ${log.location}")
        appendLine("• ${strings.byImplementTab}: $impl — $qty")
        if (log.startTimeMinutes != null && log.endTimeMinutes != null) {
            val startStr = formatTimeOfDay(log.startTimeMinutes, language)
            val endStr = formatTimeOfDay(log.endTimeMinutes, language)
            val durStr = log.calculatedDurationMinutes?.let { " (${formatDurationHoursMinutes(it, language)})" } ?: ""
            appendLine("• ${strings.timePeriodLabel}: $startStr – $endStr$durStr")
        }
        appendLine("• ${strings.driverPrefix}: ${log.driverName}")
        if (log.dieselLitres > 0) {
            appendLine("• ${strings.dieselFilledTag}: ${formatNumber(log.dieselLitres)} ${strings.litresShort}")
        }
        if (log.totalAmount > 0) {
            val status = if (log.isPaid) strings.paidLabel else strings.pendingLabel
            appendLine("• ${strings.totalAmountLabel}: ${formatRupees(log.totalAmount)} ($status)")
        }
        if (log.notes.isNotBlank()) {
            appendLine("• ${strings.notesLabel}: ${log.notes}")
        }
    }.trim()
}

fun buildFarmerSummaryShareText(
    farmerName: String,
    logs: List<WorkLogEntity>,
    totalAmount: Double,
    pendingAmount: Double,
    language: AppLanguage
): String {
    val strings = stringsFor(language)
    return buildString {
        appendLine("🚜 ${strings.appTitle} — $farmerName")
        appendLine("------------------------")
        logs.forEach { log ->
            val date = formatDateShort(log.dateMillis, language)
            val impl = implementLabel(log.implement, language)
            val qty = "${formatNumber(log.quantity)} ${unitLabel(log.workUnit, language)}"
            val timeInfo = if (log.startTimeMinutes != null && log.endTimeMinutes != null) {
                " [${formatTimeOfDay(log.startTimeMinutes, language)}–${formatTimeOfDay(log.endTimeMinutes, language)}]"
            } else ""
            val amt = if (log.totalAmount > 0) " | ${formatRupees(log.totalAmount)}" else ""
            val status = if (log.totalAmount > 0) {
                if (log.isPaid) " (${strings.paidLabel})" else " (${strings.pendingLabel})"
            } else ""
            appendLine("• $date: $impl - $qty$timeInfo (${log.location})$amt$status")
        }
        if (totalAmount > 0) {
            appendLine("------------------------")
            appendLine("${strings.totalBilledLabel}: ${formatRupees(totalAmount)}")
            if (pendingAmount > 0) {
                appendLine("${strings.pendingLabel}: ${formatRupees(pendingAmount)}")
            } else {
                appendLine("${strings.paidLabel} ✓")
            }
        }
    }.trim()
}
