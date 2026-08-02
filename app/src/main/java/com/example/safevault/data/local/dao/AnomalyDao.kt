package com.example.safevault.data.local.dao

import androidx.room.*
import com.example.safevault.data.local.entity.AnomalyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AnomalyDao {
    @Query("SELECT * FROM anomalies ORDER BY timestamp DESC")
    fun getAllAnomalies(): Flow<List<AnomalyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnomaly(anomaly: AnomalyEntity)

    @Delete
    suspend fun deleteAnomaly(anomaly: AnomalyEntity)
}
