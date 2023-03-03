package com.firstzoom.bluevisionlib.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.firstzoom.bluevisionlib.model.User;


public class SharedPrefUtils {
    synchronized public static void setUser(Context context, User customer) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.KEY_USER,GsonUtils.getGsonObject(customer));
        editor.apply();
    }

    public static User getUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String user = prefs.getString(AppConstants.KEY_USER, "");
        if(TextUtils.isEmpty(user))
            return null;
        else
            return GsonUtils.getModelObjectUser(user) ;
    }
    synchronized public static void delUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(AppConstants.KEY_USER);
        editor.apply();
    }
    public synchronized static void saveUrl(Context context,String url) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.KEY_BASE_URL,url);
        editor.apply();
    }
    public synchronized static String getUrl(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String token=prefs.getString(AppConstants.KEY_BASE_URL,"");
        return token;
    }
    public synchronized static void saveUsername(Context context,String url) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.USERNAME,url);
        editor.apply();
    }
    public synchronized static String getUsername(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String token=prefs.getString(AppConstants.USERNAME,"");
        return token;
    }


   synchronized public static void delUrl(Context context) {
       SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
       SharedPreferences.Editor editor = prefs.edit();
       editor.remove(AppConstants.KEY_BASE_URL);
       editor.apply();
    }
    public synchronized static void saveToken(Context context,String url) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(AppConstants.KEY_TOKEN,url);
        editor.apply();
    }
    public synchronized static String getToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String token=prefs.getString(AppConstants.KEY_TOKEN,"");
        return token;
    }


    synchronized public static void delToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove(AppConstants.KEY_TOKEN);
        editor.apply();
    }
}
