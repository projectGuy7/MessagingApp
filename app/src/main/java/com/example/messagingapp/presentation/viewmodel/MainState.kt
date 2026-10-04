package com.example.messagingapp.presentation.viewmodel

import com.example.messagingapp.presentation.messaging.DataMessageUI
import com.example.messagingapp.presentation.messaging.NotificationDataUI

data class MainState(
    val dataMessages: List<DataMessageUI> = emptyList(),
    val notificationData: NotificationDataUI? = null,
)
