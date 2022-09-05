package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.prices.PriceInteractor
import com.app.syspoint.models.Price
import com.app.syspoint.models.json.RequestClients
import com.app.syspoint.models.json.SpecialPriceJson
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestPrice {
    companion object {
        fun requestSavePrice(priceList: List<Price>, onSendPricesListener: PriceInteractor.SendPricesListener) {
            val priceJson = SpecialPriceJson()
            priceJson.prices = priceList

            val json = Gson().toJson(priceJson)
            Log.d("Sinc especiales", json)

            val savePrice = ApiServices.getClientRestrofit().create(
                PointApi::class.java
            ).sendPrecios(priceJson)


            savePrice.enqueue(object: Callback<SpecialPriceJson> {
                override fun onResponse(
                    call: Call<SpecialPriceJson>,
                    response: Response<SpecialPriceJson>
                ) {
                    if (response.isSuccessful) {
                        onSendPricesListener.onSendPricesSuccess()
                    } else {
                        onSendPricesListener.onSendPricesError()
                    }
                }

                override fun onFailure(call: Call<SpecialPriceJson>, t: Throwable) {
                    onSendPricesListener.onSendPricesError()
                }
            })
        }

        fun requestAllPrices(onGetSpecialPricesListener: PriceInteractor.GetSpecialPricesListener) {
            val specialPrices = ApiServices.getClientRestrofit().create(
                PointApi::class.java
            ).pricesEspecial

            specialPrices.enqueue(object: Callback<SpecialPriceJson> {
                override fun onResponse(call: Call<SpecialPriceJson>, response: Response<SpecialPriceJson>) {
                    if (response.isSuccessful) {
                        onGetSpecialPricesListener.onGetSpecialPricesSuccess(response)
                    } else {
                        onGetSpecialPricesListener.onGetSpecialPricesError()
                    }
                }

                override fun onFailure(call: Call<SpecialPriceJson>, t: Throwable) {
                    onGetSpecialPricesListener.onGetSpecialPricesError()
                }
            })
        }

        fun requestPricesByClient(client: String, onGetPricesByClientListener: PriceInteractor.GetPricesByClientListener) {
            val requestPrices = RequestClients()
            requestPrices.cuenta = client

            val pricesByClient = ApiServices.getClientRestrofit().create(
                PointApi::class.java
            ).getPreciosByClient(requestPrices)


            pricesByClient.enqueue(object: Callback<SpecialPriceJson> {
                override fun onResponse(call: Call<SpecialPriceJson>, response: Response<SpecialPriceJson>) {
                    if (response.isSuccessful) {
                        onGetPricesByClientListener.onGetPricesByClientSuccess(response)
                    } else {
                        onGetPricesByClientListener.onGGetPricesByClientError()
                    }
                }

                override fun onFailure(call: Call<SpecialPriceJson>, t: Throwable) {
                    onGetPricesByClientListener.onGGetPricesByClientError()
                }
            })
        }
    }
}