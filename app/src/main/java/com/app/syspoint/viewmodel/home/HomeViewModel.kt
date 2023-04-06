package com.app.syspoint.viewmodel.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidnetworking.error.ANError
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.interactor.charge.ChargeInteractor.OnSaveChargeListener
import com.app.syspoint.interactor.charge.ChargeInteractor.OnUpdateChargeListener
import com.app.syspoint.interactor.charge.ChargeInteractorImp
import com.app.syspoint.interactor.client.ClientInteractor.GetAllClientsListener
import com.app.syspoint.interactor.client.ClientInteractor.SaveClientListener
import com.app.syspoint.interactor.client.ClientInteractorImp
import com.app.syspoint.interactor.employee.GetEmployeeInteractor.SaveEmployeeListener
import com.app.syspoint.interactor.employee.GetEmployeesInteractorImp
import com.app.syspoint.interactor.prices.PriceInteractor.SendPricesListener
import com.app.syspoint.interactor.prices.PriceInteractorImp
import com.app.syspoint.interactor.roles.RolInteractor.OnGetAllRolesListener
import com.app.syspoint.interactor.roles.RolInteractorImp
import com.app.syspoint.interactor.visit.VisitInteractor.OnSaveVisitListener
import com.app.syspoint.interactor.visit.VisitInteractorImp
import com.app.syspoint.models.*
import com.app.syspoint.models.sealed.GetChargeViewState
import com.app.syspoint.models.sealed.GetClientsByRuteViewState
import com.app.syspoint.models.sealed.HomeViewState
import com.app.syspoint.models.sealed.SetRuteViewState
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.database.dao.*
import com.app.syspoint.repository.request.http.Servicio.ResponseOnError
import com.app.syspoint.repository.request.http.Servicio.ResponseOnSuccess
import com.app.syspoint.repository.request.http.SincVentas
import com.app.syspoint.usecases.GetChargeUseCase
import com.app.syspoint.usecases.GetDataUseCase
import com.app.syspoint.usecases.GetUpdatesUseCase
import com.app.syspoint.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import com.app.syspoint.repository.database.dao.RuteClientDao
import com.app.syspoint.repository.database.bean.AppBundle
import com.app.syspoint.repository.database.bean.RuteoBean
import com.app.syspoint.repository.database.dao.RoutingDao


const val TAG = "HomeViewModel"

class HomeViewModel: ViewModel() {

    val homeViewState = MutableLiveData<HomeViewState>()

    private var _getChargeViewState: MutableLiveData<GetChargeViewState> = MutableLiveData()
    val getChargeViewState: LiveData<GetChargeViewState>
        get() = _getChargeViewState


    private val getClients: MutableLiveData<Boolean> = MutableLiveData()
    private val getCobranzas: MutableLiveData<Boolean> = MutableLiveData()
    private val getRoles: MutableLiveData<Boolean> = MutableLiveData()
    private val savePreciosEspeciales: MutableLiveData<Boolean> = MutableLiveData()
    private val saveVentas: MutableLiveData<Boolean> = MutableLiveData()
    private val saveVisitas: MutableLiveData<Boolean> = MutableLiveData()
    private val saveAbono: MutableLiveData<Boolean> = MutableLiveData()
    private val saveCobranza: MutableLiveData<Boolean> = MutableLiveData()
    private val saveClientes: MutableLiveData<Boolean> = MutableLiveData()
    private val getClientsByRute: MutableLiveData<Boolean> = MutableLiveData()

    private val _getClientsByRuteViewState: MutableLiveData<GetClientsByRuteViewState> = MutableLiveData()
    val getClientsByRuteViewState: LiveData<GetClientsByRuteViewState>
        get() = _getClientsByRuteViewState

    private val _setUpRuteViewState: MutableLiveData<SetRuteViewState> = MutableLiveData()
    val setUpRuteViewState: LiveData<SetRuteViewState>
        get() = _setUpRuteViewState

