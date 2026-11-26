package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.calculateElapsedMinutes
import com.example.ui.i18n.UiStrings
import com.example.ui.i18n.formatDurationHoursMinutes
import com.example.ui.i18n.formatNumber
import com.example.ui.i18n.formatTimeOfDay
import com.example.ui.i18n.unitLabel
import com.example.data.model.WorkUnit
import com.example.ui.theme.OutfitFontFamily
import java.util.Calendar
import kotlin.math.roundToInt

private enum class TimePickerTarget {
    START,
    END
}

private val BreakMinuteOptions = listOf(0, 15, 30, 45, 60)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeCalculatorSection(
    startTimeMinutes: Int?,
    endTimeMinutes: Int?,
    breakMinutes: Int,
    strings: UiStrings,
    language: AppLanguage,
    onTimesChanged: (startMin: Int?, endMin: Int?, breakMin: Int, calculatedHours: Double?) -> Unit,
    modifier: Modifier = Modifier
) {
    var activePicker by remember { mutableStateOf<TimePickerTarget?>(null) }

    val elapsedMinutes = calculateElapsedMinutes(startTimeMinutes, endTimeMinutes, breakMinutes)
    val isOvernight = startTimeMinutes != null && endTimeMinutes != null && endTimeMinutes <= startTimeMinutes

    fun notifyChange(newStart: Int?, newEnd: Int?, newBreak: Int) {
        val mins = calculateElapsedMinutes(newStart, newEnd, newBreak)
        val hours = mins?.let { ((it / 60.0) * 100.0).roundToInt() / 100.0 }
        onTimesChanged(newStart, newEnd, newBreak, hours)
    }

    fun currentTimeMinutes(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccessTime,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(19.dp)
                    )
                    Text(
                        text = strings.timeCalculatorTitle,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (startTimeMinutes != null || endTimeMinutes != null) {
                    TextButton(
                        onClick = { notifyChange(null, null, 0) },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("clear_time_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.size(4.dp))
                        Text(
                            text = strings.clearTimeButton,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TimeSlotTile(
                    label = strings.startTimeLabel,
                    timeText = startTimeMinutes?.let { formatTimeOfDay(it, language) }
                        ?: strings.selectTimePlaceholder,
                    isSelected = startTimeMinutes != null,
                    icon = Icons.Filled.PlayArrow,
                    accent = MaterialTheme.colorScheme.primary,
                    onClick = { activePicker = TimePickerTarget.START },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pick_start_time_button")
                )

                TimeSlotTile(
                    label = strings.endTimeLabel,
                    timeText = endTimeMinutes?.let { formatTimeOfDay(it, language) }
                        ?: strings.selectTimePlaceholder,
                    isSelected = endTimeMinutes != null,
                    icon = Icons.Filled.Stop,
                    accent = MaterialTheme.colorScheme.secondary,
                    onClick = { activePicker = TimePickerTarget.END },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("pick_end_time_button")
                )
            }

            AnimatedVisibility(visible = startTimeMinutes != null && endTimeMinutes != null) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Break / pause quick chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = strings.breakTimeLabel,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(BreakMinuteOptions) { mins ->
                                FilterChip(
                                    selected = breakMinutes == mins,
                                    onClick = { notifyChange(startTimeMinutes, endTimeMinutes, mins) },
                                    label = {
                                        Text(
                                            text = if (mins == 0) {
                                                "0"
                                            } else if (language == AppLanguage.MARATHI) {
                                                "$mins मि."
                                            } else {
                                                "${mins}m"
                                            },
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                    ),
                                    modifier = Modifier.testTag("break_chip_$mins")
                                )
                            }
                        }
                    }

                    // Calculated Duration Result Pill
                    if (elapsedMinutes != null) {
                        val decimalHours = ((elapsedMinutes / 60.0) * 100.0).roundToInt() / 100.0
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("calculated_hours_banner")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = strings.totalTimeCalculatedLabel,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                                        )
                                        if (isOvernight) {
                                            Surface(
                                                shape = RoundedCornerShape(50),
                                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                                            ) {
                                                Text(
                                                    text = strings.overnightBadge,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        text = formatDurationHoursMinutes(elapsedMinutes, language),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)
                                ) {
                                    Text(
                                        text = "${formatNumber(decimalHours)} ${unitLabel(WorkUnit.HOURS, language)}",
                                        fontFamily = OutfitFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    activePicker?.let { target ->
        val defaultMin = when (target) {
            TimePickerTarget.START -> startTimeMinutes ?: currentTimeMinutes()
            TimePickerTarget.END -> endTimeMinutes ?: currentTimeMinutes()
        }
        val pickerState = rememberTimePickerState(
            initialHour = (defaultMin / 60).coerceIn(0, 23),
            initialMinute = (defaultMin % 60).coerceIn(0, 59),
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { activePicker = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (target == TimePickerTarget.START) strings.startTimeLabel else strings.endTimeLabel,
                        style = MaterialTheme.typography.titleLarge
                    )
                    TextButton(
                        onClick = {
                            val now = currentTimeMinutes()
                            if (target == TimePickerTarget.START) {
                                notifyChange(now, endTimeMinutes, breakMinutes)
                            } else {
                                notifyChange(startTimeMinutes, now, breakMinutes)
                            }
                            activePicker = null
                        },
                        modifier = Modifier.testTag("time_picker_now_button")
                    ) {
                        Text(strings.nowButton)
                    }
                }
            },
            text = {
                TimePicker(state = pickerState)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val pickedMinutes = pickerState.hour * 60 + pickerState.minute
                        if (target == TimePickerTarget.START) {
                            notifyChange(pickedMinutes, endTimeMinutes, breakMinutes)
                        } else {
                            notifyChange(startTimeMinutes, pickedMinutes, breakMinutes)
                        }
                        activePicker = null
                    },
                    modifier = Modifier.testTag("time_picker_ok_button")
                ) {
                    Text(strings.okButton)
                }
            },
            dismissButton = {
                TextButton(onClick = { activePicker = null }) {
                    Text(strings.cancelButton)
                }
            }
        )
    }
}

@Composable
private fun TimeSlotTile(
    label: String,
    timeText: String,
    isSelected: Boolean,
    icon: ImageVector,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) accent else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Text(
                text = timeText,
                fontFamily = if (isSelected) OutfitFontFamily else MaterialTheme.typography.titleMedium.fontFamily,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = if (isSelected) 18.sp else 14.sp,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
