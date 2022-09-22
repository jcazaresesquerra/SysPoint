package com.app.syspoint.repository.request.http

import com.app.syspoint.BuildConfig
import com.app.syspoint.utils.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiServices {
    companion object {
        private var retrofit: Retrofit? = null

        fun getClientRetrofit(): Retrofit {
            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build()

            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(getBaseURL())
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofit!!
        }

        private fun getBaseURL(): String {
            return if (BuildConfig.FLAVOR == "donaqui") {
                Constants.BASE_URL_DONAQUI
            } else {
                Constants.BASE_URL_SYSPOINT
            }
        }
    }
}