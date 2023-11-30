package com.app.syspoint.viewmodel.login

import android.os.Environment
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.syspoint.App
import com.app.syspoint.BuildConfig
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.interactor.data.GetAllDataInteractor
import com.app.syspoint.interactor.data.GetAllDataInteractorImp
import com.app.syspoint.interactor.token.TokenInteractor
import com.app.syspoint.interactor.token.TokenInteractorImpl
import com.app.syspoint.models.Resource
import com.app.syspoint.models.UserSession
import com.app.syspoint.models.enums.RoleType
import com.app.syspoint.models.sealed.DownloadApkViewState
import com.app.syspoint.models.sealed.DownloadingViewState
import com.app.syspoint.models.sealed.LoginViewState
import com.app.syspoint.repository.cache.SharedPreferencesManager
import com.app.syspoint.repository.objectBox.AppBundle
import com.app.syspoint.repository.objectBox.dao.ChargeDao
import com.app.syspoint.repository.objectBox.dao.ClientDao
import com.app.syspoint.repository.objectBox.dao.CobrosDao
import com.app.syspoint.repository.objectBox.dao.EmployeeDao
import com.app.syspoint.repository.objectBox.dao.PlayingDao
import com.app.syspoint.repository.objectBox.dao.PricePersistenceDao
import com.app.syspoint.repository.objectBox.dao.RolesDao
import com.app.syspoint.repository.objectBox.dao.RoutingDao
import com.app.syspoint.repository.objectBox.dao.RuteClientDao
import com.app.syspoint.repository.objectBox.dao.SellsDao
import com.app.syspoint.repository.objectBox.dao.SessionDao
import com.app.syspoint.repository.objectBox.dao.SpecialPricesDao
import com.app.syspoint.repository.objectBox.dao.StockDao
import com.app.syspoint.repository.objectBox.dao.StockHistoryDao
import com.app.syspoint.repository.objectBox.dao.TaskDao
import com.app.syspoint.repository.objectBox.dao.VisitsDao
import com.app.syspoint.repository.objectBox.entities.ClientBox
import com.app.syspoint.repository.objectBox.entities.EmployeeBox
import com.app.syspoint.repository.objectBox.entities.PersistancePricesBox
import com.app.syspoint.repository.objectBox.entities.RolesBox
import com.app.syspoint.repository.objectBox.entities.RoutingBox
import com.app.syspoint.repository.objectBox.entities.RuteClientBox
import com.app.syspoint.repository.objectBox.entities.SessionBox
import com.app.syspoint.repository.objectBox.entities.TaskBox
import com.app.syspoint.usecases.GetAllEmployeesUseCase
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.utils.Utils
import com.app.syspoint.viewmodel.BaseViewModel
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.time.LocalDate
import java.io.FileOutputStream as FileOutputStream1

private const val TAG = "LoginViewModel"
class LoginViewModel: BaseViewModel() {

    val loginViewState = MutableLiveData<LoginViewState>()
    val downloadingViewState = MutableLiveData<DownloadingViewState>()
    val downloadApkViewState = MutableLiveData<DownloadApkViewState>()

    init {
        //createUser()
        validatePersistence()
        validateToken()
        //sync()
    }

