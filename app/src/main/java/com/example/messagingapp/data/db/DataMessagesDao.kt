package com.example.messagingapp.data.db

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DataMessagesDao {

    @Query("SELECT * FROM ${DataMessageEntity.TABLE_NAME}")
    fun getMessages(): Flow<List<DataMessageEntity>>

    @Insert
    suspend fun insertMessage(message: DataMessageEntity)

}