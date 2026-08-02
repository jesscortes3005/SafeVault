package com.example.safevault.di

import android.content.Context
import androidx.room.Room
import com.example.safevault.data.local.dao.*
import com.example.safevault.data.local.database.SafeVaultDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SafeVaultDatabase {
        return Room.databaseBuilder(
            context,
            SafeVaultDatabase::class.java,
            "safe_vault_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(db: SafeVaultDatabase): UserDao = db.userDao()

    @Provides
    fun provideNoteDao(db: SafeVaultDatabase): NoteDao = db.noteDao()

    @Provides
    fun providePhotoDao(db: SafeVaultDatabase): PhotoDao = db.photoDao()

    @Provides
    fun provideDocumentDao(db: SafeVaultDatabase): DocumentDao = db.documentDao()

    @Provides
    fun provideHistoryDao(db: SafeVaultDatabase): HistoryDao = db.historyDao()

    @Provides
    fun provideAnomalyDao(db: SafeVaultDatabase): AnomalyDao = db.anomalyDao()
}
