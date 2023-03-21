package com.app.syspoint.models.sealed

sealed class LoginViewState {
    object LoggedIn: LoginViewState()
    data class LoginError(val error: String): LoginViewState()
    data class LoginVersionError(val error: String): LoginViewState()
    object LoadingDataStart: LoginViewState()
    object LoadingDataFinish: LoginViewState()
    object NotInternetConnection: LoginViewState()
    object ConnectedToInternet: LoginViewState()
    object NoSessionExists: LoginViewState()
}
