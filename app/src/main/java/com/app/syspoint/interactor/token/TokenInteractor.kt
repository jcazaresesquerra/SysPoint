package com.app.syspoint.interactor.token

abstract class TokenInteractor {
    interface OnGetTokenListener {
        fun onGetTokenSuccess(token: String?)
        fun onGetTokenError()
    }

    open fun executeGetToken(onGetTokenListener: OnGetTokenListener) {}
}