package com.example.safevault.data.repository

import com.example.safevault.data.local.dao.AnomalyDao
import com.example.safevault.data.local.entity.AnomalyEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnomalyRepository @Inject constructor(
    private val anomalyDao: AnomalyDao
) {
    fun getAllAnomalies(): Flow<List<AnomalyEntity>> = anomalyDao.getAllAnomalies()

    suspend fun addAnomaly(imagePath: String) {
        anomalyDao.insertAnomaly(AnomalyEntity(imagePath = imagePath))
    }

    suspend fun deleteAnomaly(anomaly: AnomalyEntity) {
        anomalyDao.deleteAnomaly(anomaly)
    }
}
