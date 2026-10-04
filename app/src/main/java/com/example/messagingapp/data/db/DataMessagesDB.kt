package com.example.messagingapp.data.db

import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [DataMessageEntity::class],
    version = 1,
    exportSchema = true
)
abstract class DataMessagesDB: RoomDatabase() {
    abstract fun dataMessagesDao(): DataMessagesDao
}