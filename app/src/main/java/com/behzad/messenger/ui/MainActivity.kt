package com.behzad.messenger.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.behzad.messenger.ui.conversation.ConversationScreen
import com.behzad.messenger.ui.main.MainScreen
import com.behzad.messenger.ui.theme.MessengerTheme
import com.behzad.messenger.utils.GetUserPhoneNumberUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val coroutineScope  = rememberCoroutineScope()

                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestMultiplePermissions()
                    ) { permissions ->
                        val allPermissionsGranted = permissions.entries.all { it.value }
                        if (allPermissionsGranted) {
                            coroutineScope.launch {
                                getUserPhoneNumberUseCase.registerUserIfNotExists()
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Permissions are required to use the app",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                    }

                   LaunchedEffect(Unit) {
                       coroutineScope.launch {
                           delay(2000L)
                           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                               permissionLauncher.launch(
                                   arrayOf(
                                       Manifest.permission.READ_PHONE_STATE,
                                       Manifest.permission.READ_SMS,
                                       Manifest.permission.READ_PHONE_NUMBERS
                                   )
                               )
                           } else {
                               permissionLauncher.launch(
                                   arrayOf(
                                       Manifest.permission.READ_PHONE_STATE,
                                       Manifest.permission.READ_SMS
                                   )
                               )

                           }
                       }
                   }
                    ChatApp()
                }
            }
        }
    }
}

@Composable
fun PermissionsRequester(onGrant: () -> Unit, onDeny: () -> Unit = {}) {
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