package com.behzad.messenger.domain.usecase

import com.behzad.messenger.domain.Message
import kotlinx.coroutines.flow.Flow

interface SendMessageUseCase {

    suspend fun send(message: Message): Flow<Boolean>
}