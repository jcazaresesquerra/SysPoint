package com.app.syspoint.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Price(
    @SerializedName("id") @Expose
    var id: Int? = null,

    @SerializedName("cliente")
    @Expose
    var cliente: String? = null,

    @SerializedName("articulo")
    @Expose
    var articulo: String? = null,

    @SerializedName("precio")
    @Expose
    var precio: Double = 0.0,

    @SerializedName("active")
    @Expose
    var active: Int? = null,

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null,

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
): Serializable
