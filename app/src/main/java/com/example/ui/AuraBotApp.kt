package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.FileDocument
import com.example.ui.components.AuraBotDrawer
import com.example.ui.components.AuraBotTopBar
import com.example.ui.components.VoiceRecorderModal
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ExploreToolsScreen
import com.example.ui.screens.FilesScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.AuraBotTheme
import kotlinx.coroutines.launch

@Composable
fun AuraBotApp(
    viewModel: AuraBotViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val conversations by viewModel.conversations.collectAsState()
    val currentConversationId by viewModel.currentConversationId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val messages by viewModel.currentMessages.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val files by viewModel.files.collectAsState()
    val usageMetrics by viewModel.usageMetrics.collectAsState()
    val systemHealth by viewModel.systemHealth.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    val inputText by viewModel.inputText.collectAsState()
    val attachedFile by viewModel.attachedFile.collectAsState()
    val isStreaming by viewModel.isStreaming.collectAsState()
    val isVoiceModalVisible by viewModel.isVoiceModalVisible.collectAsState()
    val ttsPlayingMessageId by viewModel.ttsPlayingMessageId.collectAsState()

    val selectedLanguage by viewModel.selectedLanguage.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val isMemoryOptedIn by viewModel.isMemoryOptedIn.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showFilePickerDialog by remember { mutableStateOf(false) }

    // Back button handling: Close drawer if open, or return to Welcome Screen if on another screen
    BackHandler(enabled = drawerState.isOpen || currentScreen != AuraScreen.WELCOME) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else if (currentScreen != AuraScreen.WELCOME) {
            viewModel.navigateTo(AuraScreen.WELCOME)
        }
    }

    AuraBotTheme(darkTheme = isDarkMode) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AuraBotDrawer(
                    currentScreen = currentScreen,
                    conversations = conversations,
                    activeConversationId = currentConversationId,
                    searchQuery = searchQuery,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onScreenSelect = { screen ->
                        viewModel.navigateTo(screen)
                        scope.launch { drawerState.close() }
                    },
                    onConversationSelect = { id ->
                        viewModel.selectConversation(id)
                        scope.launch { drawerState.close() }
                    },
                    onNewChatClick = {
                        viewModel.startNewChat()
                        scope.launch { drawerState.close() }
                    },
                    onDeleteConversation = { id ->
                        viewModel.deleteConversation(id)
                    }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    AuraBotTopBar(
                        currentScreen = currentScreen,
                        selectedModel = selectedModel,
                        onModelSelect = { viewModel.selectModel(it) },
                        onMenuClick = {
                            scope.launch { drawerState.open() }
                        },
                        onNewChatClick = { viewModel.startNewChat() },
                        onClearChatClick = {
                            currentConversationId?.let { viewModel.deleteConversation(it) }
                        }
                    )
                },
                modifier = modifier.fillMaxSize()
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    when (currentScreen) {
                        AuraScreen.WELCOME -> {
                            WelcomeScreen(
                                onStartPrompt = { prompt ->
                                    viewModel.startNewChat(prompt)
                                },
                                selectedModel = selectedModel,
                                selectedLanguage = selectedLanguage,
                                totalQueries = usageMetrics.totalQueries,
                                dailyQuotaLimit = usageMetrics.dailyQuotaLimit,
                                onVoiceClick = { viewModel.showVoiceModal(true) },
                                onAttachFileClick = { showFilePickerDialog = true }
                            )
                        }

                        AuraScreen.CHAT -> {
                            ChatScreen(
                                messages = messages,
                                inputText = inputText,
                                onInputChange = { viewModel.setInputText(it) },
                                onSendMessage = { viewModel.sendMessage() },
                                onStopGeneration = { viewModel.stopGeneration() },
                                onRetryLastMessage = { viewModel.retryLastMessage() },
                                isStreaming = isStreaming,
                                attachedFile = attachedFile,
                                onRemoveAttachedFile = { viewModel.setAttachedFile(null) },
                                onAttachFileClick = { showFilePickerDialog = true },
                                onMicClick = { viewModel.showVoiceModal(true) },
                                ttsPlayingMessageId = ttsPlayingMessageId,
                                onToggleTts = { id, text -> viewModel.toggleTts(id, text) },
                                onRateMessage = { id, isPos -> viewModel.rateMessage(id, isPos) },
                                onConfirmTool = { msgId, toolId -> viewModel.confirmToolAction(msgId, toolId) },
                                selectedModel = selectedModel,
                                selectedLanguage = selectedLanguage,
                                totalQueries = usageMetrics.totalQueries,
                                dailyQuotaLimit = usageMetrics.dailyQuotaLimit
                            )
                        }

                        AuraScreen.EXPLORE_TOOLS -> {
                            ExploreToolsScreen(
                                onLaunchTool = { samplePrompt ->
                                    viewModel.startNewChat(samplePrompt)
                                }
                            )
                        }

                        AuraScreen.FILES -> {
                            FilesScreen(
                                files = files,
                                onUploadFile = { name, type, size, mime, summary ->
                                    viewModel.uploadFile(name, type, size, mime, summary)
                                },
                                onDeleteFile = { id -> viewModel.deleteFile(id) },
                                onAskAboutFile = { file -> viewModel.askAboutFile(file) }
                            )
                        }

                        AuraScreen.MEMORY -> {
                            MemoryScreen(
                                memories = memories,
                                isMemoryOptedIn = isMemoryOptedIn,
                                onToggleOptIn = { viewModel.toggleMemoryOptIn() },
                                onToggleMemory = { id -> viewModel.toggleMemory(id) },
                                onDeleteMemory = { id -> viewModel.deleteMemory(id) },
                                onAddMemory = { text, cat -> viewModel.addMemory(text, cat) },
                                onClearAllMemories = { viewModel.clearAllMemories() }
                            )
                        }

                        AuraScreen.SETTINGS -> {
                            SettingsScreen(
                                selectedLanguage = selectedLanguage,
                                onLanguageSelect = { viewModel.setLanguage(it) },
                                selectedModel = selectedModel,
                                onModelSelect = { viewModel.selectModel(it) },
                                isDarkMode = isDarkMode,
                                onToggleDarkMode = { viewModel.toggleDarkMode() },
                                usageMetrics = usageMetrics,
                                onClearAllData = { viewModel.clearAllData() }
                            )
                        }

                        AuraScreen.ADMIN -> {
                            AdminDashboardScreen(
                                systemHealth = systemHealth,
                                auditLogs = auditLogs
                            )
                        }
                    }
                }
            }
        }

        // Voice Recording Bottom Sheet Modal
        if (isVoiceModalVisible) {
            VoiceRecorderModal(
                selectedLanguage = selectedLanguage,
                onLanguageChange = { viewModel.setLanguage(it) },
                onSpeechRecognized = { transcript ->
                    viewModel.onSpeechRecognized(transcript)
                },
                onDismiss = { viewModel.showVoiceModal(false) }
            )
        }

        // Attach Document Picker Dialog
        if (showFilePickerDialog) {
            AlertDialog(
                onDismissRequest = { showFilePickerDialog = false },
                title = { Text("Select Document to Attach", fontWeight = FontWeight.Bold) },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Choose an indexed document to attach to this prompt:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        files.forEach { file ->
                            Surface(
                                onClick = {
                                    viewModel.setAttachedFile(file)
                                    showFilePickerDialog = false
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = file.fileName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${file.fileType.badge} • ${file.formattedSize}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showFilePickerDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
