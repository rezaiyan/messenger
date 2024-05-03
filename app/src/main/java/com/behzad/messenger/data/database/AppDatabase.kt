package com.behzad.messenger.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.behzad.messenger.domain.Conversation
import com.behzad.messenger.domain.Message
import com.behzad.messenger.domain.User

@Database(
    entities = [
        User::class,
        Message::class,
        Conversation::class,
    ],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
}