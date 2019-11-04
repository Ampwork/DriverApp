package com.ampwork.driverapp.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;



/**
 * This class is used for storing all type of  primitive data in key-value pairs. Data Types include Booleans, floats, ints, longs, and strings.
 */

public class PreferencesManager {

    private String PREF_NAME = AppConstant.SHARED_PREF_NAME;

    // private static PreferencesManager sInstance;
    private SharedPreferences myPref;

    public PreferencesManager(Context context) {
        myPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setLongValue(String keyName , long value) {
        myPref.edit().putLong(keyName, value).commit();
    }

    public long getLongValue(String keyName) {
        return myPref.getLong(keyName, 0);
    }

    public void setIntValue(String keyName , int value) {
        myPref.edit().putInt(keyName, value).commit();
    }

    public int getIntValue(String keyName) {
        return myPref.getInt(keyName, 0);
    }

    public void setFloatValue(String keyName , Float value) {
        myPref.edit().putFloat(keyName, value).commit();
    }

    public float getFloatValue(String keyName) {
        return myPref.getFloat(keyName, 0);
    }

    public void setStringValue(String keyName , String value) {
        myPref.edit().putString(keyName, value).commit();
    }

    public String getStringValue(String keyName) {
        return myPref.getString(keyName, "");
    }

    public void setBooleanValue(String keyName , boolean value) {
        myPref.edit().putBoolean(keyName, value).commit();
    }

    public Boolean getBooleanValue(String keyName) {
        return myPref.getBoolean(keyName, false);
    }

    public void setListValue(String keyName , ArrayList<String> value) {
        myPref.edit().putString(keyName, TextUtils.join(",", value)).commit();
    }

    public ArrayList<String> getListValue(String keyName) {
        ArrayList<String> arrayList = new ArrayList<String>();
        String serialized = myPref.getString(keyName, "");
        arrayList.addAll(Arrays.asList(TextUtils.split(serialized, ",")));
        return arrayList;
    }

    public void remove(String key) {
        myPref.edit().remove(key).commit();
    }

    public boolean clear() {
        return myPref.edit().clear().commit();
    }
}
