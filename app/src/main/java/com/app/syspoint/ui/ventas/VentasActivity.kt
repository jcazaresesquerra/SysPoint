package com.app.syspoint.ui.ventas

import android.Manifest
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.syspoint.R
import com.app.syspoint.databinding.*
import com.app.syspoint.models.enum.SellType
import com.app.syspoint.models.sealed.SellViewState
import com.app.syspoint.repository.database.bean.VentasModelBean
import com.app.syspoint.repository.database.dao.ClientDao
import com.app.syspoint.repository.database.dao.ProductDao
import com.app.syspoint.repository.database.dao.SellsModelDao
import com.app.syspoint.repository.database.dao.SpecialPricesDao
import com.app.syspoint.ui.precaptura.PrecaptureActivity
import com.app.syspoint.ui.templates.ViewPDFActivity
import com.app.syspoint.ui.ventas.adapter.AdapterItemsVenta
import com.app.syspoint.utils.*
import com.app.syspoint.viewmodel.sell.SellViewModel
import java.util.*

class VentasActivity: AppCompatActivity(), LocationListener {

    private lateinit var binding: ActivityVentasBinding
    private lateinit var headerBinding: EncabezadoVentasBinding
    private lateinit var viewModel: SellViewModel
    private lateinit var adapter: AdapterItemsVenta
    private lateinit var progressDialog: ProgressDialog

    private var isBackPressed = false
    private var confirmPrecaptureClicked = false
    private lateinit var clientId: String
    private var sellType: SellType = SellType.SIN_DEFINIR
    private lateinit var geocoder: Geocoder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVentasBinding.inflate(layoutInflater)
        headerBinding = EncabezadoVentasBinding.bind(binding.ventasHeader.root)
        viewModel = ViewModelProvider(this)[SellViewModel::class.java]
        viewModel.sellViewState.observe(this, ::renderViewState)
        geocoder = Geocoder(this, Locale.getDefault())
        setContentView(binding.root)
        initToolBar()
        locationStart()

        clientId = intent.getStringExtra(Actividades.PARAM_1) ?: ""

        //showLoading()
        viewModel.clearSells()
        viewModel.setUpSells()

        val data = SellsModelDao().list() as List<VentasModelBean?>
        initRecyclerView(data)

        viewModel.updateSaldo(clientId)
        viewModel.setUpClientType(clientId)

        viewModel.setUpChargeByClient(clientId)

        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientByAccount(clientId)
        if (clienteBean != null) {
            if (clienteBean.recordatorio.isNullOrEmpty() || clienteBean.recordatorio == "null") {
                viewModel.testLoadClientes(clientId)
            } else {
                showScheduleDialog(
                    clienteBean.id.toString(),
                    clienteBean.recordatorio
                )
            }
            val saldoClient = if (clienteBean.matriz.isNullOrEmpty() || clienteBean.matriz == "null") {
                Utils.FDinero(clienteBean.saldo_credito)
            } else {
                val clientMatriz = clientDao.getClientByAccount(clienteBean.matriz)
                Utils.FDinero(clientMatriz?.saldo_credito ?: 0.0)
            }
            showClientInfo(clienteBean.nombre_comercial, clienteBean.cuenta, saldoClient)
        }
        //viewModel.loadClients(clientId)

