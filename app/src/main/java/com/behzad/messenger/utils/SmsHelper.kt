package com.behzad.messenger.utils

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.telephony.SmsManager
import javax.inject.Inject

class SmsHelper @Inject constructor(
    private val context: Context,
) {

    fun sendSMS(to: String, message: String, messageId: String) {

        val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(SmsManager::class.java)
        } else {
            SmsManager.getDefault()
        }

        val sentIntent = PendingIntent.getBroadcast(
            context,
            messageId.hashCode(),
            Intent("SMS_SENT").apply {
                putExtra("messageId", messageId)
            },
            PendingIntent.FLAG_IMMUTABLE
        )
        val deliveredIntent = PendingIntent.getBroadcast(
            context,
            messageId.hashCode(),
            Intent("SMS_DELIVERED").apply {
                putExtra("messageId", messageId)
            },
            PendingIntent.FLAG_IMMUTABLE
        )

        smsManager.sendTextMessage(to, null, message, sentIntent, deliveredIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(
                    SmsDeliveredReceiver(),
                    IntentFilter("android.intent.action.SMS_DELIVERED"),
                    Context.RECEIVER_NOT_EXPORTED,
                )
            }
        }


        smsManager.sendTextMessage(to, null, message, sentIntent, deliveredIntent)
    }
}
