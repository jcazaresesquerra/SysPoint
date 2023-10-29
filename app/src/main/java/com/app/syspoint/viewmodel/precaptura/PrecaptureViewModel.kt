package com.app.syspoint.viewmodel.precaptura

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.interactor.client.ClientInteractor
import com.app.syspoint.interactor.client.ClientInteractorImp
import com.app.syspoint.interactor.visit.VisitInteractor.OnSaveVisitListener
import com.app.syspoint.interactor.visit.VisitInteractorImp
import com.app.syspoint.models.Client
import com.app.syspoint.models.Visit
import com.app.syspoint.models.sealed.PrecaptureViewState
import com.app.syspoint.repository.objectBox.AppBundle
import com.app.syspoint.repository.objectBox.dao.*
import com.app.syspoint.repository.objectBox.entities.EmployeeBox
import com.app.syspoint.repository.objectBox.entities.VisitsBox
import com.app.syspoint.utils.Actividades
import com.app.syspoint.utils.Utils

class PrecaptureViewModel: ViewModel() {

    val precaptureViewState = MutableLiveData<PrecaptureViewState>()

    fun confirmPrecapture(accountId: String?, conceptSelectedView: String?, latitud: Double, longitud: Double) {
        val vendedoresBean = getEmployee()

        //Le indicamos al sistema que el cliente ya se ah visitado
        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientByAccount(accountId)
        clienteBean!!.visitado = 1
        clienteBean.date_sync = Utils.fechaActual()
        clienteBean.visitasNoefectivas = clienteBean.visitasNoefectivas + 1
        clientDao.insert(clienteBean)
        val routingDao = RoutingDao()
        val ruteoBean = routingDao.getRutaEstablecida()
        val ruteClientDao = RuteClientDao()
        val clientesRutaBean =
            if (ruteoBean != null) ruteClientDao.getClienteByCuentaClienteAndRute(accountId, ruteoBean.ruta, ruteoBean.dia)
            else ruteClientDao.getClienteByCuentaCliente(accountId)
        if (clientesRutaBean != null) {
            clientesRutaBean.visitado = 1
            ruteClientDao.insert(clientesRutaBean)
        }

        saveClient()

        val visitBean = VisitsBox()
        val visitsDao = VisitsDao()
        visitBean.motivo_visita = conceptSelectedView
        visitBean.empleado.target = vendedoresBean
        visitBean.cliente.target = clienteBean
        visitBean.hora = Utils.getHoraActual()
        visitBean.fecha = Utils.fechaActual()
        visitBean.latidud = clienteBean.latitud
        visitBean.longitud = clienteBean.latitud
        visitBean.updatedAt = Utils.fechaActualHMS()
        visitsDao.insert(visitBean)

        saveVisits()

        val params = HashMap<String, String>()
        params[Actividades.PARAM_1] = conceptSelectedView!!
        //params[Actividades.PARAM_2] = tipo_inventario_seleccionado
        if (vendedoresBean != null) {
            params[Actividades.PARAM_3] = vendedoresBean.nombre!!
        }
        params[Actividades.PARAM_4] = Utils.fechaActual()
        params[Actividades.PARAM_5] =
            Utils.getHoraActual()
        params[Actividades.PARAM_6] = latitud.toString()
        params[Actividades.PARAM_7] = longitud.toString()
        params[Actividades.PARAM_8] = clienteBean.cuenta!!

        precaptureViewState.value = PrecaptureViewState.PrecaptureFinished(params)
    }

    fun saveClient() {
        val clientDao = ClientDao()
        val clientListDao = clientDao.getClientsByDay(Utils.fechaActual())
        val clientList: MutableList<Client> = ArrayList()
        for (item in clientListDao) {
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
            client.updatedAt = item.updatedAt
            if (item.isCredito) {
                client.isCredito = 1
            } else {
                client.isCredito = 0
            }
            client.saldo_credito = item.saldo_credito
            client.limite_credito = item.limite_credito
            if (item.matriz === "null" && item.matriz == null) {
                client.matriz = "null"
            } else {
                client.matriz = item.matriz
            }
            clientList.add(client)
        }

        ClientInteractorImp().executeSaveClient(clientList, object : ClientInteractor.SaveClientListener {
            override fun onSaveClientSuccess() {
                precaptureViewState.value = PrecaptureViewState.SaveClientSuccessState
            }

            override fun onSaveClientError() {
                precaptureViewState.value = PrecaptureViewState.SaveClientErrorState
            }
        })
    }

    fun saveVisits() {
        val visitsDao = VisitsDao()
        val visitsBeanListBean = visitsDao.getVisitsByCurrentDay(Utils.fechaActual())

        val listaVisitas: MutableList<Visit> = ArrayList()
        var vendedoresBean = getEmployee()

        val clientDao = ClientDao()

        for (item in visitsBeanListBean) {
            val visit = Visit()
            val clientBean = clientDao.getClientByAccount(item.cliente!!.target.cuenta)
            visit.fecha = item.fecha
            visit.hora = item.hora
            visit.cuenta = clientBean!!.cuenta
            visit.latidud = item.latidud
            visit.longitud = item.longitud
            visit.motivo_visita = item.motivo_visita
            visit.updatedAt = item.updatedAt
            if (vendedoresBean != null) {
                visit.identificador = vendedoresBean.identificador
            }
            listaVisitas.add(visit)
        }

        VisitInteractorImp().executeSaveVisit(listaVisitas, object : OnSaveVisitListener {
            override fun onSaveVisitSuccess() {
                precaptureViewState.value = PrecaptureViewState.SaveVisitSuccessState
            }

            override fun onSaveVisitError() {
                precaptureViewState.value = PrecaptureViewState.SaveVisitErrorState
            }
        })
    }

    private fun getEmployee(): EmployeeBox? {
        var vendedoresBean = AppBundle.getUserBox()
        if (vendedoresBean == null) {
            val sessionBox = SessionDao().getUserSession()
            vendedoresBean = if (sessionBox != null) {
                EmployeeDao().getEmployeeByID(sessionBox.empleadoId)
            } else {
                CacheInteractor().getSeller()
            }
        }
        return vendedoresBean
    }
}