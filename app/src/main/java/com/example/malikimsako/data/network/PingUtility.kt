package com.example.malikimsako.data.network

import java.io.IOException
import java.net.InetAddress

object PingUtility {
    fun getPing(host: String = "8.8.8.8"): Long {
        val startTime = System.currentTimeMillis()
        return try {
            val address = InetAddress.getByName(host)
            if (address.isReachable(2000)) {
                System.currentTimeMillis() - startTime
            } else {
                -1
            }
        } catch (e: IOException) {
            -1
        }
    }
}