package com.behzad.messenger.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.behzad.messenger.ui.conversation.ConversationScreen
import com.behzad.messenger.ui.main.MainScreen
import com.behzad.messenger.ui.theme.MessengerTheme
import com.behzad.messenger.utils.GetUserPhoneNumberUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var getUserPhoneNumberUseCase: GetUserPhoneNumberUseCase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MessengerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    val coroutineScope  = rememberCoroutineScope()
                   LaunchedEffect(Unit) {
                       coroutineScope.launch {
                           getUserPhoneNumberUseCase.registerUserIfNotExists()
                       }
                   }
                    ChatApp()
                }
            }
        }
    }
}

@ExperimentalMaterial3Api
@Composable
fun ChatApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                onConversationCLick = { conversationId ->
                    navController.navigate("conversation/$conversationId")
                }
            )
        }
        composable("conversation/{conversationId}") {
            ConversationScreen()
        }
    }
}