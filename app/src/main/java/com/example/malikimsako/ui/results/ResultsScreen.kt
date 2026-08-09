package com.example.malikimsako.ui.results

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.malikimsako.heatmap.HeatmapGenerator
import com.example.malikimsako.ui.scanner.Measurement
import com.example.malikimsako.ui.theme.*

@Composable
fun ResultsScreen(
    measurements: List<Measurement>,
    onClose: () -> Unit
) {
    val scrollState = rememberScrollState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(24.dp)
    ) {
        Text(
            text = "SCAN COMPLETE",
            color = Excellent,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = "Room Performance Map",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Final Heatmap
        HeatmapView(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            measurements = measurements
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Summary Stats
        SummaryStats(measurements)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("DONE")
        }
    }
}

@Composable
fun HeatmapView(modifier: Modifier, measurements: List<Measurement>) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (measurements.isEmpty()) return@Canvas
            
            val bounds = measurements.map { it.position }
            val minX = bounds.minOf { it.x } - 1f
            val maxX = bounds.maxOf { it.x } + 1f
            val minY = bounds.minOf { it.y } - 1f
            val maxY = bounds.maxOf { it.y } + 1f
            
            val width = maxX - minX
            val height = maxY - minY
            
            val scaleX = size.width / width
            val scaleY = size.height / height
            
            val resolution = 10 // Pixels per grid cell
            
            for (i in 0 until (size.width / resolution).toInt()) {
                for (j in 0 until (size.height / resolution).toInt()) {
                    val xPos = i * resolution.toFloat()
                    val yPos = j * resolution.toFloat()
                    
                    val worldX = minX + xPos / scaleX
                    val worldY = minY + yPos / scaleY
                    
                    val value = HeatmapGenerator.interpolateValue(worldX, worldY, measurements) {
                        it.stats.signalPercentage.toDouble()
                    }
                    
                    val color = when {
                        value > 80 -> Excellent
                        value > 60 -> Good
                        value > 40 -> Medium
                        value > 20 -> Weak
                        else -> Poor
                    }
                    
                    drawRect(
                        color = color.copy(alpha = 0.4f),
                        topLeft = Offset(xPos, yPos),
                        size = Size(resolution.toFloat(), resolution.toFloat())
                    )
                }
            }
        }
    }
}

@Composable
fun SummaryStats(measurements: List<Measurement>) {
    val avgSignal = measurements.map { it.stats.signalPercentage }.average().toInt()
    val maxSignal = measurements.map { it.stats.signalPercentage }.maxOrNull() ?: 0
    val minSignal = measurements.map { it.stats.signalPercentage }.minOrNull() ?: 0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Statistics", color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            StatRow("Average Signal", "$avgSignal%")
            StatRow("Best Signal", "$maxSignal%")
            StatRow("Worst Signal", "$minSignal%")
            StatRow("Data Points", "${measurements.size}")
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.White.copy(alpha = 0.7f))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold)
    }
}