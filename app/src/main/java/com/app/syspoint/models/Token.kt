package com.app.syspoint.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("id")
    @Expose
    val id: String? = null,
    @SerializedName("token")
    @Expose
    val token: String? = null,
    @SerializedName("version")
    @Expose
    val version: String? = null,
    @SerializedName("subversion")
    @Expose
    val subversion: String? = null,
    @SerializedName("base_update_url")
    @Expose
    val baseUpdateUrl: String? = null
)