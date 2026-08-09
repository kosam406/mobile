package com.example.malikimsako;

public class SpeedRecord {
    private String timestamp;
    private int signalStrength;
    private double speedMbps;

    public SpeedRecord(String timestamp, int signalStrength, double speedMbps) {
        this.timestamp = timestamp;
        this.signalStrength = signalStrength;
        this.speedMbps = speedMbps;
    }

    public String getTimestamp() { return timestamp; }
    public int getSignalStrength() { return signalStrength; }
    public double getSpeedMbps() { return speedMbps; }

    @Override
    public String toString() {
        return timestamp + ": Speed: " + String.format("%.2f", speedMbps) + " Mbps (Signal: " + signalStrength + "%)";
    }
}