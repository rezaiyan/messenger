package com.behzad.messenger.utils

interface PermissionHandler {
    fun hasPermission(permission: String): Boolean
    fun requestPermissions(permissions: Array<String>, requestCode: Int)
}
