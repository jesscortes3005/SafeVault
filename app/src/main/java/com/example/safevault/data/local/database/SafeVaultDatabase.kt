package com.example.safevault.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.safevault.data.local.dao.*
import com.example.safevault.data.local.entity.*

@Database(
    entities = [
        UserEntity::class,
        NoteEntity::class,
        PhotoEntity::class,
        DocumentEntity::class,
        HistoryEntity::class,
        AnomalyEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class SafeVaultDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao
    abstract fun photoDao(): PhotoDao
    abstract fun documentDao(): DocumentDao
    abstract fun historyDao(): HistoryDao
    abstract fun anomalyDao(): AnomalyDao
}
