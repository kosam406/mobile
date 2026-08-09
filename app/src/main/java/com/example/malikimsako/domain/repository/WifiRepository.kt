package com.example.malikimsako.domain.repository

import com.example.malikimsako.domain.model.NetworkStats
import kotlinx.coroutines.flow.Flow

interface WifiRepository {
    fun observeNetworkStats(): Flow<NetworkStats>
}