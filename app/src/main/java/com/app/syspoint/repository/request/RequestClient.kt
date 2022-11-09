package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.client.ClientInteractor
import com.app.syspoint.models.Client
import com.app.syspoint.models.RequestClientsByRute
import com.app.syspoint.models.json.ClientJson
import com.app.syspoint.repository.database.bean.ClienteBean
import com.app.syspoint.repository.database.bean.ClientesRutaBean
import com.app.syspoint.repository.database.bean.RuteoBean
import com.app.syspoint.repository.database.dao.ClientDao
import com.app.syspoint.repository.database.dao.RuteClientDao
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
                                clienteBean.lunOrder = item.lunOrder
                                clienteBean.marOrder = item.marOrder
                                clienteBean.mieOrder = item.mieOrder
                                clienteBean.jueOrder = item.jueOrder
                                clienteBean.vieOrder = item.vieOrder
                                clienteBean.sabOrder = item.sabOrder
                                clienteBean.domOrder = item.domOrder
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

        fun requestGetAllClientsByDate(ruteByEmployee: String, onGetAllClientsListener: ClientInteractor.GetAllClientsListener) {
            val clientsByRute = RequestClientsByRute(rute = ruteByEmployee)
            val getClients = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getAllClientesByRute(clientsByRute)

            getClients.enqueue(object: Callback<ClientJson> {
                override fun onResponse(call: Call<ClientJson>, response: Response<ClientJson>) {
                    if (response.isSuccessful) {
                        val clientList = arrayListOf<ClienteBean>()
                        val dao = ClientDao()
                        val daoRute = RuteClientDao()

                        response.body()!!.clients!!.map { item ->
                            //Validamos si existe el cliente
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
                                clienteBean.lunOrder = item.lunOrder
                                clienteBean.marOrder = item.marOrder
                                clienteBean.mieOrder = item.mieOrder
                                clienteBean.jueOrder = item.jueOrder
                                clienteBean.vieOrder = item.vieOrder
                                clienteBean.sabOrder = item.sabOrder
                                clienteBean.domOrder = item.domOrder
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
                                bean.visitado = if (bean.visitado == 0) 0 else 1
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
                                dao.save(bean)
                                clientList.add(bean)
                            }

                            val beanRute = daoRute.getClienteByCuentaCliente(item.cuenta)

                            if (beanRute == null) {
                                val clienteBeanRute = ClientesRutaBean()
                                val clientDaoRute = RuteClientDao()
                                clienteBeanRute.nombre_comercial = item.nombreComercial
                                clienteBeanRute.calle = item.calle
                                clienteBeanRute.numero = item.numero
                                clienteBeanRute.colonia = item.colonia
                                clienteBeanRute.cuenta = item.cuenta
                                clienteBeanRute.visitado = 0
                                clienteBeanRute.rango = item.rango
                                clienteBeanRute.categoria = item.categoria
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
                                clienteBeanRute.is_credito = item.isCredito == 1
                                clienteBeanRute.recordatorio = item.recordatorio
                                clientDaoRute.insert(clienteBeanRute)
                            } else {
                                beanRute.nombre_comercial = item.nombreComercial
                                beanRute.calle = item.calle
                                beanRute.numero = item.numero
                                beanRute.colonia = item.colonia
                                beanRute.cuenta = item.cuenta
                                beanRute.visitado = if (beanRute.visitado == 0) 0 else 1
                                beanRute.rango = item.rango
                                beanRute.categoria = item.categoria
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
                                daoRute.save(beanRute)
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