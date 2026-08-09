package com.example.malikimsako.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.cos
import kotlin.math.sin

data class Position(val x: Float, val y: Float, val heading: Float)

class MovementTracker(context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)

    private var _position = MutableStateFlow(Position(0f, 0f, 0f))
    val position = _position.asStateFlow()

    private var gravity: FloatArray? = null
    private var geomagnetic: FloatArray? = null
    private var currentHeading = 0f
    private val stepSize = 0.7f // meters

    fun startTracking() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_UI)
    }

    fun stopTracking() {
        sensorManager.unregisterListener(this)
    }

    fun reset() {
        _position.value = Position(0f, 0f, 0f)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> gravity = event.values
            Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = event.values
            Sensor.TYPE_STEP_DETECTOR -> {
                updatePosition()
            }
        }

        if (gravity != null && geomagnetic != null) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                currentHeading = orientation[0] // Yaw
                _position.value = _position.value.copy(heading = currentHeading)
            }
        }
    }

    private fun updatePosition() {
        val current = _position.value
        val newX = current.x + stepSize * sin(currentHeading)
        val newY = current.y + stepSize * cos(currentHeading)
        _position.value = Position(newX, newY, currentHeading)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}