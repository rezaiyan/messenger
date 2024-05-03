package com.behzad.messenger.domain.usecase

import com.behzad.messenger.domain.Conversation
import kotlinx.coroutines.flow.Flow

interface GetConversationUseCase {

    suspend fun getConversation(conversationId: String) : Flow<Conversation>
}