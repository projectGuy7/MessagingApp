package com.example.messagingapp.presentation.viewmodel

import com.example.messagingapp.presentation.messaging.NotificationDataUI

sealed interface MainIntent {
    data class SetNotificationData(val notificationData: NotificationDataUI): MainIntent
    data object RemoveNotificationData: MainIntent
}