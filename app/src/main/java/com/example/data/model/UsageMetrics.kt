package com.example.data.model

data class UsageMetrics(
    val promptTokens: Long = 12450,
    val completionTokens: Long = 28910,
    val totalQueries: Int = 42,
    val estimatedCostUsd: Double = 0.041,
    val dailyQuotaLimit: Int = 500,
    val planName: String = "AuraBot Free Tier",
    val lastResetDate: String = "Daily at 00:00 UTC"
) {
    val totalTokens: Long get() = promptTokens + completionTokens
    val usagePercentage: Float get() = (totalQueries.toFloat() / dailyQuotaLimit.toFloat()).coerceIn(0f, 1f)
}

data class SystemHealth(
    val apiGatewayStatus: String = "Operational",
    val modelOrchestratorStatus: String = "Operational",
    val vectorDbStatus: String = "Operational",
    val averageLatencyMs: Int = 185,
    val uptimePercent: Double = 99.98,
    val errorRatePercent: Double = 0.02,
    val activeToolsCount: Int = 6
)

data class AuditLogEntry(
    val id: String,
    val action: String,
    val tool: String,
    val status: String,
    val timestamp: String
)
