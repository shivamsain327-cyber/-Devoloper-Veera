package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ModelTier
import com.example.ui.components.ChatComposer
import com.example.ui.theme.AuraBluePrimary
import com.example.ui.theme.AuraCyanAccent
import com.example.ui.theme.AuraEmerald
import com.example.ui.theme.AuraIndigoAccent

data class PersonaCard(
    val title: String,
    val subtitle: String,
    val prompt: String,
    val icon: ImageVector,
    val badge: String
)

@Composable
fun WelcomeScreen(
    onStartPrompt: (String) -> Unit,
    selectedModel: ModelTier,
    selectedLanguage: String,
    totalQueries: Int,
    dailyQuotaLimit: Int,
    onVoiceClick: () -> Unit,
    onAttachFileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val personas = listOf(
        PersonaCard(
            title = "Student / Learner",
            subtitle = "Clear explanations, notes revision & study guides",
            prompt = "Explain quantum computing and superposition with simple real-world analogies",
            icon = Icons.Default.School,
            badge = "Study Support"
        ),
        PersonaCard(
            title = "Developer / Creator",
            subtitle = "Architecture, Kotlin code scaffolds & debugging",
            prompt = "Write a high-performance Jetpack Compose repository with Kotlin Coroutines Flow",
            icon = Icons.Default.Code,
            badge = "Engineering"
        ),
        PersonaCard(
            title = "Business / Office",
            subtitle = "Spreadsheet analysis, emails & scheduling",
            prompt = "Analyze Q3 revenue metrics and draft an executive briefing memo",
            icon = Icons.Default.Business,
            badge = "Productivity"
        ),
        PersonaCard(
            title = "Everyday Hindi / Hinglish",
            subtitle = "Natural conversation in Hindi, Hinglish & English",
            prompt = "Newton ke teenon laws ko simple daily life Hinglish examples ke sath samjhao",
            icon = Icons.Default.Translate,
            badge = "Multilingual"
        )
    )

    val quickChips = listOf(
        "Explain in Hinglish" to "Namaste! Mujhe latest AI developments ke baare mein Hinglish mein batao",
        "Web Research" to "Search and summarize recent breakthroughs in generative AI models",
        "Code Scaffold" to "Create a clean M3 Card component with ripple in Jetpack Compose",
        "Set Reminder" to "Schedule a team sync meeting for tomorrow at 2:00 PM"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Hero Brand Section
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.aurabot_launcher_fg),
                            contentDescription = "AuraBot Logo",
                            modifier = Modifier.size(56.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "AuraBot AI",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Next-Generation Multilingual Personal AI Assistant",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Model & Security Status Pill
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(AuraEmerald)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Active: ${selectedModel.displayName} • Tenant Isolated",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Quick Topic Chips
            item {
                Text(
                    text = "QUICK START TOPICS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                ) {
                    items(quickChips) { (label, prompt) ->
                        FilterChip(
                            selected = false,
                            onClick = { onStartPrompt(prompt) },
                            label = { Text(label, fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = AuraBluePrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                labelColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // User Persona Scenarios (from Blueprint Section 2)
            item {
                Text(
                    text = "ASSISTANT CAPABILITIES",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            items(personas) { persona ->
                Card(
                    onClick = { onStartPrompt(persona.prompt) },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("persona_card_${persona.title}")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = persona.icon,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = persona.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = persona.badge,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = AuraBluePrimary,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = persona.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Select",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Bottom Composer on Welcome Screen
        ChatComposer(
            inputText = "",
            onInputChange = {},
            onSend = { onStartPrompt("Namaste! Help me get started with AuraBot.") },
            onStopGeneration = {},
            isStreaming = false,
            attachedFile = null,
            onRemoveAttachedFile = {},
            onAttachFileClick = onAttachFileClick,
            onMicClick = onVoiceClick,
            selectedLanguage = selectedLanguage,
            totalQueries = totalQueries,
            dailyQuotaLimit = dailyQuotaLimit
        )
    }
}
