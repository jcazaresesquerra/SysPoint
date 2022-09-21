package com.app.syspoint.ui.precaptura

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.syspoint.R
import com.app.syspoint.databinding.ActivityPreCapturaBinding
import com.app.syspoint.models.VisitType
import com.app.syspoint.models.sealed.PrecaptureViewState
import com.app.syspoint.ui.ventas.FinalizaPrecapturaActivity
import com.app.syspoint.utils.Actividades
import com.app.syspoint.utils.Constants
import com.app.syspoint.viewmodel.precaptura.PrecaptureViewModel
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import libs.mjn.prettydialog.PrettyDialog

class PrecaptureActivity: AppCompatActivity(), OnMapReadyCallback,
    GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    private lateinit var binding: ActivityPreCapturaBinding
    private lateinit var viewModel: PrecaptureViewModel
    private lateinit var adapter: ViewTypeAdapter

    private lateinit var googleApiClient: GoogleApiClient
    private lateinit var googleMap: GoogleMap
    private var lastKnownLocation: Location? = null

    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var conceptSelectedView: String? = null
    private var direccion: String? = null

    private var finishPreSellClicked = false
    private var confirmPrecaptureClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPreCapturaBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[PrecaptureViewModel::class.java]

        viewModel.precaptureViewState.observe(this, ::renderViewState)

        setContentView(binding.root)
        initToolBar()
        setUpRecyclerView()

        binding.tvVisitaNombreComercial.text = intent.getStringExtra(Actividades.PARAM_5)
        direccion = intent.getStringExtra(Actividades.PARAM_2) + " " +
                intent.getStringExtra(Actividades.PARAM_3) + ", " +
                intent.getStringExtra(Actividades.PARAM_4)

        binding.tvDirecccionPrecaptura.text = direccion

        latitud = intent.getStringExtra(Actividades.PARAM_6)!!.toDouble()
        longitud = intent.getStringExtra(Actividades.PARAM_7)!!.toDouble()

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapViewPre) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        if (!::googleApiClient.isInitialized) {
            googleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.REQUEST_PERMISSION_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation(true)
            }
        }
    }

    override fun onStart() {
        googleApiClient.connect()
        super.onStart()
    }

    override fun onStop() {
        googleApiClient.disconnect()
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_precaptura, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.finish_preventa -> {
                if (!finishPreSellClicked) {
                    finishPreSellClicked = true
                    if (conceptSelectedView == null || conceptSelectedView!!.isEmpty() || conceptSelectedView === "") {
                        showNotChecked()
                        finishPreSellClicked = false
                        return false
                    } else {
                        handleConfirmAction()
                        finishPreSellClicked = false
                    }
                }
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onConnected(bundle: Bundle?) {
        updateLastLocation(true)
    }

    override fun onConnectionSuspended(i: Int) {
        updateLastLocation(true)
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        updateLastLocation(true)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.uiSettings.isMyLocationButtonEnabled = false
        this.googleMap.uiSettings.isMapToolbarEnabled = true
        updateLastLocation(true)
    }

    private fun renderViewState(precaptureViewState: PrecaptureViewState) {
        when(precaptureViewState) {
            is PrecaptureViewState.PrecaptureFinished -> {
                Actividades.getSingleton(this, FinalizaPrecapturaActivity::class.java).muestraActividad(precaptureViewState.params)
                finish()
                confirmPrecaptureClicked = false
            }
            is PrecaptureViewState.SaveClientSuccessState -> {
                //Toast.makeText(applicationContext, "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show()
            }
            is PrecaptureViewState.SaveClientErrorState -> {
                Toast.makeText(applicationContext, "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show()
            }
            is PrecaptureViewState.SaveVisitSuccessState -> {
                //Toast.makeText(applicationContext, "Visita sincroniza", Toast.LENGTH_LONG).show()
            }
            is PrecaptureViewState.SaveVisitErrorState -> {
                Toast.makeText(applicationContext, "Ha ocurrido un error al registrar la visita", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initToolBar() {
        binding.toolbarPrecaptura.title = "Precaptura"
        setSupportActionBar(binding.toolbarPrecaptura)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = resources.getColor(R.color.purple_700)
        }
    }

    private fun setUpRecyclerView() {
        val data = arrayListOf<VisitType>()

        data.add(VisitType(1, "No Recibio", false))
        data.add(VisitType(2, "Cerrado", false))
        data.add(VisitType(3, "No Visitado", false))
        binding.rvTipoVisita.setHasFixedSize(true)

        val manager = LinearLayoutManager(this)
        binding.rvTipoVisita.layoutManager = manager

        adapter = ViewTypeAdapter(data, object: ViewTypeAdapter.OnItemClickListener {
                override fun onItemClick(position: Int) {
                    for (i in data.indices) {
                        val item: VisitType = data[i]
                        item.isSelected = false
                    }
                    if (position >= 0 && position < data.size) {
                        val item: VisitType = data[position]
                        conceptSelectedView = item.name
                        item.isSelected = true
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Ha ocurrido un error, intente nuevamente",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    binding.rvTipoVisita.adapter!!.notifyDataSetChanged()
                }
            })

        binding.rvTipoVisita.adapter = adapter
    }

    private fun showNotChecked() {
        val dialog = PrettyDialog(this)
        dialog.setTitle("Sin Concepto")
            .setTitleColor(R.color.purple_500)
            .setMessage("No ha seleccionado el motivo de la visita")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.ic_save_white, R.color.purple_500) {
                dialog.dismiss()
            }
            .addButton("OK", R.color.white, R.color.red_800) {
                dialog.dismiss()
            }
        dialog.show()
    }

    private fun handleConfirmAction() {

        val accountId = intent.getStringExtra(Actividades.PARAM_1)

        val dialog = PrettyDialog(this)
        dialog.setTitle("Confirmar accion")
            .setTitleColor(R.color.purple_500)
            .setMessage("¿Esta seguro de registrar esta accion?")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.ic_save_white, R.color.purple_500) {
                dialog.dismiss()
            }
            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {
                if (!confirmPrecaptureClicked) {
                    confirmPrecaptureClicked = true
                    viewModel.confirmPrecapture(accountId, conceptSelectedView, latitud, longitud)
                    dialog.dismiss()
                }
            }
            .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900) {
                dialog.dismiss()
            }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun updateLastLocation(move: Boolean) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Constants.REQUEST_PERMISSION_LOCATION)
            return
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient)
        googleMap.isMyLocationEnabled = false
        if (lastKnownLocation == null) {
            if (move) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latitud, longitud), 15f))
                val currentDriverPos = LatLng(latitud, longitud)
                val markerOptions = MarkerOptions()
                markerOptions.position(currentDriverPos)
                markerOptions.title(binding.tvVisitaNombreComercial.text.toString())
                googleMap.clear()
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(currentDriverPos))
                googleMap.addMarker(markerOptions)
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f))
            }
        }
    }
}