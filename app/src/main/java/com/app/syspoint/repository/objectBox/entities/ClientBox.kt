package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ClientBox (
    @Id(assignable = true)
    var id: Long = 0,
    var nombre_comercial: String? = null,
    var calle: String? = null,
    var numero: String? = null,
    var colonia: String? = null,
    var ciudad: String? = null,
    var codigo_postal: Int = 0,
    var fecha_registro: String? = null,
    var cuenta: String? = null,
    var status: Boolean = false,
    var consec: String? = null,
    var rango: String? = null,
    var lun: Int = 0,
    var mar: Int = 0,
    var mie: Int = 0,
    var jue: Int = 0,
    var vie: Int = 0,
    var sab: Int = 0,
    var dom: Int = 0,
    var lunOrder: Int  = 0,
    var marOrder: Int  = 0,
    var mieOrder: Int  = 0,
    var jueOrder: Int  = 0,
    var vieOrder: Int  = 0,
    var sabOrder: Int  = 0,
    var domOrder: Int  = 0,
    var visitado: Int  = 0,
    var latitud: String? = null,
    var longitud: String? = null,
    var contacto_phone: String? = null,
    var recordatorio: String? = null,
    var isRecordatorio: Boolean = false,
    var visitasNoefectivas: Int = 0,
    var isCredito: Boolean = false,
    var limite_credito: Double = 0.00,
    var saldo_credito: Double = 0.00,
    var matriz: String? = null,
    var date_sync: String? = null,
    var updatedAt: String? = null
)