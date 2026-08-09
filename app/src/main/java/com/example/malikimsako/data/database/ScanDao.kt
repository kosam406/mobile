package com.example.malikimsako.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanDao {
    @Insert
    suspend fun insertSession(session: ScanSessionEntity): Long

    @Update
    suspend fun updateSession(session: ScanSessionEntity)

    @Insert
    suspend fun insertMeasurement(measurement: MeasurementPointEntity)

    @Query("SELECT * FROM scan_sessions ORDER BY startTime DESC")
    fun getAllSessions(): Flow<List<ScanSessionEntity>>

    @Query("SELECT * FROM measurement_points WHERE scanSessionId = :sessionId")
    suspend fun getMeasurementsForSession(sessionId: Long): List<MeasurementPointEntity>
}