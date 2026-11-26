package com.example.ui

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.LocalGasStation
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DieselLogEntity
import com.example.data.model.WorkLogEntity
import com.example.ui.i18n.buildFarmerSummaryShareText
import com.example.ui.i18n.buildWorkLogShareText
import com.example.ui.i18n.stringsFor
import com.example.ui.screens.DieselEditorDialog
import com.example.ui.screens.DieselScreen
import com.example.ui.screens.SummaryScreen
import com.example.ui.screens.WorkLogEditorScreen
import com.example.ui.screens.WorkLogsScreen
import com.example.ui.viewmodel.TractorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class MainTab {
    LOGS,
    DIESEL,
    SUMMARY
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TractorNondApp(
    viewModel: TractorViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val strings = remember(uiState.language) { stringsFor(uiState.language) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var currentTab by rememberSaveable { mutableStateOf(MainTab.LOGS) }
    var isWorkEditorOpen by rememberSaveable { mutableStateOf(false) }
    var editingWorkLogId by rememberSaveable { mutableStateOf<Long?>(null) }

    var isDieselDialogOpen by remember { mutableStateOf(false) }
    var editingDieselLog by remember { mutableStateOf<DieselLogEntity?>(null) }

    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val ok = withContext(Dispatchers.IO) {
                    runCatching {
                        val json = viewModel.buildBackupJson()
                        context.contentResolver.openOutputStream(uri)?.use { out ->
                            out.write(json.toByteArray(Charsets.UTF_8))
                        } ?: error("Cannot open stream")
                    }.isSuccess
                }
                snackbarHostState.showSnackbar(
                    if (ok) strings.backupSavedSnackbar else strings.backupErrorSnackbar
                )
            }
        }
    }

    val exportCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val ok = withContext(Dispatchers.IO) {
                    runCatching {
                        val csv = viewModel.buildCsvReport()
                        context.contentResolver.openOutputStream(uri)?.use { out ->
                            out.write(csv.toByteArray(Charsets.UTF_8))
                        } ?: error("Cannot open stream")
                    }.isSuccess
                }
                snackbarHostState.showSnackbar(
                    if (ok) strings.csvSavedSnackbar else strings.backupErrorSnackbar
                )
            }
        }
    }

    val restoreBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                val jsonText = withContext(Dispatchers.IO) {
                    runCatching {
                        context.contentResolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }
                    }.getOrNull()
                }
                if (jsonText != null) {
                    viewModel.importBackupJson(jsonText) { success, count ->
                        scope.launch {
                            val msg = if (success) {
                                "${strings.backupRestoredSnackbar} (+$count)"
                            } else {
                                strings.backupErrorSnackbar
                            }
                            snackbarHostState.showSnackbar(msg)
                        }
                    }
                } else {
                    snackbarHostState.showSnackbar(strings.backupErrorSnackbar)
                }
            }
        }
    }

    val editingWorkLog: WorkLogEntity? = remember(editingWorkLogId, uiState.allWorkLogs) {
        editingWorkLogId?.let { id -> uiState.allWorkLogs.firstOrNull { it.id == id } }
    }

    if (isWorkEditorOpen) {
        WorkLogEditorScreen(
            existingLog = editingWorkLog,
            strings = strings,
            language = uiState.language,
            recentFarmers = uiState.recentFarmers,
            recentLocations = uiState.recentLocations,
            recentDrivers = uiState.recentDrivers,
            onSave = { log ->
                viewModel.saveWorkLog(log)
                isWorkEditorOpen = false
                editingWorkLogId = null
                scope.launch { snackbarHostState.showSnackbar(strings.savedSnackbar) }
            },
            onDelete = { id ->
                viewModel.deleteWorkLog(id)
                isWorkEditorOpen = false
                editingWorkLogId = null
                scope.launch { snackbarHostState.showSnackbar(strings.deletedSnackbar) }
            },
            onBack = {
                isWorkEditorOpen = false
                editingWorkLogId = null
            }
        )
        return
    }

    val navItems = listOf(
        NavDestination(MainTab.LOGS, strings.navLogs, Icons.Filled.Agriculture, Icons.Outlined.Agriculture),
        NavDestination(MainTab.DIESEL, strings.navDiesel, Icons.Filled.LocalGasStation, Icons.Outlined.LocalGasStation),
        NavDestination(MainTab.SUMMARY, strings.navSummary, Icons.Filled.Assessment, Icons.Outlined.Assessment)
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useNavigationRail = maxWidth >= 600.dp

        Row(modifier = Modifier.fillMaxSize()) {
            if (useNavigationRail) {
                NavigationRail(
                    containerColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    Spacer(modifier = Modifier.size(16.dp))
                    navItems.forEach { item ->
                        val selected = currentTab == item.tab
                        NavigationRailItem(
                            selected = selected,
                            onClick = { currentTab = item.tab },
                            icon = {
                                Icon(
                                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            modifier = Modifier.testTag("nav_${item.tab.name.lowercase()}")
                        )
                    }
                }
            }

            Scaffold(
                modifier = Modifier.weight(1f),
                containerColor = MaterialTheme.colorScheme.background,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    TopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Agriculture,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(26.dp)
                                )
                                Text(
                                    text = strings.appTitle,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        },
                        actions = {
                            FilledTonalButton(
                                onClick = { viewModel.toggleLanguage() },
                                shape = RoundedCornerShape(50),
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .testTag("language_toggle_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Translate,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.size(6.dp))
                                Text(
                                    text = strings.langToggleLabel,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        )
                    )
                },
                bottomBar = {
                    if (!useNavigationRail) {
                        NavigationBar(
                            containerColor = MaterialTheme.colorScheme.surface,
                            tonalElevation = 3.dp
                        ) {
                            navItems.forEach { item ->
                                val selected = currentTab == item.tab
                                NavigationBarItem(
                                    selected = selected,
                                    onClick = { currentTab = item.tab },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.label
                                        )
                                    },
                                    label = { Text(item.label) },
                                    modifier = Modifier.testTag("nav_${item.tab.name.lowercase()}")
                                )
                            }
                        }
                    }
                },
                floatingActionButton = {
                    if (currentTab == MainTab.LOGS || currentTab == MainTab.DIESEL) {
                        val isDieselTab = currentTab == MainTab.DIESEL
                        ExtendedFloatingActionButton(
                            onClick = {
                                if (isDieselTab) {
                                    editingDieselLog = null
                                    isDieselDialogOpen = true
                                } else {
                                    editingWorkLogId = null
                                    isWorkEditorOpen = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = if (isDieselTab) Icons.Filled.LocalGasStation else Icons.Filled.Add,
                                    contentDescription = null
                                )
                            },
                            text = {
                                Text(
                                    text = if (isDieselTab) strings.addDieselLog else strings.addWorkLog,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            },
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.testTag(if (isDieselTab) "fab_add_diesel" else "fab_add_work")
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentTab,
                        label = "main_tab_transition"
                    ) { tab ->
                        when (tab) {
                            MainTab.LOGS -> {
                                WorkLogsScreen(
                                    uiState = uiState,
                                    strings = strings,
                                    onSearchChange = viewModel::setSearchQuery,
                                    onImplementFilterChange = viewModel::setImplementFilter,
                                    onDateFilterChange = viewModel::setDateFilter,
                                    onAddNewClick = {
                                        editingWorkLogId = null
                                        isWorkEditorOpen = true
                                    },
                                    onEditWorkLog = { log ->
                                        editingWorkLogId = log.id
                                        isWorkEditorOpen = true
                                    },
                                    onTogglePaid = { log ->
                                        viewModel.toggleWorkLogPaid(log)
                                        scope.launch { snackbarHostState.showSnackbar(strings.paidUpdatedSnackbar) }
                                    },
                                    onShareWorkLog = { log ->
                                        sharePlainText(
                                            context = context,
                                            title = strings.shareReportTitle,
                                            body = buildWorkLogShareText(log, uiState.language)
                                        )
                                    }
                                )
                            }

                            MainTab.DIESEL -> {
                                DieselScreen(
                                    uiState = uiState,
                                    strings = strings,
                                    onAddDieselClick = {
                                        editingDieselLog = null
                                        isDieselDialogOpen = true
                                    },
                                    onEditDieselLog = { diesel ->
                                        editingDieselLog = diesel
                                        isDieselDialogOpen = true
                                    },
                                    onDeleteDieselLog = { id ->
                                        viewModel.deleteDieselLog(id)
                                        scope.launch { snackbarHostState.showSnackbar(strings.deletedSnackbar) }
                                    },
                                    onOpenSourceWorkLog = { workId ->
                                        editingWorkLogId = workId
                                        isWorkEditorOpen = true
                                    }
                                )
                            }

                            MainTab.SUMMARY -> {
                                SummaryScreen(
                                    uiState = uiState,
                                    strings = strings,
                                    onMarkFarmerAllPaid = { farmerName ->
                                        viewModel.markFarmerAllPaid(farmerName)
                                        scope.launch { snackbarHostState.showSnackbar(strings.paidUpdatedSnackbar) }
                                    },
                                    onShareFarmerSummary = { farmer ->
                                        sharePlainText(
                                            context = context,
                                            title = strings.shareReportTitle,
                                            body = buildFarmerSummaryShareText(
                                                farmerName = farmer.farmerName,
                                                logs = farmer.logs,
                                                totalAmount = farmer.totalAmount,
                                                pendingAmount = farmer.pendingAmount,
                                                language = uiState.language
                                            )
                                        )
                                    },
                                    onExportBackup = {
                                        val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH).format(Date())
                                        exportBackupLauncher.launch("advik_awajare_bank_backup_$stamp.json")
                                    },
                                    onRestoreBackup = {
                                        restoreBackupLauncher.launch(arrayOf("application/json", "*/*"))
                                    },
                                    onExportCsv = {
                                        val stamp = SimpleDateFormat("yyyyMMdd_HHmm", Locale.ENGLISH).format(Date())
                                        exportCsvLauncher.launch("advik_awajare_bank_report_$stamp.csv")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (isDieselDialogOpen) {
        DieselEditorDialog(
            existingLog = editingDieselLog,
            strings = strings,
            language = uiState.language,
            recentDrivers = uiState.recentDrivers,
            onSave = { diesel ->
                viewModel.saveDieselLog(diesel)
                isDieselDialogOpen = false
                editingDieselLog = null
                scope.launch { snackbarHostState.showSnackbar(strings.savedSnackbar) }
            },
            onDismiss = {
                isDieselDialogOpen = false
                editingDieselLog = null
            }
        )
    }
}

private data class NavDestination(
    val tab: MainTab,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

private fun sharePlainText(context: Context, title: String, body: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    context.startActivity(Intent.createChooser(intent, title))
}
