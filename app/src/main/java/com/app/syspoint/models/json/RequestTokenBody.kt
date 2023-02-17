package com.app.syspoint.models.json

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class RequestTokenBody(
    @SerializedName("version")
    @Expose
    val version: String? = null,
    @SerializedName("subversion")
    @Expose
    val subversion: String? = null
)
