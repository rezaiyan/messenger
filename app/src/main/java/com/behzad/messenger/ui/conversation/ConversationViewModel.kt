package com.behzad.messenger.ui.conversation

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel @Inject constructor(
    private val interactor: MessagesInteractor,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val conversationId: String = savedStateHandle.get<String>("conversationId").orEmpty()

    private val _event = MutableSharedFlow<ConversationUiEvent>()
    val event = _event.asSharedFlow()

    fun sendMessage(to: String, message: String) {
        viewModelScope.launch {
            interactor.sendMessage(to, message, conversationId).collect { isSent ->
                _event.emit(isSent.toEvent())
            }
        }
    }



    val state = interactor.getConversation(conversationId)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ConversationUiState.Loading
        )

}

private fun Boolean.toEvent(): ConversationUiEvent {
    return if (this) {
        ConversationUiEvent.SentMessage
    } else {
        ConversationUiEvent.Error("Failed to send message")
    }
}
