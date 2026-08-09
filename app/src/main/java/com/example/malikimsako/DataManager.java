package com.example.malikimsako;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static DataManager instance;
    private List<SpeedRecord> records;

    private DataManager() {
        records = new ArrayList<>();
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void addRecord(SpeedRecord record) {
        records.add(0, record); // Add at the beginning
    }

    public List<SpeedRecord> getRecords() {
        return records;
    }
    
    public void clearRecords() {
        records.clear();
    }
}