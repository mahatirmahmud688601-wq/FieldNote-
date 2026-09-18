package com.example

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.FieldnoteScreen
import com.example.ui.FieldnoteViewModel
import com.example.ui.components.FieldnoteBottomNavigation
import com.example.ui.components.FieldnoteTopBar
import com.example.ui.screens.CalendarTimelineScreen
import com.example.ui.screens.CopilotDrawer
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.NoteEditorScreen
import com.example.ui.screens.NotesGalleryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.ShowcaseGridScreen
import com.example.ui.screens.TaskSheetModal
import com.example.ui.screens.TasksChecklistScreen
import com.example.ui.screens.FocusModeScreen
import com.example.ui.screens.HabitsRoutinesScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.PrivacyVaultScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.components.NaturalLanguageConfirmDialog
import com.example.ui.theme.FieldnoteAppTheme
import com.example.ui.theme.FieldnoteTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as? FieldnoteApplication
        setContent {
            val viewModel: FieldnoteViewModel = viewModel(
                factory = FieldnoteViewModel.provideFactory(application)
            )
            val state by viewModel.uiState.collectAsState()

            FieldnoteAppTheme(darkTheme = state.isDarkTheme) {
                if (app != null) {
                    androidx.compose.runtime.CompositionLocalProvider(com.example.di.LocalAppContainer provides app.container) {
                        MainAppContent(viewModel = viewModel)
                    }
                } else {
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: FieldnoteViewModel) {
    val state by viewModel.uiState.collectAsState()
    val colors = FieldnoteTheme.colors

    // Speech-to-text voice recognition launcher
    val speechRecognizerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.sendVoiceText(spokenText)
            }
        }
    }

    fun startVoiceRecognition() {
        try {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak scheduling command or note...")
            }
            speechRecognizerLauncher.launch(intent)
        } catch (e: Exception) {
            // If device doesn't support direct speech intent, run simulation command
            viewModel.triggerVoiceInputSimulation()
        }
    }

    // Handle Toast alerts
    LaunchedEffect(state.toastMessage) {
        state.toastMessage?.let { msg ->
            kotlinx.coroutines.delay(2200)
            viewModel.clearToast()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        Scaffold(
            topBar = {
                if (!state.isNoteEditorOpen) {
                    FieldnoteTopBar(
                        currentScreen = state.currentScreen,
                        isDarkTheme = state.isDarkTheme,
                        onToggleDarkTheme = { viewModel.toggleDarkTheme() },
                        onToggleCopilot = { viewModel.toggleCopilot() },
                        onToggleShowcase = {
                            if (state.currentScreen == FieldnoteScreen.SHOWCASE) {
                                viewModel.setScreen(FieldnoteScreen.DASHBOARD)
                            } else {
                                viewModel.setScreen(FieldnoteScreen.SHOWCASE)
                            }
                        }
                    )
                }
            },
            bottomBar = {
                if (!state.isNoteEditorOpen) {
                    FieldnoteBottomNavigation(
                        currentScreen = state.currentScreen,
                        onScreenSelected = { viewModel.setScreen(it) },
                        onFabClick = { viewModel.openTaskSheet(null) }
                    )
                }
            },
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            containerColor = colors.bg
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (state.currentScreen) {
                    FieldnoteScreen.DASHBOARD -> DashboardScreen(
                        state = state,
                        onNavigate = { viewModel.setScreen(it) },
                        onOpenNote = { viewModel.openNoteEditor(it) },
                        onOpenTask = { viewModel.openTaskSheet(it) },
                        onCycleTaskStatus = { viewModel.cycleTaskStatus(it) },
                        onToggleCopilot = { viewModel.toggleCopilot(true) }
                    )

                    FieldnoteScreen.NOTES -> NotesGalleryScreen(
                        state = state,
                        onOpenNote = { viewModel.openNoteEditor(it) },
                        onTogglePin = { viewModel.togglePinNote(it) },
                        onToggleArchive = { viewModel.toggleArchiveNote(it) },
                        onDeleteNote = { viewModel.deleteNote(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onTagFilterChange = { viewModel.setSelectedTagFilter(it) },
                        onSortChange = { viewModel.setNotesSortBy(it) },
                        onToggleShowArchived = { viewModel.toggleShowArchivedNotes() }
                    )

                    FieldnoteScreen.TASKS -> TasksChecklistScreen(
                        state = state,
                        onOpenTask = { viewModel.openTaskSheet(it) },
                        onCycleStatus = { viewModel.cycleTaskStatus(it) },
                        onDeleteTask = { viewModel.deleteTask(it) },
                        onSyncToCalendar = { viewModel.syncTaskToCalendar(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onStatusFilterChange = { viewModel.setTaskStatusFilter(it) },
                        onPriorityFilterChange = { viewModel.setTaskPriorityFilter(it) },
                        onSortChange = { viewModel.setTasksSortBy(it) }
                    )

                    FieldnoteScreen.CALENDAR -> CalendarTimelineScreen(
                        state = state,
                        onOpenTask = { viewModel.openTaskSheet(it) },
                        onCycleStatus = { viewModel.cycleTaskStatus(it) }
                    )

                    FieldnoteScreen.FOCUS -> FocusModeScreen(
                        state = state,
                        onStartFocus = { viewModel.startFocusSession(it) },
                        onCompleteFocus = { title, mins, mode -> viewModel.completeFocusSession(title, mins, mode) },
                        onCancelFocus = { viewModel.cancelFocusTimer() }
                    )

                    FieldnoteScreen.HABITS -> HabitsRoutinesScreen(
                        state = state,
                        onToggleHabit = { viewModel.toggleHabit(it) },
                        onAddHabit = { title, cat, freq, days, color -> viewModel.addHabit(title, cat, freq, days, color) },
                        onDeleteHabit = { viewModel.deleteHabit(it) }
                    )

                    FieldnoteScreen.ANALYTICS -> AnalyticsScreen(
                        state = state
                    )

                    FieldnoteScreen.VAULT -> PrivacyVaultScreen(
                        state = state,
                        onUnlock = { pass -> viewModel.unlockVault(pass) },
                        onLock = { viewModel.lockVault() },
                        onOpenNote = { viewModel.openNoteEditor(it) }
                    )

                    FieldnoteScreen.ONBOARDING -> OnboardingScreen(
                        onComplete = { viewModel.finishOnboarding() }
                    )

                    FieldnoteScreen.SETTINGS -> SettingsScreen(
                        state = state,
                        onToggleDarkTheme = { viewModel.toggleDarkTheme(it) },
                        onSetDefaultPriority = { viewModel.setDefaultPriority(it) },
                        onToggleNotifications = { viewModel.toggleNotifications(it) },
                        onTriggerTestNotification = { viewModel.triggerTestNotification() },
                        onExportData = { onResult -> viewModel.exportData(onResult) },
                        onImportData = { json -> viewModel.importData(json) },
                        onClearAllData = { viewModel.clearAllData() },
                        onUpdatePassphrase = { pass -> viewModel.setMasterPassphrase(pass) }
                    )

                    FieldnoteScreen.SHOWCASE -> ShowcaseGridScreen(
                        state = state,
                        onNavigate = { viewModel.setScreen(it) },
                        onOpenCopilot = { viewModel.toggleCopilot(true) },
                        onOpenNoteEditor = { viewModel.openNoteEditor(null) },
                        onOpenTaskSheet = { viewModel.openTaskSheet(null) }
                    )
                }
            }
        }

        // Natural Language Confirmation Dialog
        if (state.isNlConfirmOpen && state.parsedNlTask != null) {
            NaturalLanguageConfirmDialog(
                parsedResult = state.parsedNlTask!!,
                onConfirm = { syncCal -> viewModel.confirmParsedTask(syncCal) },
                onDismiss = { viewModel.dismissParsedTask() }
            )
        }

        // Fullscreen Note Editor Overlay
        if (state.isNoteEditorOpen) {
            NoteEditorScreen(
                initialNote = state.activeEditingNote,
                onSave = { viewModel.saveNote(it) },
                onDelete = { viewModel.deleteNote(it) },
                onClose = { viewModel.closeNoteEditor() }
            )
        }

        // Task Bottom Sheet Modal
        if (state.isTaskSheetOpen) {
            TaskSheetModal(
                task = state.activeEditingTask,
                defaultPriority = state.defaultPriority,
                onSave = { task, syncCal -> viewModel.saveTask(task, syncCal) },
                onDismiss = { viewModel.closeTaskSheet() }
            )
        }

        // Gemini AI Copilot Slide-Over Drawer
        CopilotDrawer(
            state = state,
            onSendMessage = { viewModel.sendCopilotMessage(it) },
            onExecuteAction = { viewModel.executeCopilotAction(it) },
            onTriggerVoiceInput = { startVoiceRecognition() },
            onClose = { viewModel.toggleCopilot(false) }
        )

        // Floating Toast Notification Banner
        state.toastMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.accent)
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                ) {
                    Text(
                        text = msg,
                        fontSize = 12.5.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
