package com.app.syspoint.models.directions

data class Duration(
    var text: String,
    var value: Long
) {
    constructor(): this("", 0L)
}
