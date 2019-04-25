package com.tyw.moniter.main.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tyw.moniter.main.R;

public class DeviceLocationMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_device_location_map);
// back button
        findViewById(R.id.btn_device_location_map_navigation_back)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });


        Intent intent = getIntent();
        // title
        TextView title = findViewById(R.id.text_device_location_map_navigation_title);
        title.setText(getString(R.string.top_navigation_title_device_location_map, intent.getStringExtra("device")));
    }
}
