package com.mapme.mapme.mapme.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharedPreferencesManager {


    public static final String LANGUAGE_KEY = "language_preferences";
    public static final String UNITS_KEY = "units_preferences";
    public static final String RADIUS_KEY = "radius_preferences";

    private static SharedPreferences preferences;

    public static void init(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static String getLanguage() {
        String myEnumString = preferences.getString(LANGUAGE_KEY, Language.English.toString());
        return myEnumString;
    }

    public static void setLanguage(Language lang) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(LANGUAGE_KEY, lang.toString());
        editor.commit();
    }

    public Munits getUnits() {
        String myEnumString = preferences.getString(UNITS_KEY, Munits.Kilometer.toString());
        return Munits.toMyEnum(myEnumString);

    }

    public static void setUnits(Munits units) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(UNITS_KEY, units.toString());
        editor.commit();
    }

    public float getRadius() {
        return preferences.getFloat(RADIUS_KEY, 50f);
    }

    public static void setRadius(float radius) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(RADIUS_KEY, radius);
        editor.commit();
    }

    public enum Language {
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

    public enum Munits {
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

