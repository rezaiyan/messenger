package com.behzad.messenger.domain

import android.util.Log
import com.behzad.messenger.data.database.ConversationDao
import com.behzad.messenger.data.database.MessageDao
import com.behzad.messenger.data.database.UserDao
import com.behzad.messenger.ui.conversation.ConversationUiState
import com.behzad.messenger.ui.conversation.Messages
import com.behzad.messenger.ui.conversation.UiMessage
import com.behzad.messenger.utils.SmsHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import java.text.SimpleDateFormat
import javax.inject.Inject
import java.util.*


interface MessagesInteractor {
    fun getConversation(conversationId: String): Flow<ConversationUiState>
    fun sendMessage(to: String, content: String, conversationId: String?): Flow<String>
}

class MessagesInteractorImpl @Inject constructor(
    private val messagesDao: MessageDao,
    private val userDao: UserDao,
    private val conversationDao: ConversationDao,
    private val smsHelper: SmsHelper,
) : MessagesInteractor {

    override fun getConversation(conversationId: String): Flow<ConversationUiState> {
        return messagesDao.getMessagesForConversation(conversationId).map { messages ->
            Log.d("MessagesInteractor", "conversationId: $conversationId")
            val myId = userDao.getMyUserById()?.id
            if (myId == null) {
                ConversationUiState.Error("Restart the app to fetch your user id")
            } else {
                val toolbarTitle = if (messages.isEmpty()) {
                    "New conversation"
                } else {
                    "Conversation with ${messages.first().receiverId}"
                }
                ConversationUiState.Loaded(
                    toolbarTitle = toolbarTitle,
                    messages = Messages(messages.map { it.toUiConversation(myId) })
                )
            }
        }.onStart {
            emit(ConversationUiState.Loading)
        }
    }

    override fun sendMessage(to: String, content: String, conversationId: String?): Flow<String> =
        flow {
            // Generate or reuse the conversation ID
            val cId = if (conversationId.isNullOrBlank() || conversationId == "-1") {
                UUID.randomUUID().toString()
            } else {
                conversationId
            }

            // Prepare the message object
            val message = Message(
                conversationId = cId,
                content = content,
                createdAt = System.currentTimeMillis(),
                receiverId = to,
                senderId = userDao.getMyUserById()?.id ?: "",
                status = MessageStatus.SENT
            )

            // Ensure user exists before sending a message
            createUserIfDoesntExist(to)

            // Sequentially execute insert operations
            conversationDao.insertConversation(Conversation(message.conversationId))
            messagesDao.insertMessage(message)
            smsHelper.sendSMS(to, content, message.id.toString())
            emit("")  // Emit true upon successful completion of inserts
        }.catch { exception ->
            Log.d("MessagesInteractor", "Error sending message", exception)
            emit(exception.message.orEmpty())  // Emit false if any exception occurs
        }

    private suspend fun createUserIfDoesntExist(to: String) {
        val user = userDao.getUserById(to)
        if (user == null) {
            userDao.insertUser(
                User(
                    id = to,
                    username = "User $to",
                    createdAt = System.currentTimeMillis(),
                    isMine = false,
                )
            )
        }
    }
}

private fun Message.toUiConversation(myId: String): UiMessage {
    return UiMessage(
        conversationId = this.conversationId,
        id = id.toString(),
        content = content,
        createdAt = formatTimestamp(createdAt),
        receiverId = receiverId,
        senderId = senderId,
        status = status,
        isMine = senderId == myId
    )
}

fun formatTimestamp(timestamp: Long, timeZone: TimeZone = TimeZone.getDefault()): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("dd MMMM yyyy, HH:mm:ss", Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(date)
}
