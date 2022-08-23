package com.app.syspoint.ui.login

import android.content.Context
import com.app.syspoint.db.bean.*
import com.app.syspoint.db.dao.*
import com.app.syspoint.http.ApiServices
import com.app.syspoint.http.PointApi
import com.app.syspoint.interactor.GetAllDataInteractor
import com.app.syspoint.interactor.GetAllDataInteractorImp
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.cache.CacheInteractor

class LoginViewModel {

    private var employeeDao: EmpleadoDao = EmpleadoDao()

    fun validateUser(email: String, password: String): Boolean {
        val employeeBean = employeeDao.getValidaLogin(email, password)

        val sessionDao = SesionDao()
        sessionDao.clear()

        val userSession = UserSession(email, password, false)

        if (employeeBean != null) {
            val sessionBean = SesionBean()
            sessionBean.empleado = employeeBean
            sessionBean.empleadoId = employeeBean.id
            sessionBean.remember = false
            sessionDao.saveSesion(sessionBean)

            AppBundle.setUserSession(userSession)
        }

        return employeeBean != null
    }

    fun isUserAdmin(context: Context): Boolean {
        // get seller
        val sellerBean = AppBundle.getUserBean()

        // save seller in cache
        val cacheInteractor = CacheInteractor(context)
        cacheInteractor.saveSeller(sellerBean!!)

        var identificador: String? = ""
        if (sellerBean != null) {
            identificador = sellerBean.getIdentificador()
        }
        val rolesDao = RolesDao()
        val rolesBean = rolesDao.getRolByEmpleado(identificador, "Inventarios")

        return rolesBean != null && rolesBean.active
    }

    fun createUser() {
        val employeeDao = EmpleadoDao()
        val count = employeeDao.totalEmpleados
        if (count == 0) {
            val employee = EmpleadoBean()
            val dao = EmpleadoDao()
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
            val rolClienteDao = RolesDao()
            rolCliente.empleado = employee
            rolCliente.modulo = "Clientes"
            rolCliente.active = true
            rolCliente.identificador = employee.getIdentificador()
            rolClienteDao.insert(rolCliente)
            val rolProducto = RolesBean()
            val rolProductoDao = RolesDao()
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
            val rolEmpleadoDao = RolesDao()
            rolEmpleado.empleado = employee
            rolEmpleado.modulo = "Empleados"
            rolEmpleado.active = true
            rolEmpleado.identificador = employee.getIdentificador()
            rolEmpleadoDao.insert(rolEmpleado)
            val rolCobranza = RolesBean()
            val rolCobranzaDao = RolesDao()
            rolCobranza.empleado = employee
            rolCobranza.modulo = "Cobranza"
            rolCobranza.active = true
            rolCobranza.identificador = employee.getIdentificador()
            rolCobranzaDao.insert(rolCobranza)
        }
    }

    fun validatePersistence() {
        val persistenceDao = PersistenciaPrecioDao()
        val exists = persistenceDao.existePersistencia()
        if (exists == 0) {
            val persistencePriceBean = PersistenciaPrecioBean()
            val persistencePriceDao = PersistenciaPrecioDao()
            persistencePriceBean.id = java.lang.Long.valueOf(1)
            persistencePriceBean.mostrar = "All"
            persistencePriceBean.valor = java.lang.Long.valueOf(1)
            persistencePriceDao.insert(persistencePriceBean)
        }
    }

    fun sync() {
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
            val inventarioDao = InventarioDao()
            inventarioDao.clear()
            val historialDao = InventarioHistorialDao()
            historialDao.clear()
            val ventasDao = VentasDao()
            ventasDao.clear()
            val partidasDao = PartidasDao()
            partidasDao.clear()
            val visitasDao = VisitasDao()
            visitasDao.clear()
            val cobranzaDao = CobranzaDao()
            cobranzaDao.clear()
            val cobrosDao = CobrosDao()
            cobrosDao.clear()
            val ruteoDao = RuteoDao()
            ruteoDao.clear()
            val empleadoDao = EmpleadoDao()
            empleadoDao.clear()
            val rolesDao = RolesDao()
            rolesDao.clear()
            val clientesRutaDao = ClientesRutaDao()
            clientesRutaDao.clear()
            val preciosEspecialesDao = PreciosEspecialesDao()
            preciosEspecialesDao.clear()
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