package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.DieselLogEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.MetaTag
import com.example.ui.components.QuickSuggestionsRow
import com.example.ui.i18n.UiStrings
import com.example.ui.i18n.formatDateShort
import com.example.ui.i18n.formatNumber
import com.example.ui.i18n.formatRupees
import com.example.ui.theme.DieselGold
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.viewmodel.CombinedDieselEntry
import com.example.ui.viewmodel.TractorUiState

@Composable
fun DieselScreen(
    uiState: TractorUiState,
    strings: UiStrings,
    onAddDieselClick: () -> Unit,
    onEditDieselLog: (DieselLogEntity) -> Unit,
    onDeleteDieselLog: (Long) -> Unit,
    onOpenSourceWorkLog: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var pendingDeleteId by remember { mutableStateOf<Long?>(null) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 720.dp),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 8.dp,
                bottom = 96.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "diesel_summary_card") {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF8F5907),
                                        Color(0xFFC78310)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = strings.totalDieselLabel,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color(0xFFFFF0D1)
                                )
                                Text(
                                    text = "${formatNumber(uiState.totalDieselLitres)} ${strings.litresShort}",
                                    fontFamily = OutfitFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 32.sp,
                                    color = Color.White
                                )
                                if (uiState.totalDieselCost > 0) {
                                    Text(
                                        text = "${strings.dieselCostLabel}: ${formatRupees(uiState.totalDieselCost)}",
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color(0xFFFFE5B8)
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(18.dp),
                                color = Color.White.copy(alpha = 0.18f)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.LocalGasStation,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .padding(14.dp)
                                        .size(34.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.combinedDieselEntries.isEmpty()) {
                item(key = "empty_diesel") {
                    EmptyStateCard(
                        title = strings.emptyDieselTitle,
                        subtitle = strings.emptyDieselSubtitle,
                        actionLabel = strings.emptyDieselAction,
                        onAction = onAddDieselClick,
                        showIllustration = false
                    )
                }
            } else {
                items(
                    items = uiState.combinedDieselEntries,
                    key = { it.uniqueKey }
                ) { entry ->
                    DieselEntryCard(
                        entry = entry,
                        strings = strings,
                        language = uiState.language,
                        onClick = {
                            if (entry.isFromWorkLog && entry.sourceWorkLogId != null) {
                                onOpenSourceWorkLog(entry.sourceWorkLogId)
                            } else if (entry.sourceDieselLogId != null) {
                                uiState.standaloneDieselLogs
                                    .firstOrNull { it.id == entry.sourceDieselLogId }
                                    ?.let(onEditDieselLog)
                            }
                        },
                        onDelete = entry.sourceDieselLogId?.let { id ->
                            { pendingDeleteId = id }
                        }
                    )
                }
            }
        }
    }

    pendingDeleteId?.let { idToDelete ->
        AlertDialog(
            onDismissRequest = { pendingDeleteId = null },
            title = { Text(strings.deleteConfirmTitle) },
            text = { Text(strings.deleteConfirmMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteDieselLog(idToDelete)
                        pendingDeleteId = null
                    }
                ) {
                    Text(strings.deleteButton, color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteId = null }) {
                    Text(strings.cancelButton)
                }
            }
        )
    }
}