    fun login(email: String, password: String, rememberSession: Boolean) {
        val employeeDao = EmployeeDao()
        val employeeBox = employeeDao.validateLogin(email, password)

        val sessionDao = SessionDao()
        sessionDao.clear()

        val userSession = UserSession(email, password, false)
        val lastUserSession = CacheInteractor().getSeller()

        if (employeeBox != null) {


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

            val sessionBean = SessionBox()
            sessionBean.employee.target = employeeBox
            sessionBean.empleadoId = employeeBox.id
            sessionBean.employeeIdentifier = employeeBox.identificador!!
            sessionBean.remember = rememberSession
            sessionBean.clientId = employeeBox.clientId
            sessionDao.saveSession(sessionBean)
            AppBundle.setUserSession(userSession)

            // save seller in cache
            val cacheInteractor = CacheInteractor()
            cacheInteractor.saveSeller(employeeBox)

            employeeDao.removeUnnecessaryEmployees()

            NetworkStateTask { connected ->
                if (connected) {
                    viewModelScope.launch {
                        loginViewState.postValue(LoginViewState.ConnectedToInternet)
                        loginViewState.postValue(LoginViewState.LoadingDataStart)

                        GetAllDataInteractorImp().executeGetAllData(object :
                            GetAllDataInteractor.OnGetAllDataListener {
                            override fun onGetAllDataSuccess() {
                                loginViewState.postValue(
                                    if (employeeBox != null) {
                                        Timber.tag(TAG).d("loggedIn $email")
                                        saveCurrentDate()
                                        LoginViewState.LoggedIn
                                    } else {
                                        Timber.tag(TAG)
                                            .d("Usuario no encontrado verifique los datos de acceso $email")
                                        LoginViewState.LoginError("Usuario no encontrado verifique los datos de acceso")
                                    }
                                )
                            }

                            override fun onGetAllDataError() {
                                loginViewState.postValue(
                                    if (employeeBox != null) {
                                        Timber.tag(TAG).d("loggedIn $email")
                                        saveCurrentDate()
                                        LoginViewState.LoggedIn
                                    } else {
                                        Timber.tag(TAG)
                                            .d("Usuario no encontrado verifique los datos de acceso $email")
                                        LoginViewState.LoginError("Usuario no encontrado verifique los datos de acceso")
                                    }
                                )
                            }
                        })
                    }
                } else {
                    loginViewState.postValue(
                        if (employeeBox != null) {
                            Timber.tag(TAG).d("loggedIn $email")
                            saveCurrentDate()
                            LoginViewState.LoggedIn
                        } else {
                            Timber.tag(TAG)
                                .d("Usuario no encontrado verifique los datos de acceso $email")
                            LoginViewState.LoginError("Usuario no encontrado verifique los datos de acceso")
                        }
                    )
                }
            }.execute()

        } else {
            Timber.tag(TAG).d("Usuario no encontrado verifique los datos de acceso $email")
            loginViewState.postValue(
                    LoginViewState.LoginError("Usuario no encontrado verifique los datos de acceso")
            )
        }

    }

    private fun setRuteByEmployeeIfExists(employeeBox: EmployeeBox) {
        val routingDao = RoutingDao()
        val ruteoBean = routingDao.getRutaEstablecida()

        if (ruteoBean != null) {
            ruteoBean.id = 1L
            ruteoBean.fecha = Utils.fechaActual()
            if (!employeeBox.rute.isNullOrEmpty()) {
                ruteoBean.ruta = employeeBox.rute
            } else {
                ruteoBean.ruta = ""
            }
            routingDao.insert(ruteoBean)
            getClientsByRute(ruteoBean)
        } else {
            val ruteo = RoutingBox()
            ruteo.id = 1L
            ruteo.fecha = Utils.fechaActual()

            if (!employeeBox.rute.isNullOrEmpty()) {
                ruteo.ruta = employeeBox.rute
            } else {
                ruteo.ruta = ""
            }
            routingDao.insert(ruteo)

            getClientsByRute(ruteo)
        }
    }

