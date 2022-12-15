package com.app.syspoint.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Product(
    @SerializedName("id")
    @Expose
    var id: Int? = null,

    @SerializedName("articulo")
    @Expose
    var articulo: String? = null,
    
    @SerializedName("descripcion")
    @Expose
    var descripcion: String? = null,
    
    @SerializedName("status")
    @Expose
    var status: String? = null,
    
    @SerializedName("precio")
    @Expose
    var precio: Double = 0.0,
    
    @SerializedName("iva")
    @Expose
    var iva: Int = 0,
    
    @SerializedName("codigo_barras")
    @Expose
    var codigoBarras: String? = null,
    
    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null,
    
    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null,
    
    @SerializedName("path_image")
    @Expose
    var pathImage: String? = null
): Serializable
