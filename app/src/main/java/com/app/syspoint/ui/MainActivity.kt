package com.app.syspoint.ui

import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
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
import com.app.syspoint.interactor.prices.PriceInteractor.SendPricesListener
import com.app.syspoint.interactor.prices.PriceInteractorImp
import com.app.syspoint.interactor.roles.RolInteractor.OnGetAllRolesListener
import com.app.syspoint.interactor.roles.RolInteractorImp
import com.app.syspoint.interactor.token.TokenInteractor
import com.app.syspoint.interactor.token.TokenInteractorImpl
import com.app.syspoint.interactor.visit.VisitInteractor.OnSaveVisitListener
import com.app.syspoint.interactor.visit.VisitInteractorImp
import com.app.syspoint.models.*
import com.app.syspoint.models.sealed.LoginViewState
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.database.dao.*
import com.app.syspoint.repository.request.http.Servicio.ResponseOnError
import com.app.syspoint.repository.request.http.Servicio.ResponseOnSuccess
import com.app.syspoint.repository.request.http.SincVentas
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.utils.PrettyDialog
import com.app.syspoint.utils.Utils
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity: BaseActivity() {

    companion object {
        @JvmStatic
        var apikey: String? = null
        const val IS_ADMIN = "is_admin"
    }

    private var mAppBarConfiguration: AppBarConfiguration? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    // receiver
    private lateinit var mNetworkChangeReceiver: NetworkChangeReceiver

    private var isConnected = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        setUpLogo()

        val isAdmin = intent.getBooleanExtra(IS_ADMIN, false)

        var vendedoresBean = AppBundle.getUserBean()
        if (vendedoresBean == null) vendedoresBean = CacheInteractor().getSeller()
        val identificador = if (vendedoresBean != null) vendedoresBean.getIdentificador() else ""

        val rolesDao = RolesDao()
        val productsRolesBean = rolesDao.getRolByEmpleado(identificador, "Productos")
        val employeesRolesBean = rolesDao.getRolByEmpleado(identificador, "Empleados")

        val productsActive = productsRolesBean?.active ?: false
        val employeesActive = employeesRolesBean?.active ?: false

        binding.navView.apply {
            menu.clear()
            inflateMenu(R.menu.activity_main_drawer)
        }

        configureMenu(isAdmin, employeesActive, productsActive)

        mAppBarConfiguration =
            AppBarConfiguration.Builder(buildMenuSet(isAdmin, employeesActive, productsActive))
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

        validateToken()
        registerNetworkBroadcastForNougat()
        //startForegroundService(Intent(this, UpdateDataService::class.java))

    }

    private fun validateToken() {
        TokenInteractorImpl().executeGetToken(object: TokenInteractor.OnGetTokenListener {
            override fun onGetTokenSuccess(token: String?) {
                getUpdates()
            }

            override fun onGetTokenError() {
                showVersionErrorDialog("Su versión no esta soportada, por favor, actualice su aplicación")
                checkAppVersionInStore()
            }
        })
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

    private fun configureMenu(isAdmin: Boolean, employeesActive: Boolean, productsActive: Boolean) {
        val menu = binding.navView.menu
        menu.findItem(R.id.nav_home).isVisible = true
        menu.findItem(R.id.nav_producto).isVisible = true
        menu.findItem(R.id.nav_empleado).isVisible = employeesActive
        menu.findItem(R.id.nav_producto).isVisible = productsActive
        menu.findItem(R.id.nav_cliente).isVisible = true
        menu.findItem(R.id.nav_historial).isVisible = true
        menu.findItem(R.id.nav_inventario).isVisible = isAdmin
        menu.findItem(R.id.nav_cobranza).isVisible = isAdmin
    }

    private fun buildMenuSet(isAdmin: Boolean, employeesActive: Boolean, productsActive: Boolean): Set<Int> {
        //Obtiene el nombre del vendedor
        val menuSet = mutableSetOf(R.id.nav_home, R.id.nav_ruta)

        if (employeesActive) menuSet.add(R.id.nav_empleado)
        if (productsActive) menuSet.add(R.id.nav_producto)

        menuSet.add(R.id.nav_cliente)
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
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Espere un momento")
        progressDialog.setCancelable(false)
        progressDialog.show()

        Handler().postDelayed({
            NetworkStateTask { connected: Boolean ->
                progressDialog.dismiss()
                if (connected) {
                    progressDialog.setMessage("Obteniendo actualizaciones...");

                    progressDialog.show();
                    GetAllDataInteractorImp().executeGetAllDataByDate(object:  GetAllDataInteractor.OnGetAllDataByDateListener {
                        override fun onGetAllDataByDateSuccess() {
                            progressDialog.dismiss()
                        }

                        override fun onGetAllDataByDateError() {
                            progressDialog.dismiss()
                        }
                    })
                }
            }.execute()}
            ,100)
    }


    private fun registerNetworkBroadcastForNougat() {
        mNetworkChangeReceiver =
            NetworkChangeReceiver(object : ConnectionNetworkListener {
                override fun onConnected() {
                    isConnected = true
                    Handler().postDelayed({
                        NetworkStateTask { connected: Boolean ->
                            if (connected) {
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
                        }.execute()
                    }, 100)
                }

                override fun onDisconnected() {
                    isConnected = false
                }
            })
        registerReceiver(
            mNetworkChangeReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
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

    private fun saveVisitas() {
        val visitsDao = VisitsDao()
        val visitasBeanListBean = visitsDao.getVisitsByCurrentDay(Utils.fechaActual())
        val clientDao = ClientDao()
        var vendedoresBean = AppBundle.getUserBean()
        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }
        val visitList: MutableList<Visit> = ArrayList()
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

    fun saveCobranza() {
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
            cobranza.updatedAt = item.updatedAt
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


    private fun savePreciosEspeciales() {

        //Instancia la base de datos
        val dao = SpecialPricesDao()

        //Contiene la lista de precios de la db local
        var listaDB: List<PreciosEspecialesBean> = ArrayList()

        //Obtenemos la lista por id cliente
        listaDB = dao.getPreciosBydate(Utils.fechaActual())


        //Contiene la lista de lo que se envia al servidor
        val listaPreciosServidor: MutableList<Price> = ArrayList()

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
        PriceInteractorImp().executeSendPrices(listaPreciosServidor, object : SendPricesListener {
            override fun onSendPricesSuccess() {
                //Toast.makeText(requireActivity(), "Sincronizacion de lista de precios exitosa", Toast.LENGTH_LONG).show();
            }

            override fun onSendPricesError() {
                //Toast.makeText(requireActivity(), "Error al sincronizar la lista de precios intente mas tarde", Toast.LENGTH_LONG).show();
            }
        })
    }

    fun saveClientes() {
        val clientDao = ClientDao()
        val clientListDB = clientDao.getClientsByDay(Utils.fechaActual())
        val clientList: MutableList<Client> = ArrayList()
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
            client.updatedAt = item.updatedAt
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

    private fun testLoadEmpleado(id: String) {
        val employeeDao = EmployeeDao()
        var listaEmpleadosDB: List<EmpleadoBean> = ArrayList()
        listaEmpleadosDB = employeeDao.getEmployeeById(id)
        val listEmpleados: MutableList<Employee> = ArrayList()
        for (item in listaEmpleadosDB) {
            val empleado = Employee()
            empleado.nombre = item.getNombre()
            if (item.getDireccion().isEmpty()) {
                empleado.direccion = "-"
            } else {
                empleado.direccion = item.getDireccion()
            }
            empleado.email = item.getEmail()
            if (item.getTelefono().isEmpty()) {
                empleado.telefono = "-"
            } else {
                empleado.telefono = item.getTelefono()
            }
            if (item.getFecha_nacimiento().isEmpty()) {
                empleado.fechaNacimiento = "-"
            } else {
                empleado.fechaNacimiento = item.getFecha_nacimiento()
            }
            if (item.getFecha_ingreso().isEmpty()) {
                empleado.fechaIngreso = "-"
            } else {
                empleado.fechaIngreso = item.getFecha_ingreso()
            }
            empleado.contrasenia = item.getContrasenia()
            empleado.identificador = item.getIdentificador()
            empleado.status = if (item.getStatus()) 1 else 0
            if (item.getPath_image() == null || item.getPath_image().isEmpty()) {
                empleado.pathImage = ""
            } else {
                empleado.pathImage = item.getPath_image()
            }
            if (!item.rute.isEmpty()) {
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
        val paymentDao = PaymentDao()
        val cobranzaBeanList = paymentDao.getAbonosFechaActual(Utils.fechaActual())
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
            cobranza.updatedAt = item.updatedAt
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

    private fun getCobranzasByEmployee() {
        val vendedoresBean = AppBundle.getUserBean()
        if (vendedoresBean != null) {
            ChargeInteractorImp().executeGetCharge(object : OnGetChargeListener {
                override fun onGetChargeSuccess(chargeList: List<CobranzaBean>) {
                    saveCobranza()
                    saveAbonos()                }

                override fun onGetChargeError() {
                    saveCobranza()
                    saveAbonos()                }

            });
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

    private fun getClientsByRute() {
        val routingDao = RoutingDao()
        val ruteoBean = routingDao.getRutaEstablecida()
        if (ruteoBean != null) {
            val vendedoresBean = AppBundle.getUserBean()
            val ruta = if (ruteoBean.ruta != null && ruteoBean.ruta.isNotEmpty()
            ) ruteoBean.ruta else vendedoresBean.getRute()

            ClientInteractorImp().executeGetAllClientsByDate(
                ruta,
                ruteoBean.dia,
                object : GetAllClientsListener {
                    override fun onGetAllClientsSuccess(clientList: List<ClienteBean>) {
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

    private fun getRoles() {
        RolInteractorImp().executeGetAllRoles(object : OnGetAllRolesListener {
            override fun onGetAllRolesSuccess(roles: List<RolesBean>) {

            }

            override fun onGetAllRolesError() {
                //progresshide()
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al obtener roles", Toast.LENGTH_SHORT).show();
            }
        })
    }

    private fun showVersionErrorDialog(message: String) {
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

    private fun checkAppVersionInStore() {
        val storage = FirebaseStorage.getInstance()        // From our app
        val storageRef = storage.reference
        // With an initial file path and name
        val pathReference = storageRef.child("images/javasampleapproach.jpg")
        // To a file from a Google Cloud Storage URI
        val gsReference = storage.getReferenceFromUrl("gs://javasampleapproach-storage.appspot.com/images/javasampleapproach.jpg")
        // From an HTTPS URL val httpsReference = storage.getReferenceFromUrl(“https://firebasestorage.googleapis.com/v0/b/javasampleapproach-storage.appspot.com/o/images%2Fjavasampleapproach.jpg”)
    }
}