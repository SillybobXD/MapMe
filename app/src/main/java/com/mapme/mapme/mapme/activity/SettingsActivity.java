package com.mapme.mapme.mapme.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.mapme.mapme.mapme.R;
import com.mapme.mapme.mapme.fragment.SettingFragment;
import com.mapme.mapme.mapme.util.DrawerManager;
import com.mapme.mapme.mapme.util.GoogleAPIManager;
import com.mapme.mapme.mapme.util.SharedPreferencesManager;

public class SettingsActivity extends LocalizationActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    SettingFragment settingFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingFragment = new SettingFragment();
        getFragmentManager().beginTransaction().replace(R.id.settingsLayout, settingFragment).commit();

        Button btn_drawer = findViewById(R.id.btn_menu_settingsActivity);

        btn_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DrawerManager.openDrawer();

            }
        });
        DrawerManager.makeDrawer(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferencesManager.getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferencesManager.getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {

            case SharedPreferencesManager.LANGUAGE_KEY:
                String language = SharedPreferencesManager.getLanguage();
                switch (language) {
                    case "עברית":
                        setLanguage("he");
                        break;
                    case "English":
                        setLanguage("en");
                        break;
                }
                break;

            case SharedPreferencesManager.UNITS_KEY:
                settingFragment.unitsPreference.setSummary(SharedPreferencesManager.getUnits());
                break;

            case SharedPreferencesManager.RADIUS_KEY:
                float radius = SharedPreferencesManager.getRadius();
                GoogleAPIManager.setRadius(radius * 1000);
                settingFragment.seekBarPreference.setSummary(String.valueOf((int) radius) + " Km");
                break;


        }

    }

}

