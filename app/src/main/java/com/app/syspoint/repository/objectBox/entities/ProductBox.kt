package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
class ProductBox (
    @Id(assignable = true)
    var id: Long = 0,
    var articulo: String? = null,
    var descripcion: String? = null,
    var status: String? = null,
    var precio: Double = 0.0,
    var iva: Int = 0,
    var codigo_barras: String? = null,
    var path_img: String? = null,
    var existencia: Int = 0,
    var updatedAt: String? = null
) {
    constructor() : this(id = 0)
}