package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.charge.ChargeInteractor
import com.app.syspoint.models.Payment
import com.app.syspoint.models.json.PaymentJson
import com.app.syspoint.models.json.RequestCobranza
import com.app.syspoint.repository.database.dao.ClientDao
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestCharge {
    companion object {
        fun requestGetCharge(onGetChargeListener: ChargeInteractor.OnGetChargeListener) {
            val getCharge = ApiServices.getClientRestrofit().create(
                PointApi::class.java
            ).cobranza

            getCharge.enqueue(object: Callback<PaymentJson> {
                override fun onResponse(call: Call<PaymentJson>, response: Response<PaymentJson>) {
                    if (response.isSuccessful) {
                        onGetChargeListener.onGetChargeSuccess(response)
                    } else {
                        onGetChargeListener.onGetChargeError()
                    }
                }

                override fun onFailure(call: Call<PaymentJson>, t: Throwable) {
                    onGetChargeListener.onGetChargeError()
                }

            })

        }

        fun requestGetChargeByClient(account: String, onGetChargeByClientListener: ChargeInteractor.OnGetChargeByClientListener) {
            val clientDao = ClientDao()
            val clienteBean = clientDao.getClientByAccount(account)
            val requestCobranza = RequestCobranza()
            requestCobranza.cuenta = clienteBean!!.cuenta

            //Obtiene la respuesta
            val getChargeByClient = ApiServices.getClientRestrofit().create(
                PointApi::class.java
            ).getCobranzaByCliente(requestCobranza)

            getChargeByClient.enqueue(object: Callback<PaymentJson> {
                override fun onResponse(call: Call<PaymentJson>, response: Response<PaymentJson>) {
                    if (response.isSuccessful) {
                        onGetChargeByClientListener.onGetChargeByClientSuccess(response)
                    } else {
                        onGetChargeByClientListener.onGetChargeByClientError()
                    }
                }

                override fun onFailure(call: Call<PaymentJson>, t: Throwable) {
                    onGetChargeByClientListener.onGetChargeByClientError()
                }

            })

        }

        fun saveCharge(charges: List<Payment>, onSaveChargeListener: ChargeInteractor.OnSaveChargeListener) {
            val chargeJson = PaymentJson()
            chargeJson.payments = charges
            val json = Gson().toJson(chargeJson)
            Log.d("Sin Cobranza", json)

            val saveCharges = ApiServices.getClientRestrofit().create(
                PointApi::class.java
            ).sendCobranza(chargeJson)

            saveCharges.enqueue(object: Callback<PaymentJson> {
                override fun onResponse(call: Call<PaymentJson>, response: Response<PaymentJson>) {
                    if (response.isSuccessful) {
                        onSaveChargeListener.onSaveChargeSuccess()
                    } else {
                        onSaveChargeListener.onSaveChargeError()
                    }
                }

                override fun onFailure(call: Call<PaymentJson>, t: Throwable) {
                    onSaveChargeListener.onSaveChargeError()
                }

            })
        }

        fun updateCharge(charges: List<Payment>, onUpdateChargeListener: ChargeInteractor.OnUpdateChargeListener) {
            val chargeJson = PaymentJson()
            chargeJson.payments = charges
            val json = Gson().toJson(chargeJson)
            Log.d("Sin Cobranza", json)

            val updateCharge = ApiServices.getClientRestrofit().create(
                PointApi::class.java
            ).updateCobranza(chargeJson)

            updateCharge.enqueue(object : Callback<PaymentJson> {
                override fun onResponse(call: Call<PaymentJson>, response: Response<PaymentJson>) {
                    if (response.isSuccessful) {
                        onUpdateChargeListener.onUpdateChargeSuccess()
                    } else {
                        onUpdateChargeListener.onUpdateChargeError()
                    }
                }

                override fun onFailure(call: Call<PaymentJson>, t: Throwable) {
                    onUpdateChargeListener.onUpdateChargeError()
                }

            })

        }
    }
}