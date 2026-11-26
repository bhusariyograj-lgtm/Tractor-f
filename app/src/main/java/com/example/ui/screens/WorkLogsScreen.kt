package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.model.DateFilter
import com.example.data.model.ImplementType
import com.example.data.model.WorkLogEntity
import com.example.ui.components.EmptyStateCard
import com.example.ui.components.TractorHeroBanner
import com.example.ui.components.WorkLogCard
import com.example.ui.components.implementColor
import com.example.ui.components.implementIcon
import com.example.ui.i18n.UiStrings
import com.example.ui.i18n.dateFilterLabel
import com.example.ui.i18n.implementLabel
import com.example.ui.viewmodel.TractorUiState

@Composable
fun WorkLogsScreen(
    uiState: TractorUiState,
    strings: UiStrings,
    onSearchChange: (String) -> Unit,
    onImplementFilterChange: (ImplementType?) -> Unit,
    onDateFilterChange: (DateFilter) -> Unit,
    onAddNewClick: () -> Unit,
    onEditWorkLog: (WorkLogEntity) -> Unit,
    onTogglePaid: (WorkLogEntity) -> Unit,
    onShareWorkLog: (WorkLogEntity) -> Unit,
    modifier: Modifier = Modifier
) {
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
            item(key = "hero_banner") {
                TractorHeroBanner(
                    totalHours = uiState.totalHours,
                    totalTrips = uiState.totalTrips,
                    totalAcres = uiState.totalAcres,
                    totalDieselLitres = uiState.totalDieselLitres,
                    totalPendingAmount = uiState.totalPendingAmount,
                    strings = strings,
                    language = uiState.language
                )
            }

            item(key = "search_and_filters") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchChange,
                        placeholder = {
                            Text(
                                text = strings.searchPlaceholder,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        trailingIcon = {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchChange("") },
                                    modifier = Modifier.testTag("clear_search_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = strings.cancelButton
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_logs_input")
                    )

                    // Date filter row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(DateFilter.entries) { filter ->
                            val selected = uiState.selectedDateFilter == filter
                            FilterChip(
                                selected = selected,
                                onClick = { onDateFilterChange(filter) },
                                label = {
                                    Text(
                                        text = dateFilterLabel(filter, strings),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("date_filter_${filter.key.lowercase()}")
                            )
                        }
                    }

                    // Implement filter row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = uiState.selectedImplement == null,
                                onClick = { onImplementFilterChange(null) },
                                label = {
                                    Text(
                                        text = strings.allFilter,
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                modifier = Modifier.testTag("implement_filter_all")
                            )
                        }
                        items(ImplementType.entries) { impl ->
                            val selected = uiState.selectedImplement == impl
                            val accent = implementColor(impl)
                            FilterChip(
                                selected = selected,
                                onClick = {
                                    onImplementFilterChange(if (selected) null else impl)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = implementIcon(impl),
                                        contentDescription = null,
                                        tint = if (selected) MaterialTheme.colorScheme.onPrimary else accent,
                                        modifier = Modifier.size(17.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = implementLabel(impl, uiState.language),
                                        style = MaterialTheme.typography.labelLarge
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = accent,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.testTag("implement_filter_${impl.key.lowercase()}")
                            )
                        }
                    }
                }
            }

            if (uiState.allWorkLogs.isEmpty()) {
                item(key = "empty_all") {
                    EmptyStateCard(
                        title = strings.emptyLogsTitle,
                        subtitle = strings.emptyLogsSubtitle,
                        actionLabel = strings.emptyLogsAction,
                        onAction = onAddNewClick,
                        showIllustration = true
                    )
                }
            } else if (uiState.filteredWorkLogs.isEmpty()) {
                item(key = "empty_filtered") {
                    EmptyStateCard(
                        title = strings.emptyFilteredTitle,
                        subtitle = strings.emptyFilteredSubtitle,
                        showIllustration = false
                    )
                }
            } else {
                items(
                    items = uiState.filteredWorkLogs,
                    key = { it.id }
                ) { log ->
                    WorkLogCard(
                        log = log,
                        strings = strings,
                        language = uiState.language,
                        onClick = { onEditWorkLog(log) },
                        onTogglePaid = { onTogglePaid(log) },
                        onShare = { onShareWorkLog(log) }
                    )
                }
            }
        }
    }
}
