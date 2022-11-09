package com.app.syspoint.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RequestClientsByRute(
    @SerializedName("rute")
    @Expose
    var rute: String? = null,
    @SerializedName("day")
    @Expose
    var day: Int? = null
)
