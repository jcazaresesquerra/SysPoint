package com.app.syspoint.interactor.token

abstract class TokenInteractor {
    interface OnGetTokenListener {
        fun onGetTokenSuccess(token: String?, currentVersion: String)
        fun onGetTokenError(baseUpdateUrl: String, currentVersion: String, throwable: Throwable?)
    }

    open fun executeGetToken(onGetTokenListener: OnGetTokenListener) {}
}