package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne

@Entity
data class CashCloseBox (
    @Id(assignable = true)
    var id: Long = 0,
    var nombre: String? = null,
    var clienteId: Long = 0,
    var productoId: Long = 0,
    var cantidad: Int = 0,
    var precio: Double = 0.0,
    var descripcion: String? = null,
    var impuesto: Double = 0.0,
    var tipoVenta: String? = null,
    var estado: String? = null,
    var sellId: Long = 0L
) {
    lateinit var client: ToOne<ClientBox>
    lateinit var product: ToOne<ProductBox>

    constructor() : this(id = 0)

}