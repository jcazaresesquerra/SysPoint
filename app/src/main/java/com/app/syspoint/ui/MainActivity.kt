package com.app.syspoint.ui

import android.app.Dialog
import android.app.DownloadManager
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.androidnetworking.error.ANError
import com.app.syspoint.BuildConfig
import com.app.syspoint.R
import com.app.syspoint.databinding.ActivityMainBinding
import com.app.syspoint.databinding.NavHeaderMainBinding
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.interactor.charge.ChargeInteractor.*
import com.app.syspoint.interactor.charge.ChargeInteractorImp
import com.app.syspoint.interactor.client.ClientInteractor.GetAllClientsListener
import com.app.syspoint.interactor.client.ClientInteractor.SaveClientListener
import com.app.syspoint.interactor.client.ClientInteractorImp
import com.app.syspoint.interactor.data.GetAllDataInteractor
import com.app.syspoint.interactor.data.GetAllDataInteractorImp
import com.app.syspoint.interactor.employee.GetEmployeeInteractor.SaveEmployeeListener
import com.app.syspoint.interactor.employee.GetEmployeesInteractorImp
import com.app.syspoint.interactor.installer.ApkInstaller
import com.app.syspoint.interactor.prices.PriceInteractor.SendPricesListener
import com.app.syspoint.interactor.prices.PriceInteractorImp
import com.app.syspoint.interactor.roles.RolInteractor.OnGetAllRolesListener
import com.app.syspoint.interactor.roles.RolInteractorImp
import com.app.syspoint.interactor.token.TokenInteractor
import com.app.syspoint.interactor.token.TokenInteractorImpl
import com.app.syspoint.interactor.visit.VisitInteractor.OnSaveVisitListener
import com.app.syspoint.interactor.visit.VisitInteractorImp
import com.app.syspoint.models.*
import com.app.syspoint.models.enums.RoleType
import com.app.syspoint.repository.objectBox.AppBundle
import com.app.syspoint.repository.objectBox.dao.*
import com.app.syspoint.repository.objectBox.entities.ChargeBox
import com.app.syspoint.repository.objectBox.entities.ClientBox
import com.app.syspoint.repository.objectBox.entities.RolesBox
import com.app.syspoint.repository.request.http.Servicio.ResponseOnError
import com.app.syspoint.repository.request.http.Servicio.ResponseOnSuccess
import com.app.syspoint.repository.request.http.SincVentas
import com.app.syspoint.ui.login.LoginActivity
import com.app.syspoint.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat

class MainActivity: BaseActivity() {

    companion object {
        @JvmStatic
        var apikey: String? = null
        const val IS_ADMIN = "is_admin"
    }

    private var mAppBarConfiguration: AppBarConfiguration? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var isOldApkVersionDialogShowing = false

    private lateinit var progressDialog: ProgressDialog

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        setUpLogo()

        val isAdmin = intent.getBooleanExtra(IS_ADMIN, false)

        var vendedoresBean = AppBundle.getUserBox()
        if (vendedoresBean == null) vendedoresBean = CacheInteractor().getSeller()
        val identificador = if (vendedoresBean != null) vendedoresBean.identificador else ""

        val rolesDao = RolesDao()
        val productsRolesBox = rolesDao.getRolByEmpleado(identificador, RoleType.PRODUCTS.value)
        val employeesRolesBox = rolesDao.getRolByEmpleado(identificador, RoleType.EMPLOYEES.value)
        val clientsRolesBox = rolesDao.getRolByEmpleado(identificador, RoleType.CLIENTS.value)

        val productsActive = productsRolesBox?.active ?: false
        val employeesActive = employeesRolesBox?.active ?: false
        val clientsActive = clientsRolesBox?.active ?: false

        binding.navView.apply {
            menu.clear()
            inflateMenu(R.menu.activity_main_drawer)
        }

        configureMenu(isAdmin, employeesActive, productsActive, clientsActive)

        mAppBarConfiguration =
            AppBarConfiguration.Builder(buildMenuSet(isAdmin, employeesActive, productsActive, clientsActive))
           .setDrawerLayout(binding.drawerLayout)
                .build()

        navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            /*val progressDialog = ProgressDialog(this@MainActivity)
            progressDialog.setMessage("Espere un momento")
            progressDialog.setCancelable(false)
            progressDialog.show()
            Handler().postDelayed({
                NetworkStateTask { connected: Boolean ->
                    progressDialog.dismiss()
                    if (!connected) showDialogNotInternet()
                }.execute()
            }, 100)*/
            if (destination.id == R.id.nav_ruta) {
                Constants.solictaRuta = true
            }
            if (destination.id == R.id.nav_home) {
                Constants.solictaRuta = false
            }
        }

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(binding.navView, navController)
        apikey = getString(R.string.google_maps_key)

        getUpdates()
        //validateToken()
        //registerNetworkBroadcastForNougat()
        //startForegroundService(Intent(this, UpdateDataService::class.java))

    }

    private fun validateToken() {
        Handler().postDelayed({
            NetworkStateTask { connected ->
                if (connected) {
                    TokenInteractorImpl().executeGetToken(object :
                        TokenInteractor.OnGetTokenListener {
                        override fun onGetTokenSuccess(token: String?, currentVersion: String) {
                            getUpdates()
                        }

                        override fun onGetTokenError(baseUpdateUrl: String, currentVersion: String) {
                            showErrorDialog("Su versión no esta soportada, por favor, actualice su aplicación")
                            showAppOldVersion(baseUpdateUrl, currentVersion)
                        }
                    })
                }
            }.execute()
        }, 100)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp()
    }

    override fun getView(): View {
        return binding.root
    }

    private fun showDialogNotInternet() {
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.no_internet_dialog_warning)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById<View>(R.id.bt_close) as AppCompatButton).setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun setUpLogo() {
        val navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))

        when (BuildConfig.FLAVOR) {
            "donaqui" -> {
                navHeaderMainBinding.root.setBackgroundColor(resources.getColor(R.color.white))
                navHeaderMainBinding.imageView?.let { it.setImageResource(R.drawable.logo_donaqui) }
            }
            else -> {
                navHeaderMainBinding.imageView?.let { it.setImageResource(R.drawable.logo) }
            }
        }
    }

    private fun configureMenu(isAdmin: Boolean, employeesActive: Boolean, productsActive: Boolean, clientsActive: Boolean) {
        val menu = binding.navView.menu
        menu.findItem(R.id.nav_home).isVisible = true
        menu.findItem(R.id.nav_producto).isVisible = true
        menu.findItem(R.id.nav_empleado).isVisible = employeesActive
        menu.findItem(R.id.nav_producto).isVisible = productsActive
        menu.findItem(R.id.nav_cliente).isVisible = clientsActive
        menu.findItem(R.id.nav_historial).isVisible = true
        menu.findItem(R.id.nav_inventario).isVisible = isAdmin
        menu.findItem(R.id.nav_cobranza).isVisible = isAdmin
    }

    private fun buildMenuSet(isAdmin: Boolean, employeesActive: Boolean, productsActive: Boolean, clientsActive: Boolean): Set<Int> {
        //Obtiene el nombre del vendedor
        val menuSet = mutableSetOf(R.id.nav_home, R.id.nav_ruta)

        if (employeesActive) menuSet.add(R.id.nav_empleado)
        if (productsActive) menuSet.add(R.id.nav_producto)
        if (clientsActive) menuSet.add(R.id.nav_cliente)

        menuSet.add(R.id.nav_historial)

        if (isAdmin) {
            menuSet.add(R.id.nav_inventario)
            menuSet.add(R.id.nav_cobranza)
        }

        return menuSet
    }

    fun goHome() {
        if (::navController.isInitialized) {
            navController.navigate(R.id.nav_home)
        }
    }

    private fun getUpdates() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Espere un momento")
        progressDialog.setCancelable(false)
        progressDialog.show()

        Handler().postDelayed({
            NetworkStateTask { connected: Boolean ->
                dismissProgressDialog()
                if (connected) {
                    progressDialog.setMessage("Obteniendo actualizaciones...");
                    progressDialog.show();
                    getUpdated()
                }
            }.execute()}
            ,100)
    }

    /**
     * Connection Listener
     */
    interface ConnectionNetworkListener {
        fun onConnected()
        fun onDisconnected()
    }

    /**
     * Connection BroadcastReceiver
     */
    class NetworkChangeReceiver(): BroadcastReceiver() {
        private lateinit var mConnectionNetworkListener: ConnectionNetworkListener

        constructor(connectionNetworkListener: ConnectionNetworkListener): this() {
            mConnectionNetworkListener = connectionNetworkListener
        }

        override fun onReceive(context: Context?, p1: Intent?) {
            try {
                val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = cm.activeNetworkInfo
                val isConnected = netInfo != null && netInfo.isConnected
                if (isConnected) {
                    mConnectionNetworkListener.onConnected()
                } else {
                    mConnectionNetworkListener.onDisconnected()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mConnectionNetworkListener.onDisconnected()
            }
        }
    }


    private fun saveVentas() {
        lifecycleScope.launch(Dispatchers.IO) {
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
    }

    private fun saveVisitas() {
        lifecycleScope.launch(Dispatchers.IO) {
            val visitsDao = VisitsDao()
            val visitasBeanListBean = visitsDao.getVisitsByCurrentDay(Utils.fechaActual())
            val clientDao = ClientDao()
            var vendedoresBean = AppBundle.getUserBox()
            if (vendedoresBean == null) {
                vendedoresBean = CacheInteractor().getSeller()
            }
            val visitList: MutableList<Visit> = ArrayList()
            visitasBeanListBean.map {item ->
                val visita = Visit()
                visita.fecha = item.fecha
                visita.hora = item.hora
                val clienteBean = clientDao.getClientByAccount(item.cliente.target.cuenta)
                visita.cuenta = clienteBean!!.cuenta
                visita.latidud = item.latidud
                visita.longitud = item.longitud
                visita.motivo_visita = item.motivo_visita
                if (vendedoresBean != null) {
                    visita.identificador = vendedoresBean.identificador
                } else {
                    Log.e("SysPoint", "vendedoresBean is null")
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

    fun saveCobranza() {
        lifecycleScope.launch(Dispatchers.IO) {
            val cobranzaBeanList = ChargeDao().getCobranzaFechaActual(Utils.fechaActual())
            val listaCobranza: MutableList<Payment> = ArrayList()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            cobranzaBeanList.map {item ->
                val cobranza = Payment()
                cobranza.cobranza = item.cobranza
                cobranza.cuenta = item.cliente
                cobranza.importe = item.importe
                cobranza.saldo = item.saldo!!
                cobranza.venta = item.venta
                cobranza.estado = item.estado
                cobranza.observaciones = item.observaciones
                cobranza.fecha = item.fecha
                cobranza.hora = item.hora
                cobranza.identificador = item.empleado
                cobranza.updatedAt = formatter.format(item.updatedAt)
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


    private fun savePreciosEspeciales() {
        lifecycleScope.launch(Dispatchers.IO) {
            val dao = SpecialPricesDao()
            val listaDB = dao.getPreciosBydate(Utils.fechaActual())
            val listaPreciosServidor: MutableList<Price> = ArrayList()

            //Contien la lista de precios especiales locales
            listaDB.map {item ->
                val precio = Price()
                if (item.active) {
                    precio.active = 1
                } else {
                    precio.active = 0
                }
                precio.articulo = item.articulo
                precio.cliente = item.cliente
                precio.precio = item.precio
                listaPreciosServidor.add(precio)
            }
            PriceInteractorImp().executeSendPrices(
                listaPreciosServidor,
                object : SendPricesListener {
                    override fun onSendPricesSuccess() {
                        //Toast.makeText(requireActivity(), "Sincronizacion de lista de precios exitosa", Toast.LENGTH_LONG).show();
                    }

                    override fun onSendPricesError() {
                        //Toast.makeText(requireActivity(), "Error al sincronizar la lista de precios intente mas tarde", Toast.LENGTH_LONG).show();
                    }
                })
        }
    }

    fun saveClientes() {
        lifecycleScope.launch(Dispatchers.IO) {
            val clientDao = ClientDao()
            val clientListDB = clientDao.getClientsByDay(Utils.fechaActual())
            val clientList: MutableList<Client> = ArrayList()
            clientListDB.map {item->
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

    private fun testLoadEmpleado(id: Long) {
        val employeeDao = EmployeeDao()
        val listaEmpleadosDB = employeeDao.getEmployeeById(id)
        val listEmpleados: MutableList<Employee> = ArrayList()
        listaEmpleadosDB.map {item ->
            val empleado = Employee()
            empleado.nombre = item.nombre
            if (item.direccion!!.isEmpty()) {
                empleado.direccion = "-"
            } else {
                empleado.direccion = item.direccion
            }
            empleado.email = item.email
            if (item.telefono!!.isEmpty()) {
                empleado.telefono = "-"
            } else {
                empleado.telefono = item.telefono
            }
            if (item.fecha_nacimiento!!.isEmpty()) {
                empleado.fechaNacimiento = "-"
            } else {
                empleado.fechaNacimiento = item.fecha_nacimiento
            }
            if (item.fecha_ingreso!!.isEmpty()) {
                empleado.fechaIngreso = "-"
            } else {
                empleado.fechaIngreso = item.fecha_ingreso
            }
            empleado.contrasenia = item.contrasenia
            empleado.identificador = item.identificador
            empleado.status = if (item.status) 1 else 0
            if (item.path_image == null || item.path_image!!.isEmpty()) {
                empleado.pathImage = ""
            } else {
                empleado.pathImage = item.path_image
            }
            if (!item.rute!!.isEmpty()) {
                empleado.rute = item.rute
            } else {
                empleado.rute = ""
            }
            listEmpleados.add(empleado)
        }
        GetEmployeesInteractorImp().executeSaveEmployees(
            listEmpleados,
            object : SaveEmployeeListener {
                override fun onSaveEmployeeSuccess() {
                    //Toast.makeText(ActualizarEmpleadoActivity.this, "Empleados sincronizados", Toast.LENGTH_LONG).show();
                }

                override fun onSaveEmployeeError() {
                    //Toast.makeText(ActualizarEmpleadoActivity.this, "Ha ocurrido un error al sincronizar los empleados", Toast.LENGTH_LONG).show();
                }
            })
    }

    private fun saveAbonos() {
        lifecycleScope.launch(Dispatchers.IO) {
            val cobranzaBeanList = ChargeDao().getAbonosFechaActual(Utils.fechaActual())
            val listaCobranza: MutableList<Payment> = java.util.ArrayList()
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            cobranzaBeanList.map {item ->
                val cobranza = Payment()
                cobranza.cobranza = item.cobranza
                cobranza.cuenta = item.cliente
                cobranza.importe = item.importe
                cobranza.saldo = item.saldo!!
                cobranza.venta = item.venta
                cobranza.estado = item.estado
                cobranza.observaciones = item.observaciones
                cobranza.fecha = item.fecha
                cobranza.hora = item.hora
                cobranza.identificador = item.empleado
                cobranza.updatedAt = formatter.format(item.updatedAt)
                listaCobranza.add(cobranza)
            }
            ChargeInteractorImp().executeUpdateCharge(
                listaCobranza,
                object : OnUpdateChargeListener {
                    override fun onUpdateChargeSuccess() {
                        //Toast.makeText(requireActivity(), "Cobranza actualizada correctamente", Toast.LENGTH_LONG).show();
                    }

                    override fun onUpdateChargeError() {
                        //Toast.makeText(requireActivity(), "Ha ocurrido un error al actualizar la cobranza", Toast.LENGTH_LONG).show();
                    }
                })
        }
    }

    private fun getCobranzasByEmployee() {
        lifecycleScope.launch(Dispatchers.IO) {
            val vendedoresBean = AppBundle.getUserBox()
            if (vendedoresBean != null) {
                lifecycleScope.launch(Dispatchers.Default) {
                    ChargeInteractorImp().executeGetCharge(object : OnGetChargeListener {
                        override fun onGetChargeSuccess(chargeList: List<ChargeBox>) {
                            saveCobranza()
                            saveAbonos()
                        }

                        override fun onGetChargeError() {
                            saveCobranza()
                            saveAbonos()
                        }
                    })
                }

                /*ChargeInteractorImp().executeGetChargeByEmployee(
                vendedoresBean.identificador,
                object : OnGetChargeByEmployeeListener {
                    override fun onGetChargeByEmployeeSuccess(chargeByClientList: List<CobranzaBean>) {
                        saveCobranza()
                        saveAbonos()
                    }

                    override fun onGetChargeByEmployeeError() {
                        saveCobranza()
                        saveAbonos()
                    }
                })*/
            }
        }
    }

    private fun getClientsByRute() {
        lifecycleScope.launch(Dispatchers.IO) {
            val routingDao = RoutingDao()
            val ruteoBean = routingDao.getRutaEstablecida()
            if (ruteoBean != null) {
                val vendedoresBean = AppBundle.getUserBox()
                val ruta = if (ruteoBean.ruta != null && ruteoBean.ruta!!.isNotEmpty()
                ) ruteoBean.ruta else vendedoresBean.rute

                ClientInteractorImp().executeGetAllClientsByDate(
                    ruta!!,
                    ruteoBean.dia,
                    object : GetAllClientsListener {
                        override fun onGetAllClientsSuccess(clientList: List<ClientBox>) {
                            Log.d("SysPoint", "Clients updated")
                            saveClientes()
                        }

                        override fun onGetAllClientsError() {
                            Log.d("SysPoint", "Error when update clients")
                            saveClientes()
                        }
                    })
            }
        }
    }

    private fun getRoles() {
        lifecycleScope.launch(Dispatchers.IO) {
            RolInteractorImp().executeGetAllRoles(object : OnGetAllRolesListener {
                override fun onGetAllRolesSuccess(roles: List<RolesBox>) {
                }

                override fun onGetAllRolesError() {
                    //progresshide()
                    //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener roles", Toast.LENGTH_SHORT).show();
                }
            })
        }
    }

    private fun showErrorDialog(message: String) {
        val dialog = PrettyDialog(this)
        dialog.setTitle("Error")
            .setTitleColor(R.color.purple_500)
            .setMessage(message)
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(
                R.drawable.pdlg_icon_info, R.color.purple_500
            ) { };

        dialog.setCancelable(false)
        dialog.show()
    }

    /**
     * Connection Listener
     */
    interface DownloadListener {
        fun onDownloadSuccess(uri: Uri)
        fun onDownloadError(error: String)
    }

    class DownloadReceiver(): BroadcastReceiver() {
        private var id: Long = 0
        private lateinit var downloadListener: DownloadListener

        constructor(id: Long, downloadListener: DownloadListener): this() {
            this.downloadListener = downloadListener
            this.id = id
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                val downloadManager = context!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            downloadListener.onDownloadSuccess(downloadManager.getUriForDownloadedFile(id))
                        } else {
                            downloadListener.onDownloadError("Error al descargar la aplicación")
                        }
                    }
                } catch (e: Exception) {
                    downloadListener.onDownloadError("Error al descargar la aplicación")
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }
            }
        }
    }

    private fun showAppOldVersion(baseUpdateUrl: String, versionToDownload: String) {
        if (!isOldApkVersionDialogShowing) {
            isOldApkVersionDialogShowing = true
            val oldApkVersionDialog = PrettyDialog(this)
            oldApkVersionDialog.setTitle("Error")
                .setTitleColor(R.color.purple_500)
                .setMessage("Su versión no esta soportada, por favor, actualice su aplicación")
                .setMessageColor(R.color.purple_700)
                .setAnimationEnabled(false)
                .addButton(
                    getString(R.string.download_dialog),
                    R.color.pdlg_color_white,
                    R.color.green_800
                ) {
                    if (versionToDownload.isNullOrEmpty()) {
                        showErrorDialog("Ha ocurrido un error, vuelve a intentarlo")
                    } else {
                        isOldApkVersionDialogShowing = false
                        oldApkVersionDialog.dismiss()

                        val downloadManager =
                            getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                        val request = DownloadManager.Request(
                            Uri.parse(
                                Utils.getUpdateURL(baseUpdateUrl, versionToDownload)
                            )
                        )

                        val progressDialog = ProgressDialog(this@MainActivity)
                        progressDialog.setMessage("Espere un momento")
                        progressDialog.setCancelable(false)
                        progressDialog.show()

                        val id = downloadManager.enqueue(request)

                        val downloadReceiver = LoginActivity.DownloadReceiver(
                            id,
                            object : LoginActivity.DownloadListener {
                                override fun onDownloadSuccess(uri: Uri) {
                                    dismissProgressDialog()
                                    showAppOldVersion(baseUpdateUrl, versionToDownload)
                                    ApkInstaller().installApplicationFromCpanel(
                                        applicationContext,
                                        uri
                                    )
                                }

                                override fun onDownloadError(error: String) {
                                    dismissProgressDialog()
                                    showAppOldVersion(baseUpdateUrl, versionToDownload)
                                    showErrorDialog(error)
                                }

                            })
                        registerReceiver(
                            downloadReceiver,
                            IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                        )
                    }
                }
                .setIcon(
                    R.drawable.pdlg_icon_info, R.color.purple_500
                ) { }

            oldApkVersionDialog.setCancelable(false)
            oldApkVersionDialog.show()
        }

    }

    fun getLifecycleScope(): LifecycleCoroutineScope {
        return lifecycleScope
    }

    private fun getData() {
        lifecycleScope.launch(Dispatchers.IO) {
            getClientsByRute()
            getCobranzasByEmployee()
            getRoles()

            saveVentas()
            //saveCobranza()
            //saveAbonos()
            saveVisitas()
            //saveClientes()
            savePreciosEspeciales()
        }
    }

    private fun getUpdated() {
        lifecycleScope.launch(Dispatchers.IO) {
            GetAllDataInteractorImp().executeGetAllDataByDate(object:  GetAllDataInteractor.OnGetAllDataByDateListener {
                override fun onGetAllDataByDateSuccess() {
                    runOnUiThread {
                        dismissProgressDialog()
                    }
                }

                override fun onGetAllDataByDateError() {
                    runOnUiThread {
                        dismissProgressDialog()
                    }
                }
            })
        }
    }

    fun blockInput() {
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)*/
    }

    fun unblockInput() {
        //window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun dismissProgressDialog() {
        if (::progressDialog.isInitialized && progressDialog.isShowing) {
            if (window != null) {
                val decor = window.decorView
                if (decor.parent != null) {
                    progressDialog.dismiss()
                }
            }
        }
    }
}