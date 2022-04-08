package com.aariyan.pickingplan.Database;

import android.content.Context;
import android.content.SharedPreferences;

public class S_Preferences {

    SharedPreferences sharedPreferences;
    private Context context;
    private static String DEFAULT_URL = "http://102.37.0.48/Hendokpicking/";

    public S_Preferences(Context context) {
        this.context = context;
    }

    public void saveBaserUrl(String valueOrUrl, String rootName) {
        sharedPreferences = context.getSharedPreferences(rootName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("BASE_URL", valueOrUrl);
        editor.commit();
    }


    public  String getBaseUrl(String rootName) {
        sharedPreferences = context.getSharedPreferences(rootName, Context.MODE_PRIVATE);
        return sharedPreferences.getString("BASE_URL", DEFAULT_URL);
    }
}
