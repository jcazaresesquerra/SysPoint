package com.app.syspoint.models.json

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ClientInfoBodyJson(
    @SerializedName("cuenta")
    @Expose
    var clientAccount: String? = null,
    @SerializedName("clientId")
    @Expose
    var clientId: String = "tenet"
)
