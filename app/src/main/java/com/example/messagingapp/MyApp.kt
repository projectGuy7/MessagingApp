package com.example.messagingapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        val regularChannel = NotificationChannel(
            REGULAR_CHANNEL_ID,
            "Regular Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        regularChannel.setSound(null, null);
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        val urgentChannel = NotificationChannel(
            URGENT_CHANNEL_ID,
            "Urgent Channel",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            setSound(soundUri, audioAttributes)
            enableVibration(true)
        }
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(regularChannel)
        notificationManager.createNotificationChannel(urgentChannel)
    }

}

const val REGULAR_CHANNEL_ID = "regular_channel_id"
const val URGENT_CHANNEL_ID = "urgent_channel_id"