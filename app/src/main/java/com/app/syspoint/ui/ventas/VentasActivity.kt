package com.app.syspoint.ui.ventas

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.app.syspoint.databinding.ActivityVentasBinding
import com.app.syspoint.viewmodel.sell.SellViewModel
import java.io.IOException
import java.util.*

/*class VentasActivity: AppCompatActivity() {

    private lateinit var binding: ActivityVentasBinding
    private lateinit var viewModel: SellViewModel


    //Apartir de aqui empezamos a obtener la direciones y coordenadas
    private fun locationStart() {
        val mlocManager = getSystemService(LOCATION_SERVICE) as LocationManager
        val Local: Localizacion = Localizacion()
        Local.setMainActivity(this)
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
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, Local)
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, Local)
    }

    //* Aqui empieza la Clase Localizacion / se obtienen las coordenadas */
    class Localizacion : LocationListener {
        var mainActivity: VentasActivity? = null

        override fun onLocationChanged(location: Location) {
            /*Geocodificacion- Proceso de conversión de coordenadas a direccion*/
            try {
                Thread {
                    if (location.latitude != 0.0 && location.longitude != 0.0) {
                        try {
                            val geocoder =
                                Geocoder(getApplicationContext(), Locale.getDefault())
                            val list =
                                geocoder.getFromLocation(
                                    location.latitude, location.longitude, 1
                                )
                            if (!list!!.isEmpty()) {
                                latidud = "" + list[0].latitude
                                longitud = "" + list[0].longitude
                            }
                        } catch (e: Exception) {
                            runOnUiThread(Runnable {
                                Toast.makeText(
                                    this@VentasActivity,
                                    "Ha ocurrido un error, vuelva a intentar",
                                    Toast.LENGTH_LONG
                                ).show()
                            })
                            e.printStackTrace()
                        }
                    }
                }.start()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mainActivity.setLocation(location)
        }

        override fun onProviderDisabled(provider: String) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(getApplicationContext(), "GPS Desactivado", Toast.LENGTH_SHORT).show()
        }

        override fun onProviderEnabled(provider: String) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(getApplicationContext(), "GPS Activado", Toast.LENGTH_SHORT).show()
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

    /* obtener la direccion*/
    fun setLocation(loc: Location) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        try {
            Thread {
                if (loc.latitude != 0.0 && loc.longitude != 0.0) {
                    try {
                        /*Geocodificacion- Proceso de conversión de coordenadas a direccion*/
                        val geocoder = Geocoder(this, Locale.getDefault())
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
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
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
}*/