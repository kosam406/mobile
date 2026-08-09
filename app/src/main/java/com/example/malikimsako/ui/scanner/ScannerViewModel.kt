package com.example.malikimsako.ui.scanner

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.malikimsako.data.repository.WifiRepositoryImpl
import com.example.malikimsako.data.wifi.WifiDataSource
import com.example.malikimsako.domain.model.NetworkStats
import com.example.malikimsako.sensors.MovementTracker
import com.example.malikimsako.sensors.Position
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class Measurement(
    val position: Position,
    val stats: NetworkStats,
    val timestamp: Long = System.currentTimeMillis()
)

data class ScannerState(
    val isScanning: Boolean = false,
    val currentPosition: Position = Position(0f, 0f, 0f),
    val currentStats: NetworkStats = NetworkStats(),
    val measurements: List<Measurement> = emptyList()
)

class ScannerViewModel(application: Application) : AndroidViewModel(application) {
    private val wifiDataSource = WifiDataSource(application)
    private val wifiRepository = WifiRepositoryImpl(wifiDataSource)
    private val movementTracker = MovementTracker(application)

    private val _state = MutableStateFlow(ScannerState())
    val state = _state.asStateFlow()

    private var scanJob: Job? = null

    fun startScan() {
        _state.value = _state.value.copy(isScanning = true, measurements = emptyList())
        movementTracker.reset()
        movementTracker.startTracking()
        
        scanJob = viewModelScope.launch {
            combine(
                wifiRepository.observeNetworkStats(),
                movementTracker.position
            ) { stats, pos ->
                Measurement(pos, stats)
            }.collect { measurement ->
                _state.update { 
                    it.copy(
                        currentPosition = measurement.position,
                        currentStats = measurement.stats,
                        measurements = it.measurements + measurement
                    )
                }
            }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        movementTracker.stopTracking()
        _state.value = _state.value.copy(isScanning = false)
    }

    override fun onCleared() {
        super.onCleared()
        stopScan()
    }
}