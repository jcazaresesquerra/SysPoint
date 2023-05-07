package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
class SessionBox (
    @Id(assignable = true)
    var id: Long = 0,
    var remember: Boolean = false,
    var empleadoId: Long = 0,

) {
    lateinit var employee: ToOne<EmployeeBox>

    constructor(): this(id = 0, remember = false, empleadoId = 0)
}