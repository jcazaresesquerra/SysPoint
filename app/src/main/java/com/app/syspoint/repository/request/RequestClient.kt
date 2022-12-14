package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.client.ClientInteractor
import com.app.syspoint.models.Client
import com.app.syspoint.models.json.ClientJson
import com.app.syspoint.models.json.EmployeeJson
import com.app.syspoint.repository.database.bean.ClienteBean
import com.app.syspoint.repository.database.dao.ClientDao
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestClient {
    companion object {
        fun requestAllClients(onGetAllClientsListener: ClientInteractor.GetAllClientsListener) {
            val getClients = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getAllClientes()

            getClients.enqueue(object: Callback<ClientJson> {
                override fun onResponse(call: Call<ClientJson>, response: Response<ClientJson>) {
                    if (response.isSuccessful) {
                        val clientList = arrayListOf<ClienteBean>()
                        for (item in response.body()!!.clients!!) {
                            //Validamos si existe el cliente
                            val dao = ClientDao()
                            val bean = dao.getClientByAccount(item!!.cuenta)

                            if (bean == null) {
                                val clienteBean = ClienteBean()
                                val clientDao = ClientDao()
                                clienteBean.nombre_comercial = item.nombreComercial
                                clienteBean.calle = item.calle
                                clienteBean.numero = item.numero
                                clienteBean.colonia = item.colonia
                                clienteBean.ciudad = item.ciudad
                                clienteBean.codigo_postal = item.codigoPostal
                                clienteBean.fecha_registro = item.fechaRegistro
                                clienteBean.fecha_baja = item.fechaBaja
                                clienteBean.cuenta = item.cuenta
                                clienteBean.grupo = item.grupo
                                clienteBean.categoria = item.categoria
                                clienteBean.status = item.status == 1
                                clienteBean.consec = item.consec
                                clienteBean.visitado = 0
                                clienteBean.region = item.region
                                clienteBean.sector = item.sector
                                clienteBean.rango = item.rango
                                clienteBean.secuencia = item.secuencia
                                clienteBean.periodo = item.periodo
                                clienteBean.ruta = item.ruta
                                clienteBean.lun = item.lun
                                clienteBean.mar = item.mar
                                clienteBean.mie = item.mie
                                clienteBean.jue = item.jue
                                clienteBean.vie = item.vie
                                clienteBean.sab = item.sab
                                clienteBean.dom = item.dom
                                clienteBean.is_credito = item.isCredito == 1
                                clienteBean.limite_credito = item.limite_credito
                                clienteBean.saldo_credito = item.saldo_credito
                                clientDao.insert(clienteBean)
                                clientList.add(clienteBean)
                            } else {
                                bean.nombre_comercial = item.nombreComercial
                                bean.calle = item.calle
                                bean.numero = item.numero
                                bean.colonia = item.colonia
                                bean.ciudad = item.ciudad
                                bean.codigo_postal = item.codigoPostal
                                bean.fecha_registro = item.fechaRegistro
                                bean.fecha_baja = item.fechaBaja
                                bean.cuenta = item.cuenta
                                bean.grupo = item.grupo
                                bean.categoria = item.categoria
                                bean.status = item.status == 1
                                bean.consec = item.consec
                                if (bean.visitado == 0) {
                                    bean.visitado = 0
                                } else if (bean.visitado == 1) {
                                    bean.visitado = 1
                                }
                                bean.region = item.region
                                bean.sector = item.sector
                                bean.rango = item.rango
                                bean.secuencia = item.secuencia
                                bean.periodo = item.periodo
                                bean.ruta = item.ruta
                                bean.lun = item.lun
                                bean.mar = item.mar
                                bean.mie = item.mie
                                bean.jue = item.jue
                                bean.vie = item.vie
                                bean.sab = item.sab
                                bean.dom = item.dom
                                bean.latitud = item.latitud
                                bean.longitud = item.longitud
                                bean.contacto_phone = item.phone_contacto
                                bean.recordatorio = item.recordatorio
                                bean.visitasNoefectivas = item.visitas
                                bean.is_credito = item.isCredito == 1
                                bean.limite_credito = item.limite_credito
                                bean.saldo_credito = item.saldo_credito
                                bean.matriz = item.matriz
                                dao.save(bean)
                                clientList.add(bean)
                            }
                        }
                        onGetAllClientsListener.onGetAllClientsSuccess(clientList)
                    } else {
                        onGetAllClientsListener.onGetAllClientsError()
                    }
                }

                override fun onFailure(call: Call<ClientJson>, t: Throwable) {
                    onGetAllClientsListener.onGetAllClientsError()
                }

            })
        }

        // not working fix server issue
        fun requestClientById(clientId: String, onGetClientByIdListener: ClientInteractor.GetClientByIdListener) {

            val getClient = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getClienteByID(clientId)

            getClient.enqueue(object: Callback<ClientJson> {
                override fun onResponse(
                    call: Call<ClientJson>,
                    response: Response<ClientJson>
                ) {
                    if (response.isSuccessful) {
                        val clientList = arrayListOf<ClienteBean>()
                        for (item in response.body()!!.clients!!) {
                            //Validamos si existe el cliente
                            val dao = ClientDao()
                            val bean = dao.getClientByAccount(item!!.cuenta)

                            if (bean == null) {
                                val clienteBean = ClienteBean()
                                val clientDao = ClientDao()
                                clienteBean.nombre_comercial = item.nombreComercial
                                clienteBean.calle = item.calle
                                clienteBean.numero = item.numero
                                clienteBean.colonia = item.colonia
                                clienteBean.ciudad = item.ciudad
                                clienteBean.codigo_postal = item.codigoPostal
                                clienteBean.fecha_registro = item.fechaRegistro
                                clienteBean.fecha_baja = item.fechaBaja
                                clienteBean.cuenta = item.cuenta
                                clienteBean.grupo = item.grupo
                                clienteBean.categoria = item.categoria
                                clienteBean.status = item.status == 1
                                clienteBean.consec = item.consec
                                clienteBean.visitado = 0
                                clienteBean.region = item.region
                                clienteBean.sector = item.sector
                                clienteBean.rango = item.rango
                                clienteBean.secuencia = item.secuencia
                                clienteBean.periodo = item.periodo
                                clienteBean.ruta = item.ruta
                                clienteBean.lun = item.lun
                                clienteBean.mar = item.mar
                                clienteBean.mie = item.mie
                                clienteBean.jue = item.jue
                                clienteBean.vie = item.vie
                                clienteBean.sab = item.sab
                                clienteBean.dom = item.dom
                                clienteBean.is_credito = item.isCredito == 1
                                clienteBean.limite_credito = item.limite_credito
                                clienteBean.saldo_credito = item.saldo_credito
                                clientDao.insert(clienteBean)
                                clientList.add(clienteBean)
                            } else {
                                bean.nombre_comercial = item.nombreComercial
                                bean.calle = item.calle
                                bean.numero = item.numero
                                bean.colonia = item.colonia
                                bean.ciudad = item.ciudad
                                bean.codigo_postal = item.codigoPostal
                                bean.fecha_registro = item.fechaRegistro
                                bean.fecha_baja = item.fechaBaja
                                bean.cuenta = item.cuenta
                                bean.grupo = item.grupo
                                bean.categoria = item.categoria
                                bean.status = item.status == 1
                                bean.consec = item.consec
                                if (bean.visitado == 0) {
                                    bean.visitado = 0
                                } else if (bean.visitado == 1) {
                                    bean.visitado = 1
                                }
                                bean.region = item.region
                                bean.sector = item.sector
                                bean.rango = item.rango
                                bean.secuencia = item.secuencia
                                bean.periodo = item.periodo
                                bean.ruta = item.ruta
                                bean.lun = item.lun
                                bean.mar = item.mar
                                bean.mie = item.mie
                                bean.jue = item.jue
                                bean.vie = item.vie
                                bean.sab = item.sab
                                bean.dom = item.dom
                                bean.latitud = item.latitud
                                bean.longitud = item.longitud
                                bean.contacto_phone = item.phone_contacto
                                bean.recordatorio = item.recordatorio
                                bean.visitasNoefectivas = item.visitas
                                bean.is_credito = item.isCredito == 1
                                bean.limite_credito = item.limite_credito
                                bean.saldo_credito = item.saldo_credito
                                bean.matriz = item.matriz
                                dao.save(bean)
                                clientList.add(bean)
                            }
                        }
                        onGetClientByIdListener.onGetClientByIdSuccess()
                    } else {
                        onGetClientByIdListener.onGetClientByIdError()
                    }
                }

                override fun onFailure(call: Call<ClientJson>, t: Throwable) {
                    onGetClientByIdListener.onGetClientByIdError()
                }
            })
        }

        fun saveClients(clientList: List<Client>, onSaveClientListener: ClientInteractor.SaveClientListener) {

            val clientJson = ClientJson()
            clientJson.clients = clientList
            val json = Gson().toJson(clientJson)
            Log.d("SinEmpleados", json)

            val saveClients = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).sendCliente(clientJson)

            saveClients.enqueue(object: Callback<ClientJson> {
                override fun onResponse(call: Call<ClientJson>, response: Response<ClientJson>) {
                    if (response.isSuccessful) {
                        onSaveClientListener.onSaveClientSuccess()
                    } else {
                        onSaveClientListener.onSaveClientError()
                    }
                }

                override fun onFailure(call: Call<ClientJson>, t: Throwable) {
                    onSaveClientListener.onSaveClientError()
                }
            })

        }
    }
}