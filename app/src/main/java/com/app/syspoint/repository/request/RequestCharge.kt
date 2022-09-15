package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.charge.ChargeInteractor
import com.app.syspoint.models.Payment
import com.app.syspoint.models.json.PaymentJson
import com.app.syspoint.models.json.RequestCobranza
import com.app.syspoint.repository.database.bean.CobranzaBean
import com.app.syspoint.repository.database.dao.ClientDao
import com.app.syspoint.repository.database.dao.PaymentDao
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestCharge {
    companion object {
        fun requestGetCharge(onGetChargeListener: ChargeInteractor.OnGetChargeListener) {
            val getCharge = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getCobranza()

            getCharge.enqueue(object: Callback<PaymentJson> {
                override fun onResponse(call: Call<PaymentJson>, response: Response<PaymentJson>) {
                    if (response.isSuccessful) {
                        val paymentDao = PaymentDao()
                        val chargeList = arrayListOf<CobranzaBean>()
                        for (item in response.body()!!.payments!!) {
                            val cobranzaBean = paymentDao.getByCobranza(item!!.cobranza)
                            if (cobranzaBean == null) {
                                val chargeBean = CobranzaBean()
                                val paymentDao1 = PaymentDao()
                                chargeBean.cobranza = item.cobranza
                                chargeBean.cliente = item.cuenta
                                chargeBean.importe = item.importe
                                chargeBean.saldo = item.saldo
                                chargeBean.venta = item.venta
                                chargeBean.estado = item.estado
                                chargeBean.observaciones = item.observaciones
                                chargeBean.fecha = item.fecha
                                chargeBean.hora = item.hora
                                chargeBean.empleado = item.identificador
                                paymentDao1.insert(chargeBean)
                                chargeList.add(chargeBean)
                            } else {
                                cobranzaBean.cobranza = item.cobranza
                                cobranzaBean.cliente = item.cuenta
                                cobranzaBean.importe = item.importe
                                cobranzaBean.saldo = item.saldo
                                cobranzaBean.venta = item.venta
                                cobranzaBean.estado = item.estado
                                cobranzaBean.observaciones = item.observaciones
                                cobranzaBean.fecha = item.fecha
                                cobranzaBean.hora = item.hora
                                cobranzaBean.empleado = item.identificador
                                paymentDao.save(cobranzaBean)
                                chargeList.add(cobranzaBean)
                            }
                        }
                        onGetChargeListener.onGetChargeSuccess(chargeList)
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
            val getChargeByClient = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getCobranzaByCliente(requestCobranza)

            getChargeByClient.enqueue(object: Callback<PaymentJson> {
                override fun onResponse(call: Call<PaymentJson>, response: Response<PaymentJson>) {
                    if (response.isSuccessful) {
                        val paymentDao = PaymentDao()
                        val chargeByClientList = arrayListOf<CobranzaBean>()
                        for (item in response.body()!!.payments!!) {
                            val cobranzaBean = paymentDao.getByCobranza(item!!.cobranza)
                            if (cobranzaBean == null) {
                                val cobranzaBean1 = CobranzaBean()
                                val paymentDao1 = PaymentDao()
                                cobranzaBean1.cobranza = item.cobranza
                                cobranzaBean1.cliente = item.cuenta
                                cobranzaBean1.importe = item.importe
                                cobranzaBean1.saldo = item.saldo
                                cobranzaBean1.venta = item.venta
                                cobranzaBean1.estado = item.estado
                                cobranzaBean1.observaciones = item.observaciones
                                cobranzaBean1.fecha = item.fecha
                                cobranzaBean1.hora = item.hora
                                cobranzaBean1.empleado = item.identificador
                                cobranzaBean1.isCheck = false
                                paymentDao1.insert(cobranzaBean1)
                                chargeByClientList.add(cobranzaBean1)
                            } else {
                                cobranzaBean.cobranza = item.cobranza
                                cobranzaBean.cliente = item.cuenta
                                cobranzaBean.importe = item.importe
                                cobranzaBean.saldo = item.saldo
                                cobranzaBean.venta = item.venta
                                cobranzaBean.estado = item.estado
                                cobranzaBean.observaciones = item.observaciones
                                cobranzaBean.fecha = item.fecha
                                cobranzaBean.hora = item.hora
                                cobranzaBean.empleado = item.identificador
                                cobranzaBean.isCheck = false
                                paymentDao.save(cobranzaBean)
                                chargeByClientList.add(cobranzaBean)
                            }
                        }
                        onGetChargeByClientListener.onGetChargeByClientSuccess(chargeByClientList)
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

            val saveCharges = ApiServices.getClientRetrofit().create(
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

            val updateCharge = ApiServices.getClientRetrofit().create(
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