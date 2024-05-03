package com.behzad.messenger.ui.conversation

import com.behzad.messenger.domain.MessageStatus

sealed class ConversationUiState {
    data object Loading : ConversationUiState()
    data class Loaded(val toolbarTitle: String, val messages: Messages) : ConversationUiState()
    data class Error(val message: String) : ConversationUiState()
}

data class Messages(val messages: List<UiMessage>)

data class UiMessage(
    val id: String,
    val conversationId: String,
    val content: String,
    val createdAt: String,
    val receiverId: String,
    val senderId: String,
    val status: MessageStatus,
    val isMine: Boolean,
)