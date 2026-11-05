package com.example.data.model

import java.util.UUID

enum class MemoryCategory(val displayName: String, val iconName: String) {
    PREFERENCE("Preference", "tune"),
    FACT("Personal Fact", "person"),
    PROJECT("Project Context", "folder"),
    WORK("Work / Role", "business")
}

data class MemoryItem(
    val id: String = UUID.randomUUID().toString(),
    val memoryText: String,
    val category: MemoryCategory = MemoryCategory.PREFERENCE,
    val source: String = "User Conversation",
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
