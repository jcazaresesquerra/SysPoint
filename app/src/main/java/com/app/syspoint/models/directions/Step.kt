package com.app.syspoint.models.directions

import com.google.android.gms.maps.model.LatLng

data class Step(
    var distance: Distance,
    var duration: Duration,
    var endLocation: LatLng,
    var startLocation: LatLng,
    var htmlInstructions: String,
    var travelMode: String,
    var points: List<LatLng>
) {
    constructor(): this(
        distance = Distance(),
        duration = Duration(),
        endLocation = LatLng(0.0,0.0),
        startLocation = LatLng(0.0,0.0),
        htmlInstructions = "",
        travelMode = "",
        points = arrayListOf()
    )
}
