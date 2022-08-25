package com.app.syspoint.repository.database.bean

data class UserSession (
     var usuario: String?,
     var password: String,
     var remember: Boolean
) {
     constructor(): this("", "", false)
}