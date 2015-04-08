package com.kritsin.rssclient.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class PreferencesUtils { 
	 
	
	public static void setUserId(Context context, int id){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.putInt("id", id);
		editor.commit(); 
	}
	
	public static int getUserId(Context context){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		return preferences.getInt("id", -1);
	}
	
	public static void deleteUserId(Context context){
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = preferences.edit();
		editor.remove("id");
		editor.commit(); 
	}
	 
}
