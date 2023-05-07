package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class EmployeeBox(
    @Id(assignable = true)
    var id: Long = 0,
    var nombre: String? = null,
    var direccion: String? = null,
    var email: String? = null,
    var telefono: String? = null,
    var fecha_nacimiento: String? = null,
    var fecha_ingreso: String? = null,
    var contrasenia: String? = null,
    var identificador: String? = null,
    var status: Boolean = false,
    var path_image: String? = null,
    var rute: String? = null,
    var updatedAt: String? = null
) {
    constructor() : this(0, "", "", "", "", "", "",
        "", "", false, "","", "")
}