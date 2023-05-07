package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class StockHistoryBox (
    @Id(assignable = true)
    var id: Long = 0,
    var articuloId: Long = 0,
    var cantidad: Int = 0,
    var articulo_clave: String? = null
) {
    lateinit var articulo: ToOne<ProductBox>

    constructor() : this(id = 0)
}