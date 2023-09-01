package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class ReturnBox (
    @Id(assignable = true)
    var id: Long = 0,
    var sellId: Long = 0,
    var articulo: String? = null,
    var descripcion: String? = null,
    var cantidad: Int = 0,
    var precio: Double = 0.0,
    var impuesto: Double = 0.0,
    var observ: String? = null,
    var fecha: String? = null,
    var hora: String? = null,
    var clienteId: Long = 0,
    var empleadoId: Long = 0,
    var importe: Double = 0.0,
    var datos: String? = null,
    var estado: String? = null,
    var corte: String? = null,
    var temporal: Int = 0,
    var sync: Int = 0,
    var latidud: String? = null,
    var longitud: String? = null,
    var ticket: String? = null,
    var tipo_venta: String? = null,
    var usuario_cancelo: String? = null,
    var cobranza: String? = null,
    var factudado: String? = null,
    var stockId: Int = 0,
)