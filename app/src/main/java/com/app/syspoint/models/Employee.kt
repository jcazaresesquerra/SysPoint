package com.app.syspoint.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Employee (
    @SerializedName("id")
    @Expose
    var id: Int? = null,
    
    @SerializedName("nombre")
    @Expose
    var nombre: String? = null,
    
    @SerializedName("direccion")
    @Expose
    var direccion: String? = null,
    
    @SerializedName("email")
    @Expose
    var email: String? = null,
    
    @SerializedName("telefono")
    @Expose
    var telefono: String? = null,
    
    @SerializedName("fecha_nacimiento")
    @Expose
    var fechaNacimiento: String? = null,
    
    @SerializedName("fecha_ingreso")
    @Expose
    var fechaIngreso: String? = null,
    
    @SerializedName("contrasenia")
    @Expose
    var contrasenia: String? = null,
    
    @SerializedName("identificador")
    @Expose
    var identificador: String? = null,
    
    @SerializedName("status")
    @Expose
    var status: Int? = null,
    
    @SerializedName("path_image")
    @Expose
    var pathImage: String? = null,
    
    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null,
    
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null,

    @SerializedName("rute")
    @Expose
    var rute: String? = null,

    @SerializedName("clientId")
    @Expose
    var clientId: String = "tenet",
): Serializable