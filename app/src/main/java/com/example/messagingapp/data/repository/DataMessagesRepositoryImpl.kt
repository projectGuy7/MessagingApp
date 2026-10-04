package com.example.messagingapp.data.repository

import com.example.messagingapp.data.db.DataMessageEntity
import com.example.messagingapp.data.db.DataMessagesDao
import com.example.messagingapp.data.mapper.toDataMessage
import com.example.messagingapp.data.mapper.toDataMessageEntity
import com.example.messagingapp.domain.messaging.DataMessage
import com.example.messagingapp.domain.repository.DataMessagesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DataMessagesRepositoryImpl @Inject constructor (
    val dao: DataMessagesDao
): DataMessagesRepository {
    override fun observeDataMessages(): Flow<List<DataMessage>> =
        dao.getMessages().map {
            it.map(DataMessageEntity::toDataMessage)
        }

    override suspend fun addDataMessage(message: DataMessage) {
        dao.insertMessage(message.toDataMessageEntity())
    }
}