package com.behzad.messenger.domain.usecase

import com.behzad.messenger.domain.Conversation
import kotlinx.coroutines.flow.Flow

interface GetAllConversations {

    suspend fun getConversations() : Flow<List<Conversation>>

}

