package com.app.syspoint.models.json

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ClientByIdBodyJson(
    @SerializedName("cuenta")
    @Expose
    var cuenta: String? = null,
    @SerializedName("clientId")
    @Expose
    var clientId: String = "tenet"
)