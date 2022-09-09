package com.app.syspoint.models.directions

import com.google.android.gms.maps.model.LatLng

data class Leg(
    var distance: Distance,
    var duration: Duration,
    var endAddress: String,
    var endLocation: LatLng,
    var startAddress: String,
    var startLocation: LatLng,
    var steps: List<Step>
) {
    constructor(): this(Distance(), Duration(), "", LatLng(0.0, 0.0),
        "", LatLng(0.0,0.0), arrayListOf())
}
