package com.app.syspoint.viewmodel.home

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.syspoint.interactor.data.GetAllDataInteractor.OnGetAllDataByDateListener
import com.app.syspoint.interactor.data.GetAllDataInteractorImp
import com.app.syspoint.models.sealed.HomeViewState
import com.app.syspoint.repository.database.bean.ClienteBean
import com.app.syspoint.repository.database.bean.ClientesRutaBean
import com.app.syspoint.repository.database.bean.RuteoBean
import com.app.syspoint.repository.database.dao.ClientDao
import com.app.syspoint.repository.database.dao.PaymentDao
import com.app.syspoint.repository.database.dao.RoutingDao
import com.app.syspoint.repository.database.dao.RuteClientDao
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.utils.Utils

class HomeViewModel: ViewModel() {
    val homeViewState = MutableLiveData<HomeViewState>()

    fun setUpClientRute() {
        val routingDao = RoutingDao()
        val ruteoBean = routingDao.getRutaEstablecida()

        var clientRute = listOf<ClientesRutaBean>()
        if (ruteoBean != null) {
            clientRute = RuteClientDao().getAllRutaClientes()
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

    private fun sendVentas() {

    }

    private fun saveCobranza() {

    }

    private fun loadAbonos() {

    }

    private fun loadRuta() {

    }

    private fun saveVisitas() {

    }

    private fun loadClientes() {

    }

    private fun savePreciosEspeciales() {

    }
}