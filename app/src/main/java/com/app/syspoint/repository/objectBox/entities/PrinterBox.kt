package com.app.syspoint.repository.objectBox.entities

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class PrinterBox (
    @Id(assignable = true)
    var id: Long = 0,
    var name: String? = null,
    var address: String? = null,
    var idPrinter: Long? = null
)