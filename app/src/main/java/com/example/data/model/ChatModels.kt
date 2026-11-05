package com.example.data.model

import java.util.UUID

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

enum class MessageStatus {
    SENDING, STREAMING, COMPLETE, ERROR
}

enum class ModelTier(val id: String, val displayName: String, val badge: String, val description: String) {
    FLASH(
        id = "aurabot-flash",
        displayName = "AuraBot 1.0 Flash",
        badge = "Fast & Efficient",
        description = "Instant answers, everyday tasks, multilingual chat"
    ),
    PRO(
        id = "aurabot-pro",
        displayName = "AuraBot Pro Reasoning",
        badge = "Deep Thinking",
        description = "Complex coding, step-by-step logic, advanced analysis"
    ),
    MULTIMODAL(
        id = "aurabot-multimodal",
        displayName = "AuraBot Multimodal",
        badge = "Docs & Vision",
        description = "PDFs, spreadsheets, document synthesis, visual reasoning"
    )
}

enum class ToolCallType(val displayName: String) {
    WEB_SEARCH("Web Research"),
    FILE_ANALYSIS("Document Extraction"),
    CALCULATE("Precision Calculator"),
    REMINDER_CREATE("Reminder Automation"),
    CALENDAR_EVENT("Calendar Scheduling"),
    CODE_SCAFFOLD("Code Scaffolding")
}

data class Citation(
    val title: String,
    val url: String,
    val snippet: String,
    val domain: String,
    val accessDate: String = "Today"
)

data class ToolActionPreview(
    val id: String = UUID.randomUUID().toString(),
    val toolType: ToolCallType,
    val title: String,
    val description: String,
    val details: Map<String, String>,
    val isConfirmed: Boolean = false,
    val isExecuted: Boolean = false
)

data class FeedbackRating(
    val messageId: String,
    val isPositive: Boolean,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val role: MessageRole,
    val content: String,
    val model: String = ModelTier.FLASH.id,
    val status: MessageStatus = MessageStatus.COMPLETE,
    val createdAt: Long = System.currentTimeMillis(),
    val toolCalls: List<ToolActionPreview> = emptyList(),
    val citations: List<Citation> = emptyList(),
    val feedback: FeedbackRating? = null,
    val attachedFileName: String? = null
)

data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false,
    val isPinned: Boolean = false,
    val lastMessagePreview: String = "",
    val modelTier: ModelTier = ModelTier.FLASH
)
