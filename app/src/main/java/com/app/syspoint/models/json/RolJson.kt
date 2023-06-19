package com.app.syspoint.models.json

import com.app.syspoint.models.Role
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RolJson(
    @SerializedName("Roles")
    @Expose
    var roles: List<Role?>? = null,
    @SerializedName("clientId")
    @Expose
    var clientId: String = "tenet"
)
