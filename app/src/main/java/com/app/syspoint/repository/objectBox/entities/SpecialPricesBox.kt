package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class SpecialPricesBox (
    @Id(assignable = true)
    var id: Long = 0,
    var cliente: String? = null,
    var articulo: String? = null,
    var precio: Double = 0.0,
    var active: Boolean = false,
    var fecha_sync: String? = null
)