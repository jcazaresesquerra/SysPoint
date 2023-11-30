package com.app.syspoint.ui.ventas

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.syspoint.R
import com.app.syspoint.analytics.EVENT
import com.app.syspoint.analytics.PARAM
import com.app.syspoint.databinding.ActivityVentasBinding
import com.app.syspoint.databinding.DialogCantidadVentaBinding
import com.app.syspoint.databinding.DialogRecordatorioBinding
import com.app.syspoint.databinding.DialogWarningBinding
import com.app.syspoint.databinding.EncabezadoVentasBinding
import com.app.syspoint.models.enums.SellType
import com.app.syspoint.models.sealed.SellViewState
import com.app.syspoint.repository.objectBox.dao.ClientDao
import com.app.syspoint.repository.objectBox.dao.ProductDao
import com.app.syspoint.repository.objectBox.dao.SellsModelDao
import com.app.syspoint.repository.objectBox.entities.SellModelBox
import com.app.syspoint.ui.precaptura.PrecaptureActivity
import com.app.syspoint.ui.products.activities.ScannerActivity
import com.app.syspoint.ui.templates.ViewPDFActivity
import com.app.syspoint.ui.ventas.adapter.AdapterItemsVenta
import com.app.syspoint.utils.Actividades
import com.app.syspoint.utils.PrettyDialog
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.click
import com.app.syspoint.utils.setGone
import com.app.syspoint.utils.setInvisible
import com.app.syspoint.utils.setVisible
import com.app.syspoint.viewmodel.sell.SellViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.perf.ktx.performance
import timber.log.Timber
import java.util.Locale


const val TAG = "VentasActivity"

