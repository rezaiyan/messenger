package com.behzad.messenger.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresApi
import javax.inject.Inject

class PhoneNumberRetriever @Inject constructor(private val context: Context){

    companion object {
        private const val TAG = "PhoneNumberRetriever"
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    fun getPhoneNumber(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            getPhoneNumbersFromSubscriptionManager(context)?.let {
                return it
            }
        }

        return getPhoneNumberFromTelephonyManager(context)
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private fun getPhoneNumbersFromSubscriptionManager(context: Context): String? {
        try {
            val sm = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val numbers = sm.activeSubscriptionInfoList.joinToString(separator = ";") { it.number }
            if (numbers.isNotBlank()) {
                return numbers.split(";").first()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting phone numbers from SubscriptionManager", e)
        }
        return null
    }

    @SuppressLint("MissingPermission", "HardwareIds")
    private fun getPhoneNumberFromTelephonyManager(context: Context): String? {
        try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            return tm.line1Number.takeIf { it?.isNotBlank() == true }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting phone number from TelephonyManager", e)
        }
        return null
    }
}
