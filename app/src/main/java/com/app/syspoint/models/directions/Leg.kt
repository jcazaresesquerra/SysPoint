package com.app.syspoint.models.directions

import com.google.android.gms.maps.model.LatLng

data class Leg(
    var distance: Distance,
    var duration: Duration,
    var endAddress: String,
    var endLocation: LatLng,
    var startAddress: String,
    var startLocation: LatLng,
    var steps: ArrayList<Step>
) {
    constructor(): this(
        distance = Distance(),
        duration = Duration(),
        endAddress = "",
        endLocation = LatLng(0.0, 0.0),
        startAddress = "",
        startLocation = LatLng(0.0,0.0),
        steps = arrayListOf()
    )
}
