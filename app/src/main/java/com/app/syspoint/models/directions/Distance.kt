package com.app.syspoint.models.directions

data class Distance(
    var text: String,
    var value: Long
) {
    constructor(): this("", 0L)
}
