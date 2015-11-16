package com.codepath.apps.twitterclient.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.codepath.apps.twitterclient.models.User;

/**
 * Thanks to https://yakivmospan.wordpress.com/2014/03/11/best-practice-sharedpreferences/
 */
public class PreferenceManager {

    private static final String ROOT_KEY = "Settings";
    private static PreferenceManager sInstance;
    private final SharedPreferences mPref;

    private PreferenceManager(Context context) {
        mPref = context.getSharedPreferences(ROOT_KEY, 0);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferenceManager(context);
        }
    }

    public static synchronized PreferenceManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferenceManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    // setters

    public void set(String key, String value) {
        mPref.edit().putString(key, value).commit();
    }

    public void set(String key, long value) {
        mPref.edit().putLong(key, value).commit();
    }



    public final static String PREFIX = "current_user_";
    public void set(User user) {
        Log.d("prefs", "saving user data: " + user.toString());
        mPref.edit().putString(PREFIX + "screen_name", user.getScreenName());
        mPref.edit().putString(PREFIX + "name", user.getName());
        mPref.edit().putLong(PREFIX + "uid", user.getUid());
        mPref.edit().putString(PREFIX + "profile_image_url", user.getProfileImageUrl());
        mPref.edit().putString(PREFIX + "profile_image_url_https", user.getProfileImageUrlHttps());
    }

    // getters

    public long getLong(String key) {
        return mPref.getLong(key, 0);
    }

    public String getString(String key) {
        return mPref.getString(key, null);
    }


    // general function

    public void remove(String key) {
        mPref.edit().remove(key).commit();
    }

    public boolean clear() {
        return mPref.edit().clear().commit();
    }

    public void removeUser() {
        Log.d("prefs", "clearing user data");
        SharedPreferences.Editor e = mPref.edit();



        e.remove(PREFIX + "screen_name");
        e.remove(PREFIX + "name");
        e.remove(PREFIX + "uid");
        e.remove(PREFIX + "profile_image_url");
        e.remove(PREFIX + "profile_image_url_https");
        e.commit();
    }
}