package com.example.messagingapp.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.messagingapp.presentation.messaging.NotificationDataUI
import com.example.messagingapp.presentation.viewmodel.MainIntent
import com.example.messagingapp.presentation.viewmodel.MainState
import com.example.messagingapp.presentation.viewmodel.MainViewModel
import com.example.messagingapp.ui.theme.MessagingAppTheme
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.serialization.Serializable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.example.messagingapp.presentation.messaging.DataMessageUI

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseMessaging.getInstance().register()
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    Log.e("ERROR", "Failed to register with Firebase Cloud Messaging", task.exception)
                }
            }
        isPushNotificationTriggered(intent)
        enableEdgeToEdge()
        setContent {
            MessagingAppTheme {
                val state by viewModel.state.collectAsStateWithLifecycle()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationRoot(
                        mainState = state,
                        onIntent = viewModel::onIntent,
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                            .fillMaxSize()
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        isPushNotificationTriggered(intent)
    }

    private fun isPushNotificationTriggered(intent: Intent?): Boolean {
        val tag = "FCM_TRIGGER"

        val data = intent?.extras
        if (data == null) {
            Log.d(tag, "isPushNotificationTriggered: No intent extras found.")
            return false
        }

        Log.i(tag, intent.extras?.keySet().toString())

        val sender = data.getString("sender")
        val message = data.getString("message")

        if (sender.isNullOrEmpty() || message.isNullOrEmpty()) {
            Log.d(tag, "isPushNotificationTriggered: Non-FCM Intent (missing sender or message).")
            return false
        }
        val expirationDate = data.getString("expirationDate") ?: data.getString("expiration_date")

        Log.d(
            tag,
            """
        🚀 Push Notification Triggered Successfully!
        ├─ Sender: $sender
        ├─ Message: $message
        └─ Expiration Date: ${expirationDate ?: "N/A"}
        """.trimIndent()
        )

        viewModel.onIntent(
            MainIntent.SetNotificationData(
                NotificationDataUI(
                    sender = sender,
                    message = message,
                    expirationDate = expirationDate
                )
            )
        )

        // Clear intent extras to avoid re-triggering on activity configuration changes (e.g., rotation)
        intent.removeExtra("sender")
        intent.removeExtra("message")
        intent.removeExtra("isUrgent")
        intent.removeExtra("is_urgent")
        intent.removeExtra("expirationDate")
        intent.removeExtra("expiration_date")

        Log.d(tag, "FCM intent extras cleared from Intent.")

        return true
    }
}

@Composable
fun NavigationRoot(
    mainState: MainState,
    onIntent: (MainIntent) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by rememberUpdatedState(mainState)
    val backStack = rememberNavBackStack(Route.MessageList)
    LaunchedEffect(mainState.notificationData) {
        val hasDetailsInStack = backStack.lastOrNull() == Route.NotificationDetails
        if (state.notificationData != null && !hasDetailsInStack) {
            backStack.add(Route.NotificationDetails)
        } else if (state.notificationData == null && hasDetailsInStack) {
            backStack.removeAt(backStack.lastIndex)
        }
    }
    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = { key ->
            when(key) {
                Route.MessageList -> {
                    NavEntry(key) {
                        MessageListScreen(
                            messages = state.dataMessages,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Route.NotificationDetails -> {
                    NavEntry(key) {
                        val notificationData = state.notificationData
                        BackHandler {
                            onIntent(MainIntent.RemoveNotificationData)
                            if (backStack.lastOrNull() == Route.NotificationDetails) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        }
                        NotificationDetailsScreen(
                            data = notificationData,
                            onBackClick = {
                                onIntent(MainIntent.RemoveNotificationData)
                                if (backStack.lastOrNull() == Route.NotificationDetails) {
                                    backStack.removeAt(backStack.lastIndex)
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                else -> error("Unknown route: $key")
            }
        }
    )
}

@Serializable
sealed interface Route: NavKey {

    @Serializable
    object MessageList: Route

    @Serializable
    object NotificationDetails: Route

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailsScreen(
    data: NotificationDataUI?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Notification Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (data == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text("Couldn't load the notification")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Sender
                        Text(
                            text = "Sender",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = data.sender,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Message
                        Text(
                            text = "Message",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = data.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Expiration Date (if available)
                        if (!data.expirationDate.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Expiration Date",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = data.expirationDate,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageListScreen(
    messages: List<DataMessageUI>,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = "Messages") }
            )
        }
    ) { innerPadding ->
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No messages found.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    MessageCard(message = message)
                }
            }
        }
    }
}

@Composable
fun MessageCard(
    message: DataMessageUI,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = message.icon,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = message.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = message.body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}