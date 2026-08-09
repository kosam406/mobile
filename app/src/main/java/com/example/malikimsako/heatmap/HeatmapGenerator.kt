package com.example.malikimsako.heatmap

import com.example.malikimsako.ui.scanner.Measurement
import kotlin.math.pow
import kotlin.math.sqrt

object HeatmapGenerator {

    fun interpolateValue(x: Float, y: Float, measurements: List<Measurement>, property: (Measurement) -> Double): Double {
        if (measurements.isEmpty()) return 0.0
        
        var totalWeight = 0.0
        var weightedSum = 0.0
        val power = 2.0

        for (m in measurements) {
            val dist = sqrt((x - m.position.x).toDouble().pow(2) + (y - m.position.y).toDouble().pow(2))
            
            if (dist < 0.01) return property(m) // Extremely close to a point

            val weight = 1.0 / dist.pow(power)
            weightedSum += weight * property(m)
            totalWeight += weight
        }

        return if (totalWeight > 0) weightedSum / totalWeight else 0.0
    }
}