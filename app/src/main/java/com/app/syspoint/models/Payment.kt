package com.app.syspoint.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Payment(
    @SerializedName("cobranza") 
    @Expose
    var cobranza: String? = null,

    @SerializedName("cuenta")
    @Expose
    var cuenta: String? = null,
    
    @SerializedName("importe")
    @Expose
    var importe: Double = 0.0,
    
    @SerializedName("saldo")
    @Expose
    var saldo: Double = 0.0,
    
    @SerializedName("venta")
    @Expose
    var venta: Long? = null,
    
    @SerializedName("estado")
    @Expose
    var estado: String? = null,
    
    @SerializedName("observaciones")
    @Expose
    var observaciones: String? = null,
    
    @SerializedName("fecha")
    @Expose
    var fecha: String? = null,
    
    @SerializedName("hora")
    @Expose
    var hora: String? = null,
    
    @SerializedName("identificador")
    @Expose
    var identificador: String? = null
): Serializable
