package com.app.syspoint.models.directions

import com.google.android.gms.maps.model.LatLng

data class Bound(
    var northEast: LatLng,
    var southWest: LatLng
) {
    constructor(): this(
        northEast = LatLng(0.0, 0.0),
        southWest = LatLng(0.0, 0.0)
    )
}