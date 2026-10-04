package com.example.messagingapp.di

import android.content.Context
import androidx.room3.Room
import com.example.messagingapp.data.db.DataMessagesDB
import com.example.messagingapp.data.db.DataMessagesDao
import com.example.messagingapp.data.repository.DataMessagesRepositoryImpl
import com.example.messagingapp.domain.repository.DataMessagesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
abstract class MessagingModule {

    @Binds
    @Singleton
    abstract fun bindDataMessagesRepository(
        dataMessagesRepositoryImpl: DataMessagesRepositoryImpl
    ): DataMessagesRepository

    companion object {
        @Provides
        @Singleton
        fun provideDataMessagesDatabase(
            @ApplicationContext context: Context
        ): DataMessagesDB {
            return Room.databaseBuilder(
                context,
                DataMessagesDB::class.java,
                "notes.db"
            ).build()
        }
        @Provides
        fun provideDataMessagesDao(database: DataMessagesDB): DataMessagesDao {
            return database.dataMessagesDao()
        }
    }
}