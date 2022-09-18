package com.app.syspoint.models.directions

import com.google.android.gms.maps.model.LatLng

data class Route(
    val serialVersionUID: Long = 1L,
    var bound: Bound,
    var copyrights: String,
    var legs: ArrayList<Leg>,
    var overviewPolyLine: List<LatLng>,
    var summary: String
) {
    constructor(): this(
        serialVersionUID = 1L,
        bound = Bound(),
        copyrights = "",
        legs = arrayListOf(),
        overviewPolyLine = arrayListOf(),
        summary = ""
    )
}
