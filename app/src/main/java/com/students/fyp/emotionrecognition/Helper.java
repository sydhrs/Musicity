package com.students.fyp.emotionrecognition;

import android.content.Context;
import android.content.SharedPreferences;

public class Helper {

    public static void save(Context context,String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static String get(Context context,String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("App", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }
}
