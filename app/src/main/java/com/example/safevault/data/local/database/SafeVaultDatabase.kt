package com.example.safevault.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.safevault.data.local.dao.*
import com.example.safevault.data.local.entity.*

/**
 * Esta es la base de datos de la app usando Room.
 * Aquí le decimos qué tablas tenemos (entities) y qué versión de la base es.
 */
@Database(
    entities = [
        UserEntity::class,
        NoteEntity::class,
        PhotoEntity::class,
        DocumentEntity::class,
        HistoryEntity::class,
        AnomalyEntity::class // Esta es la tabla nueva para las fotos de intrusos
    ],
    version = 2, // Subimos a la versión 2 porque añadimos la tabla de anomalías
    exportSchema = false
)
abstract class SafeVaultDatabase : RoomDatabase() {
    // Estas funciones nos dan acceso a los DAOs (las interfaces para consultar datos)
    abstract fun userDao(): UserDao
    abstract fun noteDao(): NoteDao
    abstract fun photoDao(): PhotoDao
    abstract fun documentDao(): DocumentDao
    abstract fun historyDao(): HistoryDao
    abstract fun anomalyDao(): AnomalyDao
}
