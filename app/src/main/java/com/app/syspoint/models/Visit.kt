package com.app.syspoint.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Visit(
    @SerializedName("fecha")
    @Expose
    var fecha: String? = null,

    @SerializedName("hora")
    @Expose
    var hora: String? = null,
    
    @SerializedName("identificador")
    @Expose
    var identificador: String? = null,
    
    @SerializedName("cuenta")
    @Expose
    var cuenta: String? = null,
    
    @SerializedName("latidud")
    @Expose
    var latidud: String? = null,
    
    @SerializedName("longitud")
    @Expose
    var longitud: String? = null,
    
    @SerializedName("motivo_visita")
    @Expose
    var motivo_visita: String? = null,

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
): Serializable
