package com.aariyan.pickingplan.Networking;

import android.content.Context;

import com.aariyan.pickingplan.Constant.Constant;
import com.aariyan.pickingplan.Database.S_Preferences;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static Retrofit retrofit = null;
    private static String BASE_URL = Constant.BASE_URL;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .build();
        }

        return retrofit;
    }

}
