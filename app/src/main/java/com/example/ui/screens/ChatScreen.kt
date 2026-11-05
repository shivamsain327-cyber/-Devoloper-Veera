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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FileDocument
import com.example.data.model.Message
import com.example.data.model.MessageRole
import com.example.data.model.ModelTier
import com.example.ui.components.ChatComposer
import com.example.ui.components.MessageBubble
import com.example.ui.theme.AuraBluePrimary

@Composable
fun ChatScreen(
    messages: List<Message>,
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onStopGeneration: () -> Unit,
    onRetryLastMessage: () -> Unit,
    isStreaming: Boolean,
    attachedFile: FileDocument?,
    onRemoveAttachedFile: () -> Unit,
    onAttachFileClick: () -> Unit,
    onMicClick: () -> Unit,
    ttsPlayingMessageId: String?,
    onToggleTts: (String, String) -> Unit,
    onRateMessage: (String, Boolean) -> Unit,
    onConfirmTool: (String, String) -> Unit,
    selectedModel: ModelTier,
    selectedLanguage: String,
    totalQueries: Int,
    dailyQuotaLimit: Int,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // Auto-scroll to bottom as new messages or streaming tokens arrive
    LaunchedEffect(messages.size, messages.lastOrNull()?.content?.length) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (messages.isEmpty()) {
            // Empty Conversation State
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = AuraBluePrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "How can AuraBot assist you?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Ask questions, analyze uploaded spreadsheets & PDFs, or get code assistance in English, Hindi, or Hinglish.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            // Messages List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("chat_messages_list"),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    MessageBubble(
                        message = msg,
                        isTtsPlaying = ttsPlayingMessageId == msg.id,
                        onToggleTts = onToggleTts,
                        onRateMessage = onRateMessage,
                        onRetry = onRetryLastMessage,
                        onConfirmTool = onConfirmTool
                    )
                }
            }
        }

        // Pinned Composer
        ChatComposer(
            inputText = inputText,
            onInputChange = onInputChange,
            onSend = onSendMessage,
            onStopGeneration = onStopGeneration,
            isStreaming = isStreaming,
            attachedFile = attachedFile,
            onRemoveAttachedFile = onRemoveAttachedFile,
            onAttachFileClick = onAttachFileClick,
            onMicClick = onMicClick,
            selectedLanguage = selectedLanguage,
            totalQueries = totalQueries,
            dailyQuotaLimit = dailyQuotaLimit
        )
    }
}
