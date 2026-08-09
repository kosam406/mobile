package com.example.malikimsako.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_sessions")
data class ScanSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roomName: String,
    val startTime: Long,
    val endTime: Long = 0,
    val duration: Long = 0
)

@Entity(tableName = "measurement_points")
data class MeasurementPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val scanSessionId: Long,
    val timestamp: Long,
    val x: Float,
    val y: Float,
    val heading: Float,
    val rssi: Int,
    val downloadMbps: Double,
    val uploadMbps: Double,
    val pingMs: Long,
    val jitter: Double,
    val packetLoss: Double
)