package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.prices.PriceInteractor
import com.app.syspoint.models.Price
import com.app.syspoint.models.json.RequestClients
import com.app.syspoint.models.json.SpecialPriceJson
import com.app.syspoint.repository.database.bean.PreciosEspecialesBean
import com.app.syspoint.repository.database.dao.ClientDao
import com.app.syspoint.repository.database.dao.ProductDao
import com.app.syspoint.repository.database.dao.SpecialPricesDao
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

            val savePrice = ApiServices.getClientRetrofit().create(
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
            val specialPrices = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getPricesEspecial()

            specialPrices.enqueue(object: Callback<SpecialPriceJson> {
                override fun onResponse(call: Call<SpecialPriceJson>, response: Response<SpecialPriceJson>) {
                    if (response.isSuccessful) {
                        val priceList = arrayListOf<PreciosEspecialesBean>()
                        for (item in response.body()!!.prices!!) {

                            //Para obtener los datos del cliente
                            val clientDao = ClientDao()
                            val clienteBean = clientDao.getClientByAccount(item!!.cliente) ?: return

                            //Para obtener los datos del producto
                            val productDao = ProductDao()
                            val productoBean =
                                productDao.getProductoByArticulo(item.articulo) ?: return
                            val specialPricesDao = SpecialPricesDao()
                            val preciosEspecialesBean = specialPricesDao.getPrecioEspeciaPorCliente(
                                productoBean.articulo,
                                clienteBean.cuenta
                            )

                            //Si no hay precios especiales entonces crea un precio
                            if (preciosEspecialesBean == null) {
                                val dao = SpecialPricesDao()
                                val bean = PreciosEspecialesBean()
                                bean.cliente = clienteBean.cuenta
                                bean.articulo = productoBean.articulo
                                bean.precio = item.precio
                                bean.active = item.active == 1
                                dao.insert(bean)
                                priceList.add(bean)
                            } else {
                                preciosEspecialesBean.cliente = clienteBean.cuenta
                                preciosEspecialesBean.articulo = productoBean.articulo
                                preciosEspecialesBean.precio = item.precio
                                preciosEspecialesBean.active = item.active == 1
                                specialPricesDao.save(preciosEspecialesBean)
                                priceList.add(preciosEspecialesBean)
                            }
                        }
                        onGetSpecialPricesListener.onGetSpecialPricesSuccess(priceList)
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

            val pricesByClient = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getPreciosByClient(requestPrices)


            pricesByClient.enqueue(object: Callback<SpecialPriceJson> {
                override fun onResponse(call: Call<SpecialPriceJson>, response: Response<SpecialPriceJson>) {
                    if (response.isSuccessful) {
                        val pricesByClientList = arrayListOf<PreciosEspecialesBean>()
                        for (item in response.body()!!.prices!!) {

                            //Para obtener los datos del cliente
                            val clientDao = ClientDao()
                            val clienteBean = clientDao.getClientByAccount(item!!.cliente) ?: return

                            //Para obtener los datos del producto
                            val productDao = ProductDao()
                            val productoBean =
                                productDao.getProductoByArticulo(item!!.articulo) ?: return
                            val specialPricesDao = SpecialPricesDao()
                            val preciosEspecialesBean = specialPricesDao.getPrecioEspeciaPorCliente(
                                productoBean.articulo,
                                clienteBean.cuenta
                            )

                            //Si no hay precios especiales entonces crea un precio
                            if (preciosEspecialesBean == null) {
                                val dao = SpecialPricesDao()
                                val bean = PreciosEspecialesBean()
                                bean.cliente = clienteBean.cuenta
                                bean.articulo = productoBean.articulo
                                bean.precio = item.precio
                                bean.active = item.active == 1
                                dao.insert(bean)
                                pricesByClientList.add(bean)
                            } else {
                                preciosEspecialesBean.cliente = clienteBean.cuenta
                                preciosEspecialesBean.articulo = productoBean.articulo
                                preciosEspecialesBean.precio = item.precio
                                preciosEspecialesBean.active = item.active == 1
                                specialPricesDao.save(preciosEspecialesBean)
                                pricesByClientList.add(preciosEspecialesBean)
                            }
                        }
                        onGetPricesByClientListener.onGetPricesByClientSuccess(pricesByClientList)
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