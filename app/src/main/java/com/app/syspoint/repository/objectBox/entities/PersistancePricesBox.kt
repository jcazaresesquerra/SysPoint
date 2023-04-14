package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class PersistancePricesBox (
    @Id(assignable = true)
    var id: Long = 0,
    var mostrar: String? = null,
    var valor: Long? = null
)