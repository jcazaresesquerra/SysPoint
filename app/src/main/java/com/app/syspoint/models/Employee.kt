package com.app.syspoint.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Employee (
    @SerializedName("id") @Expose
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
    
    @SerializedName("fecha_egreso")
    @Expose
    var fechaEgreso: String? = null,
    
    @SerializedName("contrasenia")
    @Expose
    var contrasenia: String? = null,
    
    @SerializedName("identificador")
    @Expose
    var identificador: String? = null,
    
    @SerializedName("status")
    @Expose
    var status: Int? = null,
    
    @SerializedName("nss")
    @Expose
    var nss: String? = null,
    
    @SerializedName("rfc")
    @Expose
    var rfc: String? = null,
    
    @SerializedName("curp")
    @Expose
    var curp: String? = null,
    
    @SerializedName("puesto")
    @Expose
    var puesto: String? = null,
    
    @SerializedName("area_depto")
    @Expose
    var areaDepto: String? = null,
    
    @SerializedName("tipo_contrato")
    @Expose
    var tipoContrato: String? = null,
    
    @SerializedName("region")
    @Expose
    var region: String? = null,
    
    @SerializedName("hora_entrada")
    @Expose
    var horaEntrada: String? = null,
    
    @SerializedName("hora_salida")
    @Expose
    var horaSalida: String? = null,
    
    @SerializedName("salida_comer")
    @Expose
    var salidaComer: String? = null,
    
    @SerializedName("entrada_comer")
    @Expose
    var entradaComer: String? = null,
    
    @SerializedName("sueldo_diario")
    @Expose
    var sueldoDiario: Int = 0,
    
    @SerializedName("turno")
    @Expose
    var turno: String? = null,
    
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

    @SerializedName("day")
    @Expose
    var day: Int? = null
): Serializable