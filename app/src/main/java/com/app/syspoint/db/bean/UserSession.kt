package com.app.syspoint.db.bean

data class UserSession (
     var usuario: String?,
     var password: String,
     var remember: Boolean
) {
     constructor(): this("", "", false)
}