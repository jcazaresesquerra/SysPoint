package com.app.syspoint.repository.request

import com.app.syspoint.BuildConfig
import com.app.syspoint.interactor.token.TokenInteractor
import com.app.syspoint.models.json.RequestTokenBody
import com.app.syspoint.models.json.TokenJson
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestToken {
    companion object {
        fun getTokenByAppVersion(onGetTokenListener: TokenInteractor.OnGetTokenListener) {
            val appVersion = BuildConfig.VERSION_NAME.split(".")
            val version = appVersion[0]
            val subversion = appVersion[1] + "." + appVersion[2]
            val requestTokenBody = RequestTokenBody(version, subversion)
            val getToken = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getToken(requestTokenBody)


            getToken.enqueue(object : Callback<TokenJson> {
                override fun onResponse(call: Call<TokenJson>, response: Response<TokenJson>) {
                    if (response.isSuccessful) {
                        if (!response.body()?.tokens.isNullOrEmpty()) {
                            val token = response.body()?.tokens!![0]
                            onGetTokenListener.onGetTokenSuccess(token!!.token, token.version + "." +token.subversion)
                        } else {
                            val token = response.body()?.error!![0]
                            onGetTokenListener.onGetTokenError(token!!.baseUpdateUrl!!, token.version + "." + token.subversion, null)
                        }
                    } else {
                        val error = response.errorBody()!!.string()
                        onGetTokenListener.onGetTokenError("", "", Throwable(error))
                    }
                }

                override fun onFailure(call: Call<TokenJson>, t: Throwable) {
                    onGetTokenListener.onGetTokenError("", "", t)
                }
            })
        }
    }
}