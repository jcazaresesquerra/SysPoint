package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import java.util.Date

@Entity
data class PlayingBox (
    @Id(assignable = true)
    var id: Long = 0,
    var venta: Long? = null,
    var articuloId: Long = 0,
    var cantidad: Int = 0,
    var precio: Double = 0.0,
    var impuesto: Double = 0.0,
    var descripcion: String? = null,
    var observ: String? = null,
    var fecha: Date? = null,
    var hora: String? = null
) {
    lateinit var articulo: ToOne<ProductBox>

    constructor() : this(id = 0)
}