        //viewModel.load(clientId)
        initControls()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_CANCELED) return

        val cantidad = data!!.getStringExtra(Actividades.PARAM_1)
        val articulo = data.getStringExtra(Actividades.PARAM_2)

        val productDao = ProductDao()
        val productoBean = productDao.getProductoByArticulo(articulo)

        if (productoBean == null) {
            Log.d("SysPoint", "Ha ocurrido un error, intente nuevamente onActivityResult")
            return
        }

        //Validamos si existe el producto
        if (viewModel.validaProducto(productoBean.articulo)) {
            showProductExists()
            return
        }

        if (cantidad.isNullOrEmpty()) {
            Log.d("SysPoint", "Ha ocurrido un error, intente nuevamente onActivityResult")
            return
        }

        val cantidadVendida = cantidad.toInt()

        //Validamos los datos del cliente
        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientByAccount(clientId)

        //Validamos si hay precio especial del cliente
        val specialPricesDao = SpecialPricesDao()
        val preciosEspecialesBean =
            specialPricesDao.getPrecioEspeciaPorCliente(productoBean.articulo, clienteBean!!.cuenta)

        val preciosEspeciales = viewModel.partidasEspeciales.value?.filter {
                precioEspecialBean -> precioEspecialBean?.articulo == productoBean.articulo
                && precioEspecialBean?.active == true
        }
        val precioEspacial = if (preciosEspeciales.isNullOrEmpty()) preciosEspecialesBean else preciosEspeciales[0]

        val data = viewModel.addItem(
            productoBean.articulo,
            productoBean.descripcion,
            precioEspacial?.precio ?: productoBean.precio,
            productoBean.iva,
            cantidadVendida
        )

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
                showLoading()
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
                Utils.addActivity2Stack(this)
                Actividades.getSingleton(this@VentasActivity, PrecaptureActivity::class.java)
                    .muestraActividad(sellViewState.params)
                binding.imgBtnFinishVisita.isEnabled = true
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
        binding.imgBtnFinishSale click {
            if (binding.imgBtnFinishSale.isEnabled) {
                binding.imgBtnFinishSale.isEnabled = false
                if (viewModel.existenPartidas()) {
                    // Not selected sell type
                    if ((!headerBinding.radioContado.isChecked && !headerBinding.radioCredito.isChecked) ||
                        sellType == SellType.SIN_DEFINIR) {
                        showSelectSellType()
                    } else if (sellType == SellType.CREDITO) {
                        val subtotal = headerBinding.textViewSubtotalVentaView.text.toString()
                        val import = headerBinding.textViewImpuestoVentaView.text.toString()
                        viewModel.checkUserCredit(clientId, sellType, subtotal, import)
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
                binding.imgBtnFinishVisita.isEnabled = false
                viewModel.createPrecatureParams(clientId)
            }
        }

        headerBinding.fbAddProductos click {
            if (headerBinding.fbAddProductos.isEnabled) {
                headerBinding.fbAddProductos.isEnabled = false
                Actividades.getSingleton(this@VentasActivity, ListaProductosActivity::class.java)
                    .muestraActividadForResult(Actividades.PARAM_INT_1)
                headerBinding.fbAddProductos.isEnabled = true
            }
        }
    }

    private fun refreshRecyclerView(data: List<VentasModelBean?>) {
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

    private fun initRecyclerView(data: List<VentasModelBean?>) {
        binding.recyclerViewVentas.setHasFixedSize(true)

        val manager = LinearLayoutManager(this)
        binding.recyclerViewVentas.layoutManager = manager

        adapter = AdapterItemsVenta(data, object: AdapterItemsVenta.OnItemLongClickListener {
            override fun onItemLongClicked(sell: VentasModelBean): Boolean {

                val dialog = PrettyDialog(this@VentasActivity)
                dialog.setTitle("Eliminar")
                    .setTitleColor(R.color.purple_500)
                    .setMessage("Desea eliminar el articulo ${sell.descripcion}")
                    .setMessageColor(R.color.purple_700)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500) { dialog.dismiss() }
                    .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {
                        val dao = SellsModelDao()
                        dao.delete(sell)
                        viewModel.refreshSellData()
                        dialog.dismiss()
                    }.addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900) { dialog.dismiss() }
                dialog.setCancelable(false)
                dialog.show()
                return false
            }
        }, object : AdapterItemsVenta.OnItemClickListener {
                override fun onItemClick(sell: VentasModelBean) {
                    val dialogo = Dialog(this@VentasActivity)

                    val dialogBinding = DialogCantidadVentaBinding
                        .inflate(LayoutInflater.from(this@VentasActivity), binding.root, false)
                    dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE) //before
                    dialogo.setContentView(dialogBinding.root)
                    dialogBinding.buttonSeleccionarCantidadVentaDialog click {
                        val cantidad = dialogBinding.edittextCantidadVentaSeleccionadaDialog.text.toString()
                        if (cantidad.isNotEmpty()) {
                                val cantidadVenta = cantidad.toInt()
                                if (cantidadVenta == 0) {
                                    showQuantityErrorDialog()
                                } else {
                                    val dao = SellsModelDao()
                                    sell.cantidad = cantidadVenta
                                    dao.save(sell)
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
            Log.d("SysPoint", "Error, Location permissions not granted, Ventas")
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
                            Log.d(
                                "SysPoint",
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
         Toast.makeText(applicationContext, "GPS Desactivado", Toast.LENGTH_SHORT).show()
    }

    override fun onProviderEnabled(provider: String) {
        // Este metodo se ejecuta cuando el GPS es activado
        Toast.makeText(applicationContext, "GPS Activado", Toast.LENGTH_SHORT).show()
    }

    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
        when (status) {
            LocationProvider.AVAILABLE -> Log.d("debug", "LocationProvider.AVAILABLE")
            LocationProvider.OUT_OF_SERVICE -> Log.d("debug", "LocationProvider.OUT_OF_SERVICE")
            LocationProvider.TEMPORARILY_UNAVAILABLE -> Log.d(
                "debug",
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
        dialog.show()
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

    private fun showScheduleDialog(clientId: String, recordatorio: String) {
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
}