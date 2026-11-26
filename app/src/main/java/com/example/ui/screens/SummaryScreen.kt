package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableView
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.components.AvatarCircleBadge
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.implementColor
import com.example.ui.components.implementIcon
import com.example.ui.i18n.UiStrings
import com.example.ui.i18n.formatNumber
import com.example.ui.i18n.formatRupees
import com.example.ui.i18n.implementLabel
import com.example.ui.theme.DieselGold
import com.example.ui.theme.OutfitFontFamily
import com.example.ui.theme.PaidGreen
import com.example.ui.theme.PaidGreenBg
import com.example.ui.theme.PendingRed
import com.example.ui.theme.PendingRedBg
import com.example.ui.viewmodel.DriverSummary
import com.example.ui.viewmodel.FarmerSummary
import com.example.ui.viewmodel.ImplementSummary
import com.example.ui.viewmodel.TractorUiState

@Composable
fun SummaryScreen(
    uiState: TractorUiState,
    strings: UiStrings,
    onMarkFarmerAllPaid: (String) -> Unit,
    onShareFarmerSummary: (FarmerSummary) -> Unit,
    onExportBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onExportCsv: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSection by rememberSaveable { mutableIntStateOf(0) }
    val tabs = listOf(strings.byImplementTab, strings.byDriverTab, strings.byFarmerTab)

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
            // Financial & Fuel Overview strip
            item(key = "overview_tiles") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OverviewStatCard(
                        label = strings.totalBilledLabel,
                        value = formatRupees(uiState.totalBilledAmount),
                        accent = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    OverviewStatCard(
                        label = strings.pendingLabel,
                        value = formatRupees(uiState.totalPendingAmount),
                        accent = PendingRed,
                        modifier = Modifier.weight(1f)
                    )
                    OverviewStatCard(
                        label = strings.totalDieselLabel,
                        value = "${formatNumber(uiState.totalDieselLitres)} ${strings.litresShort}",
                        accent = DieselGold,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item(key = "summary_segment") {
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        SegmentedButton(
                            selected = selectedSection == index,
                            onClick = { selectedSection = index },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size),
                            modifier = Modifier.testTag("summary_tab_$index")
                        ) {
                            Text(
                                text = title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            val isEmpty = when (selectedSection) {
                0 -> uiState.implementSummaries.isEmpty()
                1 -> uiState.driverSummaries.isEmpty()
                else -> uiState.farmerSummaries.isEmpty()
            }

            if (isEmpty) {
                item(key = "empty_summary") {
                    EmptyStateCard(
                        title = strings.emptySummaryTitle,
                        subtitle = strings.emptySummarySubtitle,
                        showIllustration = false
                    )
                }
            } else {
                when (selectedSection) {
                    0 -> {
                        val maxJobs = uiState.implementSummaries.maxOfOrNull { it.jobCount }?.coerceAtLeast(1) ?: 1
                        items(
                            items = uiState.implementSummaries,
                            key = { it.implement.key }
                        ) { summary ->
                            ImplementSummaryCard(
                                summary = summary,
                                maxJobs = maxJobs,
                                strings = strings,
                                language = uiState.language
                            )
                        }
                    }

                    1 -> {
                        items(
                            items = uiState.driverSummaries,
                            key = { it.driverName }
                        ) { summary ->
                            DriverSummaryCard(
                                summary = summary,
                                strings = strings,
                                language = uiState.language
                            )
                        }
                    }

                    else -> {
                        items(
                            items = uiState.farmerSummaries,
                            key = { it.farmerName }
                        ) { summary ->
                            FarmerSummaryCard(
                                summary = summary,
                                strings = strings,
                                language = uiState.language,
                                onMarkAllPaid = { onMarkFarmerAllPaid(summary.farmerName) },
                                onShare = { onShareFarmerSummary(summary) }
                            )
                        }
                    }
                }
            }

            item(key = "offline_backup_card") {
                OfflineBackupCard(
                    strings = strings,
                    hasData = uiState.allWorkLogs.isNotEmpty() || uiState.standaloneDieselLogs.isNotEmpty(),
                    onExportBackup = onExportBackup,
                    onRestoreBackup = onRestoreBackup,
                    onExportCsv = onExportCsv
                )
            }
        }
    }
}

@Composable
private fun OfflineBackupCard(
    strings: UiStrings,
    hasData: Boolean,
    onExportBackup: () -> Unit,
    onRestoreBackup: () -> Unit,
    onExportCsv: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
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
                    imageVector = Icons.Filled.OfflinePin,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
                Text(
                    text = strings.offlineBackupTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = strings.offlineBackupSubtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalButton(
                    onClick = onExportBackup,
                    enabled = hasData,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("export_backup_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Download,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = strings.exportBackupButton,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = onRestoreBackup,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("restore_backup_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.UploadFile,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = strings.restoreBackupButton,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            OutlinedButton(
                onClick = onExportCsv,
                enabled = hasData,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("export_csv_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.TableView,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(strings.exportCsvButton)
            }
        }
    }
}

@Composable
private fun OverviewStatCard(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = value,
                fontFamily = OutfitFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = accent,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ImplementSummaryCard(
    summary: ImplementSummary,
    maxJobs: Int,
    strings: UiStrings,
    language: AppLanguage
) {
    val accent = implementColor(summary.implement)
    val fraction = (summary.jobCount.toFloat() / maxJobs.toFloat()).coerceIn(0.08f, 1f)

    val workBreakdown = buildList {
        if (summary.totalHours > 0) add("${formatNumber(summary.totalHours)} ${if (language == AppLanguage.MARATHI) "तास" else "Hrs"}")
        if (summary.totalTrips > 0) add("${formatNumber(summary.totalTrips)} ${if (language == AppLanguage.MARATHI) "फेऱ्या" else "Trips"}")
        if (summary.totalAcres > 0) add("${formatNumber(summary.totalAcres)} ${if (language == AppLanguage.MARATHI) "एकर" else "Acres"}")
        if (summary.totalGuntha > 0) add("${formatNumber(summary.totalGuntha)} ${if (language == AppLanguage.MARATHI) "गुंठे" else "Guntha"}")
    }.joinToString(" • ")

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = accent.copy(alpha = 0.14f)
                    ) {
                        Icon(
                            imageVector = implementIcon(summary.implement),
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier
                                .padding(10.dp)
                                .size(22.dp)
                        )
                    }
                    Column {
                        Text(
                            text = implementLabel(summary.implement, language),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "${summary.jobCount} ${strings.jobsCountSuffix}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (summary.totalAmount > 0) {
                    Text(
                        text = formatRupees(summary.totalAmount),
                        fontFamily = OutfitFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (workBreakdown.isNotBlank()) {
                Text(
                    text = workBreakdown,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = accent
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(50))
                        .background(accent)
                )
            }
        }
    }
}

@Composable
private fun DriverSummaryCard(
    summary: DriverSummary,
    strings: UiStrings,
    language: AppLanguage
) {
    val workParts = buildList {
        if (summary.totalHours > 0) add("${formatNumber(summary.totalHours)} ${if (language == AppLanguage.MARATHI) "तास" else "Hrs"}")
        if (summary.totalTrips > 0) add("${formatNumber(summary.totalTrips)} ${if (language == AppLanguage.MARATHI) "फेऱ्या" else "Trips"}")
    }.joinToString(" • ")

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                AvatarCircleBadge(
                    initial = summary.driverName,
                    accent = MaterialTheme.colorScheme.primary
                )
                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                    Text(
                        text = summary.driverName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = buildString {
                            append("${summary.jobCount} ${strings.jobsCountSuffix}")
                            if (workParts.isNotBlank()) append(" • $workParts")
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (summary.totalDieselLitres > 0) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = DieselGold.copy(alpha = 0.14f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.LocalGasStation,
                            contentDescription = null,
                            tint = DieselGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${formatNumber(summary.totalDieselLitres)} ${strings.litresShort}",
                            fontFamily = OutfitFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = DieselGold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FarmerSummaryCard(
    summary: FarmerSummary,
    strings: UiStrings,
    language: AppLanguage,
    onMarkAllPaid: () -> Unit,
    onShare: () -> Unit
) {
    val workParts = buildList {
        if (summary.totalHours > 0) add("${formatNumber(summary.totalHours)} ${if (language == AppLanguage.MARATHI) "तास" else "Hrs"}")
        if (summary.totalTrips > 0) add("${formatNumber(summary.totalTrips)} ${if (language == AppLanguage.MARATHI) "फेऱ्या" else "Trips"}")
        if (summary.totalAcres > 0) add("${formatNumber(summary.totalAcres)} ${if (language == AppLanguage.MARATHI) "एकर" else "Acres"}")
    }.joinToString(" • ")

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    AvatarCircleBadge(
                        initial = summary.farmerName,
                        accent = MaterialTheme.colorScheme.secondary
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = summary.farmerName,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (summary.locations.isNotEmpty()) {
                            Text(
                                text = summary.locations.joinToString(", "),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "${summary.jobCount} ${strings.jobsCountSuffix}" +
                                if (workParts.isNotBlank()) " • $workParts" else "",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                IconButton(
                    onClick = onShare,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("share_farmer_${summary.farmerName}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = strings.shareButton,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            if (summary.totalAmount > 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${strings.totalBilledLabel}: ${formatRupees(summary.totalAmount)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (summary.pendingAmount > 0) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = PendingRedBg,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Text(
                                    text = "${strings.pendingLabel}: ${formatRupees(summary.pendingAmount)}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = PendingRed,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = PaidGreenBg,
                                modifier = Modifier.padding(top = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.CheckCircle,
                                        contentDescription = null,
                                        tint = PaidGreen,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = strings.paidLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PaidGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    if (summary.pendingAmount > 0) {
                        FilledTonalButton(
                            onClick = onMarkAllPaid,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = PaidGreenBg,
                                contentColor = PaidGreen
                            ),
                            modifier = Modifier.testTag("mark_farmer_paid_${summary.farmerName}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.size(6.dp))
                            Text(strings.markAllPaid)
                        }
                    }
                }
            }
        }
    }
}
