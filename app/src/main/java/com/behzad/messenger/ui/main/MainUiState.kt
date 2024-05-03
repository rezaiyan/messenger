package com.behzad.messenger.ui.main


sealed class MainUiState {
    data object Loading : MainUiState()
    data class Loaded(val conversations: Conversations) : MainUiState()
    data class Error(val message: String) : MainUiState()
}

data class Conversations(val conversations: List<UiConversation>)

data class UiConversation(val id: String, val title: String, val subtitle: String)