package com.example.malikimsako.domain.model

data class NetworkStats(
    val ssid: String = "Unknown",
    val bssid: String = "Unknown",
    val rssi: Int = 0,
    val linkSpeed: Int = 0,
    val frequency: Int = 0,
    val downloadMbps: Double = 0.0,
    val uploadMbps: Double = 0.0,
    val pingMs: Long = 0,
    val jitter: Double = 0.0,
    val packetLoss: Double = 0.0
) {
    val signalPercentage: Int
        get() {
            // Convert RSSI to percentage (approximate)
            // -100 is 0%, -50 is 100%
            return when {
                rssi <= -100 -> 0
                rssi >= -50 -> 100
                else -> 2 * (rssi + 100)
            }
        }
}