package com.mapme.mapme.mapme.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String PREFERENCES_KEY = "MyPrefs";
    private static final String LANGUAGE_KEY = "language_preferences";
    private static final String UNITS_KEY = "units_preferences";

    private static SharedPreferences preferences;
    private static Language language;
    private static Munits mUnits;

    public static void init(Context context) {
        preferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        language = getLanguageFromPref();
        mUnits = getUnitsFromPref();
    }

    private static Munits getUnitsFromPref() {
        String myEnumString = preferences.getString(UNITS_KEY, mUnits.toString());
        return mUnits.toMyEnum(myEnumString);

    }

    private static Language getLanguageFromPref() {
        String myEnumString = preferences.getString(LANGUAGE_KEY, language.toString());
        return language.toMyEnum(myEnumString);
    }

    public Munits getUnits() {
        return mUnits;
    }

    public static void setUnits(Munits units) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UNITS_KEY, units.toString());
        editor.commit();
    }

    public Language getLanguage() {
        return language;
    }

    public static void setLanguage(Language lang) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE_KEY, lang.toString());
        editor.commit();

    }

    enum Language {
        English,
        Hebrew;

        public static Language toMyEnum(String myEnumString) {
            try {
                return valueOf(myEnumString);
            } catch (Exception ex) {
                // For error cases
                return Hebrew;
            }
        }
    }

    enum Munits {
        Kilometer,
        Mile;

        public static Munits toMyEnum(String myEnumString) {
            try {
                return valueOf(myEnumString);
            } catch (Exception ex) {
                // For error cases
                return Kilometer;
            }
        }
    }

}

