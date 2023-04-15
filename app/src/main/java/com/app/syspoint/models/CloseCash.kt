package com.app.syspoint.models

import java.util.Date

data class CloseCash(
    var employeeIdentifier: String = "",
    var comertialName: String = "",
    var abono: Double = 0.0,
    var ticket: String = "",
    var updatedAt: Date? = null,
    var employee: String = "",
    var status: String = "",
    var stockId: Int = 0
)