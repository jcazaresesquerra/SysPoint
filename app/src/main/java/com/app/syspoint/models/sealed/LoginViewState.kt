package com.app.syspoint.models.sealed

sealed class LoginViewState {
    object LoggedIn: LoginViewState()
    object LoginError: LoginViewState()
}
