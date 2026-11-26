package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.data.model.ImplementType
import com.example.data.model.WorkLogEntity
import com.example.data.model.WorkUnit
import com.example.ui.components.QuickSuggestionsRow
import com.example.ui.components.TimeCalculatorSection
import com.example.ui.components.implementColor
import com.example.ui.components.implementIcon
import com.example.ui.i18n.UiStrings
import com.example.ui.i18n.formatDateShort
import com.example.ui.i18n.formatNumber
import com.example.ui.i18n.implementLabel
import com.example.ui.i18n.unitLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkLogEditorScreen(
    existingLog: WorkLogEntity?,
    strings: UiStrings,
    language: AppLanguage,
    recentFarmers: List<String>,
    recentLocations: List<String>,
    recentDrivers: List<String>,
    onSave: (WorkLogEntity) -> Unit,
    onDelete: (Long) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var selectedImplement by rememberSaveable {
        mutableStateOf(existingLog?.implement ?: ImplementType.ROTAVATOR)
    }
    var farmerName by rememberSaveable { mutableStateOf(existingLog?.farmerName ?: "") }
    var location by rememberSaveable { mutableStateOf(existingLog?.location ?: "") }
    var quantityText by rememberSaveable {
        mutableStateOf(
            existingLog?.let { if (it.quantity > 0) formatNumber(it.quantity) else "" } ?: ""
        )
    }
    var selectedUnit by rememberSaveable {
        mutableStateOf(existingLog?.workUnit ?: WorkUnit.HOURS)
    }
    var driverName by rememberSaveable { mutableStateOf(existingLog?.driverName ?: "") }
    var dieselLitresText by rememberSaveable {
        mutableStateOf(
            existingLog?.let { if (it.dieselLitres > 0) formatNumber(it.dieselLitres) else "" } ?: ""
        )
    }
    var dieselCostText by rememberSaveable {
        mutableStateOf(
            existingLog?.let { if (it.dieselCost > 0) formatNumber(it.dieselCost) else "" } ?: ""
        )
    }
    var rateText by rememberSaveable {
        mutableStateOf(
            existingLog?.let { if (it.ratePerUnit > 0) formatNumber(it.ratePerUnit) else "" } ?: ""
        )
    }
    var totalAmountText by rememberSaveable {
        mutableStateOf(
            existingLog?.let { if (it.totalAmount > 0) formatNumber(it.totalAmount) else "" } ?: ""
        )
    }
    var isPaid by rememberSaveable { mutableStateOf(existingLog?.isPaid ?: false) }
    var notes by rememberSaveable { mutableStateOf(existingLog?.notes ?: "") }
    var dateMillis by rememberSaveable {
        mutableLongStateOf(existingLog?.dateMillis ?: System.currentTimeMillis())
    }
    var startTimeMinutes by rememberSaveable { mutableStateOf(existingLog?.startTimeMinutes) }
    var endTimeMinutes by rememberSaveable { mutableStateOf(existingLog?.endTimeMinutes) }
    var breakMinutes by rememberSaveable { mutableStateOf(existingLog?.breakMinutes ?: 0) }

    var farmerError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var quantityError by remember { mutableStateOf<String?>(null) }
    var driverError by remember { mutableStateOf<String?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    fun recalculateTotal(qtyStr: String, rateStr: String) {
        val q = qtyStr.toDoubleOrNull()
        val r = rateStr.toDoubleOrNull()
        if (q != null && r != null && q > 0 && r > 0) {
            totalAmountText = formatNumber(q * r)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (existingLog == null) strings.addWorkLog else strings.editWorkLog,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("editor_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = strings.cancelButton
                        )
                    }
                },
                actions = {
                    if (existingLog != null) {
                        IconButton(
                            onClick = { showDeleteConfirm = true },
                            modifier = Modifier.testTag("editor_delete_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = strings.deleteButton,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 6.dp,
                tonalElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            val trimmedFarmer = farmerName.trim()
                            val trimmedLocation = location.trim()
                            val trimmedDriver = driverName.trim()
                            val parsedQty = quantityText.trim().toDoubleOrNull()

                            farmerError = if (trimmedFarmer.isEmpty()) strings.validationFarmerRequired else null
                            locationError = if (trimmedLocation.isEmpty()) strings.validationLocationRequired else null
                            quantityError = if (parsedQty == null || parsedQty <= 0.0) strings.validationQuantityRequired else null
                            driverError = if (trimmedDriver.isEmpty()) strings.validationDriverRequired else null

                            if (farmerError == null && locationError == null && quantityError == null && driverError == null) {
                                val parsedDiesel = dieselLitresText.trim().toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                                val parsedDieselCost = dieselCostText.trim().toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                                val parsedRate = rateText.trim().toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0
                                val parsedTotal = totalAmountText.trim().toDoubleOrNull()?.coerceAtLeast(0.0)
                                    ?: if (parsedRate > 0.0) (parsedQty!! * parsedRate) else 0.0

                                onSave(
                                    WorkLogEntity(
                                        id = existingLog?.id ?: 0L,
                                        farmerName = trimmedFarmer,
                                        location = trimmedLocation,
                                        implementType = selectedImplement.key,
                                        quantity = parsedQty!!,
                                        unit = selectedUnit.key,
                                        driverName = trimmedDriver,
                                        dieselLitres = parsedDiesel,
                                        dieselCost = parsedDieselCost,
                                        ratePerUnit = parsedRate,
                                        totalAmount = parsedTotal,
                                        isPaid = isPaid,
                                        notes = notes.trim(),
                                        dateMillis = dateMillis,
                                        createdAtMillis = existingLog?.createdAtMillis ?: System.currentTimeMillis(),
                                        startTimeMinutes = if (selectedUnit == WorkUnit.HOURS) startTimeMinutes else null,
                                        endTimeMinutes = if (selectedUnit == WorkUnit.HOURS) endTimeMinutes else null,
                                        breakMinutes = if (selectedUnit == WorkUnit.HOURS && startTimeMinutes != null && endTimeMinutes != null) breakMinutes else 0
                                    )
                                )
                            }
                        },
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(vertical = 15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp)
                            .testTag("save_work_log_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = if (existingLog == null) strings.saveButton else strings.updateButton,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Implement Selection
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = strings.implementQuestion,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(ImplementType.entries) { impl ->
                                val isSelected = selectedImplement == impl
                                val accent = implementColor(impl)
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (isSelected) accent else MaterialTheme.colorScheme.surfaceVariant,
                                    border = if (isSelected) null else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .clickable {
                                            selectedImplement = impl
                                            if (impl == ImplementType.TROLLEY && existingLog == null) {
                                                selectedUnit = WorkUnit.TRIPS
                                            } else if (impl != ImplementType.TROLLEY && selectedUnit == WorkUnit.TRIPS && existingLog == null) {
                                                selectedUnit = WorkUnit.HOURS
                                            }
                                        }
                                        .testTag("select_implement_${impl.key.lowercase()}")
                                ) {
                                    Column(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = implementIcon(impl),
                                            contentDescription = null,
                                            tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else accent,
                                            modifier = Modifier.size(26.dp)
                                        )
                                        Text(
                                            text = implementLabel(impl, language),
                                            style = MaterialTheme.typography.labelLarge,
                                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Farmer & Location Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = farmerName,
                            onValueChange = {
                                farmerName = it
                                farmerError = null
                            },
                            label = { Text(strings.farmerNameLabel) },
                            placeholder = { Text(strings.farmerNameHint) },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            isError = farmerError != null,
                            supportingText = farmerError?.let { err -> { Text(err) } },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("farmer_name_input")
                        )

                        QuickSuggestionsRow(
                            label = strings.recentSuggestionsLabel,
                            suggestions = recentFarmers,
                            onSelect = {
                                farmerName = it
                                farmerError = null
                            }
                        )

                        OutlinedTextField(
                            value = location,
                            onValueChange = {
                                location = it
                                locationError = null
                            },
                            label = { Text(strings.locationLabel) },
                            placeholder = { Text(strings.locationHint) },
                            leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                            isError = locationError != null,
                            supportingText = locationError?.let { err -> { Text(err) } },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("location_input")
                        )

                        QuickSuggestionsRow(
                            label = strings.recentSuggestionsLabel,
                            suggestions = recentLocations,
                            onSelect = {
                                location = it
                                locationError = null
                            }
                        )
                    }
                }

                // 3. Work Quantity + Unit + Start/End Time Calculator + Driver + Date
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(WorkUnit.entries) { unit ->
                                FilterChip(
                                    selected = selectedUnit == unit,
                                    onClick = { selectedUnit = unit },
                                    label = {
                                        Text(
                                            text = unitLabel(unit, language),
                                            style = MaterialTheme.typography.labelLarge
                                        )
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier.testTag("unit_chip_${unit.key.lowercase()}")
                                )
                            }
                        }

                        if (selectedUnit == WorkUnit.HOURS) {
                            TimeCalculatorSection(
                                startTimeMinutes = startTimeMinutes,
                                endTimeMinutes = endTimeMinutes,
                                breakMinutes = breakMinutes,
                                strings = strings,
                                language = language,
                                onTimesChanged = { newStart, newEnd, newBreak, calcHours ->
                                    startTimeMinutes = newStart
                                    endTimeMinutes = newEnd
                                    breakMinutes = newBreak
                                    if (calcHours != null) {
                                        val formatted = formatNumber(calcHours)
                                        quantityText = formatted
                                        quantityError = null
                                        recalculateTotal(formatted, rateText)
                                    }
                                }
                            )
                        }

                        OutlinedTextField(
                            value = quantityText,
                            onValueChange = { input ->
                                val clean = input.filter { it.isDigit() || it == '.' }
                                quantityText = clean
                                quantityError = null
                                recalculateTotal(clean, rateText)
                            },
                            label = {
                                Text("${strings.quantityLabel} (${unitLabel(selectedUnit, language)})")
                            },
                            placeholder = { Text(strings.quantityHint) },
                            isError = quantityError != null,
                            supportingText = quantityError?.let { err -> { Text(err) } },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("quantity_input")
                        )

                        OutlinedTextField(
                            value = driverName,
                            onValueChange = {
                                driverName = it
                                driverError = null
                            },
                            label = { Text(strings.driverNameLabel) },
                            placeholder = { Text(strings.driverNameHint) },
                            leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null) },
                            isError = driverError != null,
                            supportingText = driverError?.let { err -> { Text(err) } },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("driver_name_input")
                        )

                        QuickSuggestionsRow(
                            label = strings.recentSuggestionsLabel,
                            suggestions = recentDrivers,
                            onSelect = {
                                driverName = it
                                driverError = null
                            }
                        )

                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("work_date_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                            Text(
                                text = "${strings.dateLabel}: ${formatDateShort(dateMillis, language)}",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }

                // 4. Diesel Section (Optional)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.LocalGasStation,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                text = strings.dieselSectionTitle,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = dieselLitresText,
                                onValueChange = { dieselLitresText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text(strings.dieselLitresLabel) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("work_diesel_litres_input")
                            )
                            OutlinedTextField(
                                value = dieselCostText,
                                onValueChange = { dieselCostText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text(strings.dieselCostLabel) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("work_diesel_cost_input")
                            )
                        }
                    }
                }

                // 5. Rate & Billing + Notes (Optional)
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Payments,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = strings.billingSectionTitle,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = rateText,
                                onValueChange = { input ->
                                    val clean = input.filter { ch -> ch.isDigit() || ch == '.' }
                                    rateText = clean
                                    recalculateTotal(quantityText, clean)
                                },
                                label = { Text(strings.ratePerUnitLabel) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("rate_input")
                            )
                            OutlinedTextField(
                                value = totalAmountText,
                                onValueChange = { totalAmountText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                                label = { Text(strings.totalAmountLabel) },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("total_amount_input")
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = strings.paymentStatusLabel,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (isPaid) strings.paidLabel else strings.pendingLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                            Switch(
                                checked = isPaid,
                                onCheckedChange = { isPaid = it },
                                modifier = Modifier.testTag("paid_status_switch")
                            )
                        }

                        OutlinedTextField(
                            value = notes,
                            onValueChange = { notes = it },
                            label = { Text("${strings.notesLabel} (${strings.optionalTag})") },
                            placeholder = { Text(strings.notesHint) },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("notes_input")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { dateMillis = it }
                        showDatePicker = false
                    }
                ) {
                    Text(strings.okButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(strings.cancelButton)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showDeleteConfirm && existingLog != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text(strings.deleteConfirmTitle) },
            text = { Text(strings.deleteConfirmMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(existingLog.id)
                    }
                ) {
                    Text(
                        text = strings.deleteButton,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text(strings.cancelButton)
                }
            }
        )
    }
}
