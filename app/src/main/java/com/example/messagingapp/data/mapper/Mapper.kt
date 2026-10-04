package com.example.messagingapp.data.mapper

import com.example.messagingapp.data.db.DataMessageEntity
import com.example.messagingapp.domain.messaging.DataMessage
import com.example.messagingapp.presentation.messaging.DataMessageUI

fun DataMessageEntity.toDataMessage(): DataMessage {
    return DataMessage(
        iconUrl = iconUrl,
        title = title,
        message = message
    )
}

fun DataMessage.toDataMessageEntity(): DataMessageEntity {
    return DataMessageEntity(
        iconUrl = iconUrl,
        title = title,
        message = message
    )
}

fun DataMessage.toDataMessageUI(): DataMessageUI {
    return DataMessageUI(
        icon = iconUrl,
        title = title,
        body = message
    )
}