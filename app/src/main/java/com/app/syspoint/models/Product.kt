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
    
    @SerializedName("unidad_medida")
    @Expose
    var unidadMedida: String? = null,
    
    @SerializedName("status")
    @Expose
    var status: String? = null,
    
    @SerializedName("clave_sat")
    @Expose
    var claveSat: String? = null,
    
    @SerializedName("unidad_sat")
    @Expose
    var unidadSat: String? = null,
    
    @SerializedName("precio")
    @Expose
    var precio: Double = 0.0,
    
    @SerializedName("costo")
    @Expose
    var costo: Double = 0.0,
    
    @SerializedName("iva")
    @Expose
    var iva: Int = 0,
    
    @SerializedName("ieps")
    @Expose
    var ieps: Int = 0,
    
    @SerializedName("prioridad")
    @Expose
    var prioridad: Int = 0,
    
    @SerializedName("region")
    @Expose
    var region: String? = null,
    
    @SerializedName("codigo_alfa")
    @Expose
    var codigoAlfa: String? = null,
    
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
