package com.example.messagingapp.data.db

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(
    tableName = DataMessageEntity.TABLE_NAME
)
data class DataMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val iconUrl: String,
    val title: String,
    val message: String
) {
    companion object {
        const val TABLE_NAME = "data_messages"
    }
}
