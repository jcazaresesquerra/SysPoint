package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * this is the last sell given from getAllClientsAndLastSellByRute [POST]
 */

@Entity
data class LastSellBox (
    @Id(assignable = true)
    var id: Long = 0,
    var ventaClientId: Int = 0,
    var ventaFecha: String? = "",
    var ventaCreatedAt: String? = "",
    var ventaUpdatedAt: String? = ""
)