package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne

@Entity
data class CobrosBox(
    @Id(assignable = true)
    var id: Long = 0,
    var cobro: Int = 0,
    var fecha: String? = null,
    var hora: String? = null,
    var clienteId: Long = 0,
    var empleadoId: Long = 0,
    var importe: Double = 0.0,
    var estado: String? = null,
    var temporal: Int = 0,
    var sinc: Int = 0,

): BaseBox() {
    lateinit var cliente: ToOne<ClientBox>
    lateinit var empleado: ToOne<EmployeeBox>
    lateinit var listaPartidas: ToMany<CobdetBox>

    constructor() : this(id = 0)
}