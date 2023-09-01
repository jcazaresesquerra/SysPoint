package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class SellModelBox (
    @Id(assignable = true)
    var id: Long = 0,
    var articulo: String? = null,
    var descripcion: String? = null,
    var cantidad: Int = 0,
    var returnQuantity: Int = 0,
    var returnId: Long = 0,
    var precio: Double = 0.0,
    var impuesto: Double = 0.0,
    var observ: String? = null
)