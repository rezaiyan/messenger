package com.behzad.messenger.data.database

import androidx.room.TypeConverter
import com.behzad.messenger.domain.MessageStatus

class Converters {

    @TypeConverter
    fun fromMessageStatus(status: MessageStatus): String = status.name

    @TypeConverter
    fun toMessageStatus(status: String): MessageStatus = MessageStatus.valueOf(status)
}
