package com.example.malikimsako;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        ListView listView = findViewById(R.id.listView);
        
        List<SpeedRecord> records = DataManager.getInstance().getRecords();
        
        // Add a header or simple summary if needed
        if (records.isEmpty()) {
            // You could show a toast or a placeholder
        }

        ArrayAdapter<SpeedRecord> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, records);
        listView.setAdapter(adapter);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Room Speed Map");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}