class VentasActivity: AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityVentasBinding
    private lateinit var headerBinding: EncabezadoVentasBinding
    private lateinit var viewModel: SellViewModel
    private lateinit var adapter: AdapterItemsVenta
    private lateinit var progressDialog: ProgressDialog

    private var isBackPressed = false
    private var confirmPrecaptureClicked = false
    private var clientId: String? = null
    private var sellType: SellType = SellType.SIN_DEFINIR
    private lateinit var geocoder: Geocoder
    private var barcode = ""

    companion object {
        var articuloSeleccionado: String = "";
    }

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVentasBinding.inflate(layoutInflater)
        headerBinding = EncabezadoVentasBinding.bind(binding.ventasHeader.root)
        viewModel = ViewModelProvider(this)[SellViewModel::class.java]
        viewModel.sellViewState.observe(this, ::renderViewState)
        geocoder = Geocoder(this, Locale.getDefault())
        val myTrace = Firebase.performance.newTrace("init_ventas")
        myTrace.start()

        firebaseAnalytics = Firebase.analytics

        setContentView(binding.root)
        initToolBar()
        locationStart()

        clientId = intent.getStringExtra(Actividades.PARAM_1)

        //showLoading()
        viewModel.clearSells()
        viewModel.setUpSells()
        viewModel.fetchUpdatedClientsByRute()

        val data = SellsModelDao().list() as List<SellModelBox?>
        initRecyclerView(data)

        viewModel.updateSaldo(clientId)
        viewModel.setUpClientType(clientId)

        viewModel.setUpChargeByClient(clientId)

        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientByAccount(clientId.toString())
        if (clienteBean != null) {
            if (clienteBean.recordatorio.isNullOrEmpty() || clienteBean.recordatorio == "null") {
                viewModel.testLoadClientes(clientId.toString())
            } else {
                showScheduleDialog(
                    clienteBean.id,
                    clienteBean.recordatorio!!
                )
            }
            val saldoClient = if (clienteBean.matriz.isNullOrEmpty() || clienteBean.matriz == "null") {
                Utils.FDinero(clienteBean.saldo_credito)
            } else {
                val clientMatriz = clientDao.getClientByAccount(clienteBean.matriz)
                Utils.FDinero(clientMatriz?.saldo_credito ?: 0.0)
            }
            showClientInfo(clienteBean.nombre_comercial!!, clienteBean.cuenta!!, saldoClient)
        }
        //viewModel.loadClients(clientId)

        //viewModel.load(clientId)

        val keyReceiver = KeyBroadcast()
        val intentFilter = IntentFilter(Intent.ACTION_MEDIA_BUTTON)
        registerReceiver(keyReceiver, intentFilter)

        initControls()
        myTrace.stop()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_CANCELED) return

        if (resultCode === ScannerActivity.SCANNER_RESULT) {
            val barCode = data!!.getStringExtra(Actividades.PARAM_1)
            val parametros = HashMap<String, String?>()
            parametros[Actividades.PARAM_1] = barCode
            val productBox = ProductDao().getProductoByBarCode(barCode)
            if (productBox != null) {
                articuloSeleccionado = productBox.articulo!!
            }
            Actividades.getSingleton(this@VentasActivity, CantidadActivity::class.java)
                .muestraActividadForResultAndParams(Actividades.PARAM_INT_1, parametros)
            return
        }

        val cantidad = data!!.getStringExtra(Actividades.PARAM_1)
        val articulo = if (!articuloSeleccionado.isNullOrEmpty()) articuloSeleccionado else data.getStringExtra(Actividades.PARAM_2)

        val productDao = ProductDao()
        val productoBean = productDao.getProductoByArticulo(articulo)

        if (productoBean == null) {
            Timber.tag(TAG).d("Ha ocurrido un error, intente nuevamente onActivityResult")
            articuloSeleccionado = ""
            return
        }

        //Validamos si existe el producto
        if (viewModel.validaProducto(productoBean.articulo!!)) {
            showProductExists()
            articuloSeleccionado = ""
            return
        }

        if (cantidad.isNullOrEmpty()) {
            Timber.tag(TAG).d("Ha ocurrido un error, intente nuevamente onActivityResult")
            articuloSeleccionado = ""
            return
        }

        val cantidadVendida = cantidad.toInt()

        //Validamos los datos del cliente
        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientByAccount(clientId.toString())

        //Validamos si hay precio especial del cliente
        val specialPricesDao = com.app.syspoint.repository.objectBox.dao.SpecialPricesDao()
        val preciosEspecialesBeanspecialPricesBox =
            specialPricesDao.getPrecioEspeciaPorCliente(productoBean.articulo, clienteBean!!.cuenta)

        val preciosEspeciales = viewModel.partidasEspeciales.value?.filter {
                precioEspecialBean -> precioEspecialBean?.articulo == productoBean.articulo
                && precioEspecialBean?.active == true
        }
        val precioEspacial = if (preciosEspeciales.isNullOrEmpty()) preciosEspecialesBeanspecialPricesBox else preciosEspeciales[0]

        val data = viewModel.addItem(
            productoBean.articulo!!,
            productoBean.descripcion!!,
            precioEspacial?.precio ?: productoBean.precio,
            productoBean.iva,
            cantidadVendida
        )

        articuloSeleccionado = ""
        refreshRecyclerView(data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart()
                return
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_ventas, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (!isBackPressed) {
                    isBackPressed = true
                    showSureEndPrecapture()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_5 || event?.keyCode == KeyEvent.KEYCODE_5) {
            val code = event?.scanCode
            val barcode = event?.unicodeChar

            if (event?.keyCode == KeyEvent.KEYCODE_ENTER) {
                Toast.makeText(
                    applicationContext,
                    "barcode1--->>>$barcode   ------  barcode2---$code>>>$barcode", Toast.LENGTH_LONG
                ).show()

            }
            return true
        }


        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!isBackPressed) {
                isBackPressed = true
                showSureEndPrecapture()
            }
            true
        } else super.onKeyDown(keyCode, event)
    }


    private fun renderViewState(sellViewState: SellViewState) {
        when (sellViewState) {
            is SellViewState.LoadingStart -> {
                //showLoading()
            }
            is SellViewState.LoadingFinish -> {
                hideLoading()
            }
            is SellViewState.NotInternetConnection -> {
                hideLoading()
                //showDialogNotConnectionInternet()
            }
            is SellViewState.SellsLoaded -> {
                initRecyclerView(sellViewState.data)
            }
            is SellViewState.SellsRefresh -> {
                refreshRecyclerView(sellViewState.data)
            }
            is SellViewState.ClientsLoaded -> {
                showClientInfo(sellViewState.clientName, sellViewState.account, sellViewState.saldoCredito)
            }
            is SellViewState.ComputedImports -> {
                showImports(sellViewState.totalFormat, sellViewState.subtotalFormat, sellViewState.importFormat)
            }
            is SellViewState.ItemAdded -> {
                refreshRecyclerView(sellViewState.data)
            }
            is SellViewState.ChargeByClientLoaded -> {
                showCharge(sellViewState.account, sellViewState.saldo)
            }
            is SellViewState.PrecatureParamsCreated -> {
                if (barcode.isEmpty()) {
                    Utils.addActivity2Stack(this)
                    Actividades.getSingleton(this@VentasActivity, PrecaptureActivity::class.java)
                        .muestraActividad(sellViewState.params)
                    binding.imgBtnFinishVisita.isEnabled = true
                }
            }
            is SellViewState.ClientType -> {
                showClientType(sellViewState.clientType)
            }
            is SellViewState.PrecatureFinished -> {
                val intent1 = Intent(this@VentasActivity, ViewPDFActivity::class.java)
                intent1.putExtra("ticket", sellViewState.ticket)
                intent1.putExtra("venta", sellViewState.sellId)
                intent1.putExtra("clienteID", sellViewState.clientId)
                intent1.putExtra("account", sellViewState.account)

                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent1)
                binding.imgBtnFinishSale.isEnabled = true
                confirmPrecaptureClicked = false
            }
            is SellViewState.NotEnoughCredit -> {
                binding.imgBtnFinishSale.isEnabled = true
                showNotEnoughCredit(sellViewState.saldo, sellViewState.isMatriz)
            }
            is SellViewState.FinishPreSell -> {
                showFinishPreSell()
            }
            is SellViewState.ShowScheduler -> {
                showScheduleDialog(sellViewState.clientId, sellViewState.recordatorio)
            }
        }
    }

    private fun initToolBar() {
        binding.toolbarVentas.title = "Venta"
        setSupportActionBar(binding.toolbarVentas)

        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.purple_700)
    }

    private fun initControls() {
        if (viewModel.existenPartidas()) {
            binding.imgBtnFinishSale.setVisible()
            binding.imgBtnFinishVisita.setGone()
        } else {
            binding.imgBtnFinishSale.setGone()
            binding.imgBtnFinishVisita.setVisible()
        }

        binding.imgBtnFinishSale click {
            if (binding.imgBtnFinishSale.isEnabled) {

                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.SCREEN_NAME, TAG)
                params.putString(FirebaseAnalytics.Param.VALUE, PARAM.BUTTON_FINISH_SELL_CLICK.value)
                firebaseAnalytics.logEvent(EVENT.SELL.value, params)

                binding.imgBtnFinishSale.isEnabled = false
                if (viewModel.existenPartidas()) {
                    // Not selected sell type
                    if ((!headerBinding.radioContado.isChecked && !headerBinding.radioCredito.isChecked) ||
                        sellType == SellType.SIN_DEFINIR) {
                        showSelectSellType()
                    } else if (sellType == SellType.CREDITO) {
                        val subtotal = headerBinding.textViewSubtotalVentaView.text.toString()
                        val import = headerBinding.textViewImpuestoVentaView.text.toString()
                        viewModel.checkUserCredit(clientId.toString(), sellType, subtotal, import)
                        //binding.imgBtnFinishSale.isEnabled = true
                    } else if (sellType == SellType.CONTADO) {
                        showFinishPreSell()
                    }
                } else {
                    // Product Not selected
                    showProductNotSelected()
                }
            }
        }

        binding.imgBtnFinishVisita click {
            if (binding.imgBtnFinishVisita.isEnabled) {

                val myTrace = Firebase.performance.newTrace(TAG)
                myTrace.start()
                myTrace.incrementMetric(PARAM.BUTTON_FINISH_SELL_CLICK.value, 1)

                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.SCREEN_NAME, TAG)
                params.putString(FirebaseAnalytics.Param.VALUE, PARAM.BUTTON_FINISH_VISIT_CLICK.value)
                firebaseAnalytics.logEvent(EVENT.SELL.value, params)

                binding.imgBtnFinishVisita.isEnabled = false
                viewModel.createPrecatureParams(clientId.toString())
                myTrace.stop()
            }
        }

        headerBinding.fbAddProductos click {
            if (headerBinding.fbAddProductos.isEnabled) {
                headerBinding.fbAddProductos.isEnabled = false

                val myTrace = Firebase.performance.newTrace(TAG)
                myTrace.start()
                myTrace.incrementMetric(PARAM.BUTTON_FINISH_VISIT_CLICK.value, 1)

                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.SCREEN_NAME, TAG)
                params.putString(FirebaseAnalytics.Param.VALUE, PARAM.BUTTON_ADD_PRODUCT_CLICK.value)
                firebaseAnalytics.logEvent(EVENT.SELL.value, params)

                Actividades.getSingleton(this@VentasActivity, ListaProductosActivity::class.java)
                    .muestraActividadForResult(Actividades.PARAM_INT_1)
                headerBinding.fbAddProductos.isEnabled = true
                myTrace.stop()
            }
        }

        headerBinding.fbAddBarProductos click {
            if (headerBinding.fbAddBarProductos.isEnabled) {
                headerBinding.fbAddBarProductos.isEnabled = false
                startActivityForResult( Intent(this@VentasActivity, ScannerActivity::class.java), 0x2)
                headerBinding.fbAddBarProductos.isEnabled = true
            }
        }
    }

    private fun refreshRecyclerView(data: List<SellModelBox?>) {
        adapter.setData(data)

        binding.recyclerViewVentas.adapter = adapter

        if (data.isEmpty()) {
            binding.emptyStateContainer.setVisible()
            binding.recyclerViewVentas.setInvisible()
        } else {
            binding.emptyStateContainer.setInvisible()
            binding.recyclerViewVentas.setVisible()
        }
        viewModel.computeImports()
    }

    private fun initRecyclerView(data: List<SellModelBox?>) {
        binding.recyclerViewVentas.setHasFixedSize(true)

        val manager = LinearLayoutManager(this)
        binding.recyclerViewVentas.layoutManager = manager

        adapter = AdapterItemsVenta(data, object: AdapterItemsVenta.OnItemLongClickListener {
            override fun onItemLongClicked(sell: SellModelBox): Boolean {

                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.SCREEN_NAME, TAG)
                params.putString(FirebaseAnalytics.Param.VALUE, PARAM.DELETE_PRODUCT_CLICK.value)
                firebaseAnalytics.logEvent(EVENT.SELL.value, params)

                val dialog = PrettyDialog(this@VentasActivity)
                dialog.setTitle("Eliminar")
                    .setTitleColor(R.color.purple_500)
                    .setMessage("Desea eliminar el articulo ${sell.descripcion}")
                    .setMessageColor(R.color.purple_700)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500) { dialog.dismiss() }
                    .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {

                        val params = Bundle()
                        params.putString(FirebaseAnalytics.Param.SCREEN_NAME, TAG)
                        params.putString(FirebaseAnalytics.Param.VALUE, PARAM.DELETE_PRODUCT_SUCCESS_CLICK.value)
                        params.putString(FirebaseAnalytics.Param.ITEM_ID, sell.articulo)
                        firebaseAnalytics.logEvent(EVENT.SELL.value, params)

                        val dao = SellsModelDao()
                        dao.delete(sell.id)
                        viewModel.refreshSellData()
                        dialog.dismiss()
                    }.addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900) { dialog.dismiss() }
                dialog.setCancelable(false)
                dialog.show()
                return false
            }
        }, object : AdapterItemsVenta.OnItemClickListener {
                override fun onItemClick(sell: SellModelBox) {
                    val dialogo = Dialog(this@VentasActivity)

                    val dialogBinding = DialogCantidadVentaBinding
                        .inflate(LayoutInflater.from(this@VentasActivity), binding.root, false)
                    dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE) //before
                    dialogo.setContentView(dialogBinding.root)
                    dialogBinding.buttonSeleccionarCantidadVentaDialog click {
                        val cantidad = dialogBinding.edittextCantidadVentaSeleccionadaDialog.text.toString()
                        if (cantidad.isNotEmpty()) {
                            val params = Bundle()
                            params.putString(FirebaseAnalytics.Param.SCREEN_NAME, TAG)
                            params.putString(FirebaseAnalytics.Param.VALUE, PARAM.ADD_PRODUCT_SUCCESS_CLICK.value)
                            params.putString(FirebaseAnalytics.Param.ITEM_ID, sell.articulo)
                            params.putString(FirebaseAnalytics.Param.QUANTITY, cantidad)
                            firebaseAnalytics.logEvent(EVENT.SELL.value, params)

                            val cantidadVenta = cantidad.toInt()
                            if (cantidadVenta == 0) {
                                showQuantityErrorDialog()
                            } else {
                                val dao = SellsModelDao()
                                sell.cantidad = cantidadVenta
                                dao.insert(sell)
                                viewModel.refreshSellData()
                                dialogo.dismiss()
                            }
                        }
                    }
                    dialogo.show()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(dialogBinding.edittextCantidadVentaSeleccionadaDialog.windowToken, 0)
                    dialogBinding.edittextCantidadVentaSeleccionadaDialog.requestFocus()
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
                }
            })

        viewModel.refreshSellData()
    }

    fun onRadioButtonClicked(view: View) {
        val checked = (view as RadioButton).isChecked
        when (view.getId()) {
            R.id.radio_contado -> if (checked) sellType = SellType.CONTADO
            R.id.radio_credito -> if (checked) sellType = SellType.CREDITO
        }
    }

    //Apartir de aqui empezamos a obtener la direciones y coordenadas
    private fun locationStart() {
        val mlocManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) {
            /* permite realizar algunas configuraciones del movil para el permiso */
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            Timber.tag(TAG).d("Error, Location permissions not granted, Ventas")
            return
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, this)
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }


    override fun onLocationChanged(location: Location) {
        /*Geocodificacion- Proceso de conversión de coordenadas a direccion*/
        /*try {
            Thread {
                if (location.latitude != 0.0 && location.longitude != 0.0) {
                    try {
                        val list = geocoder.getFromLocation(
                                location.latitude, location.longitude, 1)
                        if (list!!.isNotEmpty()) {
                            viewModel.setLocation(list[0].latitude, list[0].longitude)
                        }
                    } catch (e: Exception) {
                        runOnUiThread {
                            Timber.tag(TAG).d(
                                "Ha ocurrido un error, intente nuevamente onLocationChanged"
                            )
                        }
                        e.printStackTrace()
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    override fun onProviderDisabled(provider: String) {
        // Este metodo se ejecuta cuando el GPS es desactivado
         //Toast.makeText(applicationContext, "GPS Desactivado", Toast.LENGTH_SHORT).show()
    }

    override fun onProviderEnabled(provider: String) {
        // Este metodo se ejecuta cuando el GPS es activado
        //Toast.makeText(applicationContext, "GPS Activado", Toast.LENGTH_SHORT).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        when (status) {
            LocationProvider.AVAILABLE -> Timber.tag(TAG).d( "LocationProvider.AVAILABLE")
            LocationProvider.OUT_OF_SERVICE -> Timber.tag(TAG).d( "LocationProvider.OUT_OF_SERVICE")
            LocationProvider.TEMPORARILY_UNAVAILABLE -> Timber.tag(TAG).d(
                "LocationProvider.TEMPORARILY_UNAVAILABLE"
            )
        }
    }

    private fun showClientInfo(clientName: String, account: String, saldoCredito: String) {
        headerBinding.textViewClienteNombreVentaView.text = clientName
        headerBinding.textViewClienteVentaView.text = "$account(${saldoCredito.replace(" ", "")})"
        hideLoading()
    }

    private fun showImports(totalFormat: String, subtotalFormat: String, importFormat: String) {
        //Contie el SubTotal de la venta
        headerBinding.textViewSubtotalVentaView.text = subtotalFormat

        //Contiene el total de los impuesto
        headerBinding.textViewImpuestoVentaView.text = importFormat

        //Contiene el total de la venta
        headerBinding.textViewTotalVentaView.text = totalFormat

        if (viewModel.existenPartidas()) {
            binding.imgBtnFinishSale.setVisible()
            binding.imgBtnFinishVisita.setGone()
        } else {
            binding.imgBtnFinishSale.setGone()
            binding.imgBtnFinishVisita.setVisible()
        }
    }

    private fun showCharge(account: String, saldo: Double) {
        headerBinding.textViewClienteVentaView.text = account + "(" + Utils.FDinero(saldo) + ")"
    }

    private fun showClientType(sellType: SellType) {
        this.sellType = sellType
        headerBinding.radioContado.setVisible()

        if (sellType == SellType.CREDITO) {
            headerBinding.radioCredito.setVisible()
        } else {
            headerBinding.radioCredito.setInvisible()
        }
    }

    /*******************************   DIALOGS ********************************/

    private fun showNotEnoughCredit(saldo_disponible: Double, isMatrizCredit: Boolean) {
        val dialog = PrettyDialog(this@VentasActivity)
        dialog.setTitle("Crédito insuficiente")
            .setTitleColor(R.color.purple_500)
            .setMessage(
                if (isMatrizCredit )"La matriz solo cuenta con un saldo " + Utils.FDinero(saldo_disponible) + "  para terminar la venta a crédito  "
                else "Este cliente solo cuenta con " + Utils.FDinero(saldo_disponible) + "  de credito disponible para ventas a credito"
            )
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) { dialog.dismiss() }
            .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800) { dialog.dismiss() }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showSelectSellType() {
        val dialogo = PrettyDialog(this@VentasActivity)
        dialogo.setTitle("Tipo de venta")
            .setTitleColor(R.color.purple_500)
            .setMessage("Debe de seleccionar el tipo de venta")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) {
                dialogo.dismiss()
                binding.imgBtnFinishSale.isEnabled = true
            }.addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800) {
                dialogo.dismiss()
                binding.imgBtnFinishSale.isEnabled = true
            }
        dialogo.setCancelable(false)
        dialogo.show()
    }

    private fun showFinishPreSell() {
        val dialog = PrettyDialog(this@VentasActivity)
        dialog.setTitle("Terminar")
            .setTitleColor(R.color.purple_500)
            .setMessage("¿Desea terminal la venta?")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.ic_save_white, R.color.purple_500) {
                dialog.dismiss()
                binding.imgBtnFinishSale.isEnabled = true
            }
            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {
                binding.imgBtnFinishSale.isEnabled = true
                if (!confirmPrecaptureClicked) {
                    confirmPrecaptureClicked = true

                    val params = Bundle()
                    params.putString(FirebaseAnalytics.Param.SCREEN_NAME, TAG)
                    params.putString(FirebaseAnalytics.Param.VALUE, PARAM.BUTTON_CONFIRM_FINISH_SELL_CLICK.value)
                    firebaseAnalytics.logEvent(EVENT.SELL.value, params)

                    Utils.addActivity2Stack(this@VentasActivity)

                    val subtotal = headerBinding.textViewSubtotalVentaView.text.toString()
                    val import = headerBinding.textViewImpuestoVentaView.text.toString()
                    viewModel.finishPrecature(clientId, sellType, subtotal, import)
                }
            }
            .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900) {
                dialog.dismiss()
                binding.imgBtnFinishSale.isEnabled = true
            }

        dialog.setCancelable(false)
        try {
            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showProductNotSelected() {
        val dialog = PrettyDialog(this@VentasActivity)
        dialog.setTitle("Finalizar")
            .setTitleColor(R.color.purple_500)
            .setMessage("Debe de agregar un producto para finalizar la venta")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) {
                dialog.dismiss()
                binding.imgBtnFinishSale.isEnabled = true

            }.addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800) {
                dialog.dismiss()
                binding.imgBtnFinishSale.isEnabled = true
            }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showProductExists() {
        val dialogo = PrettyDialog(this@VentasActivity)
        dialogo.setTitle("Existe")
            .setTitleColor(R.color.purple_500)
            .setMessage("El producto ingresado ya existe en la venta")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) {
                dialogo.dismiss()
            }.addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800) {
                dialogo.dismiss()
            }
        dialogo.setCancelable(false)
        dialogo.show()
    }

    private fun showSureEndPrecapture() {
        val dialog = PrettyDialog(this@VentasActivity)
        dialog.setTitle("Salir")
            .setTitleColor(R.color.purple_500)
            .setMessage("Desea salir de la venta")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500) {
                dialog.dismiss()
                isBackPressed = false
            }.addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {
                viewModel.clearSells()


                val params = Bundle()
                params.putString(FirebaseAnalytics.Param.SCREEN_NAME, TAG)
                params.putString(FirebaseAnalytics.Param.VALUE, PARAM.BUTTON_EXIT_SELL_CLICK.value)
                firebaseAnalytics.logEvent(EVENT.SELL.value, params)

                finish()
                dialog.dismiss()
                isBackPressed = false
            }.addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900) {
                dialog.dismiss()
                isBackPressed = false
            }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showQuantityErrorDialog() {
        val dialog = PrettyDialog(this@VentasActivity)
        dialog.setTitle("Precio")
            .setTitleColor(R.color.purple_500)
            .setMessage("El precio debe de ser mayor a cero")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) { dialog.dismiss() }
            .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800) { dialog.dismiss() }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showDialogNotConnectionInternet() {
        val dialogBinding = DialogWarningBinding.inflate(LayoutInflater.from(this@VentasActivity), binding.root, false)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(true)


        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogBinding.btClose click {
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun showScheduleDialog(clientId: Long, recordatorio: String) {
        val dialogBinding = DialogRecordatorioBinding.inflate(
            LayoutInflater.from(this@VentasActivity), binding.root, false)

        val dialog = Dialog(this@VentasActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialogBinding.etRecordatorio.isEnabled = false
        dialogBinding.etRecordatorio.setText(recordatorio)

        dialogBinding.btCancel click {
            dialog.dismiss()
        }
        dialogBinding.btSubmit click {
            val params = Bundle()
            params.putString(FirebaseAnalytics.Param.SCREEN_NAME, TAG)
            params.putString(FirebaseAnalytics.Param.VALUE, PARAM.BUTTON_SUBMIT_SCHEDULE.value)
            firebaseAnalytics.logEvent(EVENT.SELL.value, params)

            viewModel.submitSchedule(clientId)
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.attributes = lp
    }

    private fun showLoading() {
        try {
            progressDialog = ProgressDialog(this@VentasActivity)
            progressDialog.setMessage("Espere un momento")
            progressDialog.setCancelable(false)
            if (!progressDialog.isShowing)
                progressDialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun hideLoading() {
        if (::progressDialog.isInitialized) {
            progressDialog.dismiss()
        }
    }

    class KeyBroadcast: BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (Intent.ACTION_MEDIA_BUTTON == intent!!.action) {
                val event: KeyEvent? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
                } else {
                    intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
                }
                if (event != null) {
                    Log.d("KEY_EVENT", event.keyCode.toString())
                    if (KeyEvent.KEYCODE_MEDIA_PLAY === event!!.keyCode) {

                    }
                }
            }
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode != KeyEvent.KEYCODE_ENTER)
            barcode += event.unicodeChar.toChar()

        if (event.keyCode == KeyEvent.KEYCODE_ENTER && barcode.isNotEmpty()) {
            barcode = removeRepeatedCharacters(barcode)

            val productBox = ProductDao().getProductoByBarCode(barcode)
            if (productBox != null) {
                val parametros = HashMap<String, String?>()
                parametros[Actividades.PARAM_1] = barcode
                articuloSeleccionado = productBox.articulo!!
                Actividades.getSingleton(this@VentasActivity, CantidadActivity::class.java)
                    .muestraActividadForResultAndParams(Actividades.PARAM_INT_1, parametros)
            }
            barcode = ""
        }

        return true
    }

    fun removeRepeatedCharacters(input: String): String {
        return input.filterIndexed { index, _ -> index % 2 == 1 }
    }

}