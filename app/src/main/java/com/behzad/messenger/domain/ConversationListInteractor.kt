@file:OptIn(ExperimentalCoroutinesApi::class)

package com.behzad.messenger.domain

import com.behzad.messenger.data.database.ConversationDao
import com.behzad.messenger.data.database.MessageDao
import com.behzad.messenger.ui.main.Conversations
import com.behzad.messenger.ui.main.MainUiState
import com.behzad.messenger.ui.main.UiConversation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import javax.inject.Inject

interface ConversationListInteractor {
    fun getConversations(): Flow<MainUiState>
}

class ConversationListInteractorImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) : ConversationListInteractor {

    override fun getConversations(): Flow<MainUiState> {
        // Fetch conversations and map each conversation to a UiConversation
        val flatMapConcat : Flow<MainUiState> = conversationDao.getConversations().flatMapConcat { conversations ->
            // Combine latest messages into a single flow of list of UiConversations
            if (conversations.isEmpty()) {
                return@flatMapConcat flowOf(MainUiState.Loaded(Conversations(emptyList())))
            }
            combine(
                conversations.map { conversation ->
                    messageDao.getMessagesForConversation(conversation.id)
                        .map { messages ->
                            val lastMessage = messages.lastOrNull()
                            UiConversation(
                                id = conversation.id,
                                title = lastMessage?.receiverId.orEmpty().truncate(),
                                subtitle = lastMessage?.content.orEmpty().truncate(),
                            )
                        }
                }
            ) { uiConversations ->
                MainUiState.Loaded(Conversations(uiConversations.toList()))
            }
        }
        return flatMapConcat.onStart {
            emit(MainUiState.Loading)
        }.catch {
            emit(MainUiState.Error("Failed to load conversations"))
        }.flowOn(Dispatchers.IO) // Ensure this work happens on the I/O dispatcher
    }

}

private fun String.truncate(length: Int = 35): String {
    return if (this.length > length) {
        this.substring(0, length) + "..."
    } else {
        this
    }
}