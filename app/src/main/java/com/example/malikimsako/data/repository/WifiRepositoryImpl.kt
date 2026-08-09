package com.example.malikimsako.data.repository

import com.example.malikimsako.data.wifi.WifiDataSource
import com.example.malikimsako.domain.model.NetworkStats
import com.example.malikimsako.domain.repository.WifiRepository
import kotlinx.coroutines.flow.Flow

class WifiRepositoryImpl(private val wifiDataSource: WifiDataSource) : WifiRepository {
    override fun observeNetworkStats(): Flow<NetworkStats> {
        return wifiDataSource.getWifiStatsFlow()
    }
}