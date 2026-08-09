package com.example.malikimsako;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private TextView statusText;
    private Button btnStartStop;
    private boolean isMeasuring = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable measureRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);
        btnStartStop = findViewById(R.id.btnStartStop);
        Button btnViewResults = findViewById(R.id.btnViewResults);

        btnStartStop.setOnClickListener(v -> {
            if (isMeasuring) {
                stopMeasuring();
            } else {
                startMeasuring();
            }
        });

        btnViewResults.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intent);
        });

        measureRunnable = new Runnable() {
            @Override
            public void run() {
                captureSpeed();
                handler.postDelayed(this, 2000); // Capture every 2 seconds
            }
        };
    }

    private void startMeasuring() {
        if (checkPermissions()) {
            isMeasuring = true;
            btnStartStop.setText("Stop Measuring");
            statusText.setText("Measuring speed... Walk around!");
            DataManager.getInstance().clearRecords();
            handler.post(measureRunnable);
        } else {
            requestPermissions();
        }
    }

    private void stopMeasuring() {
        isMeasuring = false;
        btnStartStop.setText("Start Measuring");
        statusText.setText("Measurement stopped.");
        handler.removeCallbacks(measureRunnable);
        Toast.makeText(this, "Tracking finished. View results to see your room map.", Toast.LENGTH_LONG).show();
    }

    private void captureSpeed() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        
        int rssi = wifiInfo.getRssi();
        int level = WifiManager.calculateSignalLevel(rssi, 100); // 0 to 99
        
        // Simulating speed based on signal level + some randomness
        double baseSpeed = level * 0.8; // Max around 80 Mbps
        double randomVariation = new Random().nextDouble() * 5;
        double speed = Math.max(0, baseSpeed + randomVariation);

        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        SpeedRecord record = new SpeedRecord(time, level, speed);
        DataManager.getInstance().addRecord(record);
        
        statusText.setText("Current: " + String.format("%.1f", speed) + " Mbps (" + level + "%)");
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMeasuring();
            } else {
                Toast.makeText(this, "Permission required to access Wi-Fi info", Toast.LENGTH_SHORT).show();
            }
        }
    }
}