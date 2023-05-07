package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class StockBox (
    @Id(assignable = true)
    var id: Long = 0,
    var estado: String? = null,
    var articuloId: Long = 0,
    var cantidad: Int = 0,
    var lastCantidad: Int = 0,
    var totalCantidad: Int = 0,
    var precio: Double = 0.0,
    var impuesto: Double = 0.0,
    var fecha: String? = null,
    var hora: String? = null,
    var articulo_clave: String? = null,
    var stockId: Int = 0,
    var loadId: Int = 0,
): BaseBox() {
    lateinit var articulo: ToOne<ProductBox>

    constructor() : this(id = 0)
}