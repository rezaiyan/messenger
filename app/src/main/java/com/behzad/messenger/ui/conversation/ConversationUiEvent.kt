package com.behzad.messenger.ui.conversation

sealed class ConversationUiEvent {
    data object None : ConversationUiEvent()
    data object SentMessage : ConversationUiEvent()
    data class Error(val message: String) : ConversationUiEvent()
}