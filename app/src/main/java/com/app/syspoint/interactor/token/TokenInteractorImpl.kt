package com.app.syspoint.interactor.token

import com.app.syspoint.repository.request.RequestToken
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class TokenInteractorImpl: TokenInteractor() {

    override fun executeGetToken(onGetTokenListener: OnGetTokenListener) {
        super.executeGetToken(onGetTokenListener)
        GlobalScope.launch {
            RequestToken.getTokenByAppVersion(onGetTokenListener)
        }
    }
}