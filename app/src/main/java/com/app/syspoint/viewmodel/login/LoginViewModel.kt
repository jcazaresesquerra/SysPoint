package com.app.syspoint.viewmodel.login

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.database.dao.*
import com.app.syspoint.interactor.data.GetAllDataInteractor
import com.app.syspoint.interactor.data.GetAllDataInteractorImp
import com.app.syspoint.models.sealed.LoginViewState
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.cache.CacheInteractor
import com.app.syspoint.viewmodel.BaseViewModel

class LoginViewModel: BaseViewModel() {

    val loginViewState = MutableLiveData<LoginViewState>()

    init {
        createUser()
        validatePersistence()
        sync()
    }

    fun login(email: String, password: String) {
        val employeeDao = EmployeeDao()
        val employeeBean = employeeDao.validateLogin(email, password)

        val sessionDao = SessionDao()
        sessionDao.clear()

        val userSession = UserSession(email, password, false)

        if (employeeBean != null) {
            val sessionBean = SesionBean()
            sessionBean.empleado = employeeBean
            sessionBean.empleadoId = employeeBean.id
            sessionBean.remember = false
            sessionDao.saveSession(sessionBean)

            AppBundle.setUserSession(userSession)
        }

        loginViewState.postValue(
            if (employeeBean != null) LoginViewState.LoggedIn
            else LoginViewState.LoginError
        )
    }

    fun isUserAdmin(context: Context): Boolean {
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

    private fun sync() {
        if (!existTask()) {
            GetAllDataInteractorImp().executeGetAllData(object: GetAllDataInteractor.OnGetAllDataListener {
                override fun onGetAllDataSuccess() {

                }

                override fun onGetAllDataError() {

                }
            })
        }
    }

    private fun existTask(): Boolean {
        var exist = false
        val taskDao = TaskDao()
        val taskBean = taskDao.getTask(Utils.fechaActual())
        if (taskBean == null) {
            val stockDao = StockDao()
            stockDao.clear()
            val historialDao =
                StockHistoryDao()
            historialDao.clear()
            val ventasDao = SellsDao()
            ventasDao.clear()
            val itemDao = ItemDao()
            itemDao.clear()
            val visitasDao = VisitsDao()
            visitasDao.clear()
            val cobranzaDao =
                PaymentDao()
            cobranzaDao.clear()
            val chargesDao =
                ChargesDao()
            chargesDao.clear()
            val routingDao =
                RoutingDao()
            routingDao.clear()
            val employeeDao =
                EmployeeDao()
            employeeDao.clear()
            val rolesDao = RolesDao()
            rolesDao.clear()
            val clientesRutaDao =
                RuteClientDao()
            clientesRutaDao.clear()
            val specialPricesDao =
                SpecialPricesDao()
            specialPricesDao.clear()
            val dao = TaskDao()
            dao.clear()
            val bean = TaskBean()
            bean.date = Utils.fechaActual()
            bean.task = "Sincronización"
            dao.insert(bean)
            exist = false
        } else {
            exist = true
        }
        return exist
    }
}