package com.app.syspoint.repository.request.http;

import static com.app.syspoint.utils.Constants.BASE_URL_DONAQUI;
import static com.app.syspoint.utils.Constants.BASE_URL_SYSPOINT;

import com.app.syspoint.BuildConfig;
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
                    .baseUrl(getBaseURL())
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    private static String getBaseURL() {
        if (BuildConfig.FLAVOR.equals("donaqui")) {
            return BASE_URL_DONAQUI;
        } else {
            return BASE_URL_SYSPOINT;
        }
    }

}
