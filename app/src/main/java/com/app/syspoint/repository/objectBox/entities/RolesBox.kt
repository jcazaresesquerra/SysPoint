package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class RolesBox (
    @Id(assignable = true)
    var id: Long = 0,
    var empleadoId: Long = 0,
    var modulo: String? = null,
    var active: Boolean = false,
    var identificador: String? = null
) {
    lateinit var empleado: ToOne<EmployeeBox>

    constructor() : this(0)
}