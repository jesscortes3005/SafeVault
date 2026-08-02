package com.example.safevault.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.safevault.data.repository.AnomalyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val anomalyRepository: AnomalyRepository
) : ViewModel() {

    fun recordAnomaly(imagePath: String) {
        viewModelScope.launch {
            anomalyRepository.addAnomaly(imagePath)
        }
    }

    fun deleteAnomaly(anomaly: com.example.safevault.data.local.entity.AnomalyEntity) {
        viewModelScope.launch {
            anomalyRepository.deleteAnomaly(anomaly)
        }
    }

    val anomalies = anomalyRepository.getAllAnomalies()
}
