package com.app.syspoint.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RequestChargeByRute(
    @SerializedName("cuenta")
    @Expose
    var account: String? = null
)
