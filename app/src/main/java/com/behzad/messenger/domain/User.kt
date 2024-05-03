package com.behzad.messenger.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey val id: String,
    val username: String,
    val createdAt: Long,
    val isMine : Boolean,
)