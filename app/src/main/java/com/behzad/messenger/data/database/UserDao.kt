package com.behzad.messenger.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.behzad.messenger.domain.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM User WHERE id = :userId")
    suspend fun getUserById(userId: String): User?
    @Query("SELECT * FROM User WHERE isMine = :isMine")
    suspend fun getMyUserById(isMine: Boolean = true): User?
}