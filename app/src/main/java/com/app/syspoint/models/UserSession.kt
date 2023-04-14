package com.app.syspoint.models

data class UserSession (
     var usuario: String?,
     var password: String,
     var remember: Boolean
) {
     constructor(): this("", "", false)
}