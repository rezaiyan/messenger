package com.behzad.messenger.domain

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(foreignKeys = [
    ForeignKey(entity = Conversation::class,
        parentColumns = ["id"],
        childColumns = ["conversationId"],
        onDelete = ForeignKey.NO_ACTION)
])
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val conversationId: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val createdAt: Long,
    val status: MessageStatus
)
