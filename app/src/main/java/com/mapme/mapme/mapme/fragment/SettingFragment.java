package com.mapme.mapme.mapme.fragment;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.mapme.mapme.mapme.R;
import com.mapme.mapme.mapme.util.DrawerManager;
import com.mapme.mapme.mapme.util.SharedPreferencesManager;

import java.util.Locale;

/**
 * Created by Yitschak on 05/02/2018.
 */

public class SettingFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    ListPreference languagesSettings;
    ListPreference unintsSettings;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_screen);

        languagesSettings = (ListPreference) findPreference("language_preferences");
        languagesSettings.setSummary(SharedPreferencesManager.getLanguage());

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        String languageToLoad = "";

        switch (key) {

            case SharedPreferencesManager.LANGUAGE_KEY:
                languagesSettings.setSummary(SharedPreferencesManager.getLanguage());
                if (SharedPreferencesManager.getLanguage() == "English") {
                    languageToLoad = "en";
                } else if (SharedPreferencesManager.getLanguage() == "עברית") {
                    languageToLoad = "he";
                }

                Locale locale = new Locale(languageToLoad);
                Locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getActivity().getBaseContext().getResources().updateConfiguration(config,
                        getActivity().getBaseContext().getResources().getDisplayMetrics());

                DrawerManager.makeDrawer(getActivity());


        }

    }
}
