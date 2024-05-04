package com.behzad.messenger.utils

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.behzad.messenger.data.database.MessageDao
import com.behzad.messenger.domain.MessageStatus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SmsDeliveredReceiver : BroadcastReceiver() {

    @Inject
    lateinit var messageDao: MessageDao
    override fun onReceive(context: Context, intent: Intent) {
        val messageId = intent.getStringExtra("messageId")
        if (messageId != null) {
            when (resultCode) {
                RESULT_OK -> {
                    CoroutineScope(Dispatchers.Main).launch {
                        messageDao.getMessage(messageId).let {
                            messageDao.insertMessage(it.copy(status = MessageStatus.DELIVERED))
                        }
                    }

                    // Message delivered successfully
                    Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show()
                }

                RESULT_CANCELED -> {
                    // Message delivery failed
                    Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
