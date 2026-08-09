package com.example.malikimsako.data.network

import java.io.InputStream
import java.net.URL
import kotlin.system.measureTimeMillis

object SpeedTestUtility {
    // A small file to test download speed without using too much data
    private const val TEST_FILE_URL = "https://www.google.com/images/branding/googlelogo/2x/googlelogo_color_272x92dp.png"

    suspend fun measureDownloadSpeedMbps(): Double {
        return try {
            var bytesReadTotal = 0L
            val timeTaken = measureTimeMillis {
                val url = URL(TEST_FILE_URL)
                val connection = url.openConnection()
                connection.connect()
                val inputStream: InputStream = connection.getInputStream()
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    bytesReadTotal += bytesRead
                }
                inputStream.close()
            }
            
            if (timeTaken > 0) {
                val speedBps = (bytesReadTotal * 8.0) / (timeTaken / 1000.0)
                speedBps / 1_000_000.0 // Mbps
            } else {
                0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }
}