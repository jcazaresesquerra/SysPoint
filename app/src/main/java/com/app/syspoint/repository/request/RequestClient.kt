package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.client.ClientInteractor
import com.app.syspoint.models.Client
import com.app.syspoint.models.Data
import com.app.syspoint.models.RequestClientsByRute
import com.app.syspoint.models.json.ClientJson
import com.app.syspoint.repository.objectBox.entities.ChargeBox
import com.app.syspoint.repository.objectBox.dao.ChargeDao
import com.app.syspoint.repository.objectBox.dao.ClientDao
import com.app.syspoint.repository.objectBox.dao.RuteClientDao
import com.app.syspoint.repository.objectBox.dao.SpecialPricesDao
import com.app.syspoint.repository.objectBox.entities.ClientBox
import com.app.syspoint.repository.objectBox.entities.RuteClientBox
import com.app.syspoint.repository.objectBox.entities.SpecialPricesBox
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class RequestClient {
    companion object {
        fun requestAllClients(onGetAllClientsListener: ClientInteractor.GetAllClientsListener) {
            val getClients = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getAllClientes()

            getClients.enqueue(object: Callback<ClientJson> {
                override fun onResponse(call: Call<ClientJson>, response: Response<ClientJson>) {
                    if (response.isSuccessful) {
                        val dao = ClientDao()

                        val clientList = arrayListOf<ClientBox>()
                        val newClientList: MutableList<ClientBox> = arrayListOf()

                        response.body()!!.clients!!.map {item ->

                            val bean = dao.getClientByAccount(item!!.cuenta)

                            if (bean == null) {
                                val clientBox = ClientBox()
                                clientBox.nombre_comercial = item.nombreComercial
                                clientBox.calle = item.calle
                                clientBox.numero = item.numero
                                clientBox.colonia = item.colonia
                                clientBox.ciudad = item.ciudad
                                clientBox.codigo_postal = item.codigoPostal
                                clientBox.fecha_registro = item.fechaRegistro
                                clientBox.cuenta = item.cuenta
                                clientBox.status = item.status == 1
                                clientBox.consec = item.consec ?: "0"
                                clientBox.visitado = 0
                                clientBox.rango = item.rango
                                clientBox.lun = item.lun
                                clientBox.mar = item.mar
                                clientBox.mie = item.mie
                                clientBox.jue = item.jue
                                clientBox.vie = item.vie
                                clientBox.sab = item.sab
                                clientBox.dom = item.dom
                                clientBox.lunOrder = item.lunOrder
                                clientBox.marOrder = item.marOrder
                                clientBox.mieOrder = item.mieOrder
                                clientBox.jueOrder = item.jueOrder
                                clientBox.vieOrder = item.vieOrder
                                clientBox.sabOrder = item.sabOrder
                                clientBox.domOrder = item.domOrder
                                clientBox.latitud = item.latitud
                                clientBox.longitud = item.longitud
                                clientBox.isCredito = item.isCredito == 1
                                clientBox.limite_credito = item.limite_credito
                                clientBox.saldo_credito = item.saldo_credito
                                clientBox.contacto_phone = item.phone_contacto
                                clientBox.updatedAt = item.updatedAt
                                dao.insert(clientBox)
                                newClientList.add(clientBox)
                            } else {
                                val update = if (!bean.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val dateItem = formatter.parse(item.updatedAt)
                                    val dateBean = formatter.parse(bean.updatedAt)
                                    dateItem?.compareTo(dateBean) ?: 1
                                } else 1

                                if (update > 0) {
                                    bean.nombre_comercial = item.nombreComercial
                                    bean.calle = item.calle
                                    bean.numero = item.numero
                                    bean.colonia = item.colonia
                                    bean.ciudad = item.ciudad
                                    bean.codigo_postal = item.codigoPostal
                                    bean.fecha_registro = item.fechaRegistro
                                    bean.cuenta = item.cuenta
                                    bean.status = item.status == 1
                                    bean.consec = item.consec ?: "0"
                                    bean.visitado = if (bean.visitado == 1) 1 else 0
                                    bean.rango = item.rango
                                    bean.lun = item.lun
                                    bean.mar = item.mar
                                    bean.mie = item.mie
                                    bean.jue = item.jue
                                    bean.vie = item.vie
                                    bean.sab = item.sab
                                    bean.dom = item.dom
                                    bean.lunOrder = item.lunOrder
                                    bean.marOrder = item.marOrder
                                    bean.mieOrder = item.mieOrder
                                    bean.jueOrder = item.jueOrder
                                    bean.vieOrder = item.vieOrder
                                    bean.sabOrder = item.sabOrder
                                    bean.domOrder = item.domOrder
                                    bean.latitud = item.latitud
                                    bean.longitud = item.longitud
                                    bean.contacto_phone = item.phone_contacto
                                    bean.recordatorio = item.recordatorio
                                    bean.visitasNoefectivas = item.visitas
                                    bean.isCredito = item.isCredito == 1
                                    bean.limite_credito = item.limite_credito
                                    bean.saldo_credito = item.saldo_credito
                                    bean.matriz = item.matriz
                                    bean.updatedAt = item.updatedAt
                                }
                                dao.insert(bean)
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

        fun requestGetAllClientsByDate(ruteByEmployee: String, day: Int, onGetAllClientsListener: ClientInteractor.GetAllClientsListener) {
            val clientsByRute = RequestClientsByRute(rute = ruteByEmployee)
            val getClients = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getAllClientesByRute(clientsByRute)

            getClients.enqueue(object: Callback<ClientJson> {
                override fun onResponse(call: Call<ClientJson>, response: Response<ClientJson>) {
                    if (response.isSuccessful) {

                        val clientList = arrayListOf<ClientBox>()
                        val dao = ClientDao()
                        val daoRute = com.app.syspoint.repository.objectBox.dao.RuteClientDao()

                        response.body()!!.clients!!.map { item ->
                            val bean = dao.getClientByAccount(item!!.cuenta)

                            if (bean == null) {
                                val clientBox = ClientBox()

                                clientBox.nombre_comercial = item.nombreComercial
                                clientBox.calle = item.calle
                                clientBox.numero = item.numero
                                clientBox.colonia = item.colonia
                                clientBox.ciudad = item.ciudad
                                clientBox.codigo_postal = item.codigoPostal
                                clientBox.fecha_registro = item.fechaRegistro
                                clientBox.cuenta = item.cuenta
                                clientBox.status = item.status == 1
                                clientBox.consec = item.consec ?: "0"
                                clientBox.visitado = 0
                                clientBox.rango = item.rango
                                clientBox.lun = item.lun
                                clientBox.mar = item.mar
                                clientBox.mie = item.mie
                                clientBox.jue = item.jue
                                clientBox.vie = item.vie
                                clientBox.sab = item.sab
                                clientBox.dom = item.dom
                                clientBox.lunOrder = item.lunOrder
                                clientBox.marOrder = item.marOrder
                                clientBox.mieOrder = item.mieOrder
                                clientBox.jueOrder = item.jueOrder
                                clientBox.vieOrder = item.vieOrder
                                clientBox.sabOrder = item.sabOrder
                                clientBox.domOrder = item.domOrder
                                clientBox.latitud = item.latitud
                                clientBox.longitud = item.longitud
                                clientBox.isCredito = item.isCredito == 1
                                clientBox.limite_credito = item.limite_credito
                                clientBox.saldo_credito = item.saldo_credito
                                clientBox.contacto_phone = item.phone_contacto
                                clientBox.matriz = item.matriz
                                clientBox.updatedAt = item.updatedAt
                                dao.insert(clientBox)
                                clientList.add(clientBox)
                            } else {

                                val update = if (!bean.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val dateItem = try {
                                        formatter.parse(item.updatedAt)
                                    } catch (e:Exception) {
                                        formatter.parse(item.updatedAt + "00:00:00")
                                    }
                                    val dateBean = try {
                                        formatter.parse(bean.updatedAt)
                                    } catch (e:Exception) {
                                        formatter.parse(bean.updatedAt + "00:00:00")
                                    }
                                    dateItem?.compareTo(dateBean) ?: 1
                                } else 1

                                if (update > 0) {
                                    bean.nombre_comercial = item.nombreComercial
                                    bean.calle = item.calle
                                    bean.numero = item.numero
                                    bean.colonia = item.colonia
                                    bean.ciudad = item.ciudad
                                    bean.codigo_postal = item.codigoPostal
                                    bean.fecha_registro = item.fechaRegistro
                                    bean.cuenta = item.cuenta
                                    bean.status = item.status == 1
                                    bean.consec = item.consec ?: "0"
                                    bean.visitado = if (bean.visitado == 0) 0 else 1
                                    bean.rango = item.rango
                                    bean.lun = item.lun
                                    bean.mar = item.mar
                                    bean.mie = item.mie
                                    bean.jue = item.jue
                                    bean.vie = item.vie
                                    bean.sab = item.sab
                                    bean.dom = item.dom
                                    bean.lunOrder = item.lunOrder
                                    bean.marOrder = item.marOrder
                                    bean.mieOrder = item.mieOrder
                                    bean.jueOrder = item.jueOrder
                                    bean.vieOrder = item.vieOrder
                                    bean.sabOrder = item.sabOrder
                                    bean.domOrder = item.domOrder
                                    bean.latitud = item.latitud
                                    bean.longitud = item.longitud
                                    bean.contacto_phone = item.phone_contacto
                                    bean.recordatorio = item.recordatorio
                                    bean.visitasNoefectivas = item.visitas
                                    bean.isCredito = item.isCredito == 1
                                    bean.limite_credito = item.limite_credito
                                    bean.saldo_credito = item.saldo_credito
                                    bean.matriz = item.matriz
                                    bean.updatedAt = item.updatedAt
                                    dao.insert(bean)
                                }

                                clientList.add(bean)
                            }

                            if (saveClientWithDay(item, day)) {
                                val ruteClientBox1 = daoRute.getClienteByCuentaCliente(item.cuenta)

                                if (ruteClientBox1 == null) {
                                    val ruteClientBox = RuteClientBox()

                                    ruteClientBox.nombre_comercial = item.nombreComercial
                                    ruteClientBox.calle = item.calle
                                    ruteClientBox.numero = item.numero
                                    ruteClientBox.colonia = item.colonia
                                    ruteClientBox.cuenta = item.cuenta
                                    ruteClientBox.visitado = 0
                                    ruteClientBox.rango = item.rango
                                    ruteClientBox.status = item.status == 1
                                    ruteClientBox.lun = item.lun
                                    ruteClientBox.mar = item.mar
                                    ruteClientBox.mie = item.mie
                                    ruteClientBox.jue = item.jue
                                    ruteClientBox.vie = item.vie
                                    ruteClientBox.sab = item.sab
                                    ruteClientBox.dom = item.dom
                                    ruteClientBox.lunOrder = item.lunOrder
                                    ruteClientBox.marOrder = item.marOrder
                                    ruteClientBox.mieOrder = item.mieOrder
                                    ruteClientBox.jueOrder = item.jueOrder
                                    ruteClientBox.vieOrder = item.vieOrder
                                    ruteClientBox.sabOrder = item.sabOrder
                                    ruteClientBox.domOrder = item.domOrder
                                    ruteClientBox.latitud = item.latitud
                                    ruteClientBox.longitud = item.longitud
                                    ruteClientBox.isCredito = item.isCredito == 1
                                    ruteClientBox.recordatorio = item.recordatorio
                                    ruteClientBox.phone_contact = item.phone_contacto
                                    ruteClientBox.updatedAt = item.updatedAt

                                    daoRute.insert(ruteClientBox)
                                } else {
                                    val update = if (!ruteClientBox1.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val dateItem = formatter.parse(item.updatedAt)
                                        val dateBean = formatter.parse(ruteClientBox1.updatedAt)
                                        dateItem?.compareTo(dateBean) ?: 1
                                    } else 1

                                    if (update > 0) {
                                        ruteClientBox1.nombre_comercial = item.nombreComercial
                                        ruteClientBox1.calle = item.calle
                                        ruteClientBox1.numero = item.numero
                                        ruteClientBox1.colonia = item.colonia
                                        ruteClientBox1.cuenta = item.cuenta
                                        ruteClientBox1.visitado = if (ruteClientBox1.visitado == 0) 0 else 1
                                        ruteClientBox1.rango = item.rango
                                        ruteClientBox1.status = item.status == 1
                                        ruteClientBox1.lun = item.lun
                                        ruteClientBox1.mar = item.mar
                                        ruteClientBox1.mie = item.mie
                                        ruteClientBox1.jue = item.jue
                                        ruteClientBox1.vie = item.vie
                                        ruteClientBox1.sab = item.sab
                                        ruteClientBox1.dom = item.dom
                                        ruteClientBox1.lunOrder = item.lunOrder
                                        ruteClientBox1.marOrder = item.marOrder
                                        ruteClientBox1.mieOrder = item.mieOrder
                                        ruteClientBox1.jueOrder = item.jueOrder
                                        ruteClientBox1.vieOrder = item.vieOrder
                                        ruteClientBox1.sabOrder = item.sabOrder
                                        ruteClientBox1.domOrder = item.domOrder
                                        ruteClientBox1.latitud = item.latitud
                                        ruteClientBox1.longitud = item.longitud
                                        ruteClientBox1.isCredito = item.isCredito == 1
                                        ruteClientBox1.recordatorio = item.recordatorio
                                        ruteClientBox1.phone_contact = item.phone_contacto
                                        ruteClientBox1.updatedAt = item.updatedAt
                                    }
                                    daoRute.insert(ruteClientBox1)
                                }
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
                        val dao = ClientDao()
                        val clientList = arrayListOf<ClientBox>()

                        response.body()!!.clients!!.map {item ->
                            val clientBox1 = dao.getClientByAccount(item!!.cuenta)

                            if (clientBox1 == null) {
                                val clientBox = ClientBox()
                                val clientDao = ClientDao()
                                clientBox.nombre_comercial = item.nombreComercial
                                clientBox.calle = item.calle
                                clientBox.numero = item.numero
                                clientBox.colonia = item.colonia
                                clientBox.ciudad = item.ciudad
                                clientBox.codigo_postal = item.codigoPostal
                                clientBox.fecha_registro = item.fechaRegistro
                                clientBox.cuenta = item.cuenta
                                clientBox.status = item.status == 1
                                clientBox.consec = item.consec ?: "0"
                                clientBox.visitado = 0
                                clientBox.rango = item.rango
                                clientBox.lun = item.lun
                                clientBox.mar = item.mar
                                clientBox.mie = item.mie
                                clientBox.jue = item.jue
                                clientBox.vie = item.vie
                                clientBox.sab = item.sab
                                clientBox.dom = item.dom
                                clientBox.latitud = item.latitud
                                clientBox.longitud = item.longitud
                                clientBox.isCredito = item.isCredito == 1
                                clientBox.limite_credito = item.limite_credito
                                clientBox.saldo_credito = item.saldo_credito
                                clientBox.contacto_phone = item.phone_contacto
                                clientBox.matriz = item.matriz
                                clientBox.updatedAt = item.updatedAt

                                clientDao.insert(clientBox)
                                clientList.add(clientBox)
                            } else {
                                val update = if (!clientBox1.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val dateItem = formatter.parse(item.updatedAt)
                                    val dateBean = formatter.parse(clientBox1.updatedAt)
                                    dateItem?.compareTo(dateBean) ?: 1
                                } else 1

                                if (update > 0) {
                                    clientBox1.nombre_comercial = item.nombreComercial
                                    clientBox1.calle = item.calle
                                    clientBox1.numero = item.numero
                                    clientBox1.colonia = item.colonia
                                    clientBox1.ciudad = item.ciudad
                                    clientBox1.codigo_postal = item.codigoPostal
                                    clientBox1.fecha_registro = item.fechaRegistro
                                    clientBox1.cuenta = item.cuenta
                                    clientBox1.status = item.status == 1
                                    clientBox1.consec = item.consec ?: "0"
                                    clientBox1.visitado = if (clientBox1.visitado == 1) 1 else 0
                                    clientBox1.rango = item.rango
                                    clientBox1.lun = item.lun
                                    clientBox1.mar = item.mar
                                    clientBox1.mie = item.mie
                                    clientBox1.jue = item.jue
                                    clientBox1.vie = item.vie
                                    clientBox1.sab = item.sab
                                    clientBox1.dom = item.dom
                                    clientBox1.latitud = item.latitud
                                    clientBox1.longitud = item.longitud
                                    clientBox1.contacto_phone = item.phone_contacto
                                    clientBox1.recordatorio = item.recordatorio
                                    clientBox1.visitasNoefectivas = item.visitas
                                    clientBox1.isCredito = item.isCredito == 1
                                    clientBox1.limite_credito = item.limite_credito
                                    clientBox1.saldo_credito = item.saldo_credito
                                    clientBox1.matriz = item.matriz
                                    clientBox1.updatedAt = item.updatedAt
                                }
                                dao.insert(clientBox1)
                                clientList.add(clientBox1)
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
                        val error = response.errorBody()!!.string()
                        onSaveClientListener.onSaveClientError()
                    }
                }

                override fun onFailure(call: Call<ClientJson>, t: Throwable) {
                    onSaveClientListener.onSaveClientError()
                }
            })
        }

        fun findClient(clientName: String, onFindClientListener: ClientInteractor.FindClientListener) {
            val saveClients = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).findClient(clientName)

            saveClients.enqueue(object: Callback<ClientJson> {
                override fun onResponse(call: Call<ClientJson>, response: Response<ClientJson>) {
                    if (response.isSuccessful) {
                        val clientDao = ClientDao()
                        val clientList = arrayListOf<ClientBox>()

                        response.body()!!.clients!!.map {item ->
                            val clientBox1 = clientDao.getClientByAccount(item!!.cuenta)

                            if (clientBox1 == null) {
                                val clientBox = ClientBox()

                                clientBox.nombre_comercial = item.nombreComercial
                                clientBox.calle = item.calle
                                clientBox.numero = item.numero
                                clientBox.colonia = item.colonia
                                clientBox.ciudad = item.ciudad
                                clientBox.codigo_postal = item.codigoPostal
                                clientBox.fecha_registro = item.fechaRegistro
                                clientBox.cuenta = item.cuenta
                                clientBox.status = item.status == 1
                                clientBox.consec = item.consec ?: "0"
                                clientBox.visitado = 0
                                clientBox.rango = item.rango
                                clientBox.lun = item.lun
                                clientBox.mar = item.mar
                                clientBox.mie = item.mie
                                clientBox.jue = item.jue
                                clientBox.vie = item.vie
                                clientBox.sab = item.sab
                                clientBox.dom = item.dom
                                clientBox.latitud = item.latitud
                                clientBox.longitud = item.longitud
                                clientBox.isCredito = item.isCredito == 1
                                clientBox.limite_credito = item.limite_credito
                                clientBox.saldo_credito = item.saldo_credito
                                clientBox.contacto_phone = item.phone_contacto
                                clientBox.matriz = item.matriz
                                clientBox.updatedAt = item.updatedAt

                                clientDao.insert(clientBox)
                                clientList.add(clientBox)
                            } else {

                                val update = if (!clientBox1.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val dateItem = formatter.parse(item.updatedAt)
                                    val dateBean = formatter.parse(clientBox1.updatedAt)
                                    dateItem?.compareTo(dateBean) ?: 1
                                } else 1

                                if (update > 0) {
                                    clientBox1.nombre_comercial = item.nombreComercial
                                    clientBox1.calle = item.calle
                                    clientBox1.numero = item.numero
                                    clientBox1.colonia = item.colonia
                                    clientBox1.ciudad = item.ciudad
                                    clientBox1.codigo_postal = item.codigoPostal
                                    clientBox1.fecha_registro = item.fechaRegistro
                                    clientBox1.cuenta = item.cuenta
                                    clientBox1.status = item.status == 1
                                    clientBox1.consec = item.consec ?: "0"
                                    clientBox1.visitado = if (clientBox1.visitado == 1) 1 else 0
                                    clientBox1.rango = item.rango
                                    clientBox1.lun = item.lun
                                    clientBox1.mar = item.mar
                                    clientBox1.mie = item.mie
                                    clientBox1.jue = item.jue
                                    clientBox1.vie = item.vie
                                    clientBox1.sab = item.sab
                                    clientBox1.dom = item.dom
                                    clientBox1.latitud = item.latitud
                                    clientBox1.longitud = item.longitud
                                    clientBox1.contacto_phone = item.phone_contacto
                                    clientBox1.recordatorio = item.recordatorio
                                    clientBox1.visitasNoefectivas = item.visitas
                                    clientBox1.isCredito = item.isCredito == 1
                                    clientBox1.limite_credito = item.limite_credito
                                    clientBox1.saldo_credito = item.saldo_credito
                                    clientBox1.matriz = item.matriz
                                    clientBox1.updatedAt = item.updatedAt
                                }
                                clientDao.insert(clientBox1)
                                clientList.add(clientBox1)
                            }
                        }
                        onFindClientListener.onFindClientSuccess(clientList)
                    } else {
                        val error = response.errorBody()!!.string()
                        onFindClientListener.onFindClientError()
                    }
                }

                override fun onFailure(call: Call<ClientJson>, t: Throwable) {
                    onFindClientListener.onFindClientError()
                }
            })
        }

        fun requestGetClientByAccount(account: String, onGetClientByAccount: ClientInteractor.GetClientByAccount) {
            val getClient = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getClientInfo(account)

            getClient.enqueue(object: Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            val clientDao = ClientDao()
                            val ruteClientDao = RuteClientDao()

                            response.body()!!.data.clientes.map {
                                val clientBox1 = clientDao.getClientByAccount(it.cuenta)
                                val ruteClientBox = ruteClientDao.getClienteByCuentaCliente(it.cuenta)

                                if (clientBox1 == null) {
                                    val clientBox = ClientBox()
                                    clientBox.nombre_comercial = it.nombreComercial
                                    clientBox.calle = it.calle
                                    clientBox.numero = it.numero
                                    clientBox.colonia = it.colonia
                                    clientBox.ciudad = it.ciudad
                                    clientBox.codigo_postal = it.codigoPostal
                                    clientBox.fecha_registro = it.fechaRegistro
                                    clientBox.cuenta = it.cuenta
                                    clientBox.status = it.status == 1
                                    clientBox.consec = it.consec ?: "0"
                                    clientBox.visitado = 0
                                    clientBox.rango = it.rango
                                    clientBox.lun = it.lun
                                    clientBox.mar = it.mar
                                    clientBox.mie = it.mie
                                    clientBox.jue = it.jue
                                    clientBox.vie = it.vie
                                    clientBox.sab = it.sab
                                    clientBox.dom = it.dom
                                    clientBox.lunOrder = it.lunOrder
                                    clientBox.marOrder = it.marOrder
                                    clientBox.mieOrder = it.mieOrder
                                    clientBox.jueOrder = it.jueOrder
                                    clientBox.vieOrder = it.vieOrder
                                    clientBox.sabOrder = it.sabOrder
                                    clientBox.domOrder = it.domOrder
                                    clientBox.latitud = it.latitud
                                    clientBox.longitud = it.longitud
                                    clientBox.contacto_phone = it.phone_contacto
                                    clientBox.recordatorio = it.recordatorio
                                    clientBox.visitasNoefectivas = it.visitas
                                    clientBox.isCredito = it.isCredito == 1
                                    clientBox.limite_credito = it.limite_credito
                                    clientBox.saldo_credito = it.saldo_credito
                                    clientBox.matriz = it.matriz
                                    clientBox.updatedAt = it.updatedAt
                                    clientDao.insert(clientBox)
                                } else {
                                    val update = if (!clientBox1.updatedAt.isNullOrEmpty() && !it.updatedAt.isNullOrEmpty()) {
                                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val dateItem = formatter.parse(it.updatedAt)
                                        val dateBean = formatter.parse(clientBox1.updatedAt)
                                        dateItem?.compareTo(dateBean) ?: 1
                                    } else 1

                                    if (update > 0) {
                                        clientBox1.nombre_comercial = it.nombreComercial
                                        clientBox1.calle = it.calle
                                        clientBox1.numero = it.numero
                                        clientBox1.colonia = it.colonia
                                        clientBox1.ciudad = it.ciudad
                                        clientBox1.codigo_postal = it.codigoPostal
                                        clientBox1.fecha_registro = it.fechaRegistro
                                        clientBox1.cuenta = it.cuenta
                                        clientBox1.status = it.status == 1
                                        clientBox1.consec = it.consec ?: "0"
                                        clientBox1.visitado = if (clientBox1.visitado == 1) 1 else 0
                                        clientBox1.rango = it.rango
                                        clientBox1.lun = it.lun
                                        clientBox1.mar = it.mar
                                        clientBox1.mie = it.mie
                                        clientBox1.jue = it.jue
                                        clientBox1.vie = it.vie
                                        clientBox1.sab = it.sab
                                        clientBox1.dom = it.dom
                                        clientBox1.lunOrder = it.lunOrder
                                        clientBox1.marOrder = it.marOrder
                                        clientBox1.mieOrder = it.mieOrder
                                        clientBox1.jueOrder = it.jueOrder
                                        clientBox1.vieOrder = it.vieOrder
                                        clientBox1.sabOrder = it.sabOrder
                                        clientBox1.domOrder = it.domOrder
                                        clientBox1.latitud = it.latitud
                                        clientBox1.longitud = it.longitud
                                        clientBox1.contacto_phone = it.phone_contacto
                                        clientBox1.recordatorio = it.recordatorio
                                        clientBox1.visitasNoefectivas = it.visitas
                                        clientBox1.isCredito = it.isCredito == 1
                                        clientBox1.limite_credito = it.limite_credito
                                        clientBox1.saldo_credito = it.saldo_credito
                                        clientBox1.matriz = it.matriz
                                        clientBox1.updatedAt = it.updatedAt
                                    }
                                    clientDao.insert(clientBox1)
                                }


                                if (ruteClientBox == null) {
                                    val clienteBeanRute = RuteClientBox()
                                    clienteBeanRute.nombre_comercial = it.nombreComercial
                                    clienteBeanRute.calle = it.calle
                                    clienteBeanRute.numero = it.numero
                                    clienteBeanRute.colonia = it.colonia
                                    clienteBeanRute.cuenta = it.cuenta
                                    clienteBeanRute.visitado = 0
                                    clienteBeanRute.rango = it.rango
                                    clienteBeanRute.status = it.status == 1
                                    clienteBeanRute.lun = it.lun
                                    clienteBeanRute.mar = it.mar
                                    clienteBeanRute.mie = it.mie
                                    clienteBeanRute.jue = it.jue
                                    clienteBeanRute.vie = it.vie
                                    clienteBeanRute.sab = it.sab
                                    clienteBeanRute.dom = it.dom
                                    clienteBeanRute.lunOrder = it.lunOrder
                                    clienteBeanRute.marOrder = it.marOrder
                                    clienteBeanRute.mieOrder = it.mieOrder
                                    clienteBeanRute.jueOrder = it.jueOrder
                                    clienteBeanRute.vieOrder = it.vieOrder
                                    clienteBeanRute.sabOrder = it.sabOrder
                                    clienteBeanRute.domOrder = it.domOrder
                                    clienteBeanRute.latitud = it.latitud
                                    clienteBeanRute.longitud = it.longitud
                                    clienteBeanRute.isCredito = it.isCredito == 1
                                    clienteBeanRute.recordatorio = it.recordatorio
                                    clienteBeanRute.phone_contact = it.phone_contacto
                                    clienteBeanRute.updatedAt = it.updatedAt
                                    ruteClientDao.insert(clienteBeanRute)
                                } else {

                                    val update = if (!ruteClientBox.updatedAt.isNullOrEmpty() && !it.updatedAt.isNullOrEmpty()) {
                                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val dateItem = formatter.parse(it.updatedAt)
                                        val dateBean = formatter.parse(ruteClientBox.updatedAt)
                                        dateItem?.compareTo(dateBean) ?: 1
                                    } else 1

                                    if (update > 0) {
                                        ruteClientBox.nombre_comercial = it.nombreComercial
                                        ruteClientBox.calle = it.calle
                                        ruteClientBox.numero = it.numero
                                        ruteClientBox.colonia = it.colonia
                                        ruteClientBox.cuenta = it.cuenta
                                        ruteClientBox.visitado = if (ruteClientBox.visitado == 0) 0 else 1
                                        ruteClientBox.rango = it.rango
                                        ruteClientBox.status = it.status == 1
                                        ruteClientBox.lun = it.lun
                                        ruteClientBox.mar = it.mar
                                        ruteClientBox.mie = it.mie
                                        ruteClientBox.jue = it.jue
                                        ruteClientBox.vie = it.vie
                                        ruteClientBox.sab = it.sab
                                        ruteClientBox.dom = it.dom
                                        ruteClientBox.lunOrder = it.lunOrder
                                        ruteClientBox.marOrder = it.marOrder
                                        ruteClientBox.mieOrder = it.mieOrder
                                        ruteClientBox.jueOrder = it.jueOrder
                                        ruteClientBox.vieOrder = it.vieOrder
                                        ruteClientBox.sabOrder = it.sabOrder
                                        ruteClientBox.domOrder = it.domOrder
                                        ruteClientBox.latitud = it.latitud
                                        ruteClientBox.longitud = it.longitud
                                        ruteClientBox.isCredito = it.isCredito == 1
                                        ruteClientBox.recordatorio = it.recordatorio
                                        ruteClientBox.phone_contact = it.phone_contacto
                                        ruteClientBox.updatedAt = it.updatedAt
                                    }
                                    ruteClientDao.insert(ruteClientBox)
                                }
                            }

                            val specialPricesDao = SpecialPricesDao()

                            response.body()!!.data.precios.map { item ->
                                val preciosEspecialesBean =
                                    specialPricesDao.getPrecioEspeciaPorCliente(
                                        item.articulo,
                                        item.cliente
                                    )

                                //Si no hay precios especiales entonces crea un precio
                                if (preciosEspecialesBean == null) {
                                    val specialPricesBox = SpecialPricesBox()
                                    specialPricesBox.cliente = item.cliente
                                    specialPricesBox.articulo = item.articulo
                                    specialPricesBox.precio = item.precio
                                    specialPricesBox.active = item.active == 1
                                    specialPricesDao.insert(specialPricesBox)
                                } else {
                                    preciosEspecialesBean.cliente = item.cliente
                                    preciosEspecialesBean.articulo = item.articulo
                                    preciosEspecialesBean.precio = item.precio
                                    preciosEspecialesBean.active = item.active == 1
                                    specialPricesDao.insert(preciosEspecialesBean)
                                }
                            }

                            val chargeDao = ChargeDao()
                            response.body()!!.data.cobranzas.map { item ->
                                val chargeBox1 = chargeDao.getByCobranza(item.cobranza)
                                if (chargeBox1 == null) {
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
                                    chargeDao.insert(chargeBox)
                                } else {
                                    chargeBox1.cobranza = item.cobranza
                                    chargeBox1.cliente = item.cuenta
                                    chargeBox1.importe = item.importe
                                    chargeBox1.saldo = item.saldo
                                    chargeBox1.venta = item.venta
                                    chargeBox1.estado = item.estado
                                    chargeBox1.observaciones = item.observaciones
                                    chargeBox1.fecha = item.fecha
                                    chargeBox1.hora = item.hora
                                    chargeBox1.empleado = item.identificador
                                    chargeBox1.isCheck = false
                                    chargeDao.insert(chargeBox1)
                                }
                            }
                        }
                        onGetClientByAccount.onGetClientSuccess()
                    } else {
                        val error = response.errorBody()
                        onGetClientByAccount.onGetClientError()
                    }
                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                    onGetClientByAccount.onGetClientError()
                }
            })
        }

        fun getLastCLient(onGetLastClient: ClientInteractor.GetLastClient) {
            val getClients = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getLastClient()

            getClients.enqueue(object: Callback<ClientJson> {
                override fun onResponse(call: Call<ClientJson>, response: Response<ClientJson>) {
                    if (response.isSuccessful) {

                        if (!response.body()!!.clients.isNullOrEmpty()) {
                            val client: ClientBox
                            val item = response.body()!!.clients!![0]
                            val clientDao = ClientDao()
                            val bean = clientDao.getClientByAccount(item!!.cuenta)

                            if (bean == null) {
                                val clientBox = ClientBox()
                                clientBox.nombre_comercial = item.nombreComercial
                                clientBox.calle = item.calle
                                clientBox.numero = item.numero
                                clientBox.colonia = item.colonia
                                clientBox.ciudad = item.ciudad
                                clientBox.codigo_postal = item.codigoPostal
                                clientBox.fecha_registro = item.fechaRegistro
                                clientBox.cuenta = item.cuenta
                                clientBox.status = item.status == 1
                                clientBox.consec = item.consec ?: "0"
                                clientBox.visitado = 0
                                clientBox.rango = item.rango
                                clientBox.lun = item.lun
                                clientBox.mar = item.mar
                                clientBox.mie = item.mie
                                clientBox.jue = item.jue
                                clientBox.vie = item.vie
                                clientBox.sab = item.sab
                                clientBox.dom = item.dom
                                clientBox.lunOrder = item.lunOrder
                                clientBox.marOrder = item.marOrder
                                clientBox.mieOrder = item.mieOrder
                                clientBox.jueOrder = item.jueOrder
                                clientBox.vieOrder = item.vieOrder
                                clientBox.sabOrder = item.sabOrder
                                clientBox.domOrder = item.domOrder
                                clientBox.latitud = item.latitud
                                clientBox.longitud = item.longitud
                                clientBox.isCredito = item.isCredito == 1
                                clientBox.limite_credito = item.limite_credito
                                clientBox.saldo_credito = item.saldo_credito
                                clientBox.contacto_phone = item.phone_contacto
                                clientBox.updatedAt = item.updatedAt
                                clientDao.insert(clientBox)
                                client = clientBox
                            } else {
                                val update = if (!bean.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val dateItem = formatter.parse(item.updatedAt)
                                    val dateBean = formatter.parse(bean.updatedAt)
                                    dateItem?.compareTo(dateBean) ?: 1
                                } else 1

                                if (update > 0) {
                                    bean.nombre_comercial = item.nombreComercial
                                    bean.calle = item.calle
                                    bean.numero = item.numero
                                    bean.colonia = item.colonia
                                    bean.ciudad = item.ciudad
                                    bean.codigo_postal = item.codigoPostal
                                    bean.fecha_registro = item.fechaRegistro
                                    bean.cuenta = item.cuenta
                                    bean.status = item.status == 1
                                    bean.consec = item.consec ?: "0"
                                    bean.visitado = if (bean.visitado == 1) 1 else 0
                                    bean.rango = item.rango
                                    bean.lun = item.lun
                                    bean.mar = item.mar
                                    bean.mie = item.mie
                                    bean.jue = item.jue
                                    bean.vie = item.vie
                                    bean.sab = item.sab
                                    bean.dom = item.dom
                                    bean.lunOrder = item.lunOrder
                                    bean.marOrder = item.marOrder
                                    bean.mieOrder = item.mieOrder
                                    bean.jueOrder = item.jueOrder
                                    bean.vieOrder = item.vieOrder
                                    bean.sabOrder = item.sabOrder
                                    bean.domOrder = item.domOrder
                                    bean.latitud = item.latitud
                                    bean.longitud = item.longitud
                                    bean.contacto_phone = item.phone_contacto
                                    bean.recordatorio = item.recordatorio
                                    bean.visitasNoefectivas = item.visitas
                                    bean.isCredito = item.isCredito == 1
                                    bean.limite_credito = item.limite_credito
                                    bean.saldo_credito = item.saldo_credito
                                    bean.matriz = item.matriz
                                    bean.updatedAt = item.updatedAt
                                }
                                clientDao.insert(bean)
                                client = bean
                            }
                            onGetLastClient.onGetLastClientSuccess(client)
                        } else {
                            onGetLastClient.onGetLastClientError()
                        }
                    } else {
                        onGetLastClient.onGetLastClientError()
                    }
                }

                override fun onFailure(call: Call<ClientJson>, t: Throwable) {
                    onGetLastClient.onGetLastClientError()
                }

            })
        }

        fun saveClientWithDay(client: Client, day: Int): Boolean {
            return if (day == 1 && client.lun == 1)
                true
            else if (day == 2 && client.mar == 1)
                true
            else if (day == 3 && client.mie == 1)
                true
            else if (day == 4 && client.jue == 1)
                true
            else if (day == 5 && client.vie == 1)
                true
            else if (day == 6 && client.sab == 1)
                true
            else if (day == 7 && client.dom == 1)
                true
            else false
        }
    }


}