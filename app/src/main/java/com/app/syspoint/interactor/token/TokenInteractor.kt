package com.app.syspoint.interactor.token

abstract class TokenInteractor {
    interface OnGetTokenListener {
        fun onGetTokenSuccess(token: String?, currentVersion: String)
        fun onGetTokenError(currentVersion: String)
    }

    open fun executeGetToken(onGetTokenListener: OnGetTokenListener) {}
}