package com.app.syspoint.models.directions

import com.google.android.gms.maps.model.LatLng

data class Bound(
    var northEast: LatLng,
    var southWest: LatLng
) {
    constructor(): this(LatLng(0.0, 0.0), LatLng(0.0, 0.0))
}