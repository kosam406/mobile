package com.example.malikimsako.data.wifi

import android.content.Context
import android.net.wifi.WifiManager
import com.example.malikimsako.data.network.PingUtility
import com.example.malikimsako.domain.model.NetworkStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class WifiDataSource(private val context: Context) {
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun getWifiStatsFlow(intervalMs: Long = 2000): Flow<NetworkStats> = flow {
        while (true) {
            val wifiInfo = wifiManager.connectionInfo
            val ping = withContext(Dispatchers.IO) {
                PingUtility.getPing()
            }
            
            val stats = NetworkStats(
                ssid = wifiInfo.ssid.removeSurrounding("\""),
                bssid = wifiInfo.bssid ?: "Unknown",
                rssi = wifiInfo.rssi,
                linkSpeed = wifiInfo.linkSpeed,
                frequency = wifiInfo.frequency,
                pingMs = ping
            )
            emit(stats)
            delay(intervalMs)
        }
    }
}