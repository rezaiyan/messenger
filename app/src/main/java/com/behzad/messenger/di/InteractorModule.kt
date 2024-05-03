package com.behzad.messenger.di

import com.behzad.messenger.data.database.ConversationDao
import com.behzad.messenger.data.database.MessageDao
import com.behzad.messenger.data.database.UserDao
import com.behzad.messenger.ui.conversation.MessagesInteractor
import com.behzad.messenger.ui.conversation.MessagesInteractorImpl
import com.behzad.messenger.ui.main.ConversationListInteractor
import com.behzad.messenger.ui.main.ConversationListInteractorImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class InteractorModule {

    @Provides
    @Singleton
    fun provideMessagesInteractor(
        messageDao: MessageDao,
        userDao: UserDao,
        conversationDao: ConversationDao
    ): MessagesInteractor =
        MessagesInteractorImpl(messageDao, userDao, conversationDao)

    @Provides
    @Singleton
    fun provideConversationInteractor(
        conversationDao: ConversationDao,
        messageDao: MessageDao
    ): ConversationListInteractor =
        ConversationListInteractorImpl(conversationDao, messageDao)
}