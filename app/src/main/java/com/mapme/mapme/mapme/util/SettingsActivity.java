package com.mapme.mapme.mapme.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mapme.mapme.mapme.DrawerManager;
import com.mapme.mapme.mapme.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button btn_drawer = findViewById(R.id.btn_menu_settingsActivity);

        btn_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DrawerManager.openDrawer();

            }
        });
        DrawerManager.makeDrawer(this);
    }
}
