package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.client.ClientInteractor
import com.app.syspoint.models.Client
import com.app.syspoint.models.Data
import com.app.syspoint.models.RequestClientsByRute
import com.app.syspoint.models.json.ClientJson
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.database.dao.*
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

                        val clientList = arrayListOf<ClienteBean>()
                        val newClientList: MutableList<ClienteBean> = arrayListOf()

                        for (item in response.body()!!.clients!!) {

                            val bean = dao.getClientByAccount(item!!.cuenta)

                            if (bean == null) {
                                val clienteBean = ClienteBean()
                                clienteBean.nombre_comercial = item.nombreComercial
                                clienteBean.calle = item.calle
                                clienteBean.numero = item.numero
                                clienteBean.colonia = item.colonia
                                clienteBean.ciudad = item.ciudad
                                clienteBean.codigo_postal = item.codigoPostal
                                clienteBean.fecha_registro = item.fechaRegistro
                                clienteBean.cuenta = item.cuenta
                                clienteBean.status = item.status == 1
                                clienteBean.consec = item.consec ?: "0"
                                clienteBean.visitado = 0
                                clienteBean.rango = item.rango
                                clienteBean.lun = item.lun
                                clienteBean.mar = item.mar
                                clienteBean.mie = item.mie
                                clienteBean.jue = item.jue
                                clienteBean.vie = item.vie
                                clienteBean.sab = item.sab
                                clienteBean.dom = item.dom
                                clienteBean.lunOrder = item.lunOrder
                                clienteBean.marOrder = item.marOrder
                                clienteBean.mieOrder = item.mieOrder
                                clienteBean.jueOrder = item.jueOrder
                                clienteBean.vieOrder = item.vieOrder
                                clienteBean.sabOrder = item.sabOrder
                                clienteBean.domOrder = item.domOrder
                                clienteBean.latitud = item.latitud
                                clienteBean.longitud = item.longitud
                                clienteBean.is_credito = item.isCredito == 1
                                clienteBean.limite_credito = item.limite_credito
                                clienteBean.saldo_credito = item.saldo_credito
                                clienteBean.contacto_phone = item.phone_contacto
                                clienteBean.updatedAt = item.updatedAt
                                newClientList.add(clienteBean)
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
                                    bean.is_credito = item.isCredito == 1
                                    bean.limite_credito = item.limite_credito
                                    bean.saldo_credito = item.saldo_credito
                                    bean.matriz = item.matriz
                                    bean.updatedAt = item.updatedAt
                                }
                                dao.save(bean)
                                clientList.add(bean)
                            }
                        }

                        if (newClientList.isNotEmpty()) {
                            dao.insertAll(newClientList.toList())
                            clientList.addAll(newClientList)
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

                        val clientList = arrayListOf<ClienteBean>()
                        val newClientList: MutableList<ClienteBean> = arrayListOf()
                        val dao = ClientDao()
                        val daoRute = RuteClientDao()

                        dao.beginTransaction()
                        daoRute.beginTransaction()
                        response.body()!!.clients!!.map { item ->
                            val bean = dao.getClientByAccount(item!!.cuenta)

                            if (bean == null) {
                                val clienteBean = ClienteBean()

                                clienteBean.nombre_comercial = item.nombreComercial
                                clienteBean.calle = item.calle
                                clienteBean.numero = item.numero
                                clienteBean.colonia = item.colonia
                                clienteBean.ciudad = item.ciudad
                                clienteBean.codigo_postal = item.codigoPostal
                                clienteBean.fecha_registro = item.fechaRegistro
                                clienteBean.cuenta = item.cuenta
                                clienteBean.status = item.status == 1
                                clienteBean.consec = item.consec ?: "0"
                                clienteBean.visitado = 0
                                clienteBean.rango = item.rango
                                clienteBean.lun = item.lun
                                clienteBean.mar = item.mar
                                clienteBean.mie = item.mie
                                clienteBean.jue = item.jue
                                clienteBean.vie = item.vie
                                clienteBean.sab = item.sab
                                clienteBean.dom = item.dom
                                clienteBean.lunOrder = item.lunOrder
                                clienteBean.marOrder = item.marOrder
                                clienteBean.mieOrder = item.mieOrder
                                clienteBean.jueOrder = item.jueOrder
                                clienteBean.vieOrder = item.vieOrder
                                clienteBean.sabOrder = item.sabOrder
                                clienteBean.domOrder = item.domOrder
                                clienteBean.latitud = item.latitud
                                clienteBean.longitud = item.longitud
                                clienteBean.is_credito = item.isCredito == 1
                                clienteBean.limite_credito = item.limite_credito
                                clienteBean.saldo_credito = item.saldo_credito
                                clienteBean.contacto_phone = item.phone_contacto
                                clienteBean.matriz = item.matriz
                                clienteBean.updatedAt = item.updatedAt
                                dao.insert(clienteBean)
                                clientList.add(clienteBean)
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
                                    bean.is_credito = item.isCredito == 1
                                    bean.limite_credito = item.limite_credito
                                    bean.saldo_credito = item.saldo_credito
                                    bean.matriz = item.matriz
                                    bean.updatedAt = item.updatedAt
                                    dao.save(bean)
                                }

                                clientList.add(bean)
                            }

                            if (saveClientWithDay(item, day)) {
                                val beanRute = daoRute.getClienteByCuentaCliente(item.cuenta)

                                if (beanRute == null) {
                                    val clienteBeanRute = ClientesRutaBean()

                                    clienteBeanRute.nombre_comercial = item.nombreComercial
                                    clienteBeanRute.calle = item.calle
                                    clienteBeanRute.numero = item.numero
                                    clienteBeanRute.colonia = item.colonia
                                    clienteBeanRute.cuenta = item.cuenta
                                    clienteBeanRute.visitado = 0
                                    clienteBeanRute.rango = item.rango
                                    clienteBeanRute.status = item.status == 1
                                    clienteBeanRute.lun = item.lun
                                    clienteBeanRute.mar = item.mar
                                    clienteBeanRute.mie = item.mie
                                    clienteBeanRute.jue = item.jue
                                    clienteBeanRute.vie = item.vie
                                    clienteBeanRute.sab = item.sab
                                    clienteBeanRute.dom = item.dom
                                    clienteBeanRute.lunOrder = item.lunOrder
                                    clienteBeanRute.marOrder = item.marOrder
                                    clienteBeanRute.mieOrder = item.mieOrder
                                    clienteBeanRute.jueOrder = item.jueOrder
                                    clienteBeanRute.vieOrder = item.vieOrder
                                    clienteBeanRute.sabOrder = item.sabOrder
                                    clienteBeanRute.domOrder = item.domOrder
                                    clienteBeanRute.latitud = item.latitud
                                    clienteBeanRute.longitud = item.longitud
                                    clienteBeanRute.is_credito = item.isCredito == 1
                                    clienteBeanRute.recordatorio = item.recordatorio
                                    clienteBeanRute.phone_contact = item.phone_contacto
                                    clienteBeanRute.updatedAt = item.updatedAt

                                    daoRute.insert(clienteBeanRute)
                                } else {
                                    val update = if (!beanRute.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val dateItem = formatter.parse(item.updatedAt)
                                        val dateBean = formatter.parse(beanRute.updatedAt)
                                        dateItem?.compareTo(dateBean) ?: 1
                                    } else 1

                                    if (update > 0) {
                                        beanRute.nombre_comercial = item.nombreComercial
                                        beanRute.calle = item.calle
                                        beanRute.numero = item.numero
                                        beanRute.colonia = item.colonia
                                        beanRute.cuenta = item.cuenta
                                        beanRute.visitado = if (beanRute.visitado == 0) 0 else 1
                                        beanRute.rango = item.rango
                                        beanRute.status = item.status == 1
                                        beanRute.lun = item.lun
                                        beanRute.mar = item.mar
                                        beanRute.mie = item.mie
                                        beanRute.jue = item.jue
                                        beanRute.vie = item.vie
                                        beanRute.sab = item.sab
                                        beanRute.dom = item.dom
                                        beanRute.lunOrder = item.lunOrder
                                        beanRute.marOrder = item.marOrder
                                        beanRute.mieOrder = item.mieOrder
                                        beanRute.jueOrder = item.jueOrder
                                        beanRute.vieOrder = item.vieOrder
                                        beanRute.sabOrder = item.sabOrder
                                        beanRute.domOrder = item.domOrder
                                        beanRute.latitud = item.latitud
                                        beanRute.longitud = item.longitud
                                        beanRute.is_credito = item.isCredito == 1
                                        beanRute.recordatorio = item.recordatorio
                                        beanRute.phone_contact = item.phone_contacto
                                        beanRute.updatedAt = item.updatedAt
                                    }
                                    daoRute.save(beanRute)
                                }
                            }
                        }
                        dao.commmit()
                        daoRute.commmit()

                        if (newClientList.isNotEmpty()) {
                            //dao.insertAll(newClientList.toList())
                            clientList.addAll(newClientList)
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
                                clienteBean.cuenta = item.cuenta
                                clienteBean.status = item.status == 1
                                clienteBean.consec = item.consec ?: "0"
                                clienteBean.visitado = 0
                                clienteBean.rango = item.rango
                                clienteBean.lun = item.lun
                                clienteBean.mar = item.mar
                                clienteBean.mie = item.mie
                                clienteBean.jue = item.jue
                                clienteBean.vie = item.vie
                                clienteBean.sab = item.sab
                                clienteBean.dom = item.dom
                                clienteBean.latitud = item.latitud
                                clienteBean.longitud = item.longitud
                                clienteBean.is_credito = item.isCredito == 1
                                clienteBean.limite_credito = item.limite_credito
                                clienteBean.saldo_credito = item.saldo_credito
                                clienteBean.contacto_phone = item.phone_contacto
                                clienteBean.matriz = item.matriz
                                clienteBean.updatedAt = item.updatedAt

                                clientDao.insert(clienteBean)
                                clientList.add(clienteBean)
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
                                    bean.latitud = item.latitud
                                    bean.longitud = item.longitud
                                    bean.contacto_phone = item.phone_contacto
                                    bean.recordatorio = item.recordatorio
                                    bean.visitasNoefectivas = item.visitas
                                    bean.is_credito = item.isCredito == 1
                                    bean.limite_credito = item.limite_credito
                                    bean.saldo_credito = item.saldo_credito
                                    bean.matriz = item.matriz
                                    bean.updatedAt = item.updatedAt
                                }
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
                                clienteBean.cuenta = item.cuenta
                                clienteBean.status = item.status == 1
                                clienteBean.consec = item.consec ?: "0"
                                clienteBean.visitado = 0
                                clienteBean.rango = item.rango
                                clienteBean.lun = item.lun
                                clienteBean.mar = item.mar
                                clienteBean.mie = item.mie
                                clienteBean.jue = item.jue
                                clienteBean.vie = item.vie
                                clienteBean.sab = item.sab
                                clienteBean.dom = item.dom
                                clienteBean.latitud = item.latitud
                                clienteBean.longitud = item.longitud
                                clienteBean.is_credito = item.isCredito == 1
                                clienteBean.limite_credito = item.limite_credito
                                clienteBean.saldo_credito = item.saldo_credito
                                clienteBean.contacto_phone = item.phone_contacto
                                clienteBean.matriz = item.matriz
                                clienteBean.updatedAt = item.updatedAt

                                clientDao.insert(clienteBean)
                                clientList.add(clienteBean)
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
                                    bean.latitud = item.latitud
                                    bean.longitud = item.longitud
                                    bean.contacto_phone = item.phone_contacto
                                    bean.recordatorio = item.recordatorio
                                    bean.visitasNoefectivas = item.visitas
                                    bean.is_credito = item.isCredito == 1
                                    bean.limite_credito = item.limite_credito
                                    bean.saldo_credito = item.saldo_credito
                                    bean.matriz = item.matriz
                                    bean.updatedAt = item.updatedAt
                                }
                                dao.save(bean)
                                clientList.add(bean)
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
                            var clientDao = ClientDao()
                            val daoRute = RuteClientDao()
                            var clienteBean = ClienteBean()

                            response.body()!!.data.clientes.map {
                                val bean = clientDao.getClientByAccount(it.cuenta)
                                val beanRute = daoRute.getClienteByCuentaCliente(it.cuenta)

                                if (bean == null) {
                                    clienteBean = ClienteBean()
                                    clientDao = ClientDao()
                                    clienteBean.nombre_comercial = it.nombreComercial
                                    clienteBean.calle = it.calle
                                    clienteBean.numero = it.numero
                                    clienteBean.colonia = it.colonia
                                    clienteBean.ciudad = it.ciudad
                                    clienteBean.codigo_postal = it.codigoPostal
                                    clienteBean.fecha_registro = it.fechaRegistro
                                    clienteBean.cuenta = it.cuenta
                                    clienteBean.status = it.status == 1
                                    clienteBean.consec = it.consec ?: "0"
                                    clienteBean.visitado = 0
                                    clienteBean.rango = it.rango
                                    clienteBean.lun = it.lun
                                    clienteBean.mar = it.mar
                                    clienteBean.mie = it.mie
                                    clienteBean.jue = it.jue
                                    clienteBean.vie = it.vie
                                    clienteBean.sab = it.sab
                                    clienteBean.dom = it.dom
                                    clienteBean.lunOrder = it.lunOrder
                                    clienteBean.marOrder = it.marOrder
                                    clienteBean.mieOrder = it.mieOrder
                                    clienteBean.jueOrder = it.jueOrder
                                    clienteBean.vieOrder = it.vieOrder
                                    clienteBean.sabOrder = it.sabOrder
                                    clienteBean.domOrder = it.domOrder
                                    clienteBean.latitud = it.latitud
                                    clienteBean.longitud = it.longitud
                                    clienteBean.contacto_phone = it.phone_contacto
                                    clienteBean.recordatorio = it.recordatorio
                                    clienteBean.visitasNoefectivas = it.visitas
                                    clienteBean.is_credito = it.isCredito == 1
                                    clienteBean.limite_credito = it.limite_credito
                                    clienteBean.saldo_credito = it.saldo_credito
                                    clienteBean.matriz = it.matriz
                                    clienteBean.updatedAt = it.updatedAt
                                    clientDao.insert(clienteBean)
                                } else {
                                    val update = if (!bean.updatedAt.isNullOrEmpty() && !it.updatedAt.isNullOrEmpty()) {
                                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val dateItem = formatter.parse(it.updatedAt)
                                        val dateBean = formatter.parse(bean.updatedAt)
                                        dateItem?.compareTo(dateBean) ?: 1
                                    } else 1

                                    if (update > 0) {
                                        bean.nombre_comercial = it.nombreComercial
                                        bean.calle = it.calle
                                        bean.numero = it.numero
                                        bean.colonia = it.colonia
                                        bean.ciudad = it.ciudad
                                        bean.codigo_postal = it.codigoPostal
                                        bean.fecha_registro = it.fechaRegistro
                                        bean.cuenta = it.cuenta
                                        bean.status = it.status == 1
                                        bean.consec = it.consec ?: "0"
                                        bean.visitado = if (bean.visitado == 1) 1 else 0
                                        bean.rango = it.rango
                                        bean.lun = it.lun
                                        bean.mar = it.mar
                                        bean.mie = it.mie
                                        bean.jue = it.jue
                                        bean.vie = it.vie
                                        bean.sab = it.sab
                                        bean.dom = it.dom
                                        bean.lunOrder = it.lunOrder
                                        bean.marOrder = it.marOrder
                                        bean.mieOrder = it.mieOrder
                                        bean.jueOrder = it.jueOrder
                                        bean.vieOrder = it.vieOrder
                                        bean.sabOrder = it.sabOrder
                                        bean.domOrder = it.domOrder
                                        bean.latitud = it.latitud
                                        bean.longitud = it.longitud
                                        bean.contacto_phone = it.phone_contacto
                                        bean.recordatorio = it.recordatorio
                                        bean.visitasNoefectivas = it.visitas
                                        bean.is_credito = it.isCredito == 1
                                        bean.limite_credito = it.limite_credito
                                        bean.saldo_credito = it.saldo_credito
                                        bean.matriz = it.matriz
                                        bean.updatedAt = it.updatedAt
                                    }
                                    clientDao.save(bean)
                                }


                                if (beanRute == null) {
                                    val clienteBeanRute = ClientesRutaBean()
                                    val clientDaoRute = RuteClientDao()
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
                                    clienteBeanRute.is_credito = it.isCredito == 1
                                    clienteBeanRute.recordatorio = it.recordatorio
                                    clienteBeanRute.phone_contact = it.phone_contacto
                                    clienteBeanRute.updatedAt = it.updatedAt
                                    clientDaoRute.insert(clienteBeanRute)
                                } else {

                                    val update = if (!beanRute.updatedAt.isNullOrEmpty() && !it.updatedAt.isNullOrEmpty()) {
                                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val dateItem = formatter.parse(it.updatedAt)
                                        val dateBean = formatter.parse(beanRute.updatedAt)
                                        dateItem?.compareTo(dateBean) ?: 1
                                    } else 1

                                    if (update > 0) {
                                        beanRute.nombre_comercial = it.nombreComercial
                                        beanRute.calle = it.calle
                                        beanRute.numero = it.numero
                                        beanRute.colonia = it.colonia
                                        beanRute.cuenta = it.cuenta
                                        beanRute.visitado = if (beanRute.visitado == 0) 0 else 1
                                        beanRute.rango = it.rango
                                        beanRute.status = it.status == 1
                                        beanRute.lun = it.lun
                                        beanRute.mar = it.mar
                                        beanRute.mie = it.mie
                                        beanRute.jue = it.jue
                                        beanRute.vie = it.vie
                                        beanRute.sab = it.sab
                                        beanRute.dom = it.dom
                                        beanRute.lunOrder = it.lunOrder
                                        beanRute.marOrder = it.marOrder
                                        beanRute.mieOrder = it.mieOrder
                                        beanRute.jueOrder = it.jueOrder
                                        beanRute.vieOrder = it.vieOrder
                                        beanRute.sabOrder = it.sabOrder
                                        beanRute.domOrder = it.domOrder
                                        beanRute.latitud = it.latitud
                                        beanRute.longitud = it.longitud
                                        beanRute.is_credito = it.isCredito == 1
                                        beanRute.recordatorio = it.recordatorio
                                        beanRute.phone_contact = it.phone_contacto
                                        beanRute.updatedAt = it.updatedAt
                                    }
                                    daoRute.save(beanRute)
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
                                    val bean = PreciosEspecialesBean()
                                    bean.cliente = item.cliente
                                    bean.articulo = item.articulo
                                    bean.precio = item.precio
                                    bean.active = item.active == 1
                                    specialPricesDao.insert(bean)
                                } else {
                                    preciosEspecialesBean.cliente = item.cliente
                                    preciosEspecialesBean.articulo = item.articulo
                                    preciosEspecialesBean.precio = item.precio
                                    preciosEspecialesBean.active = item.active == 1
                                    specialPricesDao.save(preciosEspecialesBean)
                                }
                            }

                            val paymentDao = PaymentDao()
                            response.body()!!.data.cobranzas.map { item ->
                                val cobranzaBean = paymentDao.getByCobranza(item.cobranza)
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
                            val client: ClienteBean
                            val item = response.body()!!.clients!![0]
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
                                clienteBean.cuenta = item.cuenta
                                clienteBean.status = item.status == 1
                                clienteBean.consec = item.consec ?: "0"
                                clienteBean.visitado = 0
                                clienteBean.rango = item.rango
                                clienteBean.lun = item.lun
                                clienteBean.mar = item.mar
                                clienteBean.mie = item.mie
                                clienteBean.jue = item.jue
                                clienteBean.vie = item.vie
                                clienteBean.sab = item.sab
                                clienteBean.dom = item.dom
                                clienteBean.lunOrder = item.lunOrder
                                clienteBean.marOrder = item.marOrder
                                clienteBean.mieOrder = item.mieOrder
                                clienteBean.jueOrder = item.jueOrder
                                clienteBean.vieOrder = item.vieOrder
                                clienteBean.sabOrder = item.sabOrder
                                clienteBean.domOrder = item.domOrder
                                clienteBean.latitud = item.latitud
                                clienteBean.longitud = item.longitud
                                clienteBean.is_credito = item.isCredito == 1
                                clienteBean.limite_credito = item.limite_credito
                                clienteBean.saldo_credito = item.saldo_credito
                                clienteBean.contacto_phone = item.phone_contacto
                                clienteBean.updatedAt = item.updatedAt
                                clientDao.insert(clienteBean)
                                client = clienteBean
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
                                    bean.is_credito = item.isCredito == 1
                                    bean.limite_credito = item.limite_credito
                                    bean.saldo_credito = item.saldo_credito
                                    bean.matriz = item.matriz
                                    bean.updatedAt = item.updatedAt
                                }
                                dao.save(bean)
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

        fun saveClientWithDay(clienteBean: Client, day: Int): Boolean {
            return if (day == 1 && clienteBean.lun == 1)
                true
            else if (day == 2 && clienteBean.mar == 1)
                true
            else if (day == 3 && clienteBean.mie == 1)
                true
            else if (day == 4 && clienteBean.jue == 1)
                true
            else if (day == 5 && clienteBean.vie == 1)
                true
            else if (day == 6 && clienteBean.sab == 1)
                true
            else if (day == 7 && clienteBean.dom == 1)
                true
            else false
        }
    }


}