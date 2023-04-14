package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class CobdetBox (
    @Id(assignable = true)
    var id: Long = 0,
    var cobro: Long? = null,
    var cobranza: String? = null,
    var clienteId: Long = 0,
    var fecha: String? = null,
    var hora: String? = null,
    var importe: Double = 0.0,
    var venta: Long = 0,
    var empleadoId: Long = 0,
    var abono: Int? = null,
    var saldo: Double = 0.0,
) {
    lateinit var cliente: ToOne<ClientBox>
    lateinit var empleado: ToOne<EmployeeBox>

    constructor() : this(id = 0)
}