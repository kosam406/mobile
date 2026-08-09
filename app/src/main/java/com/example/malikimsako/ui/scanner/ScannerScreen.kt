package com.example.malikimsako.ui.scanner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.malikimsako.domain.model.NetworkStats
import com.example.malikimsako.ui.theme.*

@Composable
fun ScannerScreen(
    viewModel: ScannerViewModel = viewModel(),
    onStopScan: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(DarkBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SCANNING...",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = state.currentStats.ssid,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Button(
                    onClick = { 
                        viewModel.stopScan()
                        onStopScan()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Poor)
                ) {
                    Text("STOP SCAN")
                }
            }

            // Live Map
            LiveMap(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                measurements = state.measurements,
                currentPos = state.currentPosition
            )

            // Stats Footer
            StatsFooter(state.currentStats)
        }
    }

    // Auto-start scan when screen opens
    LaunchedEffect(Unit) {
        viewModel.startScan()
    }
}

@Composable
fun LiveMap(
    modifier: Modifier,
    measurements: List<Measurement>,
    currentPos: com.example.malikimsako.sensors.Position
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val scale = 50f // Pixels per meter

            // Draw grid
            val gridStep = 100f
            for (x in 0 until (size.width / gridStep).toInt() + 1) {
                drawLine(Color.DarkGray.copy(alpha = 0.3f), Offset(x * gridStep, 0f), Offset(x * gridStep, size.height))
            }
            for (y in 0 until (size.height / gridStep).toInt() + 1) {
                drawLine(Color.DarkGray.copy(alpha = 0.3f), Offset(0f, y * gridStep), Offset(size.width, y * gridStep))
            }

            // Draw measurements
            measurements.forEach { m ->
                val color = when {
                    m.stats.signalPercentage > 80 -> Excellent
                    m.stats.signalPercentage > 60 -> Good
                    m.stats.signalPercentage > 40 -> Medium
                    m.stats.signalPercentage > 20 -> Weak
                    else -> Poor
                }
                drawCircle(
                    color = color.copy(alpha = 0.6f),
                    radius = 8f,
                    center = Offset(center.x + m.position.x * scale, center.y + m.position.y * scale)
                )
            }

            // Draw current position
            drawCircle(
                color = Color.White,
                radius = 12f,
                center = Offset(center.x + currentPos.x * scale, center.y + currentPos.y * scale)
            )
            drawCircle(
                color = primaryColor,
                radius = 16f,
                center = Offset(center.x + currentPos.x * scale, center.y + currentPos.y * scale),
                style = Stroke(width = 4f)
            )
        }
    }
}

@Composable
fun StatsFooter(stats: NetworkStats) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardBackground,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatItem("Signal", "${stats.rssi} dBm")
            StatItem("Ping", "${stats.pingMs} ms")
            StatItem("Link", "${stats.linkSpeed} Mbps")
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = Color.Gray, fontSize = 12.sp)
        Text(text = value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}