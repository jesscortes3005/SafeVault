package com.example.safevault.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "anomalies")
data class AnomalyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String,
    val timestamp: Long = System.currentTimeMillis(),
    val reason: String = "Failed login attempts"
)
