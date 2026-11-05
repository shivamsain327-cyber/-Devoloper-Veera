package com.example.ui

import android.app.Application
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Conversation
import com.example.data.model.FeedbackRating
import com.example.data.model.FileDocument
import com.example.data.model.FileType
import com.example.data.model.MemoryCategory
import com.example.data.model.MemoryItem
import com.example.data.model.Message
import com.example.data.model.MessageRole
import com.example.data.model.MessageStatus
import com.example.data.model.ModelTier
import com.example.data.model.ReminderItem
import com.example.data.model.UsageMetrics
import com.example.data.repository.AuraBotRepository
import com.example.domain.ai.AiStreamEvent
import com.example.domain.ai.AuraBotAiEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.UUID

enum class AuraScreen(val title: String) {
    WELCOME("Welcome"),
    CHAT("AuraBot Chat"),
    EXPLORE_TOOLS("Explore Tools"),
    FILES("Files & Docs"),
    MEMORY("Memory Engine"),
    SETTINGS("Settings"),
    ADMIN("Admin & Observability")
}

class AuraBotViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuraBotRepository()
    private val aiEngine = AuraBotAiEngine()

    private val _currentScreen = MutableStateFlow(AuraScreen.WELCOME)
    val currentScreen: StateFlow<AuraScreen> = _currentScreen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val conversations: StateFlow<List<Conversation>> = combine(
        repository.conversations,
        _searchQuery
    ) { list, query ->
        if (query.isBlank()) list
        else list.filter { it.title.contains(query, ignoreCase = true) || it.lastMessagePreview.contains(query, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val currentConversationId: StateFlow<String?> = repository.currentConversationId

    val currentMessages: StateFlow<List<Message>> = combine(
        repository.messages,
        repository.currentConversationId
    ) { messagesMap, activeId ->
        if (activeId == null) emptyList()
        else messagesMap[activeId].orEmpty()
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val memories: StateFlow<List<MemoryItem>> = repository.memories
    val files: StateFlow<List<FileDocument>> = repository.files
    val reminders: StateFlow<List<ReminderItem>> = repository.reminders
    val usageMetrics: StateFlow<UsageMetrics> = repository.usageMetrics
    val systemHealth = repository.systemHealth
    val auditLogs = repository.auditLogs

    // Composer State
    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _attachedFile = MutableStateFlow<FileDocument?>(null)
    val attachedFile: StateFlow<FileDocument?> = _attachedFile.asStateFlow()

    private val _isStreaming = MutableStateFlow(false)
    val isStreaming: StateFlow<Boolean> = _isStreaming.asStateFlow()

    private val _activeStreamingMessageId = MutableStateFlow<String?>(null)
    val activeStreamingMessageId: StateFlow<String?> = _activeStreamingMessageId.asStateFlow()

    // Voice & TTS State
    private val _isVoiceModalVisible = MutableStateFlow(false)
    val isVoiceModalVisible: StateFlow<Boolean> = _isVoiceModalVisible.asStateFlow()

    private val _ttsPlayingMessageId = MutableStateFlow<String?>(null)
    val ttsPlayingMessageId: StateFlow<String?> = _ttsPlayingMessageId.asStateFlow()

    private var tts: TextToSpeech? = null
    private var streamingJob: Job? = null

    // Preferences
    val selectedLanguage = repository.selectedLanguage
    val selectedModel = repository.selectedModel
    val isMemoryOptedIn = repository.isMemoryOptedIn
    val isDarkMode = repository.isDarkMode

    init {
        initTts(application)
    }

    private fun initTts(context: Application) {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }
    }

    fun navigateTo(screen: AuraScreen) {
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setInputText(text: String) {
        _inputText.value = text
    }

    fun setAttachedFile(file: FileDocument?) {
        _attachedFile.value = file
    }

    fun selectModel(tier: ModelTier) {
        selectedModel.value = tier
    }

    fun setLanguage(lang: String) {
        selectedLanguage.value = lang
    }

    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
    }

    fun toggleMemoryOptIn() {
        isMemoryOptedIn.value = !isMemoryOptedIn.value
    }

    fun startNewChat(initialPrompt: String? = null) {
        val newId = repository.createNewConversation(tier = selectedModel.value)
        _currentScreen.value = AuraScreen.CHAT
        if (!initialPrompt.isNullOrBlank()) {
            sendMessage(initialPrompt)
        }
    }

    fun selectConversation(id: String) {
        repository.selectConversation(id)
        _currentScreen.value = AuraScreen.CHAT
    }

    fun renameConversation(id: String, newTitle: String) {
        repository.renameConversation(id, newTitle)
    }

    fun deleteConversation(id: String) {
        repository.deleteConversation(id)
    }

    fun toggleArchive(id: String) {
        repository.toggleArchiveConversation(id)
    }

    fun sendMessage(customPrompt: String? = null) {
        val prompt = (customPrompt ?: _inputText.value).trim()
        if (prompt.isEmpty() && _attachedFile.value == null) return

        val convId = repository.currentConversationId.value ?: repository.createNewConversation(tier = selectedModel.value)
        _currentScreen.value = AuraScreen.CHAT

        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            conversationId = convId,
            role = MessageRole.USER,
            content = prompt.ifEmpty { "Analyze attached file" },
            attachedFileName = _attachedFile.value?.fileName
        )
        repository.addMessage(convId, userMessage)

        _inputText.value = ""
        val attachedDoc = _attachedFile.value
        _attachedFile.value = null

        // Create Assistant Placeholder
        val assistantMessageId = UUID.randomUUID().toString()
        val assistantPlaceholder = Message(
            id = assistantMessageId,
            conversationId = convId,
            role = MessageRole.ASSISTANT,
            content = "",
            model = selectedModel.value.id,
            status = MessageStatus.STREAMING
        )
        repository.addMessage(convId, assistantPlaceholder)

        _isStreaming.value = true
        _activeStreamingMessageId.value = assistantMessageId

        // Active memories list
        val enabledMemories = if (isMemoryOptedIn.value) {
            memories.value.filter { it.isEnabled }.map { it.memoryText }
        } else emptyList()

        streamingJob = viewModelScope.launch {
            try {
                aiEngine.generateStreamingResponse(
                    prompt = prompt,
                    modelTier = selectedModel.value,
                    language = selectedLanguage.value,
                    attachedFileName = attachedDoc?.fileName,
                    activeMemories = enabledMemories
                ).collect { event ->
                    when (event) {
                        is AiStreamEvent.Token -> {
                            repository.updateMessage(convId, assistantMessageId) { msg ->
                                msg.copy(content = msg.content + event.text)
                            }
                        }
                        is AiStreamEvent.ToolDetected -> {
                            repository.updateMessage(convId, assistantMessageId) { msg ->
                                msg.copy(toolCalls = msg.toolCalls + event.toolPreview)
                            }
                        }
                        is AiStreamEvent.CitationsFound -> {
                            repository.updateMessage(convId, assistantMessageId) { msg ->
                                msg.copy(citations = event.citations)
                            }
                        }
                        is AiStreamEvent.Completed -> {
                            repository.updateMessage(convId, assistantMessageId) { msg ->
                                msg.copy(
                                    content = event.fullContent,
                                    status = MessageStatus.COMPLETE
                                )
                            }
                        }
                        is AiStreamEvent.Failed -> {
                            repository.updateMessage(convId, assistantMessageId) { msg ->
                                msg.copy(
                                    content = event.errorMessage,
                                    status = MessageStatus.ERROR
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                repository.updateMessage(convId, assistantMessageId) { msg ->
                    msg.copy(
                        content = msg.content.ifEmpty { "An unexpected error occurred: ${e.localizedMessage}. Please retry." },
                        status = MessageStatus.ERROR
                    )
                }
            } finally {
                _isStreaming.value = false
                _activeStreamingMessageId.value = null
            }
        }
    }

    fun stopGeneration() {
        streamingJob?.cancel()
        streamingJob = null
        val convId = repository.currentConversationId.value
        val msgId = _activeStreamingMessageId.value
        if (convId != null && msgId != null) {
            repository.updateMessage(convId, msgId) { msg ->
                msg.copy(status = MessageStatus.COMPLETE)
            }
        }
        _isStreaming.value = false
        _activeStreamingMessageId.value = null
    }

    fun retryLastMessage() {
        val convId = repository.currentConversationId.value ?: return
        val msgList = currentMessages.value
        val lastUserMessage = msgList.lastOrNull { it.role == MessageRole.USER } ?: return
        sendMessage(lastUserMessage.content)
    }

    fun confirmToolAction(messageId: String, toolActionId: String) {
        val convId = repository.currentConversationId.value ?: return
        repository.confirmToolAction(convId, messageId, toolActionId)
        Toast.makeText(getApplication(), "Action confirmed & executed securely!", Toast.LENGTH_SHORT).show()
    }

    fun rateMessage(messageId: String, isPositive: Boolean, comment: String = "") {
        val convId = repository.currentConversationId.value ?: return
        repository.rateMessage(convId, messageId, isPositive, comment)
        val msg = if (isPositive) "Thank you for your feedback!" else "Feedback noted for model improvement."
        Toast.makeText(getApplication(), msg, Toast.LENGTH_SHORT).show()
    }

    // Voice push-to-talk modal
    fun showVoiceModal(visible: Boolean) {
        _isVoiceModalVisible.value = visible
    }

    fun onSpeechRecognized(transcript: String) {
        _isVoiceModalVisible.value = false
        if (transcript.isNotBlank()) {
            sendMessage(transcript)
        }
    }

    // TTS playback
    fun toggleTts(messageId: String, textToSpeak: String) {
        if (_ttsPlayingMessageId.value == messageId) {
            tts?.stop()
            _ttsPlayingMessageId.value = null
        } else {
            tts?.stop()
            _ttsPlayingMessageId.value = messageId
            tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, messageId)
        }
    }

    // Memory operations
    fun addMemory(text: String, category: MemoryCategory) {
        if (text.isNotBlank()) {
            repository.addMemory(text, category)
        }
    }

    fun toggleMemory(id: String) {
        repository.toggleMemory(id)
    }

    fun deleteMemory(id: String) {
        repository.deleteMemory(id)
    }

    fun clearAllMemories() {
        repository.clearAllMemories()
    }

    // File operations
    fun uploadFile(name: String, type: FileType, sizeBytes: Long, mimeType: String, summary: String = "") {
        repository.addFile(name, type, sizeBytes, mimeType, summary)
    }

    fun deleteFile(id: String) {
        repository.deleteFile(id)
    }

    fun askAboutFile(file: FileDocument) {
        _attachedFile.value = file
        startNewChat("Explain and summarize the key findings in ${file.fileName}")
    }

    // Reminder operations
    fun addReminder(title: String, dueAt: String, priority: String) {
        repository.addReminder(title, dueAt, priority)
    }

    fun toggleReminder(id: String) {
        repository.toggleReminderStatus(id)
    }

    fun deleteReminder(id: String) {
        repository.deleteReminder(id)
    }

    fun clearAllData() {
        repository.clearAllData()
        _currentScreen.value = AuraScreen.WELCOME
    }

    override fun onCleared() {
        super.onCleared()
        tts?.stop()
        tts?.shutdown()
    }
}
