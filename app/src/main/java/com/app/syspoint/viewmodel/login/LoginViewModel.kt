package com.app.syspoint.viewmodel.login

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.syspoint.App
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.interactor.data.GetAllDataInteractor
import com.app.syspoint.interactor.data.GetAllDataInteractorImp
import com.app.syspoint.models.sealed.LoginViewState
import com.app.syspoint.repository.cache.SharedPreferencesManager
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.database.dao.*
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.utils.Utils
import com.app.syspoint.viewmodel.BaseViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginViewModel: BaseViewModel() {

    val loginViewState = MutableLiveData<LoginViewState>()

    init {
        //createUser()
        validatePersistence()
        sync()
    }

    fun login(email: String, password: String) {
        val employeeDao = EmployeeDao()
        val employeeBean = employeeDao.validateLogin(email, password)

        val sessionDao = SessionDao()
        sessionDao.clear()

        val userSession = UserSession(email, password, false)
        val lastUserSession = CacheInteractor().getSeller()

        if (employeeBean != null) {


            if (lastUserSession != null && !lastUserSession.email.isNullOrEmpty() && lastUserSession.email != userSession.usuario) {
                loginViewState.postValue(LoginViewState.LoginError("Solo se permite una cuenta de usuario por dia en este dispositivo"))
                return
                /*val clientesRutaDao = RuteClientDao()
                clientesRutaDao.clear()
                val clientDao = ClientDao()
                clientDao.clear()
                val routingDao = RoutingDao()
                val ruteoBean = routingDao.getRutaEstablecida()
                routingDao.clear()
                if (ruteoBean != null) {
                    employeeBean.setRute(employeeBean.getRute())
                    employeeDao.save(employeeBean)
                }*/
            }

            val sessionBean = SesionBean()
            sessionBean.empleado = employeeBean
            sessionBean.empleadoId = employeeBean.id
            sessionBean.remember = false
            sessionDao.saveSession(sessionBean)
            AppBundle.setUserSession(userSession)
        }

        loginViewState.postValue(
            if (employeeBean != null) LoginViewState.LoggedIn
            else LoginViewState.LoginError("Usuario no encontrado verifique los datos de acceso")
        )
    }

    private fun setRuteByEmployeeIfExists(employeeBean: EmpleadoBean) {
        val routingDao = RoutingDao()
        val ruteoBean = routingDao.getRutaEstablecida()

        if (ruteoBean != null) {
            ruteoBean.id = 1L
            ruteoBean.fecha = Utils.fechaActual()
            if (employeeBean.getDay() > 0 && !employeeBean.getRute().isNullOrEmpty()) {
                ruteoBean.dia = employeeBean.getDay()
                ruteoBean.ruta = employeeBean.getRute()
            } else {
                ruteoBean.dia = 0
                ruteoBean.ruta = ""
            }
            routingDao.save(ruteoBean)
            getClientsByRute(ruteoBean)
        } else {
            val ruteo = RuteoBean()
            ruteo.id = 1L
            ruteo.fecha = Utils.fechaActual()

            if (employeeBean.getDay() > 0 && !employeeBean.getRute().isNullOrEmpty()) {
                ruteo.dia = employeeBean.getDay()
                ruteo.ruta = employeeBean.getRute()
            } else {
                ruteo.dia = 0
                ruteo.ruta = ""
            }
            routingDao.insert(ruteo)

            getClientsByRute(ruteo)
        }
    }

    private fun getClientsByRute(ruteoBean: RuteoBean) {
        if (ruteoBean.dia == 1) {
            saveData(ClientDao().getClientsByMondayRute(ruteoBean.ruta, 1), 1)
        } else if (ruteoBean.dia == 2) {
            saveData(ClientDao().getListaClientesRutaMartes(ruteoBean.ruta, 1), 2)
        }
        if (ruteoBean.dia == 3) {
            saveData(ClientDao().getListaClientesRutaMiercoles(ruteoBean.ruta, 1), 3)
        }
        if (ruteoBean.dia == 4) {
            saveData(ClientDao().getListaClientesRutaJueves(ruteoBean.ruta, 1), 4)
        }
        if (ruteoBean.dia == 5) {
            saveData(ClientDao().getListaClientesRutaViernes(ruteoBean.ruta, 1), 5)
        }
        if (ruteoBean.dia == 6) {
            saveData(ClientDao().getListaClientesRutaSabado(ruteoBean.ruta, 1), 6)
        }
        if (ruteoBean.dia == 7) {
            saveData(ClientDao().getListaClientesRutaDomingo(ruteoBean.ruta, 1), 7)
        }
    }

    private fun saveData(listaClientes: List<ClienteBean>, day: Int) {
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
                when (day) {
                    1 -> bean.order = item.lunOrder
                    2 -> bean.order = item.marOrder
                    3 -> bean.order = item.mieOrder
                    4 -> bean.order = item.jueOrder
                    5 -> bean.order = item.vieOrder
                    6 -> bean.order = item.sabOrder
                    7 -> bean.order = item.domOrder
                }
                bean.visitado = 0
                bean.latitud = item.latitud
                bean.longitud = item.longitud
                bean.phone_contact = item.contacto_phone
                try {
                    dao.insert(bean)
                } catch (e: Exception) {
                    dao.save(bean)
                }
            }
        }
    }

    fun isUserAdmin(): Boolean {
        // get seller
        val sellerBean = AppBundle.getUserBean()

        // save seller in cache
        val cacheInteractor = CacheInteractor()
        cacheInteractor.saveSeller(sellerBean!!)

        var identificador: String? = ""
        if (sellerBean != null) {
            identificador = sellerBean.getIdentificador()
        }
        val rolesDao = RolesDao()
        val rolesBean = rolesDao.getRolByEmpleado(identificador, "Inventarios")

        return rolesBean != null && rolesBean.active
    }

    private fun createUser() {
        val employeeDao = EmployeeDao()
        val count = employeeDao.getTotalEmployees()
        if (count == 0) {
            val employee = EmpleadoBean()
            val dao = EmployeeDao()
            employee.setNombre("Osvaldo Cazares")
            employee.setDireccion("Conocida")
            employee.setEmail("dev@gmail.com")
            employee.setTelefono("6672081920")
            employee.setFecha_nacimiento("00/00/0000")
            employee.setFecha_ingreso("00/00/0000")
            employee.setFecha_egreso("00/00/0000")
            employee.setContrasenia("123")
            employee.setIdentificador("E001")
            employee.setNss("")
            employee.setRfc("")
            employee.setCurp("")
            employee.setPuesto("")
            employee.setArea_depto("SYS")
            employee.setTipo_contrato("INDETERMINADO")
            employee.setRegion("UNO")
            employee.setHora_entrada("10:00")
            employee.setHora_salida("17:00")
            employee.setSalida_comer("13:00")
            employee.setEntrada_comer("13:30")
            employee.setSueldo_diario(0.0)
            employee.setTurno("")
            //employee.setEdit_ruta(false)
            dao.insert(employee)
            val rolCliente = RolesBean()
            val rolClienteDao =
                RolesDao()
            rolCliente.empleado = employee
            rolCliente.modulo = "Clientes"
            rolCliente.active = true
            rolCliente.identificador = employee.getIdentificador()
            rolClienteDao.insert(rolCliente)
            val rolProducto = RolesBean()
            val rolProductoDao =
                RolesDao()
            rolProducto.empleado = employee
            rolProducto.modulo = "Productos"
            rolProducto.active = true
            rolProducto.identificador = employee.getIdentificador()
            rolProductoDao.insert(rolProducto)
            val rolVentas = RolesBean()
            val rolVentasDao = RolesDao()
            rolVentas.empleado = employee
            rolVentas.modulo = "Ventas"
            rolVentas.active = true
            rolVentas.identificador = employee.getIdentificador()
            rolVentasDao.insert(rolVentas)
            val rolEmpleado = RolesBean()
            val rolEmpleadoDao =
                RolesDao()
            rolEmpleado.empleado = employee
            rolEmpleado.modulo = "Empleados"
            rolEmpleado.active = true
            rolEmpleado.identificador = employee.getIdentificador()
            rolEmpleadoDao.insert(rolEmpleado)
            val rolCobranza = RolesBean()
            val rolCobranzaDao =
                RolesDao()
            rolCobranza.empleado = employee
            rolCobranza.modulo = "Cobranza"
            rolCobranza.active = true
            rolCobranza.identificador = employee.getIdentificador()
            rolCobranzaDao.insert(rolCobranza)
        }
    }

    private fun validatePersistence() {
        val persistenceDao =
            PricePersistenceDao()
        val exists = persistenceDao.existePersistencia()
        if (exists == 0) {
            val persistencePriceBean = PersistenciaPrecioBean()
            val persistencePriceDao =
                PricePersistenceDao()
            persistencePriceBean.id = java.lang.Long.valueOf(1)
            persistencePriceBean.mostrar = "All"
            persistencePriceBean.valor = java.lang.Long.valueOf(1)
            persistencePriceDao.insert(persistencePriceBean)
        }
    }

    fun sync() {

        if (!isSync()) {
            loginViewState.value = LoginViewState.LoadingDataStart
            Handler().postDelayed({
                NetworkStateTask { connected ->
                    if (connected) {
                        viewModelScope.launch {
                            loginViewState.postValue(LoginViewState.ConnectedToInternet)
                            GetAllDataInteractorImp().executeGetAllData(object :
                                GetAllDataInteractor.OnGetAllDataListener {
                                override fun onGetAllDataSuccess() {
                                    //updateSession(true)
                                    loginViewState.postValue(LoginViewState.LoadingDataFinish)
                                }

                                override fun onGetAllDataError() {
                                    //updateSession(false)
                                    loginViewState.postValue(LoginViewState.LoadingDataFinish)
                                    removeLocalSync()
                                    loginViewState.postValue(LoginViewState.NotInternetConnection)
                                }
                            })
                        }
                    } else {
                        viewModelScope.launch {
                            removeLocalSync()
                            delay(300)
                            loginViewState.postValue(LoginViewState.LoadingDataFinish)
                            delay(300)
                            loginViewState.postValue(LoginViewState.NotInternetConnection)
                        }
                    }
                }.execute()
            }, 100)
        }
    }

    fun isSync(): Boolean {
        val taskDao = TaskDao()
        val taskBean = taskDao.getTask(Utils.fechaActual())
        val exist: Boolean
        //val isUpdated = isSessionUpdated()

        if (taskBean == null || (taskBean != null && taskBean.date != Utils.fechaActual())) {
            val stockDao = StockDao()
            stockDao.clear()
            val historialDao = StockHistoryDao()
            historialDao.clear()
            val ventasDao = SellsDao()
            ventasDao.clear()
            val itemDao = ItemDao()
            itemDao.clear()
            val visitasDao = VisitsDao()
            visitasDao.clear()
            val cobranzaDao = PaymentDao()
            cobranzaDao.clear()
            val chargesDao = ChargesDao()
            chargesDao.clear()
            val routingDao = RoutingDao()
            routingDao.clear()
            val employeeDao = EmployeeDao()
            employeeDao.clear()
            val rolesDao = RolesDao()
            rolesDao.clear()
            val clientesRutaDao = RuteClientDao()
            clientesRutaDao.clear()
            val clientDao = ClientDao()
            clientDao.clear()
            val specialPricesDao = SpecialPricesDao()
            specialPricesDao.clear()
            val dao = TaskDao()
            dao.clear()
            val bean = TaskBean()
            CacheInteractor().removeSellerFromCache()
            bean.date = Utils.fechaActual()
            bean.task = "Sincronización"
            dao.insert(bean)
            exist = false
            //updateSession(false)
        } else {
            exist = true
        }

        return exist
    }

    fun removeLocalSync() {
        val dao = TaskDao()
        dao.clear()
    }

    private fun updateSession(updated: Boolean) {
        App.INSTANCE?.baseContext?.let {
            SharedPreferencesManager(it).storeLocalSession(true)
        }
    }

    private fun isSessionUpdated(): Boolean {
        App.INSTANCE?.baseContext?.let {
            return SharedPreferencesManager(it).isSessionUpdated()
        }
        return false
    }
}