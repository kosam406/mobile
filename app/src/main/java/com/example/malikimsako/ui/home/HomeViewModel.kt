package com.example.malikimsako.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.malikimsako.data.repository.WifiRepositoryImpl
import com.example.malikimsako.data.wifi.WifiDataSource
import com.example.malikimsako.domain.model.NetworkStats
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val wifiDataSource = WifiDataSource(application)
    private val wifiRepository = WifiRepositoryImpl(wifiDataSource)

    val networkStats: StateFlow<NetworkStats> = wifiRepository.observeNetworkStats()
        .stateIn(
            scope = kotlinx.coroutines.MainScope(), // Simplified for now
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = NetworkStats()
        )
}