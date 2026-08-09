package com.example.malikimsako

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.malikimsako.ui.home.HomeScreen
import com.example.malikimsako.ui.results.ResultsScreen
import com.example.malikimsako.ui.scanner.ScannerScreen
import com.example.malikimsako.ui.scanner.ScannerViewModel
import com.example.malikimsako.ui.theme.WiFiRoomScannerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WiFiRoomScannerTheme {
                val scannerViewModel: ScannerViewModel = viewModel()
                val navController = rememberNavController()
                
                val permissions = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                
                val launcher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { }

                LaunchedEffect(Unit) {
                    launcher.launch(permissions)
                }

                NavHost(navController = navController, startDestination = "home") {
                    composable("home") {
                        HomeScreen(onStartScan = { navController.navigate("scanner") })
                    }
                    composable("scanner") {
                        ScannerScreen(
                            viewModel = scannerViewModel,
                            onStopScan = { navController.navigate("results") }
                        )
                    }
                    composable("results") {
                        ResultsScreen(
                            measurements = scannerViewModel.state.collectAsState().value.measurements,
                            onClose = { navController.popBackStack("home", inclusive = false) }
                        )
                    }
                }
            }
        }
    }
}