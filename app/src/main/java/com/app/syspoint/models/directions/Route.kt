package com.app.syspoint.models.directions

import com.google.android.gms.maps.model.LatLng

data class Route(
    val serialVersionUID: Long = 1L,
    var bound: Bound,
    var copyrights: String,
    var legs: List<Leg>,
    var overviewPolyLine: List<LatLng>,
    var summary: String
) {
    constructor(): this(1L, Bound(), "", arrayListOf(), arrayListOf(), "")
}
