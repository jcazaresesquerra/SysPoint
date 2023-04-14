package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class RoutingBox (
    @Id(assignable = true)
    var id: Long = 0,
    var region: String? = null,
    var ruta: String? = null,
    var dia: Int = 0,
    var fecha: String? = null
) {
    constructor(): this(id = 0, dia = 0)
}