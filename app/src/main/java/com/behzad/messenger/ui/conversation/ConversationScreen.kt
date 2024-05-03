@file:OptIn(ExperimentalComposeUiApi::class)

package com.behzad.messenger.ui.conversation

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@ExperimentalMaterial3Api
@Composable
fun ConversationScreen() {
    val viewModel = hiltViewModel<ConversationViewModel>()
    val state by viewModel.state.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val events by viewModel.event.collectAsStateWithLifecycle(
        initialValue = ConversationUiEvent.None,
        lifecycle = lifecycleOwner.lifecycle
    )

    LaunchedEffect(events) {
        when (val currentEvent = events) {
            is ConversationUiEvent.Error -> Toast.makeText(
                context,
                currentEvent.message,
                Toast.LENGTH_SHORT
            ).show()

            ConversationUiEvent.None -> {

            }

            ConversationUiEvent.SentMessage -> Toast.makeText(
                context,
                "Message Sent!",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    when (val uiState = state) {
        is ConversationUiState.Error -> ErrorScreen(
            errorMessage = uiState.message,
        )

        is ConversationUiState.Loaded -> LoadedConversationScreen(
            toolbarTitle = uiState.toolbarTitle,
            chatMessages = uiState.messages.messages,
            onSendMessage = viewModel::sendMessage
        )

        ConversationUiState.Loading -> LoadingScreen()
    }
}


@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(errorMessage: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = errorMessage,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}


@ExperimentalMaterial3Api
@Composable
fun LoadedConversationScreen(
    toolbarTitle: String,
    chatMessages: List<UiMessage>,
    onSendMessage: (String, String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = toolbarTitle, fontSize = 16.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            )
        },
        content = {
            ConversationLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = it.calculateTopPadding()),
                chatMessages, onSendMessage
            )
        }
    )
}

@Composable
fun ConversationLayout(
    modifier: Modifier = Modifier,
    chatMessages: List<UiMessage>,
    onSendMessage: (String, String) -> Unit
) {
    ConstraintLayout(modifier = modifier.fillMaxWidth()) {
        val (topInput, list, bottomInput) = createRefs()

        var receiverId by remember { mutableStateOf(chatMessages.firstOrNull()?.receiverId.orEmpty()) }
        if (chatMessages.isEmpty()) {
            MessageInput(
                modifier = Modifier
                    .constrainAs(topInput) {
                        top.linkTo(parent.top)
                    }
                    .fillMaxWidth(),
                hint = "Phone number",
                action = ImeAction.Done,
                keyboardType = KeyboardType.Phone,
                value = receiverId,
                onTextChange = { phoneNumber ->
                    receiverId = phoneNumber
                },
            )
        }
        val listState = rememberLazyListState()
        LaunchedEffect(key1 = chatMessages) {
            snapshotFlow { chatMessages.size }
                .collect {
                    if (it == 0) return@collect
                    listState.animateScrollToItem(it - 1)
                }
        }

        ChatMessageList(
            modifier = Modifier
                .constrainAs(list) {
                    top.linkTo(topInput.bottom, margin = 16.dp)
                    bottom.linkTo(bottomInput.top, margin = 16.dp)
                }
                .fillMaxWidth(),
            state = listState,
            chatMessages = chatMessages,
        )

        FooterInput(
            modifier = Modifier
                .constrainAs(bottomInput) {
                    bottom.linkTo(parent.bottom, margin = 16.dp)
                }
                .padding(top = 8.dp)
                .fillMaxWidth(),
            receiverId = receiverId,
            onSendMessage = onSendMessage
        )
    }
}

@Composable
fun FooterInput(
    modifier: Modifier = Modifier,
    receiverId: String,
    onSendMessage: (String, String) -> Unit
) {
    var rememberMessage by remember { mutableStateOf("") }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        MessageInput(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            hint = "Type a message...",
            value = rememberMessage,
            action = ImeAction.Send,
            keyboardType = KeyboardType.Text,
            onTextChange = { rememberMessage = it }
        ) { message ->
            if (message.isEmpty()) return@MessageInput
            onSendMessage(receiverId, message)
            rememberMessage = ""
        }
        Icon(
            modifier = Modifier
                .size(42.dp)
                .padding(8.dp)
                .clickable {
                    if (rememberMessage.isEmpty()) return@clickable
                    onSendMessage(receiverId, rememberMessage)
                    rememberMessage = ""
                },
            imageVector = Icons.Default.Send,
            contentDescription = "Send Message",
        )
    }

}

@Composable
fun ChatMessageList(
    modifier: Modifier = Modifier,
    state: LazyListState,
    chatMessages: List<UiMessage>
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth(),
        state = state,
    ) {
        items(chatMessages, key = { it.id }) {
            AnimatedChatMessageItem(it)
        }
    }
}

@Composable
fun AnimatedChatMessageItem(message: UiMessage) {
    AnimatedVisibility(
        visible = true,
        enter = expandVertically(animationSpec = tween(300)) + fadeIn(animationSpec = tween(300)),
        exit = shrinkVertically(animationSpec = tween(300)) + fadeOut(animationSpec = tween(300))
    ) {
        ChatMessageItem(message)
    }
}

@Composable
fun ChatMessageItem(message: UiMessage) {
    val horizontalAlignment = if (message.isMine) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        horizontalAlignment = horizontalAlignment
    ) {
        Text(
            text = message.content,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(
                    color = if (message.isMine) Color.Green else Color.Gray,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(12.dp)
        )
        Text(
            text = message.createdAt,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun MessageInput(
    modifier: Modifier = Modifier,
    hint: String,
    value: String,
    action: ImeAction,
    keyboardType: KeyboardType,
    onTextChange: (String) -> Unit = {},
    onDone: (String) -> Unit = {}
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = value,
            onValueChange = {
                onTextChange(it)
            },
            keyboardOptions = KeyboardOptions(imeAction = action, keyboardType = keyboardType),
            keyboardActions = KeyboardActions(onSend = {
                onDone(value)
                keyboardController?.hide()
            }),
            placeholder = { Text(hint) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