    fun getCharges() {
        viewModelScope.launch(Dispatchers.Default) {
            val vendedoresBean = AppBundle.getUserBean()
            if (vendedoresBean != null) {
                GetChargeUseCase().invoke().onEach {
                    getCobranzas.postValue(true)
                    saveCobranza()
                    saveAbonos()
                    if (it is Resource.Success) {
                        //_getChargeViewState.postValue(GetChargeViewState.GetChargeSuccess(it.data))
                    } else if (it is Resource.Error) {
                        //_getChargeViewState.postValue(GetChargeViewState.GetChargeError(it.message))
                    }

                }.launchIn(viewModelScope)
            }
        }
    }

    fun getUpdates() {
        viewModelScope.launch(Dispatchers.Main) {
            val clientDao = ClientDao()
            val listaClientesCredito = clientDao.getClientsByDay(Utils.fechaActual())
            val paymentDao = PaymentDao()
            listaClientesCredito.map {item ->
                try {
                    val dao = ClientDao()
                    item.saldo_credito = paymentDao.getTotalSaldoDocumentosCliente(item.cuenta)
                    dao.save(item)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

            update()
        }
    }

    fun getData() {
        viewModelScope.launch(Dispatchers.Default) {
            homeViewState.postValue(HomeViewState.LoadingStart)
            GetDataUseCase().invoke().onEach {
                 if (it is Resource.Loading) {
                    homeViewState.postValue(HomeViewState.LoadingStart)
                } else {
                    homeViewState.postValue(HomeViewState.LoadingFinish)
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getUpdate() {
        viewModelScope.launch(Dispatchers.Default) {
            homeViewState.postValue(HomeViewState.LoadingStart)

            val clientDao = ClientDao()
            val listaClientesCredito = clientDao.getClientsByDay(Utils.fechaActual())
            val paymentDao = PaymentDao()
            listaClientesCredito.map { item ->
                try {
                    val dao = ClientDao()
                    item.saldo_credito = paymentDao.getTotalSaldoDocumentosCliente(item.cuenta)
                    dao.save(item)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            GetUpdatesUseCase().invoke().onEach {

            }.launchIn(viewModelScope)

        }
    }

    fun update() {

        getCobranzas.value = false
        saveCobranza.value = false
        saveAbono.value = false
        getRoles.value = false
        saveVentas.value = false
        saveVisitas.value = false
        savePreciosEspeciales.value = false

        homeViewState.value = HomeViewState.LoadingStart

        viewModelScope.launch(Dispatchers.IO) {
            getClientsByRute(true)
            getCharges()
            getRoles()

            saveVentas()
            saveVisitas()
            savePreciosEspeciales()

            while (isResquesting()) {
                //Log.d("HomeViewModel", "\ngetCobranzas: ${getCobranzas.value} \n getRoles: ${getRoles.value} \n saveVentas: ${saveVentas.value} \n saveVisitas: ${saveVisitas.value} \n savePreciosEspeciales: ${savePreciosEspeciales.value} \n saveAbono: ${saveAbono.value} \n saveCobranza: ${saveCobranza.value}")
            }

            Log.d("HomeViewModel", "\ngetCobranzas: ${getCobranzas.value} \n getRoles: ${getRoles.value} \n saveVentas: ${saveVentas.value} \n saveVisitas: ${saveVisitas.value} \n savePreciosEspeciales: ${savePreciosEspeciales.value} \n saveAbono: ${saveAbono.value} \n saveCobranza: ${saveCobranza.value}")
            Log.d("HomeViewModel", "LoadingFinish")
            homeViewState.postValue(HomeViewState.LoadingFinish)
        }
    }

    fun getClientsByRute(isUpdate: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            getClientsByRute.postValue(false)

            val routingDao = RoutingDao()
            val ruteoBean = routingDao.getRutaEstablecida()
            if (ruteoBean != null) {
                val vendedoresBean = AppBundle.getUserBean()
                val ruta = if (ruteoBean.ruta != null && !ruteoBean.ruta.isEmpty()) ruteoBean.ruta else vendedoresBean.getRute()
                ClientInteractorImp().executeGetAllClientsByDate(ruta, ruteoBean.dia, object : GetAllClientsListener {
                    override fun onGetAllClientsSuccess(clientList: List<ClienteBean>) {
                        getClientsByRute.postValue(true)
                        _getClientsByRuteViewState.postValue(GetClientsByRuteViewState.GetClientsByRuteSuccess(clientList))
                        saveClientes()
                        if (!isUpdate)
                            homeViewState.postValue(HomeViewState.LoadingFinish)
                        Log.d(TAG, "Clientes actualizados correctamente")
                    }

                    override fun onGetAllClientsError() {
                        getClientsByRute.postValue(true)
                        _getClientsByRuteViewState.postValue(GetClientsByRuteViewState.GetClientsByRuteError(""))
                        saveClientes()

                        if (!isUpdate)
                            homeViewState.postValue(HomeViewState.LoadingFinish)
                        Log.d(TAG, "Ha ocurrido un error. Conectate a internet para cambiar de ruta u obtener los clientes")
                    }
                })
            }
        }
    }

    private fun getRoles() {
        viewModelScope.launch(Dispatchers.IO) {
            RolInteractorImp().executeGetAllRoles(object : OnGetAllRolesListener {
                override fun onGetAllRolesSuccess(roles: List<RolesBean>) {
                    getRoles.postValue(true)
                    Log.d(TAG, "Roles actualizados")
                }

                override fun onGetAllRolesError() {
                    getRoles.postValue(true)
                    Log.d(TAG, "Ha ocurrido un error al obtener roles")
                }
            })
        }
    }

    private fun saveVisitas() {
        viewModelScope.launch(Dispatchers.IO) {
            val visitsDao = VisitsDao()
            val visitasBeanListBean = visitsDao.getVisitsByCurrentDay(Utils.fechaActual())
            val clientDao = ClientDao()
            var vendedoresBean = AppBundle.getUserBean()
            if (vendedoresBean == null) {
                vendedoresBean = CacheInteractor().getSeller()
            }
            val visitList: MutableList<Visit> = ArrayList()
            visitasBeanListBean.map {item ->
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
                    Log.e(TAG, "vendedoresBean is null")
                }
                visitList.add(visita)
            }
            VisitInteractorImp().executeSaveVisit(visitList, object : OnSaveVisitListener {
                override fun onSaveVisitSuccess() {
                    saveVisitas.postValue(true)
                    Log.d(TAG, "Visita registrada correctamente")
                }

                override fun onSaveVisitError() {
                    saveVisitas.postValue(true)
                    Log.d(TAG, "Ha ocurrido un error al registrar la visita")
                }
            })
        }
    }

    private fun saveVentas() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val sincVentas = SincVentas()
                sincVentas.setOnSuccess(object : ResponseOnSuccess() {
                    @Throws(JSONException::class)
                    override fun onSuccess(response: JSONArray) {
                        saveVentas.postValue(true)
                    }

                    @Throws(java.lang.Exception::class)
                    override fun onSuccessObject(response: JSONObject) {
                        saveVentas.postValue(true)
                    }
                })
                sincVentas.setOnError(object : ResponseOnError() {
                    override fun onError(error: ANError) {
                        saveVentas.postValue(true)
                    }

                    override fun onError(error: String) {
                        saveVentas.postValue(true)
                    }
                })
                sincVentas.postObject()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun savePreciosEspeciales() {
        viewModelScope.launch(Dispatchers.IO) {
            val dao = SpecialPricesDao()
            val listaDB = dao.getPreciosBydate(Utils.fechaActual())
            val listaPreciosServidor: MutableList<Price> = ArrayList()
            listaDB.map {item ->
                val precio = Price()
                precio.active = if (item.active) 1 else 0
                precio.articulo = item.articulo
                precio.cliente = item.cliente
                precio.precio = item.precio
                listaPreciosServidor.add(precio)
            }
            PriceInteractorImp().executeSendPrices(
                listaPreciosServidor,
                object : SendPricesListener {
                    override fun onSendPricesSuccess() {
                        savePreciosEspeciales.postValue(true)
                        Log.d(TAG, "Precios actualizados")
                    }

                    override fun onSendPricesError() {
                        savePreciosEspeciales.postValue(true)
                        Log.d(TAG, "Ha ocurrido un error al obtener los precios")
                    }
                })
        }
    }

    fun saveCobranza() {
        viewModelScope.launch(Dispatchers.IO) {
            val paymentDao = PaymentDao()
            val cobranzaBeanList = paymentDao.getCobranzaFechaActual(Utils.fechaActual())
            val listaCobranza: MutableList<Payment> = ArrayList()
            cobranzaBeanList.map {item ->
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
                    saveCobranza.postValue(true)
                    Log.d(TAG, "Cobranza guardada correctamente")
                }

                override fun onSaveChargeError() {
                    saveCobranza.postValue(true)
                    Log.d(TAG, "Ha ocurrido un problema al guardar la cobranza")
                }
            })
        }
    }

    private fun saveAbonos() {
        viewModelScope.launch(Dispatchers.IO) {
            val paymentDao = PaymentDao()
            val cobranzaBeanList = paymentDao.getAbonosFechaActual(Utils.fechaActual())
            val listaCobranza: MutableList<Payment> = ArrayList()
            cobranzaBeanList.map {item ->
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
                cobranza.updatedAt = item.updatedAt
                listaCobranza.add(cobranza)
            }
            ChargeInteractorImp().executeUpdateCharge(
                listaCobranza,
                object : OnUpdateChargeListener {
                    override fun onUpdateChargeSuccess() {
                        saveAbono.postValue(true)
                        Log.d(TAG, "Abono guardada correctamente")
                    }

                    override fun onUpdateChargeError() {
                        saveAbono.postValue(true)
                        Log.d(TAG, "Ha ocurrido un problema al guardar el abono")
                    }
                })
        }
    }

    private fun isResquesting(): Boolean {
        if (!getCobranzas.value!!) return true
        if (!getRoles.value!!) return true
        if (!saveVentas.value!!) return true
        if (!saveVentas.value!!) return true
        if (!saveVisitas.value!!) return true
        if (!savePreciosEspeciales.value!!) return true
        if (!saveAbono.value!!) return true
        if (!saveCobranza.value!!) return true
        if (saveClientes.value != null && saveClientes.value == false) return true

        return false
    }

    fun saveClientes() {
        saveClientes.value = false
        viewModelScope.launch(Dispatchers.IO) {
            val clientDao = ClientDao()
            val clientListDB = clientDao.getClientsByDay(Utils.fechaActual())
            val clientList: MutableList<Client> = ArrayList()
            clientListDB.map {item ->
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
                client.phone_contacto = item.contacto_phone
                client.recordatorio = item.recordatorio
                client.visitas = item.visitasNoefectivas
                client.isCredito = if (item.is_credito) 1 else 0
                client.saldo_credito = (item.saldo_credito)
                client.limite_credito = (item.limite_credito)
                if (item.matriz == null || item.matriz != null && item.matriz == "null") {
                    client.matriz = "null"
                } else {
                    client.matriz = item.matriz
                }
                client.updatedAt = item.updatedAt
                clientList.add(client)
            }
            ClientInteractorImp().executeSaveClient(clientList, object : SaveClientListener {
                override fun onSaveClientSuccess() {
                    saveClientes.postValue(true)
                    Log.d(TAG, "Sincronizacion de clientes exitosa")
                }

                override fun onSaveClientError() {
                    saveClientes.postValue(true)
                    Log.d(TAG, "Ha ocurrido un error al sincronizar los clientes")
                }
            })
        }
    }

    fun setUpRute(dia: String, ruta: String) {
        viewModelScope.launch(Dispatchers.Default) {
            val clientDao = ClientDao()
            clientDao.updateVisited()

            val routingDao = RoutingDao()
            routingDao.clear()

            val ruteoBean = RuteoBean()

            if (dia == "Lunes") {
                ruteoBean.dia = 1
            } else if (dia == "Martes") {
                ruteoBean.dia = 2
            } else if (dia == "Miercoles") {
                ruteoBean.dia = 3
            } else if (dia == "Jueves") {
                ruteoBean.dia = 4
            } else if (dia == "Viernes") {
                ruteoBean.dia = 5
            } else if (dia == "Sabado") {
                ruteoBean.dia = 6
            } else if (dia == "Domingo") {
                ruteoBean.dia = 7
            }
            ruteoBean.id = 1L
            ruteoBean.fecha = Utils.fechaActual()

            val vendedoresBean1 = AppBundle.getUserBean()
            var ruta_ = ruta.ifEmpty { vendedoresBean1.getRute() }

            if (ruta_ == "0") {
                val vendedoresBean = CacheInteractor().getSeller()
                if (vendedoresBean != null) ruta_ = vendedoresBean.rute
            }

            ruteoBean.ruta = ruta_

            try {
                routingDao.insert(ruteoBean)
            } catch (e: java.lang.Exception) {
                routingDao.save(ruteoBean)
            }

            setRute(ruteoBean)

            vendedoresBean1.setRute(ruta_)
            vendedoresBean1.setUpdatedAt(Utils.fechaActualHMS())

            EmployeeDao().save(vendedoresBean1)
            val idEmpleado = vendedoresBean1.id.toString()
            testLoadEmpleado(idEmpleado)
        }
    }

    private fun setRute(ruteoBean: RuteoBean) {
        _setUpRuteViewState.postValue(SetRuteViewState.Loading)
        val vendedoresBean = AppBundle.getUserBean()
        val ruta = if (ruteoBean.ruta != null && ruteoBean.ruta.isNotEmpty()) ruteoBean.ruta else vendedoresBean.getRute()
        val clients = RuteClientDao().getAllRutaClientes(ruta, ruteoBean.dia)
        if (clients != null && clients.isNotEmpty()) {
            _setUpRuteViewState.postValue(SetRuteViewState.RuteDefined(clients))
        } else {
            getClientsByRute(false)
        }
    }

    private fun testLoadEmpleado(id: String) {
        homeViewState.postValue(HomeViewState.LoadingStart)
        viewModelScope.launch(Dispatchers.IO) {
            val employeeDao = EmployeeDao()
            val listaEmpleadosDB = employeeDao.getEmployeeById(id)
            val listEmpleados: MutableList<Employee> = ArrayList()
            listaEmpleadosDB.map {item ->
                val empleado = Employee()
                empleado.nombre = item.getNombre()
                empleado.direccion = item.getDireccion().ifEmpty { "-" }
                empleado.email = item.getEmail()
                empleado.telefono = item.getTelefono().ifEmpty { "-" }
                empleado.fechaNacimiento = item.getFecha_nacimiento().ifEmpty { "-" }
                empleado.fechaIngreso = item.getFecha_ingreso().ifEmpty {"-"}
                empleado.contrasenia = item.getContrasenia()
                empleado.identificador = item.getIdentificador()
                empleado.status = if (item.getStatus()) 1 else 0
                empleado.updatedAt = item.getUpdatedAt()
                empleado.rute = item.rute.ifEmpty { "" }
                if (item.getPath_image() == null || item.getPath_image().isEmpty()) {
                    empleado.pathImage = ""
                } else {
                    empleado.pathImage = item.getPath_image()
                }

                listEmpleados.add(empleado)
            }
            GetEmployeesInteractorImp().executeSaveEmployees(
                listEmpleados,
                object : SaveEmployeeListener {
                    override fun onSaveEmployeeSuccess() {
                        homeViewState.postValue(HomeViewState.LoadingFinish)
                        Log.d(TAG, "Empleados sincronizados")
                    }

                    override fun onSaveEmployeeError() {
                        homeViewState.postValue(HomeViewState.LoadingFinish)
                        Log.d(TAG, "Ha ocurrido un error al sincronizar los empleados")
                    }
                })
        }
    }
}
