package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import java.util.Date

@Entity
data class ChargeBox(
    @Id(assignable = true)
    var id: Long = 0,
    var cobranza: String? = null,
    var cliente: String? = null,
    var importe: Double = 0.0,
    var saldo: Double? = null,
    var acuenta: Double? = null,
    var venta: Long? = 0,
    var estado: String? = null,
    var observaciones: String? = null,
    var fecha: String? = null,
    var hora: String? = null,
    var empleado: String? = null,
    var isCheck: Boolean = false,
    var abono: Boolean = false,
    var updatedAt: Date? = null,
    var stockId: Int = 0
)