    private fun getClientsByRute(ruteoBean: RoutingBox) {
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

    private fun saveData(listaClientes: List<ClientBox>, day: Int) {
        var count = 0
        for (item in listaClientes) {
            count += 1
            val ruteClientDao = RuteClientDao()
            val clientesRutaBean = ruteClientDao.getClienteByCuentaCliente(item.cuenta)
            //Guardamos al clientes en la ruta actual
            if (clientesRutaBean == null) {
                val bean = RuteClientBox()
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
                dao.insert(bean)
            }
        }
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

    fun isUserAdmin(): Boolean {
        // get seller
        val sellerBean = getEmployee()

        // save seller in cache
        val cacheInteractor = CacheInteractor()
        cacheInteractor.saveSeller(sellerBean!!)

        var identificador: String? = ""
        if (sellerBean != null) {
            identificador = sellerBean.identificador
        }
        val rolesDao = RolesDao()
        val rolesBean = rolesDao.getRolByEmpleado(identificador, RoleType.STOCK.value)

        return rolesBean != null && rolesBean.active
    }

    private fun createUser() {
        val employeeDao = EmployeeDao()
        val count = employeeDao.getTotalEmployees()
        if (count == 0) {
            val employee = EmployeeBox()
            val dao = EmployeeDao()
            employee.nombre = "Osvaldo Cazares"
            employee.direccion = "Conocida"
            employee.email = "dev@gmail.com"
            employee.telefono = "6672081920"
            employee.fecha_nacimiento =  "00/00/0000"
            employee.fecha_ingreso = "00/00/0000"
            employee.contrasenia = "123"
            employee.identificador = "E001"
            dao.insert(employee)
            val rolCliente = RolesBox()
            val rolClienteDao =
                RolesDao()
            rolCliente.empleado.target = employee
            rolCliente.modulo = "Clientes"
            rolCliente.active = true
            rolCliente.identificador = employee.identificador
            rolClienteDao.insert(rolCliente)
            val rolProducto = RolesBox()
            val rolProductoDao =
                RolesDao()
            rolProducto.empleado.target = employee
            rolProducto.modulo = "Productos"
            rolProducto.active = true
            rolProducto.identificador = employee.identificador
            rolProductoDao.insert(rolProducto)
            val rolVentas = RolesBox()
            val rolVentasDao = RolesDao()
            rolVentas.empleado.target = employee
            rolVentas.modulo = "Ventas"
            rolVentas.active = true
            rolVentas.identificador = employee.identificador
            rolVentasDao.insert(rolVentas)
            val rolEmpleado = RolesBox()
            val rolEmpleadoDao =
                RolesDao()
            rolEmpleado.empleado.target = employee
            rolEmpleado.modulo = "Empleados"
            rolEmpleado.active = true
            rolEmpleado.identificador = employee.identificador
            rolEmpleadoDao.insert(rolEmpleado)
            val rolCobranza = RolesBox()
            val rolCobranzaDao =
                RolesDao()
            rolCobranza.empleado.target = employee
            rolCobranza.modulo = "Cobranza"
            rolCobranza.active = true
            rolCobranza.identificador = employee.identificador
            rolCobranzaDao.insert(rolCobranza)
        }
    }

    private fun validatePersistence() {
        val persistenceDao =
            PricePersistenceDao()
        val exists = persistenceDao.existePersistencia()
        if (exists == 0) {
            val persistencePriceBean = PersistancePricesBox()
            val persistencePriceDao =
                PricePersistenceDao()
            persistencePriceBean.id = java.lang.Long.valueOf(1)
            persistencePriceBean.mostrar = "All"
            persistencePriceBean.valor = java.lang.Long.valueOf(1)
            persistencePriceDao.insert(persistencePriceBean)
        }
    }

    fun validateToken() {
        //sync()

        Handler().postDelayed({
            NetworkStateTask { connected ->
                if (connected) {
                    viewModelScope.launch {
                        TokenInteractorImpl().executeGetToken(object :
                            TokenInteractor.OnGetTokenListener {
                            override fun onGetTokenSuccess(token: String?, currentVersion: String) {
                                sync()
                            }

                            override fun onGetTokenError(baseUpdateUrl: String, currentVersion: String, throwable: Throwable?) {
                                if (throwable == null) {
                                    downloadApkViewState.postValue(
                                        DownloadApkViewState.ApkOldVersion(
                                            baseUpdateUrl,
                                            currentVersion
                                        )
                                    )
                                }
                            }
                        })
                    }
                } else {
                    viewModelScope.launch {
                        //removeLocalSync()
                        delay(300)
                        val existSession = existUserSession()
                        if (existSession) {
                            loginViewState.postValue(LoginViewState.LoadingDataFinish)
                            delay(300)
                            loginViewState.postValue(LoginViewState.NotInternetConnection)
                        } else {
                            loginViewState.postValue(LoginViewState.NoSessionExists)
                        }
                    }
                }
            }.execute()
        }, 100)
    }

    fun sync() {
        Timber.tag(TAG).d("sync")
        if (!isSync()) {
            loginViewState.value = LoginViewState.LoadingDataStart
            Handler().postDelayed({
                NetworkStateTask { connected ->
                    Timber.tag(TAG).d("sync -> %s", connected)
                    if (connected) {

                        viewModelScope.launch {
                            loginViewState.postValue(LoginViewState.ConnectedToInternet)
                            downloadingViewState.postValue(DownloadingViewState.StartDownloadViewState)
                            GetAllEmployeesUseCase().invoke().onEach { response ->
                                if (response is Resource.Success) {
                                    updateSession(true)
                                    downloadingViewState.postValue(DownloadingViewState.DownloadCompletedViewState)
                                    loginViewState.postValue(LoginViewState.LoadingDataFinish)
                                } else if (response is Resource.Error){
                                    downloadingViewState.postValue(DownloadingViewState.DownloadCancelledViewState)
                                    loginViewState.postValue(LoginViewState.LoadingDataFinish)
                                    removeLocalSync()
                                    loginViewState.postValue(LoginViewState.NotInternetConnection)
                                }
                            }.launchIn(this)
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

    private fun existUserSession(): Boolean {
        val seller = CacheInteractor().getSeller()
        if (seller != null) return true
        return SessionDao().getUserSession() != null
    }

    fun isSync(): Boolean {
        val taskDao = TaskDao()
        val taskBean = taskDao.getTask(Utils.fechaActual())
        val exist: Boolean
        val isUpdated = isSessionUpdated()

        if (taskBean == null || (taskBean != null && taskBean.date != Utils.fechaActual()) || !isUpdated) {
            //forceUpdate(false)
            exist = false
            //updateSession(false)
        } else {
            exist = true
        }

        return exist
    }

    fun forceUpdate(removeTask: Boolean) {
        Timber.tag(TAG).d("sync -> forceUpdate")

        App.mBoxStore?.removeAllObjects()

        val sessionDao = SessionDao()
        sessionDao.clear()
        val stockDao = StockDao()
        stockDao.clear()
        val stockHistoryDao = StockHistoryDao()
        stockHistoryDao.clear()
        val sellDao = SellsDao()
        sellDao.clear()
        val playingDao = PlayingDao()
        playingDao.clear()
        val visitasDao = VisitsDao()
        visitasDao.clear()
        val cobranzaDao = CobrosDao()
        cobranzaDao.clear()
        val chargesDao = ChargeDao()
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
        val bean = TaskBox()

        bean.date = if (removeTask) "" else Utils.fechaActual()
        bean.task = "Deleted"

        CacheInteractor().removeSellerFromCache()
        CacheInteractor().resetStockId()
        CacheInteractor().resetLoadId()

        dao.insert(bean)
    }

    fun removeLocalSync() {
        Timber.tag(TAG).d("sync -> removeLocalSync")
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

    fun checkAppVersionInFirebaseStore(versionToDownload: String) {
        loginViewState.value = LoginViewState.LoadingDataStart
        val flavor = BuildConfig.FLAVOR
        val buildType = BuildConfig.BUILD_TYPE
        val fileName = flavor + "_" + buildType + "_" + versionToDownload + ".apk"
        val storage = Firebase.storage
        val storageRef: StorageReference = storage.reference
        val islandRef = storageRef.child("apks/$flavor/$buildType/$fileName")


        islandRef.downloadUrl.addOnSuccessListener {
            startDownloadInFirebase(islandRef, fileName, versionToDownload)
        }.addOnFailureListener {
            downloadApkViewState.postValue(
                DownloadApkViewState.DownloadApkError(versionToDownload)
            )
            loginViewState.postValue(
                LoginViewState.LoadingDataFinish
            )
        }
    }

    private fun startDownloadInFirebase(islandRef: StorageReference, fileName: String, versionToDownload: String) {
        val rootPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "tenet/apks/")
        if (!rootPath.exists()) {
            rootPath.mkdirs()
        }
        val file = File(rootPath, fileName)
        val stream = FileOutputStream1(rootPath.path + "/"+fileName)

        val BYTES: Long = 1024 * 1024 * 90
        islandRef.getBytes(BYTES).addOnSuccessListener {
            stream.write(it)
            forceUpdate(true)
            downloadApkViewState.postValue(
                DownloadApkViewState.DownloadApkSuccess(file, versionToDownload)
            )
            loginViewState.postValue(
                LoginViewState.LoadingDataFinish
            )
        }.addOnFailureListener {

            downloadApkViewState.postValue(
                DownloadApkViewState.DownloadApkError(versionToDownload)
            )
            loginViewState.postValue(
                LoginViewState.LoadingDataFinish
            )
        }
    }

    private fun saveCurrentDate() {
        App.INSTANCE?.baseContext?.let {
            val currentDate = LocalDate.now()
            SharedPreferencesManager(it).storeCurrentDate(currentDate)
        }
    }
}