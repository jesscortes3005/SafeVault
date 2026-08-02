package com.example.safevault.data.repository

import com.example.safevault.data.local.dao.AnomalyDao
import com.example.safevault.data.local.entity.AnomalyEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * El Repositorio es como un "mandadero" que va a la base de datos por nosotros.
 * Así el resto de la app no tiene que saber cómo se guardan las cosas exactamente.
 */
@Singleton
class AnomalyRepository @Inject constructor(
    private val anomalyDao: AnomalyDao
) {
    // Trae todas las fotos de intrusos de la base de datos
    fun getAllAnomalies(): Flow<List<AnomalyEntity>> = anomalyDao.getAllAnomalies()

    // Guarda una nueva foto de alguien que intentó entrar sin permiso
    suspend fun addAnomaly(imagePath: String) {
        anomalyDao.insertAnomaly(AnomalyEntity(imagePath = imagePath))
    }

    // Borra un registro si ya no lo queremos
    suspend fun deleteAnomaly(anomaly: AnomalyEntity) {
        anomalyDao.deleteAnomaly(anomaly)
    }
}
