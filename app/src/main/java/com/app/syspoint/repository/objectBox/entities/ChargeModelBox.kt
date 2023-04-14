package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Id

data class ChargeModelBox(
    @Id(assignable = true)
    var id: Long = 0,
    var venta: Long = 0,
    var cobranza: String? = null,
    var importe: Double = 0.0,
    var saldo: Double = 0.0,
    var acuenta: Double = 0.0,
    var no_referen: String? = null
)