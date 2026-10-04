package com.example.messagingapp.presentation

import android.annotation.SuppressLint
import android.util.Log
import com.example.messagingapp.domain.messaging.DataMessage
import com.example.messagingapp.domain.repository.DataMessagesRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService: FirebaseMessagingService() {

    @Inject
    lateinit var dataMessagesRepository: DataMessagesRepository
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val title = message.data["title"]
        val body = message.data["body"]
        val icon = message.data["icon"]
        if(title != null && body != null && icon != null) {
            scope.launch {
                dataMessagesRepository.addDataMessage(
                    DataMessage(
                        title = title,
                        message = body,
                        iconUrl = icon
                    )
                )
            }
        }
    }

    override fun onRegistered(installationId: String) {
        Log.i("FCM_TOKEN", installationId)
        super.onRegistered(installationId)
    }


    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}