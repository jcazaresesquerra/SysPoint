package com.app.syspoint.models.json

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ClientByIdBodyJson(
    @SerializedName("ids")
    @Expose
    var ids: List<String?>? = null,
    @SerializedName("clientId")
    @Expose
    var clientId: String = "tenet"
)