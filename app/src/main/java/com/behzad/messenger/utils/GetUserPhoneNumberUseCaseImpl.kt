package com.behzad.messenger.utils

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import com.behzad.messenger.data.database.UserDao
import com.behzad.messenger.domain.User
import javax.inject.Inject


interface GetUserPhoneNumberUseCase {
    suspend fun registerUserIfNotExists(): String?
}

class GetUserPhoneNumberUseCaseImpl @Inject constructor(
    private val userDao: UserDao,
    private val phoneNumberRetriever: PhoneNumberRetriever,
    private val permissionHandler: PermissionHandler
) : GetUserPhoneNumberUseCase {

    companion object {
        const val PERMISSION_REQUEST_READ_PHONE_STATE = 1
    }

    override suspend fun registerUserIfNotExists(): String? {
        val hasReadPhoneStatePermission =
            !permissionHandler.hasPermission(Manifest.permission.READ_PHONE_STATE)
        val hasReadPhoneNumberPermission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                !permissionHandler.hasPermission(Manifest.permission.READ_PHONE_NUMBERS)
            } else {
                true
            }
        val hasSmsReadPermission = !permissionHandler.hasPermission(Manifest.permission.READ_SMS)

        if (hasReadPhoneStatePermission && hasReadPhoneNumberPermission && hasSmsReadPermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                permissionHandler.requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_PHONE_NUMBERS,
                        Manifest.permission.READ_SMS,
                    ),
                    PERMISSION_REQUEST_READ_PHONE_STATE
                )
            } else {
                permissionHandler.requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.READ_SMS,
                    ),
                    PERMISSION_REQUEST_READ_PHONE_STATE
                )
            }
            return null
        }
        val phoneNumber = phoneNumberRetriever.getPhoneNumber()
        if (phoneNumber != null) {
            val user = userDao.getUserById(phoneNumber)
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
