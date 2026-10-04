package com.example.messagingapp.domain.repository

import com.example.messagingapp.domain.messaging.DataMessage
import kotlinx.coroutines.flow.Flow


interface DataMessagesRepository {
    fun observeDataMessages(): Flow<List<DataMessage>>
    suspend fun addDataMessage(message: DataMessage)
}