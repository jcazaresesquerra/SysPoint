package com.app.syspoint.ui.clientes

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import com.app.syspoint.R
import com.app.syspoint.databinding.ActivityActualizaClienteBinding
import com.app.syspoint.interactor.client.ClientInteractor.SaveClientListener
import com.app.syspoint.interactor.client.ClientInteractorImp
import com.app.syspoint.models.Client
import com.app.syspoint.repository.objectBox.dao.ClientDao
import com.app.syspoint.repository.objectBox.entities.ClientBox
import com.app.syspoint.utils.*
import java.io.IOException
import java.util.*

class UpdateClientActivity: AppCompatActivity() {

    private lateinit var binding: ActivityActualizaClienteBinding

    private lateinit var listaCamposValidos: ArrayList<String>
    private var status_seleccionado: String? = null
    private var ruta_seleccionado: String? = null
    private var statusSeleccionadoDB: String? = null
    private var rutaSeleccionadoDB: String? = null
    private var clienteGlobal: String? = null
    private var isLocation = false
    private var idCliente: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizaClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.initToolBar()
        this.initControls()
        this.getData()
        this.loadSpinnerStatus()
        this.loadSpinnerRuta()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_CANCELED) return
        if (requestCode == 200) {
            val cuenta = data!!.getStringExtra(Actividades.PARAM_1)
            binding.inpMatrizAsignadaActualizaCliente.setText("" + cuenta)
        } else if (requestCode == 300) {
            val numero = data!!.getStringExtra(Actividades.PARAM_1)
            val calle = data.getStringExtra(Actividades.PARAM_2)
            val localidad = data.getStringExtra(Actividades.PARAM_3)
            val colonia = data.getStringExtra(Actividades.PARAM_4)
            val estado = data.getStringExtra(Actividades.PARAM_5)
            val pais = data.getStringExtra(Actividades.PARAM_6)
            val cp = data.getStringExtra(Actividades.PARAM_7)
            val lat = data.getStringExtra(Actividades.PARAM_8)
            val lng = data.getStringExtra(Actividades.PARAM_9)
            binding.etCalleActualizaCliente.setText(calle)
            binding.etCpUpdateClient.setText(cp)
            binding.etColoniaActualizaCliente.setText("" + localidad)
            binding.etCiudadActualizaCliente.setText("" + estado)
            binding.etNumeroActualizaCliente.setText(numero)
            binding.etActualizaLatitud.setText(lat)
            binding.etActualizaLongitud.setText(lng)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_actualiza_cliente, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.actualizaUbicacionCliente -> {
                isLocation = false
                Actividades.getSingleton(
                    this@UpdateClientActivity,
                    MapsClienteActivity::class.java
                ).muestraActividadForResult(300)
                true
            }
            R.id.actualizaCliente -> {
                if (validaCampos()) {
                    if (validaCliente()) {
                        val dialog = PrettyDialog(this)
                        dialog.setTitle("Actualizar")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Desea actualizar el cliente")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.ic_save_white, R.color.purple_500) {
                                dialog.dismiss()
                            }
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {
                                actualizaCliente()
                                dialog.dismiss()
                            }
                            .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900) {
                                dialog.dismiss()
                            }
                        dialog.setCancelable(false)
                        dialog.show()
                    }
                } else {
                    val campos = StringBuilder()
                    for (validItem in listaCamposValidos) {
                        campos.append(validItem).append("\n")
                    }
                    val dialog = PrettyDialog(this)
                    dialog.setTitle("Campos requeridos")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Debe de completar los campos requeridos \n$campos")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) { dialog.dismiss() }
                        .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.light_blue_700) {
                            dialog.dismiss()
                        }
                    dialog.setCancelable(false)
                    dialog.show()
                }
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getArrayString(id: Int): Array<String> {
        return this.resources.getStringArray(id)
    }

    fun initToolBar() {
        binding.toolbarActualizaCliente.title = "Actualiza cliente"
        setSupportActionBar(binding.toolbarActualizaCliente)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.purple_700)
    }

    private fun getData() {
        val intent = intent
        clienteGlobal = intent.getStringExtra(Actividades.PARAM_1)
        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientByAccount(clienteGlobal)
        if (clienteBean != null) {
            binding.etNombreActualizaCliente.setText(clienteBean.nombre_comercial)
            binding.etCalleActualizaCliente.setText(clienteBean.calle)
            binding.etNumeroActualizaCliente.setText(clienteBean.numero)
            binding.etColoniaActualizaCliente.setText(clienteBean.colonia)
            binding.etCiudadActualizaCliente.setText(clienteBean.ciudad)
            binding.etCpUpdateClient.setText(clienteBean.codigo_postal.toString())
            binding.inpNoCuentaActualizaCliente.setText(clienteBean.cuenta)
            binding.inpFechaAltaActualizaCliente.setText(clienteBean.fecha_registro)
            binding.inpContactoPhoneActualizaCliente.setText(clienteBean.contacto_phone)
            statusSeleccionadoDB = if (!clienteBean.status) "Activo" else "InActivo"
            rutaSeleccionadoDB = clienteBean.rango
            binding.checkborLunesActualizaCliente.isChecked = clienteBean.lun == 1
            binding.checkborMartesActualizaCliente.isChecked = clienteBean.mar == 1
            binding.checkborMiercolesActualizaCliente.isChecked = clienteBean.mie == 1
            binding.checkborJuevesActualizaCliente.isChecked = clienteBean.jue == 1
            binding.checkborViernesActualizaCliente.isChecked = clienteBean.vie == 1
            binding.checkborSabadoActualizaCliente.isChecked = clienteBean.sab == 1
            binding.checkborDomingoActualizaCliente.isChecked = clienteBean.dom == 1
            binding.etActualizaCredito.isChecked = clienteBean.isCredito
            binding.etActualizaLatitud.setText(clienteBean.latitud)
            binding.etActualizaLongitud.setText(clienteBean.longitud)
            binding.inpMatrizAsignadaActualizaCliente.setText(clienteBean.matriz)
            binding.etRegistroActualizaLimiteCredito.setText(clienteBean.limite_credito.toString())
            binding.etRegistroActualizaCredito.setText(clienteBean.saldo_credito.toString())
        }
    }

    private fun initControls() {
        binding.imgSearchClienteActualizaCliente click  {
            Actividades.getSingleton(
                this,
                ListaClientesActivity::class.java
            ).muestraActividadForResult(200)
        }
        binding.imgFechaAltaActualizaCliente click  {
            dateFechaactualiza()
        }
        binding.btnActualizaLocation click {
            isLocation = true
            locationStart()
        }
    }

    private fun dateFechaactualiza() {
        val c = Calendar.getInstance()
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]
        val datePickerDialog = DatePickerDialog(this,
            { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                if (dayOfMonth < 9) {
                    binding.inpFechaAltaActualizaCliente.setText("0" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year)
                } else {
                    binding.inpFechaAltaActualizaCliente.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year)
                }
            }, mYear, mMonth, mDay
        )
        datePickerDialog.show()
    }

    private fun loadSpinnerStatus() {
        val array: Array<String> = getArrayString(R.array.status_producto)
        val arrayList = Utils.convertArrayStringListString(array)
        val adapter = ArrayAdapter(this, R.layout.item_status_producto, arrayList)

        binding.spinnerStatusActualizaCliente.adapter = adapter
        binding.spinnerStatusActualizaCliente.setSelection(arrayList.indexOf(statusSeleccionadoDB))
        binding.spinnerStatusActualizaCliente.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                status_seleccionado = binding.spinnerStatusActualizaCliente.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun loadSpinnerRuta() {
        val array = getArrayString(R.array.ruteo_rango_rutas)
        val arrayList = Utils.convertArrayStringListString(array)
        val adapter = ArrayAdapter(this, R.layout.item_status_producto, arrayList)
        binding.spinnerRangoActualizaCliente.adapter = adapter
        binding.spinnerRangoActualizaCliente.setSelection(arrayList.indexOf(rutaSeleccionadoDB))
        binding.spinnerRangoActualizaCliente.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                ruta_seleccionado = binding.spinnerRangoActualizaCliente.selectedItem.toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun validaCampos(): Boolean {
        var valida = true
        listaCamposValidos = arrayListOf()
        val nombre: String = binding.etNombreActualizaCliente.text.toString()
        val calle: String = binding.etCalleActualizaCliente.text.toString()
        val numero: String = binding.etNumeroActualizaCliente.text.toString()
        val colonia: String = binding.etColoniaActualizaCliente.text.toString()
        val ciudad: String = binding.etCiudadActualizaCliente.text.toString()
        val cp: String = binding.etCpUpdateClient.text.toString()

        if (nombre.isEmpty()) {
            valida = false
            listaCamposValidos.add("nombre")
        }
        if (calle.isEmpty()) {
            valida = false
            listaCamposValidos.add("ciudad")
        }
        if (numero.isEmpty()) {
            valida = false
            listaCamposValidos.add("numero")
        }
        if (colonia.isEmpty()) {
            valida = false
            listaCamposValidos.add("colonia")
        }
        if (ciudad.isEmpty()) {
            valida = false
            listaCamposValidos.add("ciudad")
        }
        if (cp.isEmpty()) {
            valida = false
            listaCamposValidos.add("C.P")
        }
        return valida
    }

    private fun validaCliente(): Boolean {
        val dao = ClientDao()
        val bean = dao.getClientByAccount(binding.inpNoCuentaActualizaCliente.text.toString())
        return bean != null
    }

    private fun actualizaCliente() {
        val cp: String = binding.etCpUpdateClient.text.toString()
        val numero: String = binding.etNumeroActualizaCliente.text.toString()
        if (!cp.isNullOrEmpty() && !numero.isNullOrEmpty() && numero != "null") {
            val dao = ClientDao()
            val clienteBean = dao.getClientByAccount(clienteGlobal)

            val bean = dao.getClientByAccount(binding.inpNoCuentaActualizaCliente.text.toString())
            bean!!.nombre_comercial = binding.etNombreActualizaCliente.text.toString()
            bean.calle = binding.etCalleActualizaCliente.text.toString()
            bean.numero = numero
            bean.colonia = binding.etColoniaActualizaCliente.text.toString()
            bean.ciudad = binding.etCiudadActualizaCliente.text.toString()
            bean.codigo_postal = cp.toInt()
            bean.fecha_registro = binding.inpFechaAltaActualizaCliente.text.toString()
            bean.cuenta = binding.inpNoCuentaActualizaCliente.text.toString()
            bean.status = status_seleccionado!!.compareTo("Activo", ignoreCase = true) == 0
            bean.consec = if (!bean.consec.isNullOrEmpty()) bean.consec else clienteBean?.consec ?: "0"
            bean.rango = ruta_seleccionado
            bean.lun = if (binding.checkborLunesActualizaCliente.isChecked) 1 else 0
            bean.mar = if (binding.checkborMartesActualizaCliente.isChecked) 1 else 0
            bean.mie = if (binding.checkborMiercolesActualizaCliente.isChecked) 1 else 0
            bean.jue = if (binding.checkborJuevesActualizaCliente.isChecked) 1 else 0
            bean.vie = if (binding.checkborViernesActualizaCliente.isChecked) 1 else 0
            bean.sab = if (binding.checkborSabadoActualizaCliente.isChecked) 1 else 0
            bean.dom = if (binding.checkborDomingoActualizaCliente.isChecked) 1 else 0
            bean.latitud = binding.etActualizaLatitud.text.toString()
            bean.longitud = binding.etActualizaLongitud.text.toString()
            bean.contacto_phone = binding.inpContactoPhoneActualizaCliente.text.toString()
            bean.isCredito = binding.etActualizaCredito.isChecked
            bean.limite_credito = binding.etRegistroActualizaLimiteCredito.text.toString().replace("$", "").replace(" ", "").toDouble()
            bean.saldo_credito = binding.etRegistroActualizaCredito.text.toString().replace("$", "").replace(" ", "").toDouble()
            bean.matriz = binding.inpMatrizAsignadaActualizaCliente.text.toString()
            bean.date_sync = Utils.fechaActual()
            bean.updatedAt = Utils.fechaActualHMS()
            dao.insert(bean)
            idCliente = bean.id.toString()
            if (!Utils.isNetworkAvailable(application)) {
                //showDialogNotConnectionInternet();
            } else {
                testLoadClientes(bean)
            }
        } else {
            Toast.makeText(
                this,
                "Necesita llenar todos los campos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showDialogNotConnectionInternet() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_warning)
        dialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById<View>(R.id.bt_close) as AppCompatButton).setOnClickListener { v: View? ->
            //testLoadClientes(idCliente)
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun testLoadClientes(client: ClientBox) {
        if (!idCliente.isNullOrEmpty()) {
            binding.rlprogressClienteActualiza.setVisible()
            //val clientDao = ClientDao()
            //val listaClientesDB = clientDao.getByIDClient(idCliente)
            //val listaClientesDB = client
            val listaClientes: MutableList<Client> = ArrayList()
            //for (item in listaClientesDB) {
            client.let { item ->
                val cliente = Client()
                cliente.nombreComercial = item.nombre_comercial
                cliente.calle = item.calle
                cliente.numero = item.numero
                cliente.colonia = item.colonia
                cliente.ciudad = item.ciudad
                cliente.codigoPostal = item.codigo_postal
                cliente.fechaRegistro = item.fecha_registro
                cliente.cuenta = item.cuenta
                cliente.status = if (item.status) 1 else 0
                cliente.consec = item.consec
                cliente.rango = item.rango
                cliente.lun = item.lun
                cliente.mar = item.mar
                cliente.mie = item.mie
                cliente.jue = item.jue
                cliente.vie = item.vie
                cliente.sab = item.sab
                cliente.dom = item.dom
                cliente.latitud = item.latitud
                cliente.longitud = item.longitud
                cliente.phone_contacto = item.contacto_phone
                cliente.recordatorio = item.recordatorio ?: "null"
                cliente.visitas = item.visitasNoefectivas
                cliente.isCredito = if (item.isCredito) 1 else 0
                cliente.saldo_credito = item.saldo_credito
                cliente.limite_credito = item.limite_credito
                if (item.matriz === "null" && item.matriz == null) {
                    cliente.matriz = "null"
                } else {
                    cliente.matriz = item.matriz
                }
                cliente.updatedAt = item.updatedAt
                listaClientes.add(cliente)
            }
            ClientInteractorImp().executeSaveClient(listaClientes, object : SaveClientListener {
                override fun onSaveClientSuccess() {
                    binding.rlprogressClienteActualiza.setGone()
                    //Toast.makeText(getApplicationContext(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
                    finish()
                }

                override fun onSaveClientError() {
                    binding.rlprogressClienteActualiza.setGone()
                    //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
                    finish()
                }
            })
        }
    }


    //Apartir de aqui empezamos a obtener la direciones y coordenadas
    private fun locationStart() {
        val mlocManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val local = Localizacion()
        local.mainActivity = this
        val gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) {
            /* permite realizar algunas configuraciones del movil para el permiso */
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
            return
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, local)
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, local)
    }

    /* obtener la direccion*/
    fun setLocation(loc: Location) {
        Thread {
            //Obtener la direccion de la calle a partir de la latitud y la longitud
            if (loc.latitude != 0.0 && loc.longitude != 0.0) {
                try {
                    /*Geocodificacion- Proceso de conversión de coordenadas a direccion*/
                    val geocoder =
                        Geocoder(applicationContext, Locale.getDefault())
                    val list =
                        geocoder.getFromLocation(
                            loc.latitude, loc.longitude, 1
                        )
                    if (!list!!.isEmpty()) {
                        val DirCalle = list[0]
                        // editTextDireccion.setText(DirCalle.getAddressLine(0));
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    class Localizacion : LocationListener {
        var mainActivity: UpdateClientActivity? = null

        override fun onLocationChanged(location: Location) {
            Thread {
                if (location.latitude != 0.0 && location.longitude != 0.0) {
                    try {
                        /*Geocodificacion- Proceso de conversión de coordenadas a direccion*/
                        val geocoder =
                            Geocoder(mainActivity!!.applicationContext, Locale.getDefault())
                        val list =
                            geocoder.getFromLocation(
                                location.latitude, location.longitude, 1
                            )
                        if (list!!.isNotEmpty()) {
                            val address = list[0].getAddressLine(0)
                            val cityName = list[0].locality
                            val stateName = list[0].adminArea
                            val codigo = list[0].postalCode
                            mainActivity!!.runOnUiThread {
                                if (!mainActivity!!.isLocation) {
                                    mainActivity!!.binding.etCalleActualizaCliente.setText(address)
                                    mainActivity!!.binding.etCpUpdateClient.setText(codigo)
                                    mainActivity!!.binding.etColoniaActualizaCliente.setText(cityName)
                                    mainActivity!!.binding.etCiudadActualizaCliente.setText(stateName)
                                }
                                mainActivity!!.binding.etActualizaLatitud.setText(list[0].latitude.toString())
                                mainActivity!!.binding.etActualizaLongitud.setText(list[0].longitude.toString())
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }.start()
            mainActivity!!.setLocation(location)
        }

        override fun onProviderDisabled(provider: String) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(mainActivity!!.applicationContext, "GPS Desactivado", Toast.LENGTH_SHORT).show()
        }

        override fun onProviderEnabled(provider: String) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(mainActivity!!.applicationContext, "GPS Activado", Toast.LENGTH_SHORT).show()
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
    }
}