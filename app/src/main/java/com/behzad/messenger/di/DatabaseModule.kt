package com.behzad.messenger.di

import android.content.Context
import androidx.room.Room
import com.behzad.messenger.data.database.AppDatabase
import com.behzad.messenger.data.database.ConversationDao
import com.behzad.messenger.data.database.MessageDao
import com.behzad.messenger.data.database.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    fun provideContext(@ApplicationContext context: Context): Context = context

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
    @Provides
    fun provideMessageDao(database: AppDatabase): MessageDao = database.messageDao()
    @Provides
    fun provideConversationDao(database: AppDatabase): ConversationDao = database.conversationDao()
}
