package com.mapme.mapme.mapme.fragment;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import com.mapme.mapme.mapme.R;
import com.mapme.mapme.mapme.util.SeekBarPrefernce;
import com.mapme.mapme.mapme.util.SharedPreferencesManager;

/**
 * Created by Yitschak on 05/02/2018.
 */

public class SettingFragment extends PreferenceFragment {

    public ListPreference languagesPreference;
    public ListPreference unitsPreference;
    public SeekBarPrefernce seekBarPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings_screen);

        languagesPreference = (ListPreference) findPreference("language_preferences");
        languagesPreference.setSummary(SharedPreferencesManager.getLanguage());

        unitsPreference = (ListPreference) findPreference("units_preferences");
        unitsPreference.setSummary(SharedPreferencesManager.getUnits());

        seekBarPreference = (SeekBarPrefernce) findPreference("radius_preferences");
        seekBarPreference.setSummary(String.valueOf((int) SharedPreferencesManager.getRadius()) + " Km");

    }




}
