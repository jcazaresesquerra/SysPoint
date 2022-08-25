package com.app.syspoint.http;

import com.app.syspoint.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServices {

    private static Retrofit retrofit = null;

    public static Retrofit getClientRestrofit(){

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        if (retrofit == null){
            retrofit = new Retrofit.Builder()
                    //.baseUrl("https://api.ocsistems.com/public/api/") operacion
                    //.baseUrl("https://apiqa.ocsistems.com/public/api/")
                    .baseUrl(Constants.BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
