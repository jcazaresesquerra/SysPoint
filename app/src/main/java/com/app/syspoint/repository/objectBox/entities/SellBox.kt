package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
data class SellBox (
    @Id(assignable = true)
    var id: Long = 0,
    var venta: Long = 0,
    var tipo_doc: String? = null,
    var fecha: String? = null,
    var hora: String? = null,
    var clienteId: Long = 0,
    var empleadoId: Long = 0,
    var importe: Double = 0.0,
    var impuesto: Double = 0.0,
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
    var updatedAt: String? = null,
): BaseBox() {
    lateinit var client: ToOne<ClientBox>
    lateinit var employee: ToOne<EmployeeBox>
    lateinit var listaPartidas: ToMany<PlayingBox>

    constructor() : this(id = 0)
}