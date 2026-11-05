package com.example.data.model

import java.util.UUID

enum class ReminderStatus {
    PENDING, COMPLETED, CANCELLED
}

data class ReminderItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val dueAt: String,
    val priority: String = "Normal",
    val category: String = "Task",
    val status: ReminderStatus = ReminderStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)
