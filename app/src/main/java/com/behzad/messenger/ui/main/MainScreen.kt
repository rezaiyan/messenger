package com.behzad.messenger.ui.main

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.behzad.messenger.ui.conversation.ErrorScreen
import com.behzad.messenger.ui.conversation.LoadingScreen

@Composable
fun MainScreen(onConversationCLick: (String) -> Unit) {
    val viewModel = hiltViewModel<MainViewModel>()

    val state by viewModel.state.collectAsState()

    when (val uiState = state) {
        is MainUiState.Error -> ErrorScreen(
            errorMessage = uiState.message,
        )

        is MainUiState.Loaded -> LoadedConversationListScreen(
            uiState.conversations,
            onConversationCLick = onConversationCLick
        )

        MainUiState.Loading -> LoadingScreen()
    }

}

@Composable
fun LoadedConversationListScreen(
    conversations: Conversations,
    onConversationCLick: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(conversations.conversations) { conversation ->
                ConversationItem(
                    title = conversation.title,
                    subtitle = conversation.subtitle,
                    onClick = { onConversationCLick(conversation.id) }
                )

            }
        }

        FloatingActionButton(
            onClick = { onConversationCLick("-1") },
            modifier = Modifier
                .padding(32.dp)
                .align(Alignment.BottomEnd)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.padding(8.dp)
            ) {
                Text("New conversation")
                Icon(Icons.Default.Add, contentDescription = "Add conversation")
            }
        }
    }

}

@Composable
fun ConversationItem(title: String, subtitle: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.onSecondary)
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(text = title, fontSize = 24.sp)
        Text(text = subtitle, fontSize = 16.sp)
    }
}