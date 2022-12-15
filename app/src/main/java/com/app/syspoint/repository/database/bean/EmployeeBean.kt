package com.app.syspoint.repository.database.bean

import org.greenrobot.greendao.annotation.Entity
import org.greenrobot.greendao.annotation.Generated
import org.greenrobot.greendao.annotation.Id
import org.greenrobot.greendao.annotation.Index

@Entity(nameInDb = "empleados", indexes = [Index(value = "identificador")])

data class EmployeeBean(
    @Id(autoincrement = true)
    var id: Long? = null,
    var nombre: String? = null,
    var direccion: String? = null,
    var email: String? = null,
    var telefono: String? = null,
    var fecha_nacimiento: String? = null,
    var fecha_ingreso: String? = null,
    var contrasenia: String? = null,
    var identificador: String? = null,
    var status: Boolean = false,
    var region: String? = null,
    var path_image: String? = null,
    var rute: String? = null
) {

    @Generated
    constructor(): this(null, null, null, null, null,
         null, null, null, null, false,
        null, null, null)

}
