package com.mapme.mapme.mapme.activity;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.mapme.mapme.mapme.R;
import com.mapme.mapme.mapme.util.DrawerManager;

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


        Fragment fragment = new SettingsScreen();

        android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            fragmentTransaction.add(R.id.settingsLayout, fragment, "settings_fragment");
            // fragment.setArguments();
            fragmentTransaction.commit();

        } else {
            fragment = getFragmentManager().findFragmentByTag("settings_fragment");
        }

    }

    public static class SettingsScreen extends PreferenceFragment {

        public static final String KEY_PREF_SYNC_CONN = "pref_syncConnectionType";

        ListPreference languagesSettings = (ListPreference) findPreference("languages_settings");
        ListPreference distanceUnitsSettings = (ListPreference) findPreference("distance_settings");
        ListPreference radiusSettings = (ListPreference) findPreference("radius_settings");

        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (key.equals(KEY_PREF_SYNC_CONN)) {
                Preference connectionPref = findPreference(key);
                connectionPref.setSummary(sharedPreferences.getString(key, ""));

            }

        }


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_screen);

            languagesSettings.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {


                    return false;
                }
            });


        }




    }
}
