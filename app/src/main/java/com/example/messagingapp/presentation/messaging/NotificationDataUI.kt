package com.example.messagingapp.presentation.messaging

data class NotificationDataUI(
    val sender: String,
    val message: String,
    val expirationDate: String?
)