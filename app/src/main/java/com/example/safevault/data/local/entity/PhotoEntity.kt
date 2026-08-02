package com.example.safevault.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val filePath: String,
    val description: String?,
    val timestamp: Long = System.currentTimeMillis()
)
