package com.behzad.messenger.utils

import android.util.Log
import com.behzad.messenger.data.database.UserDao
import com.behzad.messenger.domain.User
import javax.inject.Inject


interface GetUserPhoneNumberUseCase {
    suspend fun registerUserIfNotExists(): String?
}

class GetUserPhoneNumberUseCaseImpl @Inject constructor(
    private val userDao: UserDao,
    private val phoneNumberRetriever: PhoneNumberRetriever,
) : GetUserPhoneNumberUseCase {

    override suspend fun registerUserIfNotExists(): String? {

        val phoneNumber = phoneNumberRetriever.getPhoneNumber()
        if (phoneNumber != null) {
            val user = userDao.getUserById(phoneNumber)
            Log.i("GetUserPhoneNumber", "User: $user")
            if (user == null) {
                userDao.insertUser(
                    User(
                        id = phoneNumber,
                        username = "User $phoneNumber",
                        createdAt = System.currentTimeMillis(),
                        isMine = true,
                    )
                )
            }
        }

        return phoneNumber

    }

}
