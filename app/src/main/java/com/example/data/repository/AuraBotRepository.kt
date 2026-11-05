package com.example.data.repository

import com.example.data.model.AuditLogEntry
import com.example.data.model.Citation
import com.example.data.model.Conversation
import com.example.data.model.FeedbackRating
import com.example.data.model.FileChunk
import com.example.data.model.FileDocument
import com.example.data.model.FileType
import com.example.data.model.MemoryCategory
import com.example.data.model.MemoryItem
import com.example.data.model.Message
import com.example.data.model.MessageRole
import com.example.data.model.MessageStatus
import com.example.data.model.ModelTier
import com.example.data.model.ReminderItem
import com.example.data.model.ReminderStatus
import com.example.data.model.SystemHealth
import com.example.data.model.ToolActionPreview
import com.example.data.model.ToolCallType
import com.example.data.model.UsageMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class AuraBotRepository {

    private val _conversations = MutableStateFlow<List<Conversation>>(emptyList())
    val conversations: StateFlow<List<Conversation>> = _conversations.asStateFlow()

    private val _currentConversationId = MutableStateFlow<String?>(null)
    val currentConversationId: StateFlow<String?> = _currentConversationId.asStateFlow()

    private val _messages = MutableStateFlow<Map<String, List<Message>>>(emptyMap())
    val messages: StateFlow<Map<String, List<Message>>> = _messages.asStateFlow()

    private val _memories = MutableStateFlow<List<MemoryItem>>(emptyList())
    val memories: StateFlow<List<MemoryItem>> = _memories.asStateFlow()

    private val _files = MutableStateFlow<List<FileDocument>>(emptyList())
    val files: StateFlow<List<FileDocument>> = _files.asStateFlow()

    private val _reminders = MutableStateFlow<List<ReminderItem>>(emptyList())
    val reminders: StateFlow<List<ReminderItem>> = _reminders.asStateFlow()

    private val _usageMetrics = MutableStateFlow(UsageMetrics())
    val usageMetrics: StateFlow<UsageMetrics> = _usageMetrics.asStateFlow()

    private val _systemHealth = MutableStateFlow(SystemHealth())
    val systemHealth: StateFlow<SystemHealth> = _systemHealth.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLogEntry>>(emptyList())
    val auditLogs: StateFlow<List<AuditLogEntry>> = _auditLogs.asStateFlow()

    // Settings
    val selectedLanguage = MutableStateFlow("Hinglish") // English, Hindi, Hinglish
    val selectedModel = MutableStateFlow(ModelTier.FLASH)
    val isMemoryOptedIn = MutableStateFlow(true)
    val isDarkMode = MutableStateFlow(true)

    init {
        seedInitialData()
    }

    private fun seedInitialData() {
        val now = System.currentTimeMillis()

        // 1. Initial seeded conversations
        val conv1Id = UUID.randomUUID().toString()
        val conv2Id = UUID.randomUUID().toString()
        val conv3Id = UUID.randomUUID().toString()

        val conv1 = Conversation(
            id = conv1Id,
            title = "AuraBot Architecture Review",
            createdAt = now - 7200000L,
            updatedAt = now - 3600000L,
            isPinned = true,
            lastMessagePreview = "Architecture verified: modular monolith with clean boundaries",
            modelTier = ModelTier.PRO
        )

        val conv2 = Conversation(
            id = conv2Id,
            title = "Data Analysis: Q3 Revenue",
            createdAt = now - 86400000L,
            updatedAt = now - 72000000L,
            isPinned = false,
            lastMessagePreview = "CSV analysis completed with duplicate checks and formulas",
            modelTier = ModelTier.MULTIMODAL
        )

        val conv3 = Conversation(
            id = conv3Id,
            title = "Hinglish Study Assistant",
            createdAt = now - 172800000L,
            updatedAt = now - 160000000L,
            isPinned = false,
            lastMessagePreview = "Newton ke teenon laws ko simple daily life examples se explain kiya gaya hai",
            modelTier = ModelTier.FLASH
        )

        _conversations.value = listOf(conv1, conv2, conv3)
        _currentConversationId.value = conv1Id

        // Messages for conv1
        val conv1Messages = listOf(
            Message(
                id = UUID.randomUUID().toString(),
                conversationId = conv1Id,
                role = MessageRole.USER,
                content = "Please summarize the core technical architecture guidelines from the AuraBot AI blueprint.",
                createdAt = now - 3600000L
            ),
            Message(
                id = UUID.randomUUID().toString(),
                conversationId = conv1Id,
                role = MessageRole.ASSISTANT,
                content = """
                    Here is the verified architectural summary for **AuraBot AI**:
                    
                    ### 1. High-Level Topology
                    - **Modular Monolith:** Recommended for initial launch to maximize cohesion, reduce latency, and ensure strict tenant isolation.
                    - **Protocol:** HTTP REST for CRUD operations, and Server-Sent Events (SSE) for responsive token-by-token text streaming.
                    
                    ### 2. Guardrails & Safety
                    - **Tool Sandboxing:** All tools (search, file extraction, calculations) are allow-listed with strict schemas.
                    - **Explicit Confirmation:** Side-effect operations (e.g. calendar invites, task updates) require mandatory interactive user approval before execution.
                """.trimIndent(),
                createdAt = now - 3500000L,
                model = ModelTier.PRO.id,
                status = MessageStatus.COMPLETE,
                citations = listOf(
                    Citation(
                        title = "AuraBot Technical Blueprint Section 6",
                        url = "https://docs.aurabot.ai/v1/architecture",
                        snippet = "Modular monolith for first release. Keep services separated by domain.",
                        domain = "docs.aurabot.ai"
                    )
                )
            )
        )

        _messages.value = mapOf(conv1Id to conv1Messages)

        // Seeded Memories
        _memories.value = listOf(
            MemoryItem(
                id = UUID.randomUUID().toString(),
                memoryText = "User prefers responses in a friendly mix of Hinglish and English",
                category = MemoryCategory.PREFERENCE,
                source = "Settings & Onboarding",
                isEnabled = true,
                createdAt = now - 86400000L
            ),
            MemoryItem(
                id = UUID.randomUUID().toString(),
                memoryText = "Working as a Senior Android Software Engineer building modern Compose apps",
                category = MemoryCategory.WORK,
                source = "Profile setup",
                isEnabled = true,
                createdAt = now - 86400000L
            ),
            MemoryItem(
                id = UUID.randomUUID().toString(),
                memoryText = "Focusing on AuraBot AI assistant deployment and production readiness",
                category = MemoryCategory.PROJECT,
                source = "User Conversation",
                isEnabled = true,
                createdAt = now - 43200000L
            )
        )

        // Seeded Uploaded Files
        _files.value = listOf(
            FileDocument(
                id = UUID.randomUUID().toString(),
                fileName = "AuraBot_Project_Blueprint.pdf",
                fileType = FileType.PDF,
                sizeBytes = 432100,
                mimeType = "application/pdf",
                status = "Indexed (8 Chunks)",
                summary = "Complete 25-page project blueprint including architecture, data models, and UI guidelines.",
                chunks = listOf(
                    FileChunk(1, "Executive Summary", "AuraBot AI multi-platform personal AI assistant with ChatGPT-style experience.", 85),
                    FileChunk(2, "UI/UX Specifications", "Clean dark navy SaaS interface, spacious chat area, restrained blue accent.", 120),
                    FileChunk(3, "AI Orchestrator", "Tool registry, memory controls, streaming token delivery, and safe execution.", 140)
                ),
                uploadedAt = now - 86400000L
            ),
            FileDocument(
                id = UUID.randomUUID().toString(),
                fileName = "Q3_Revenue_Analysis.csv",
                fileType = FileType.CSV,
                sizeBytes = 45200,
                mimeType = "text/csv",
                status = "Ready (4 Columns)",
                summary = "Quarterly financial records, conversion benchmarks, and growth percentages.",
                chunks = listOf(
                    FileChunk(1, "Columns & Schema", "Month, Revenue_USD, Active_Users, Cost_Per_Token", 45)
                ),
                uploadedAt = now - 172800000L
            )
        )

        // Seeded Reminders
        _reminders.value = listOf(
            ReminderItem(
                id = UUID.randomUUID().toString(),
                title = "Review AuraBot AI Production Milestone",
                dueAt = "Today, 5:30 PM",
                priority = "High",
                category = "Work",
                status = ReminderStatus.PENDING,
                createdAt = now - 18000000L
            ),
            ReminderItem(
                id = UUID.randomUUID().toString(),
                title = "Validate Token Quota Limits & Cost Alerts",
                dueAt = "Tomorrow, 11:00 AM",
                priority = "Normal",
                category = "Operations",
                status = ReminderStatus.PENDING,
                createdAt = now - 14000000L
            )
        )

        // Seeded Audit Logs
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        _auditLogs.value = listOf(
            AuditLogEntry(UUID.randomUUID().toString(), "Tool Invoked", "web_search", "Success", timeFormat.format(Date(now - 3600000L))),
            AuditLogEntry(UUID.randomUUID().toString(), "Permission Verified", "read_uploaded_file", "Authorized", timeFormat.format(Date(now - 7200000L))),
            AuditLogEntry(UUID.randomUUID().toString(), "Model Routing", "aurabot-pro", "Optimal Latency", timeFormat.format(Date(now - 10800000L))),
            AuditLogEntry(UUID.randomUUID().toString(), "Rate Limit Check", "token_bucket", "Within Safe Limits", timeFormat.format(Date(now - 14400000L)))
        )
    }

    fun selectConversation(id: String) {
        _currentConversationId.value = id
    }

    fun createNewConversation(title: String = "New Conversation", tier: ModelTier = selectedModel.value): String {
        val newId = UUID.randomUUID().toString()
        val newConv = Conversation(
            id = newId,
            title = title,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            modelTier = tier
        )
        _conversations.update { listOf(newConv) + it }
        _currentConversationId.value = newId
        _messages.update { it + (newId to emptyList()) }
        return newId
    }

    fun renameConversation(id: String, newTitle: String) {
        _conversations.update { list ->
            list.map { if (it.id == id) it.copy(title = newTitle, updatedAt = System.currentTimeMillis()) else it }
        }
    }

    fun deleteConversation(id: String) {
        _conversations.update { list -> list.filter { it.id != id } }
        _messages.update { it - id }
        if (_currentConversationId.value == id) {
            _currentConversationId.value = _conversations.value.firstOrNull()?.id
        }
    }

    fun toggleArchiveConversation(id: String) {
        _conversations.update { list ->
            list.map { if (it.id == id) it.copy(isArchived = !it.isArchived) else it }
        }
    }

    fun addMessage(conversationId: String, message: Message) {
        _messages.update { currentMap ->
            val list = currentMap[conversationId].orEmpty()
            currentMap + (conversationId to (list + message))
        }

        // Update conversation preview and title if it was first message
        _conversations.update { convList ->
            convList.map { conv ->
                if (conv.id == conversationId) {
                    val newTitle = if (conv.title == "New Conversation" && message.role == MessageRole.USER) {
                        message.content.take(30).trim()
                    } else conv.title

                    conv.copy(
                        title = newTitle,
                        updatedAt = System.currentTimeMillis(),
                        lastMessagePreview = message.content.take(60)
                    )
                } else conv
            }
        }

        // Update usage
        _usageMetrics.update {
            it.copy(
                totalQueries = it.totalQueries + 1,
                promptTokens = it.promptTokens + 45,
                completionTokens = it.completionTokens + (message.content.length / 4)
            )
        }
    }

    fun updateMessage(conversationId: String, messageId: String, transform: (Message) -> Message) {
        _messages.update { currentMap ->
            val list = currentMap[conversationId].orEmpty()
            currentMap + (conversationId to list.map { if (it.id == messageId) transform(it) else it })
        }
    }

    fun confirmToolAction(conversationId: String, messageId: String, toolActionId: String) {
        val now = System.currentTimeMillis()
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

        _messages.update { currentMap ->
            val list = currentMap[conversationId].orEmpty()
            currentMap + (conversationId to list.map { msg ->
                if (msg.id == messageId) {
                    val updatedTools = msg.toolCalls.map { tool ->
                        if (tool.id == toolActionId) {
                            tool.copy(isConfirmed = true, isExecuted = true)
                        } else tool
                    }
                    msg.copy(toolCalls = updatedTools)
                } else msg
            })
        }

        // If it was reminder/calendar, add to reminders list
        val msg = _messages.value[conversationId]?.find { it.id == messageId }
        val tool = msg?.toolCalls?.find { it.id == toolActionId }
        if (tool != null) {
            val title = tool.details["Item"] ?: tool.title
            val dueAt = tool.details["Target Date"] ?: "Tomorrow, 10:00 AM"
            addReminder(title = title, dueAt = dueAt, priority = "Normal")

            _auditLogs.update {
                listOf(
                    AuditLogEntry(
                        id = UUID.randomUUID().toString(),
                        action = "User Confirmed Tool Execution",
                        tool = tool.toolType.name,
                        status = "Executed Successfully",
                        timestamp = timeFormat.format(Date(now))
                    )
                ) + it
            }
        }
    }

    fun rateMessage(conversationId: String, messageId: String, isPositive: Boolean, comment: String = "") {
        updateMessage(conversationId, messageId) { msg ->
            msg.copy(feedback = FeedbackRating(messageId, isPositive, comment))
        }
    }

    // Memories management
    fun addMemory(text: String, category: MemoryCategory = MemoryCategory.PREFERENCE) {
        val item = MemoryItem(
            id = UUID.randomUUID().toString(),
            memoryText = text,
            category = category,
            source = "User Added"
        )
        _memories.update { listOf(item) + it }
    }

    fun toggleMemory(id: String) {
        _memories.update { list ->
            list.map { if (it.id == id) it.copy(isEnabled = !it.isEnabled) else it }
        }
    }

    fun deleteMemory(id: String) {
        _memories.update { list -> list.filter { it.id != id } }
    }

    fun clearAllMemories() {
        _memories.value = emptyList()
    }

    // Files management
    fun addFile(fileName: String, fileType: FileType, sizeBytes: Long, mimeType: String, summary: String = "") {
        val newFile = FileDocument(
            id = UUID.randomUUID().toString(),
            fileName = fileName,
            fileType = fileType,
            sizeBytes = sizeBytes,
            mimeType = mimeType,
            status = "Ready (Extracted)",
            summary = summary.ifBlank { "Analyzed document ready for contextual Q&A and semantic retrieval." },
            chunks = listOf(
                FileChunk(1, "Overview", "Extracted content from $fileName ready for AuraBot synthesis.", 90)
            )
        )
        _files.update { listOf(newFile) + it }
    }

    fun deleteFile(id: String) {
        _files.update { list -> list.filter { it.id != id } }
    }

    // Reminders management
    fun addReminder(title: String, dueAt: String, priority: String = "Normal") {
        val rem = ReminderItem(
            id = UUID.randomUUID().toString(),
            title = title,
            dueAt = dueAt,
            priority = priority
        )
        _reminders.update { listOf(rem) + it }
    }

    fun toggleReminderStatus(id: String) {
        _reminders.update { list ->
            list.map {
                if (it.id == id) {
                    val nextStatus = if (it.status == ReminderStatus.COMPLETED) ReminderStatus.PENDING else ReminderStatus.COMPLETED
                    it.copy(status = nextStatus)
                } else it
            }
        }
    }

    fun deleteReminder(id: String) {
        _reminders.update { list -> list.filter { it.id != id } }
    }

    fun clearAllData() {
        _conversations.value = emptyList()
        _messages.value = emptyMap()
        _memories.value = emptyList()
        _files.value = emptyList()
        _reminders.value = emptyList()
        _currentConversationId.value = null
        createNewConversation("Fresh Conversation")
    }
}
