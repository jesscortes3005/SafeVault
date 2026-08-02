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

    val anomalies = anomalyRepository.getAllAnomalies()
}
