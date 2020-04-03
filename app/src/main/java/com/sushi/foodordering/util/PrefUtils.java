package com.sushi.foodordering.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PrefUtils {
    private static PrefUtils instance = new PrefUtils();
    private SharedPreferences sharedPreferences;
    private Editor editor;

    public PrefUtils() {
    }

    public static PrefUtils getInstance() {
        return instance;
    }

    public void init(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void storeString(String key, String value){
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();

    }

    public String getString(String key, String defaultValue){
        return sharedPreferences.getString(key, defaultValue);
    }

    public void storeBoolean(String key, boolean value){
        editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defValue){
        return sharedPreferences.getBoolean(key, defValue);
    }
}