@Composable
private fun DieselEntryCard(
    entry: CombinedDieselEntry,
    strings: UiStrings,
    language: AppLanguage,
    onClick: () -> Unit,
    onDelete: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("diesel_card_${entry.uniqueKey}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = DieselGold.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = if (entry.isFromWorkLog) strings.fromWorkLogBadge else strings.standaloneRefillBadge,
                            style = MaterialTheme.typography.labelSmall,
                            color = DieselGold,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                        )
                    }
                    Text(
                        text = formatDateShort(entry.dateMillis, language),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (entry.placeOrFarm.isNotBlank()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = entry.placeOrFarm,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (entry.driverName.isNotBlank()) {
                        MetaTag(
                            icon = Icons.Filled.Person,
                            text = entry.driverName,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    if (entry.cost > 0) {
                        Text(
                            text = formatRupees(entry.cost),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.tertiaryContainer
                ) {
                    Text(
                        text = "${formatNumber(entry.litres)} ${strings.litresShort}",
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                if (onDelete != null) {
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("delete_diesel_${entry.sourceDieselLogId}")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = strings.deleteButton,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DieselEditorDialog(
    existingLog: DieselLogEntity?,
    strings: UiStrings,
    language: AppLanguage,
    recentDrivers: List<String>,
    onSave: (DieselLogEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var litresText by remember {
        mutableStateOf(existingLog?.let { formatNumber(it.litres) } ?: "")
    }
    var costText by remember {
        mutableStateOf(existingLog?.let { if (it.cost > 0) formatNumber(it.cost) else "" } ?: "")
    }
    var driverName by remember { mutableStateOf(existingLog?.driverName ?: "") }
    var pumpOrPlace by remember { mutableStateOf(existingLog?.pumpOrPlace ?: "") }
    var notes by remember { mutableStateOf(existingLog?.notes ?: "") }
    var dateMillis by remember { mutableLongStateOf(existingLog?.dateMillis ?: System.currentTimeMillis()) }

    var litresError by remember { mutableStateOf<String?>(null) }
    var driverError by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (existingLog == null) strings.addDieselLog else strings.editDieselLog,
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = litresText,
                        onValueChange = {
                            litresText = it.filter { ch -> ch.isDigit() || ch == '.' }
                            litresError = null
                        },
                        label = { Text(strings.dieselLitresLabel) },
                        isError = litresError != null,
                        supportingText = litresError?.let { err -> { Text(err) } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("diesel_dialog_litres_input")
                    )
                    OutlinedTextField(
                        value = costText,
                        onValueChange = { costText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        label = { Text(strings.dieselCostLabel) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("diesel_dialog_cost_input")
                    )
                }

                OutlinedTextField(
                    value = driverName,
                    onValueChange = {
                        driverName = it
                        driverError = null
                    },
                    label = { Text(strings.driverNameLabel) },
                    placeholder = { Text(strings.driverNameHint) },
                    isError = driverError != null,
                    supportingText = driverError?.let { err -> { Text(err) } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("diesel_dialog_driver_input")
                )

                QuickSuggestionsRow(
                    label = strings.recentSuggestionsLabel,
                    suggestions = recentDrivers,
                    onSelect = {
                        driverName = it
                        driverError = null
                    }
                )

                OutlinedTextField(
                    value = pumpOrPlace,
                    onValueChange = { pumpOrPlace = it },
                    label = { Text("${strings.pumpOrPlaceLabel} (${strings.optionalTag})") },
                    placeholder = { Text(strings.pumpOrPlaceHint) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("diesel_dialog_pump_input")
                )

                OutlinedButton(
                    onClick = { showDatePicker = true },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("${strings.dateLabel}: ${formatDateShort(dateMillis, language)}")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsedLitres = litresText.trim().toDoubleOrNull()
                    val trimmedDriver = driverName.trim()
                    litresError = if (parsedLitres == null || parsedLitres <= 0.0) strings.validationLitresRequired else null
                    driverError = if (trimmedDriver.isEmpty()) strings.validationDriverRequired else null

                    if (litresError == null && driverError == null) {
                        onSave(
                            DieselLogEntity(
                                id = existingLog?.id ?: 0L,
                                litres = parsedLitres!!,
                                cost = costText.trim().toDoubleOrNull()?.coerceAtLeast(0.0) ?: 0.0,
                                driverName = trimmedDriver,
                                pumpOrPlace = pumpOrPlace.trim(),
                                notes = notes.trim(),
                                dateMillis = dateMillis,
                                createdAtMillis = existingLog?.createdAtMillis ?: System.currentTimeMillis()
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    contentColor = MaterialTheme.colorScheme.onSecondary
                ),
                modifier = Modifier.testTag("save_diesel_button")
            ) {
                Text(strings.saveButton)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancelButton)
            }
        }
    )

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { dateMillis = it }
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
            DatePicker(state = pickerState)
        }
    }
}
