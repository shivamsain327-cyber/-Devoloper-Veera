package com.example.data.model

import java.util.UUID

enum class FileType(val extension: String, val badge: String) {
    PDF("pdf", "PDF Document"),
    CSV("csv", "Spreadsheet / Data"),
    TXT("txt", "Plain Text"),
    DOCX("docx", "Word Document"),
    IMAGE("png", "Image Attachment")
}

data class FileChunk(
    val index: Int,
    val heading: String,
    val content: String,
    val tokenEstimate: Int
)

data class FileDocument(
    val id: String = UUID.randomUUID().toString(),
    val fileName: String,
    val fileType: FileType,
    val sizeBytes: Long,
    val mimeType: String,
    val chunks: List<FileChunk> = emptyList(),
    val summary: String = "",
    val status: String = "Ready",
    val uploadedAt: Long = System.currentTimeMillis()
) {
    val formattedSize: String
        get() = when {
            sizeBytes < 1024 -> "$sizeBytes B"
            sizeBytes < 1024 * 1024 -> "${sizeBytes / 1024} KB"
            else -> "%.1f MB".format(sizeBytes.toDouble() / (1024 * 1024))
        }
}
