package com.app.syspoint.models.directions

import java.io.Serializable

data class Direction(
    val serialVersionUID: Long = -4198690398884769235L,
    var durationText: String,
    var html_instructions: String,
    var distanceText: String
): Serializable {
    constructor(): this(-4198690398884769235L, "", "", "")
}
