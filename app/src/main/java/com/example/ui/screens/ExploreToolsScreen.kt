package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AuraBluePrimary
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraIndigoAccent

data class ToolInfo(
    val name: String,
    val identifier: String,
    val description: String,
    val permissionLevel: String,
    val samplePrompt: String,
    val icon: ImageVector,
    val parametersSchema: String
)

@Composable
fun ExploreToolsScreen(
    onLaunchTool: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val toolCatalog = listOf(
        ToolInfo(
            name = "Web Research Engine",
            identifier = "web_search",
            description = "Searches the live web, extracts domain sources, formats citations, and handles stale sources safely.",
            permissionLevel = "Read-Only (Sourced Facts)",
            samplePrompt = "Search and cite latest Android Jetpack Compose release benchmarks 2026",
            icon = Icons.Default.Search,
            parametersSchema = "{ query: string, recency: 'recent', cite_urls: true }"
        ),
        ToolInfo(
            name = "Document & File Reader",
            identifier = "read_uploaded_file",
            description = "Extracts text from PDF, CSV, DOCX, and TXT files, chunks content, and enables contextual Q&A.",
            permissionLevel = "User-Scoped & Scanned",
            samplePrompt = "Analyze attached blueprint document and highlight architecture rules",
            icon = Icons.Default.Description,
            parametersSchema = "{ file_id: uuid, page_range: 'all', summarize: true }"
        ),
        ToolInfo(
            name = "Precision Calculator",
            identifier = "calculate",
            description = "Executes strict arithmetic, statistical formulas, budget breakdowns, and unit conversions.",
            permissionLevel = "Deterministic Sandbox",
            samplePrompt = "Calculate 15% compound annual growth on $12,500 over 4 years step by step",
            icon = Icons.Default.Calculate,
            parametersSchema = "{ expression: string, precision: '2_decimals', show_steps: true }"
        ),
        ToolInfo(
            name = "Reminder Automation",
            identifier = "create_reminder",
            description = "Creates timezone-aware task reminders with title, due dates, and priority tags.",
            permissionLevel = "User Approval Required",
            samplePrompt = "Remind me to review sprint deployment milestones today at 5:00 PM",
            icon = Icons.Default.Notifications,
            parametersSchema = "{ title: string, due_at: timestamp, priority: 'High' }"
        ),
        ToolInfo(
            name = "Calendar Event Planner",
            identifier = "create_calendar_event",
            description = "Generates event action previews before scheduling, with interactive confirmation dialogs.",
            permissionLevel = "Explicit Interactive Confirmation",
            samplePrompt = "Schedule a 45-minute architectural review meeting for tomorrow at 11 AM",
            icon = Icons.Default.DateRange,
            parametersSchema = "{ title: string, start: timestamp, end: timestamp, preview_first: true }"
        ),
        ToolInfo(
            name = "Code Architecture & Scaffolding",
            identifier = "code_scaffold",
            description = "Generates clean Kotlin/Python modules, explains bugs, and provides copy-ready syntax.",
            permissionLevel = "Syntax Generation Only",
            samplePrompt = "Write a production Kotlin ViewModel managing state flows with clean unit tests",
            icon = Icons.Default.Code,
            parametersSchema = "{ language: 'kotlin', framework: 'compose', m3_compliant: true }"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Column(modifier = Modifier.padding(bottom = 6.dp)) {
                    Text(
                        text = "AuraBot Tool Registry",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Allow-listed, secure capabilities orchestrated with strict permissions and user verification.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            items(toolCatalog) { tool ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tool_card_${tool.identifier}")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = tool.icon,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tool.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "id: ${tool.identifier}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AuraBluePrimary,
                                    fontSize = 11.sp
                                )
                            }

                            Surface(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = tool.permissionLevel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = AuraEmerald,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = tool.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Schema: ${tool.parametersSchema}",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = { onLaunchTool(tool.samplePrompt) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Test Tool with Sample Prompt",
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
