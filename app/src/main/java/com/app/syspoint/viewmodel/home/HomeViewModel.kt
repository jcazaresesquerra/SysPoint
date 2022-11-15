package com.app.syspoint.viewmodel.home

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidnetworking.error.ANError
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.interactor.charge.ChargeInteractor.*
import com.app.syspoint.interactor.charge.ChargeInteractorImp
import com.app.syspoint.interactor.client.ClientInteractor.GetAllClientsListener
import com.app.syspoint.interactor.client.ClientInteractor.SaveClientListener
import com.app.syspoint.interactor.client.ClientInteractorImp
import com.app.syspoint.interactor.data.GetAllDataInteractor.OnGetAllDataByDateListener
import com.app.syspoint.interactor.data.GetAllDataInteractorImp
import com.app.syspoint.interactor.employee.GetEmployeeInteractor.GetEmployeesListener
import com.app.syspoint.interactor.employee.GetEmployeesInteractorImp
import com.app.syspoint.interactor.prices.PriceInteractor.GetSpecialPricesListener
import com.app.syspoint.interactor.prices.PriceInteractor.SendPricesListener
import com.app.syspoint.interactor.prices.PriceInteractorImp
import com.app.syspoint.interactor.product.GetProductInteractor.OnGetProductsListener
import com.app.syspoint.interactor.product.GetProductsInteractorImp
import com.app.syspoint.interactor.roles.RolInteractor.OnGetAllRolesListener
import com.app.syspoint.interactor.roles.RolInteractorImp
import com.app.syspoint.interactor.visit.VisitInteractor.OnSaveVisitListener
import com.app.syspoint.interactor.visit.VisitInteractorImp
import com.app.syspoint.models.Client
import com.app.syspoint.models.Payment
import com.app.syspoint.models.Price
import com.app.syspoint.models.Visit
import com.app.syspoint.models.sealed.HomeViewState
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.database.dao.*
import com.app.syspoint.repository.request.http.Servicio.ResponseOnError
import com.app.syspoint.repository.request.http.Servicio.ResponseOnSuccess
import com.app.syspoint.repository.request.http.SincVentas
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.utils.Utils
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class HomeViewModel: ViewModel() {
    val homeViewState = MutableLiveData<HomeViewState>()

    fun setUpClientRute() {
        val routingDao = RoutingDao()
        val ruteoBean = routingDao.getRutaEstablecida()

        var clientRute = listOf<ClientesRutaBean>()
        if (ruteoBean != null) {
            //clientRute = RuteClientDao().getAllRutaClientes(ruteoBean.getRuta(), ruteoBean.getDia())
        }

        homeViewState.postValue(HomeViewState.ClientRuteDefined(clientRute))
    }

    fun createSelectedRute() {
        val dao = RoutingDao()
        val bean = dao.getRutaEstablecidaFechaActual(Utils.fechaActual())

        if (bean != null) {
            homeViewState.value = HomeViewState.UpdateRute(bean)
        } else {
            homeViewState.value = HomeViewState.CreateRute
        }
    }

    fun updateCredits() {
        val clientDao = ClientDao()
        val clientCreditList = clientDao.getClientsByDay(Utils.fechaActual())
        val paymentDao = PaymentDao()
        for (item in clientCreditList) {
            try {
                val dao = ClientDao()
                item.saldo_credito = paymentDao.getTotalSaldoDocumentosCliente(item.cuenta)
                dao.save(item)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        homeViewState.value = HomeViewState.LoadingStart

        Handler().postDelayed({
            NetworkStateTask { connected: Boolean ->
                homeViewState.postValue(HomeViewState.LoadingFinish)
                if (connected) {
                    homeViewState.postValue(HomeViewState.GettingUpdates)

                    GetAllDataInteractorImp().executeGetAllDataByDate(object : OnGetAllDataByDateListener {
                        override fun onGetAllDataByDateSuccess() {
                            homeViewState.postValue(HomeViewState.LoadingFinish)
                        }

                        override fun onGetAllDataByDateError() {
                            homeViewState.postValue(HomeViewState.LoadingFinish)
                            homeViewState.postValue(HomeViewState.ErrorWhileGettingData)
                        }
                    })
                    sendVentas()
                    saveCobranza()
                    loadAbonos()
                    saveVisitas()
                    loadClientes()
                    savePreciosEspeciales()
                }
            }.execute()
        }, 100)
    }

    fun confirmRute(day: String, rute: String) {
        //Clientes normales
        val clientDao = ClientDao()
        clientDao.updateVisited()
        val ruteClientDao = RuteClientDao()
        ruteClientDao.clear()
        val routingDao = RoutingDao()
        routingDao.clear()
        val ruteoBean = RuteoBean()
        if (day.compareTo("Lunes", ignoreCase = true) == 0) {
            ruteoBean.dia = 1
        } else if (day.compareTo("Martes", ignoreCase = true) == 0) {
            ruteoBean.dia = 2
        } else if (day.compareTo("Miercoles", ignoreCase = true) == 0) {
            ruteoBean.dia = 3
        } else if (day.compareTo("Jueves", ignoreCase = true) == 0) {
            ruteoBean.dia = 4
        } else if (day.compareTo("Viernes", ignoreCase = true) == 0) {
            ruteoBean.dia = 5
        } else if (day.compareTo("Sabado", ignoreCase = true) == 0) {
            ruteoBean.dia = 6
        } else if (day.compareTo("Domingo", ignoreCase = true) == 0) {
            ruteoBean.dia = 7
        }
        ruteoBean.id = java.lang.Long.valueOf(1)
        ruteoBean.fecha = Utils.fechaActual()
        ruteoBean.ruta = rute
        routingDao.insert(ruteoBean)

        val establishedRute = routingDao.getRutaEstablecida()

        if (establishedRute != null) {

            if (establishedRute.dia == 1) {
                saveData(ClientDao().getClientsByMondayRute(establishedRute.ruta, 1))
            } else if (establishedRute.dia == 2) {
                saveData(ClientDao().getListaClientesRutaMartes(establishedRute.ruta, 1))
            }
            if (establishedRute.dia == 3) {
                saveData(ClientDao().getListaClientesRutaMiercoles(establishedRute.ruta, 1))
            }
            if (establishedRute.dia == 4) {
                saveData(ClientDao().getListaClientesRutaJueves(establishedRute.ruta, 1))
            }
            if (establishedRute.dia == 5) {
                saveData(ClientDao().getListaClientesRutaViernes(establishedRute.ruta, 1))
            }
            if (establishedRute.dia == 6) {
                saveData(ClientDao().getListaClientesRutaSabado(establishedRute.ruta, 1))
            }
            if (establishedRute.dia == 7) {
                saveData(ClientDao().getListaClientesRutaDomingo(establishedRute.ruta, 1))
            }
        }
    }

    private fun saveData(listaClientes: List<ClienteBean>) {
            var count = 0
            for (item in listaClientes) {
                count += 1
                val ruteClientDao = RuteClientDao()
                val clientesRutaBean = ruteClientDao.getClienteByCuentaCliente(item.cuenta)
                //Guardamos al clientes en la ruta actual
                if (clientesRutaBean == null) {
                    val bean = ClientesRutaBean()
                    val dao = RuteClientDao()
                    bean.id = java.lang.Long.valueOf(count.toLong())
                    bean.nombre_comercial = item.nombre_comercial
                    bean.calle = item.calle
                    bean.numero = item.numero
                    bean.colonia = item.colonia
                    bean.cuenta = item.cuenta
                    bean.rango = item.rango
                    bean.lun = item.lun
                    bean.mar = item.mar
                    bean.mie = item.mie
                    bean.jue = item.jue
                    bean.vie = item.vie
                    bean.sab = item.sab
                    bean.dom = item.dom
                    bean.visitado = 0
                    bean.latitud = item.latitud
                    bean.longitud = item.longitud
                    bean.phone_contact = item.contacto_phone
                    dao.insert(bean)
                }
            }
            loadRuta()

    }

    fun getData() {
        homeViewState.value = HomeViewState.LoadingStart
        ChargeInteractorImp().executeGetCharge(object : OnGetChargeListener {
            override fun onGetChargeSuccess(chargeList: List<CobranzaBean>) {
                homeViewState.value = HomeViewState.LoadingFinish
            }

            override fun onGetChargeError() {
                homeViewState.value = HomeViewState.LoadingFinish
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener cobranzas", Toast.LENGTH_SHORT).show();
            }
        })
        homeViewState.value = HomeViewState.LoadingStart
        GetEmployeesInteractorImp().executeGetEmployees(object : GetEmployeesListener {
            override fun onGetEmployeesSuccess(employees: List<EmpleadoBean?>) {
                homeViewState.value = HomeViewState.LoadingFinish
            }

            override fun onGetEmployeesError() {
                homeViewState.value = HomeViewState.LoadingFinish
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener empleados", Toast.LENGTH_SHORT).show();
            }
        })
        homeViewState.value = HomeViewState.LoadingStart
 /*       ClientInteractorImp().executeGetAllClients(object : GetAllClientsListener {
            override fun onGetAllClientsSuccess(clientList: List<ClienteBean>) {
                homeViewState.value = HomeViewState.LoadingFinish
            }

            override fun onGetAllClientsError() {
                homeViewState.value = HomeViewState.LoadingFinish
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener clientes", Toast.LENGTH_SHORT).show();
            }
        })*/
        homeViewState.value = HomeViewState.LoadingStart
        GetProductsInteractorImp().executeGetProducts(object : OnGetProductsListener {
            override fun onGetProductsSuccess(products: List<ProductoBean?>) {
                homeViewState.value = HomeViewState.LoadingFinish
            }

            override fun onGetProductsError() {
                homeViewState.value = HomeViewState.LoadingFinish
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener productos", Toast.LENGTH_SHORT).show();
            }
        })
        homeViewState.value = HomeViewState.LoadingStart
        RolInteractorImp().executeGetAllRoles(object : OnGetAllRolesListener {
            override fun onGetAllRolesSuccess(roles: List<RolesBean>) {
                homeViewState.value = HomeViewState.LoadingFinish
            }

            override fun onGetAllRolesError() {
                homeViewState.value = HomeViewState.LoadingFinish
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener roles", Toast.LENGTH_SHORT).show();
            }
        })
        homeViewState.value = HomeViewState.LoadingStart
        PriceInteractorImp().executeGetSpecialPrices(object : GetSpecialPricesListener {
            override fun onGetSpecialPricesSuccess(priceList: List<PreciosEspecialesBean>) {
                homeViewState.value = HomeViewState.LoadingFinish
            }

            override fun onGetSpecialPricesError() {
                homeViewState.value = HomeViewState.LoadingFinish
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener precios", Toast.LENGTH_SHORT).show();
            }
        })
    }

    private fun sendVentas() {
        try {
            val sincVentas = SincVentas()
            sincVentas.setOnSuccess(object : ResponseOnSuccess() {
                @Throws(JSONException::class)
                override fun onSuccess(response: JSONArray) {
                }

                @Throws(java.lang.Exception::class)
                override fun onSuccessObject(response: JSONObject) {
                }
            })
            sincVentas.setOnError(object : ResponseOnError() {
                override fun onError(error: ANError) {}
                override fun onError(error: String) {}
            })
            sincVentas.postObject()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun saveCobranza() {
        viewModelScope.launch {
            val paymentDao = PaymentDao()
            val cobranzaBeanList = paymentDao.getCobranzaFechaActual(Utils.fechaActual())

            val listaCobranza: MutableList<Payment> = ArrayList()
            for (item in cobranzaBeanList) {
                val cobranza = Payment()
                cobranza.cobranza = item.cobranza
                cobranza.cuenta = item.cliente
                cobranza.importe = item.importe
                cobranza.saldo = item.saldo
                cobranza.venta = item.venta
                cobranza.estado = item.estado
                cobranza.observaciones = item.observaciones
                cobranza.fecha = item.fecha
                cobranza.hora = item.hora
                cobranza.identificador = item.empleado
                listaCobranza.add(cobranza)
            }

            ChargeInteractorImp().executeSaveCharge(listaCobranza, object : OnSaveChargeListener {
                override fun onSaveChargeSuccess() {
                    //Toast.makeText(requireActivity(), "Cobranza guardada correctamente", Toast.LENGTH_LONG).show();
                }

                override fun onSaveChargeError() {
                    //Toast.makeText(requireActivity(), "Ha ocurrido un problema al guardar la cobranza", Toast.LENGTH_LONG).show();
                }
            })
        }
    }

    private fun loadAbonos() {
        viewModelScope.launch {
            val paymentDao = PaymentDao()
            var cobranzaBeanList: List<CobranzaBean> = java.util.ArrayList()
            cobranzaBeanList = paymentDao.getAbonosFechaActual(Utils.fechaActual())

            val listaCobranza: MutableList<Payment> = java.util.ArrayList()
            for (item in cobranzaBeanList) {
                val cobranza = Payment()
                cobranza.cobranza = item.cobranza
                cobranza.cuenta = item.cliente
                cobranza.importe = item.importe
                cobranza.saldo = item.saldo
                cobranza.venta = item.venta
                cobranza.estado = item.estado
                cobranza.observaciones = item.observaciones
                cobranza.fecha = item.fecha
                cobranza.hora = item.hora
                cobranza.identificador = item.empleado
                listaCobranza.add(cobranza)
            }

            ChargeInteractorImp().executeUpdateCharge(listaCobranza, object : OnUpdateChargeListener {
                override fun onUpdateChargeSuccess() {
                    //Toast.makeText(requireActivity(), "Cobranza actualizada correctamente", Toast.LENGTH_LONG).show();
                }

                override fun onUpdateChargeError() {
                    //Toast.makeText(requireActivity(), "Ha ocurrido un error al actualizar la cobranza", Toast.LENGTH_LONG).show();
                }
            })
        }
    }

    private fun loadRuta() {
        var mData = listOf<ClientesRutaBean?>()
        val routingDao = RoutingDao()
        val ruteoBean = routingDao.getRutaEstablecida()

        if (ruteoBean != null) {
            //mData = RuteClientDao().getAllRutaClientes(ruteoBean.ruta, ruteoBean.dia)
        }
        homeViewState.value = HomeViewState.RuteLoaded(mData)
    }

    private fun saveVisitas() {
        viewModelScope.launch {
            val visitsDao = VisitsDao()
            val visitasBeanListBean = visitsDao.getVisitsByCurrentDay(Utils.fechaActual())
            val clientDao = ClientDao()
            var vendedoresBean = AppBundle.getUserBean()

            if (vendedoresBean == null) {
                vendedoresBean = CacheInteractor().getSeller()
            }

            val visitList: MutableList<Visit> = java.util.ArrayList()
            for (item in visitasBeanListBean) {
                val visita = Visit()
                visita.fecha = item.fecha
                visita.hora = item.hora
                val clienteBean = clientDao.getClientByAccount(item.cliente.cuenta)
                visita.cuenta = clienteBean!!.cuenta
                visita.latidud = item.latidud
                visita.longitud = item.longitud
                visita.motivo_visita = item.motivo_visita
                if (vendedoresBean != null) {
                    visita.identificador = vendedoresBean.getIdentificador()
                } else {
                    //Log.e(HomeFragment.TAG, "vendedoresBean is null")
                }
                visitList.add(visita)
            }

            VisitInteractorImp().executeSaveVisit(visitList, object : OnSaveVisitListener {
                override fun onSaveVisitSuccess() {
                    //Toast.makeText(requireActivity(), "Visita registrada correctamente", Toast.LENGTH_LONG).show();
                }

                override fun onSaveVisitError() {
                    //Toast.makeText(requireActivity(), "Ha ocurrido un error al registrar la visita", Toast.LENGTH_LONG).show();
                }
            })
        }
    }

    private fun loadClientes() {
        viewModelScope.launch {
            val clientDao = ClientDao()
            val clientListDB = clientDao.getClientsByDay(Utils.fechaActual())

            val clientList: MutableList<Client> = java.util.ArrayList()

            for (item in clientListDB) {
                val client = Client()
                client.nombreComercial = item.nombre_comercial
                client.calle = item.calle
                client.numero = item.numero
                client.colonia = item.colonia
                client.ciudad = item.ciudad
                client.codigoPostal = item.codigo_postal
                client.fechaRegistro = item.fecha_registro
                client.cuenta = item.cuenta
                client.status = if (item.status) 1 else 0
                client.consec = item.consec
                client.rango = item.rango
                client.lun = item.lun
                client.mar = item.mar
                client.mie = item.mie
                client.jue = item.jue
                client.vie = item.vie
                client.sab = item.sab
                client.dom = item.dom
                client.latitud = item.latitud
                client.longitud = item.longitud
                client.phone_contacto = "" + item.contacto_phone
                client.recordatorio = "" + item.recordatorio
                client.visitas = item.visitasNoefectivas
                if (item.is_credito) {
                    client.isCredito = 1
                } else {
                    client.isCredito = 0
                }
                client.saldo_credito = item.saldo_credito
                client.limite_credito = item.limite_credito
                if (item.matriz == null || item.matriz != null && item.matriz == "null") {
                    client.matriz = "null"
                } else {
                    client.matriz = item.matriz
                }
                clientList.add(client)
            }

            ClientInteractorImp().executeSaveClient(clientList, object : SaveClientListener {
                override fun onSaveClientSuccess() {
                    //Toast.makeText(requireActivity(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
                }

                override fun onSaveClientError() {
                    //Toast.makeText(requireActivity(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
                }
            })
        }
    }

    private fun savePreciosEspeciales() {
        viewModelScope.launch {
            //Instancia la base de datos


            //Instancia la base de datos
            val dao = SpecialPricesDao()

            //Contiene la lista de precios de la db local

            //Contiene la lista de precios de la db local
            var listaDB: List<PreciosEspecialesBean> = java.util.ArrayList()

            //Obtenemos la lista por id cliente

            //Obtenemos la lista por id cliente
            listaDB = dao.getPreciosBydate(Utils.fechaActual())


            //Contiene la lista de lo que se envia al servidor


            //Contiene la lista de lo que se envia al servidor
            val listaPreciosServidor: MutableList<Price> = java.util.ArrayList()

            //Contien la lista de precios especiales locales

            //Contien la lista de precios especiales locales
            for (items in listaDB) {
                val precio = Price()
                if (items.active) {
                    precio.active = 1
                } else {
                    precio.active = 0
                }
                precio.articulo = items.articulo
                precio.cliente = items.cliente
                precio.precio = items.precio
                listaPreciosServidor.add(precio)
            }

            PriceInteractorImp().executeSendPrices(
                listaPreciosServidor,
                object : SendPricesListener {
                    override fun onSendPricesSuccess() {
                        //progresshide()
                        //Toast.makeText(requireActivity(), "Sincronizacion de lista de precios exitosa", Toast.LENGTH_LONG).show();
                    }

                    override fun onSendPricesError() {
                        //progresshide()
                        //Toast.makeText(requireActivity(), "Error al sincronizar la lista de precios intente mas tarde", Toast.LENGTH_LONG).show();
                    }
                })
        }
    }
}