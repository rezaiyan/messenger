package com.behzad.messenger.di

import android.content.Context
import com.behzad.messenger.data.database.ConversationDao
import com.behzad.messenger.data.database.MessageDao
import com.behzad.messenger.data.database.UserDao
import com.behzad.messenger.domain.MessagesInteractor
import com.behzad.messenger.domain.MessagesInteractorImpl
import com.behzad.messenger.domain.ConversationListInteractor
import com.behzad.messenger.domain.ConversationListInteractorImpl
import com.behzad.messenger.utils.SmsHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object InteractorModule {

    @Provides
    @Singleton
    fun provideSMSHelper(@ApplicationContext context: Context): SmsHelper {
        return SmsHelper(context)
    }

    @Provides
    @Singleton
    fun provideMessagesInteractor(
        messageDao: MessageDao,
        userDao: UserDao,
        conversationDao: ConversationDao,
        smsHelper: SmsHelper,
    ): MessagesInteractor =
        MessagesInteractorImpl(messageDao, userDao, conversationDao, smsHelper)

    @Provides
    @Singleton
    fun provideConversationInteractor(
        conversationDao: ConversationDao,
        messageDao: MessageDao
    ): ConversationListInteractor =
        ConversationListInteractorImpl(conversationDao, messageDao)
}