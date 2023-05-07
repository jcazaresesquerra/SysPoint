package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class VisitsBox (
    @Id(assignable = true)
    var id: Long = 0,
    var fecha: String? = null,
    var hora: String? = null,
    var clienteId: Long = 0,
    var empleadoId: Long = 0,
    var latidud: String? = null,
    var longitud: String? = null,
    var motivo_visita: String? = null,
    var updatedAt: String? = null
) {
    lateinit var cliente: ToOne<ClientBox>
    lateinit var empleado: ToOne<EmployeeBox>

    constructor() : this(id = 0)
}