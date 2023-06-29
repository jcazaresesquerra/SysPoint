package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class TempRuteClientBox (
    @Id(assignable = true)
    var id: Long = 0,
    var nombre_comercial: String? = null,
    var calle: String? = null,
    var numero: String? = null,
    var colonia: String? = null,
    var cuenta: String? = null,
    var rango: String? = null,
    var lun: Int = 0,
    var mar: Int = 0,
    var mie: Int = 0,
    var jue: Int = 0,
    var vie: Int = 0,
    var sab: Int = 0,
    var dom: Int = 0,
    var lunOrder: Int = 0,
    var marOrder: Int = 0,
    var mieOrder: Int = 0,
    var jueOrder: Int = 0,
    var vieOrder: Int = 0,
    var sabOrder: Int = 0,
    var domOrder: Int = 0,
    var order: Int = 0,
    var visitado: Int = 0,
    var latitud: String? = null,
    var longitud: String? = null,
    var phone_contact: String? = null,
    var status: Boolean = false,
    var isCredito: Boolean = false,
    var recordatorio: String? = null,
    var isRecordatorio: Boolean = false,
    var date_sync: String? = null,
    var updatedAt: String? = null,

    /**
     * this is the last sell given from getAllClientsAndLastSellByRute [POST]
     */
    var ventaClientId: Int = 0,
    var ventaFecha: String? = "",
    var ventaCreatedAt: String? = "",
    var ventaUpdatedAt: String? = ""
)