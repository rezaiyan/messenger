package com.behzad.messenger.domain.usecase

import com.behzad.messenger.domain.Conversation
import kotlinx.coroutines.flow.Flow

class GetAllConversationsImpl : GetAllConversations {
    override suspend fun getConversations(): Flow<List<Conversation>> {
        TODO("Not yet implemented")
    }
}