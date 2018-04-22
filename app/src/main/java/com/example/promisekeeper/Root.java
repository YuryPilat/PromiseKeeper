package com.example.promisekeeper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.annotation.StringRes;

import java.util.Set;

public class Root extends Application {

    private static SharedPreferences prefs;
//    private static SecurePreferences preferences;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        prefs = getSharedPreferences(getString(R.string.prefsName), 0);
//		FirebaseApp.initializeApp(this);
//        String id = FirebaseInstanceId.getInstance().getId();
//        preferences = new SecurePreferences(this, "system", id, true);
//        Constants.Initialize();
    }

    public static Context getRootContext() {
        return context;
    }

    public static void setInteger(String key, int value) {
        Editor ed = prefs.edit();
        ed.putInt(key, value);
        ed.apply();
    }
    public static String getStringRes(@StringRes int resourceValue){
        return context.getResources().getString(resourceValue);
    }

    public static void setString(String key, String value) {
        Editor ed = prefs.edit();
        ed.putString(key, value);
        ed.apply();
    }

    public static void setStringSet(String key, Set<String> value) {
        Editor ed = prefs.edit();
        ed.putStringSet(key, value);
        ed.apply();
    }

    public static void setBoolean(String key, boolean value) {
        Editor ed = prefs.edit();
        ed.putBoolean(key, value);
        ed.apply();
    }

    public static int getInteger(String key, int defValue) {
        return prefs.getInt(key, defValue);
    }

    public static String getString(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    public static Set<String> getStringSet(String key, Set<String> defValue) {
        return prefs.getStringSet(key, defValue);
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    public static void removeRow(String key) {
        Editor ed = prefs.edit();
        ed.remove(key);
        ed.apply();
    }

    public static void clear() {
        Editor ed = prefs.edit();
        ed.clear();
        ed.apply();
    }

//    public static void setSecureString(String key, String value) {
//        preferences.put(key, value);
//    }
//
//    public static String getSecureString(String key) {
//        return preferences.getString(key);
//    }
}