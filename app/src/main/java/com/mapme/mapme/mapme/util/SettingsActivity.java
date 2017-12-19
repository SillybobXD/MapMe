package com.mapme.mapme.mapme.util;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mapme.mapme.mapme.DrawerManager;
import com.mapme.mapme.mapme.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DrawerManager.makeDrawer(this);
    }
}
