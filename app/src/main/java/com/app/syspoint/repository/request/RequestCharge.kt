package com.app.syspoint.repository.request

import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Looper
import android.util.Log
import com.app.syspoint.BuildConfig
import com.app.syspoint.interactor.charge.ChargeInteractor
import com.app.syspoint.models.Payment
import com.app.syspoint.models.RequestChargeByRute
import com.app.syspoint.models.json.PaymentJson
import com.app.syspoint.models.json.RequestCobranza
import com.app.syspoint.models.json.RequestTokenBody
import com.app.syspoint.repository.objectBox.dao.ChargeDao
import com.app.syspoint.repository.objectBox.dao.ClientDao
import com.app.syspoint.repository.objectBox.dao.StockDao
import com.app.syspoint.repository.objectBox.entities.ChargeBox
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.text.SimpleDateFormat


class RequestCharge {
    companion object {
        fun requestGetCharge2(): Call<PaymentJson> {
            val appVersion = BuildConfig.VERSION_NAME.split(".")
            val version = appVersion[0]
            val subversion = appVersion[1] + "." + appVersion[2]
            val requestTokenBody = RequestTokenBody(version, subversion)

            val getCharge = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getCobranza()

            return getCharge
        }

        fun requestGetCharge(onGetChargeListener: ChargeInteractor.OnGetChargeListener): Call<PaymentJson> {
            val appVersion = BuildConfig.VERSION_NAME.split(".")
            val version = appVersion[0]
            val subversion = appVersion[1] + "." + appVersion[2]
            val requestTokenBody = RequestTokenBody(version, subversion)

            val getCharge = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getCobranza()

            val isUiThread =
                if (VERSION.SDK_INT >= VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread
            Log.d("RuestCharge", "requestGetCharge request isUIThread: $isUiThread")

            getCharge.enqueue(object: Callback<PaymentJson> {
                override fun onResponse(call: Call<PaymentJson>, response: Response<PaymentJson>) {
                    val isUiThread =
                        if (VERSION.SDK_INT >= VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread
                    Log.d("RuestCharge", "requestGetCharge response isUIThread: $isUiThread")

                    if (response.isSuccessful) {

                        val chargeList = arrayListOf<ChargeBox>()
                        val stockId = StockDao().getCurrentStockId()
                        val chargeDao = ChargeDao()

                        response.body()!!.payments!!.map {item ->
                            val charge = chargeDao.getByCobranza(item!!.cobranza)

                            if (charge == null) {
                                val chargeBox = ChargeBox()
                                chargeBox.cobranza = item.cobranza
                                chargeBox.cliente = item.cuenta
                                chargeBox.importe = item.importe
                                chargeBox.saldo = item.saldo
                                chargeBox.venta = item.venta
                                chargeBox.estado = item.estado
                                chargeBox.observaciones = item.observaciones
                                chargeBox.fecha = item.fecha
                                chargeBox.hora = item.hora
                                chargeBox.empleado = item.identificador
                                chargeBox.updatedAt = item.updatedAt
                                chargeBox.stockId = stockId
                                chargeDao.insert(chargeBox)
                                chargeList.add(chargeBox)
                            } else {

                                val update = if (!charge.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val dateItem = try {
                                        formatter.parse(item.updatedAt)
                                    } catch (e: Exception) {
                                        formatter.parse(item.updatedAt + "00:00:00")
                                    }
                                    val dateBean = try {
                                        formatter.parse(charge.updatedAt)
                                    } catch (e: Exception) {
                                        formatter.parse(charge.updatedAt + "00:00:00")
                                    }
                                    dateItem?.compareTo(dateBean) ?: 1
                                } else 1

                                if (update > 0) {
                                    charge.cobranza = item.cobranza
                                    charge.cliente = item.cuenta
                                    charge.importe = item.importe
                                    charge.saldo = item.saldo
                                    charge.venta = item.venta
                                    charge.estado = item.estado
                                    charge.observaciones = item.observaciones
                                    charge.fecha = item.fecha
                                    charge.hora = item.hora
                                    charge.empleado = item.identificador
                                    charge.updatedAt = item.updatedAt
                                    charge.stockId = stockId
                                    chargeDao.insert(charge)
                                }
                                chargeList.add(charge)
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

            return getCharge
        }

        fun requestGetChargeByEmployee(id: String, onGetChargeListener: ChargeInteractor.OnGetChargeByEmployeeListener) {
            val requestChargesByRute = RequestChargeByRute(id)
            val getCharge = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getAllCobranzaByEmployee(requestChargesByRute)

            val isUiThread =
                if (VERSION.SDK_INT >= VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread
            Log.d("RuestCharge", "requestGetChargeByEmployee request isUIThread: $isUiThread")

            getCharge.enqueue(object: Callback<PaymentJson> {
                override fun onResponse(call: Call<PaymentJson>, response: Response<PaymentJson>) {
                    val isUiThread =
                        if (VERSION.SDK_INT >= VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread
                    Log.d("RuestCharge", "requestGetChargeByEmployee response isUIThread: $isUiThread")
                    if (response.isSuccessful) {
                        val chargeList = arrayListOf<ChargeBox>()
                        val stockId = StockDao().getCurrentStockId()
                        val chargeDao = ChargeDao()

                        response.body()!!.payments!!.map {item ->
                            val charge = chargeDao.getByCobranza(item!!.cobranza)
                            if (charge == null) {
                                val chargeBox = ChargeBox()
                                chargeBox.cobranza = item.cobranza
                                chargeBox.cliente = item.cuenta
                                chargeBox.importe = item.importe
                                chargeBox.saldo = item.saldo
                                chargeBox.venta = item.venta
                                chargeBox.estado = item.estado
                                chargeBox.observaciones = item.observaciones
                                chargeBox.fecha = item.fecha
                                chargeBox.hora = item.hora
                                chargeBox.empleado = item.identificador
                                chargeBox.updatedAt = item.updatedAt
                                chargeBox.stockId = stockId
                                chargeDao.insert(chargeBox)
                                chargeList.add(chargeBox)
                            } else {
                                val update = if (!charge.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val dateItem = try {
                                        formatter.parse(item.updatedAt)
                                    } catch (e: Exception) {
                                        formatter.parse(item.updatedAt + "00:00:00")
                                    }
                                    val dateBean = try {
                                        formatter.parse(charge.updatedAt)
                                    } catch (e: Exception) {
                                        formatter.parse(charge.updatedAt + "00:00:00")
                                    }
                                    dateItem?.compareTo(dateBean) ?: 1
                                } else 1

                                if (update > 0) {
                                    charge.cobranza = item.cobranza
                                    charge.cliente = item.cuenta
                                    charge.importe = item.importe
                                    charge.saldo = item.saldo
                                    charge.venta = item.venta
                                    charge.estado = item.estado
                                    charge.observaciones = item.observaciones
                                    charge.fecha = item.fecha
                                    charge.hora = item.hora
                                    charge.empleado = item.identificador
                                    charge.updatedAt = item.updatedAt
                                    charge.stockId = stockId
                                    chargeDao.insert(charge)
                                }
                                chargeList.add(charge)
                            }
                        }
                        onGetChargeListener.onGetChargeByEmployeeSuccess(chargeList)
                    } else {
                        onGetChargeListener.onGetChargeByEmployeeError()
                    }
                }

                override fun onFailure(call: Call<PaymentJson>, t: Throwable) {
                    onGetChargeListener.onGetChargeByEmployeeError()
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

            val isUiThread =
                if (VERSION.SDK_INT >= VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread
            Log.d("RuestCharge", "requestGetChargeByClient request isUIThread: $isUiThread")

            getChargeByClient.enqueue(object: Callback<PaymentJson> {
                override fun onResponse(call: Call<PaymentJson>, response: Response<PaymentJson>) {
                    val isUiThread =
                        if (VERSION.SDK_INT >= VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread
                    Log.d("RuestCharge", " requestGetChargeByClient response isUIThread: $isUiThread")
                    if (response.isSuccessful) {
                        val chargeList = arrayListOf<ChargeBox>()
                        val stockId = StockDao().getCurrentStockId()
                        val chargeDao = ChargeDao()

                        response.body()!!.payments!!.map {item ->

                            val charge = chargeDao.getByCobranza(item!!.cobranza)
                            if (charge == null) {
                                val chargeBox = ChargeBox()
                                chargeBox.cobranza = item.cobranza
                                chargeBox.cliente = item.cuenta
                                chargeBox.importe = item.importe
                                chargeBox.saldo = item.saldo
                                chargeBox.venta = item.venta
                                chargeBox.estado = item.estado
                                chargeBox.observaciones = item.observaciones
                                chargeBox.fecha = item.fecha
                                chargeBox.hora = item.hora
                                chargeBox.empleado = item.identificador
                                chargeBox.isCheck = false
                                chargeBox.updatedAt = item.updatedAt
                                chargeBox.stockId = stockId
                                chargeDao.insert(chargeBox)
                                chargeList.add(chargeBox)
                            } else {
                                val update = if (!charge.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val dateItem = try {
                                        formatter.parse(item.updatedAt)
                                    } catch (e: Exception) {
                                        formatter.parse(item.updatedAt + "00:00:00")
                                    }
                                    val dateBean = try {
                                        formatter.parse(charge.updatedAt)
                                    } catch (e: Exception) {
                                        formatter.parse(charge.updatedAt + "00:00:00")
                                    }
                                    dateItem?.compareTo(dateBean) ?: 1
                                } else 1

                                if (update > 0) {
                                    charge.cobranza = item.cobranza
                                    charge.cliente = item.cuenta
                                    charge.importe = item.importe
                                    charge.saldo = item.saldo
                                    charge.venta = item.venta
                                    charge.estado = item.estado
                                    charge.observaciones = item.observaciones
                                    charge.fecha = item.fecha
                                    charge.hora = item.hora
                                    charge.empleado = item.identificador
                                    charge.isCheck = false
                                    charge.updatedAt = item.updatedAt
                                    charge.stockId = stockId
                                    chargeDao.insert(charge)
                                }
                                chargeList.add(charge)
                            }
                        }

                        onGetChargeByClientListener.onGetChargeByClientSuccess(chargeList)
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
            Log.d("saveCharge Sin Cobranza", json)

            val saveCharges = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).sendCobranza(chargeJson)

            val isUiThread =
                if (VERSION.SDK_INT >= VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread
            Log.d("RuestCharge", "save request isUIThread: $isUiThread")

            saveCharges.enqueue(object: Callback<PaymentJson> {
                override fun onResponse(call: Call<PaymentJson>, response: Response<PaymentJson>) {
                    val isUiThread =
                        if (VERSION.SDK_INT >= VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread
                    Log.d("RuestCharge", "save response isUIThread: $isUiThread")
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
            Log.d("updateCharge Sin Cobranza", json)

            val updateCharge = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).updateCobranza(chargeJson)

            val isUiThread =
                if (VERSION.SDK_INT >= VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread
            Log.d("RuestCharge", "update request isUIThread: $isUiThread")

            updateCharge.enqueue(object : Callback<PaymentJson> {
                override fun onResponse(call: Call<PaymentJson>, response: Response<PaymentJson>) {
                    val isUiThread =
                        if (VERSION.SDK_INT >= VERSION_CODES.M) Looper.getMainLooper().isCurrentThread else Thread.currentThread() === Looper.getMainLooper().thread
                    Log.d("RuestCharge", "update response isUIThread: $isUiThread")
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