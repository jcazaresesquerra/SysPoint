package com.app.syspoint.utils.gmap

import com.app.syspoint.models.directions.*
import com.google.android.gms.maps.model.LatLng
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

class Directions {
    private val ROUTES = "routes"
    private val SUMMARY = "summary"
    private val LEGS = "legs"
    private val DISTANCE = "distance"
    private val TEXT = "text"

    private val STEPS = "steps"
    private val DURATION = "duration"
    private val END_LOCATION = "end_location"
    private val LATITUDE = "lat"
    private val HTML_INSTRUCTION = "html_instructions"
    private val OVERVIEW_POLYLINE = "overview_polyline"
    private val POLYLINE = "polyline"
    private val POINTS = "points"

    private val START_LOCATION = "start_location"
    private val LONGITUDE = "lng"

    private val VALUE = "value"

    @Throws(Exception::class)
    fun convertStreamToString(input: InputStream): String? {
        return try {
            val reader = BufferedReader(InputStreamReader(input))
            val sBuf = StringBuffer()
            var line: String? = null
            while (reader.readLine().also { line = it } != null) {
                sBuf.append(line)
            }
            sBuf.toString()
        } catch (e: Exception) {
            throw e
        } finally {
            try {
                input.close()
            } catch (e: Exception) {
                throw e
            }
        }
    }

    @Throws(java.lang.Exception::class)
    fun parse(routesJSONString: String?): List<Route>? {
        return try {
            val routeList: MutableList<Route> = ArrayList()
            val jSONObject = JSONObject(routesJSONString)
            val routeJSONArray = jSONObject.getJSONArray(ROUTES)
            var route: Route
            var routesJSONObject: JSONObject
            for (m in 0 until routeJSONArray.length()) {
                route = Route()
                routesJSONObject = routeJSONArray.getJSONObject(m)
                var legsJSONArray: JSONArray
                route.summary = routesJSONObject.getString(SUMMARY)
                route.overviewPolyLine = decodePolyLines(
                    routesJSONObject.getJSONObject(OVERVIEW_POLYLINE).getString(POINTS)
                )
                legsJSONArray = routesJSONObject.getJSONArray(LEGS)
                var legJSONObject: JSONObject
                var leg: Leg
                var stepsJSONArray: JSONArray
                for (b in 0 until legsJSONArray.length()) {
                    leg = Leg()
                    legJSONObject = legsJSONArray.getJSONObject(b)
                    leg.distance = Distance(
                        legJSONObject.optJSONObject(DISTANCE).optString(TEXT),
                        legJSONObject.optJSONObject(DISTANCE).optLong(VALUE)
                    )
                    leg.duration = Duration(
                        legJSONObject.optJSONObject(DURATION).optString(TEXT),
                        legJSONObject.optJSONObject(DURATION).optLong(VALUE)
                    )
                    stepsJSONArray = legJSONObject.getJSONArray(STEPS)
                    var stepJSONObject: JSONObject
                    var stepDurationJSONObject: JSONObject
                    var legPolyLineJSONObject: JSONObject
                    var stepStartLocationJSONObject: JSONObject
                    var stepEndLocationJSONObject: JSONObject
                    var step: Step
                    var encodedString: String?
                    var stepStartLocationLatLng: LatLng
                    var stepEndLocationLatLng: LatLng
                    for (i in 0 until stepsJSONArray.length()) {
                        stepJSONObject = stepsJSONArray.getJSONObject(i)
                        step = Step()
                        val stepDistanceJSONObject = stepJSONObject.getJSONObject(DISTANCE)
                        step.distance = Distance(
                            stepDistanceJSONObject.getString(TEXT),
                            stepDistanceJSONObject.getLong(VALUE)
                        )
                        stepDurationJSONObject = stepJSONObject.getJSONObject(DURATION)
                        step.duration = Duration(
                            stepDurationJSONObject.getString(TEXT),
                            stepDurationJSONObject.getLong(VALUE)
                        )
                        stepEndLocationJSONObject = stepJSONObject.getJSONObject(END_LOCATION)
                        stepEndLocationLatLng = LatLng(
                            stepEndLocationJSONObject.getDouble(LATITUDE),
                            stepEndLocationJSONObject.getDouble(LONGITUDE)
                        )
                        step.endLocation = stepEndLocationLatLng
                        step.htmlInstructions = stepJSONObject.getString(HTML_INSTRUCTION)
                        legPolyLineJSONObject = stepJSONObject.getJSONObject(POLYLINE)
                        encodedString = legPolyLineJSONObject.getString(POINTS)
                        step.points = decodePolyLines(encodedString)
                        stepStartLocationJSONObject = stepJSONObject.getJSONObject(START_LOCATION)
                        stepStartLocationLatLng = LatLng(
                            stepStartLocationJSONObject.getDouble(LATITUDE),
                            stepStartLocationJSONObject.getDouble(LONGITUDE)
                        )
                        step.startLocation = stepStartLocationLatLng
                        leg.steps.add(step)
                    }
                    route.legs.add(leg)
                }
                routeList.add(route)
            }
            routeList
        } catch (e: java.lang.Exception) {
            throw e
        }
    }

    private fun decodePolyLines(encoded: String): java.util.ArrayList<LatLng> {
        val poly = java.util.ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val position = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            poly.add(position)
        }
        return poly
    }
}