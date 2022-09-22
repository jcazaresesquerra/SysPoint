package com.app.syspoint.utils.gmap

import android.content.Context
import com.app.syspoint.models.directions.Route
import com.app.syspoint.ui.MainActivity.Companion.apikey
import com.google.android.gms.maps.model.LatLng
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request

class MapDirectionAPI {
    companion object {
        fun getDirection(pickUp: LatLng, destination: LatLng): Call {
            val client = OkHttpClient()
            val gMapDirection = GMapDirection()
            val request = Request.Builder()
                .url(gMapDirection.getUrl(pickUp, destination, GMapDirection.MODE_DRIVING, false))
                .build()
            return client.newCall(request)
        }

        fun getAddress(address: LatLng): Call {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + address.latitude + "," + address.longitude + "&key=" + apikey)
                .build()
            return client.newCall(request)
        }

        fun getDirectionVia(pickUp: LatLng?, vararg destination: LatLng?): Call? {
            val client = OkHttpClient()
            val gMapDirection = GMapDirection()
            val request = Request.Builder()
                .url(
                    gMapDirection.getUrlVia(
                        GMapDirection.MODE_DRIVING,
                        false,
                        pickUp,
                        *destination
                    )
                )
                .build()
            return client.newCall(request)
        }

        fun getDistance(context: Context?, json: String?): Long {
            var dist: Long = 0
            if (json != null) {
                val directions = Directions()
                val routes: List<Route>?
                routes = try {
                    directions.parse(json)
                } catch (e: Exception) {
                    e.printStackTrace()
                    return -1L
                }
                for (route in routes!!) {
                    for (leg in route.legs) {
                        for (step in leg.steps) {
                            dist += step.distance.value
                        }
                    }
                }
                if (routes.isEmpty()) return -1L
            }
            return dist
        }

        fun getTimeDistance(json: String?): String {
            var time = "0 mins"
            if (json != null) {
                val directions = Directions()
                var routes: List<Route>? = null
                try {
                    routes = directions.parse(json)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                for (route in routes!!) {
                    for (leg in route.legs) {
                        time = leg.duration.text
                    }
                }
            }
            return time
        }
    }
}