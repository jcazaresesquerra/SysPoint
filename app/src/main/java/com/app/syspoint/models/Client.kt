package com.app.syspoint.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Client (
    @SerializedName("id") @Expose
    var id: Int = 0,

    @SerializedName("visitas_no_efectivas")
    @Expose
    var visitas: Int = 0,

    @SerializedName("nombre_comercial")
    @Expose
    var nombreComercial: String? = null,

    @SerializedName("calle")
    @Expose
    var calle: String? = null,

    @SerializedName("numero")
    @Expose
    var numero: String? = null,

    @SerializedName("colonia")
    @Expose
    var colonia: String? = null,

    @SerializedName("ciudad")
    @Expose
    var ciudad: String? = null,

    @SerializedName("codigo_postal")
    @Expose
    var codigoPostal: Int = 0,

    @SerializedName("fecha_registro")
    @Expose
    var fechaRegistro: String? = null,

    @SerializedName("fecha_baja")
    @Expose
    var fechaBaja: String? = null,

    @SerializedName("cuenta")
    @Expose
    var cuenta: String? = null,

    @SerializedName("grupo")
    @Expose
    var grupo: String? = null,

    @SerializedName("categoria")
    @Expose
    var categoria: String? = null,

    @SerializedName("status")
    @Expose
    var status: Int = 0,

    @SerializedName("consec")
    @Expose
    var consec: Int = 0,

    @SerializedName("region")
    @Expose
    var region: String? = null,

    @SerializedName("sector")
    @Expose
    var sector: String? = null,

    @SerializedName("rango")
    @Expose
    var rango: String? = null,

    @SerializedName("ruta")
    @Expose
    var ruta: String? = null,

    @SerializedName("secuencia")
    @Expose
    var secuencia: Int = 0,

    @SerializedName("periodo")
    @Expose
    var periodo: Int = 0,

    @SerializedName("lun")
    @Expose
    var lun: Int = 0,

    @SerializedName("mar")
    @Expose
    var mar: Int = 0,

    @SerializedName("mie")
    @Expose
    var mie: Int = 0,

    @SerializedName("jue")
    @Expose
    var jue: Int = 0,

    @SerializedName("vie")
    @Expose
    var vie: Int = 0,

    @SerializedName("sab")
    @Expose
    var sab: Int = 0,

    @SerializedName("dom")
    @Expose
    var dom: Int = 0,

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null,

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null,


    @SerializedName("latitud")
    @Expose
    var latitud: String? = null,

    @SerializedName("longitud")
    @Expose
    var longitud: String? = null,

    @SerializedName("phone_contacto")
    @Expose
    var phone_contacto: String? = null,

    @SerializedName("comentarios")
    @Expose
    var recordatorio: String? = null,

    @SerializedName("saldo_credito")
    @Expose
    var saldo_credito: Double = 0.0,

    @SerializedName("is_credito")
    @Expose
    var isCredito: Int? = null,

    @SerializedName("limite_credito")
    @Expose
    var limite_credito: Double = 0.0,

    @SerializedName("matriz")
    @Expose
    var matriz: String? = null
): Serializable