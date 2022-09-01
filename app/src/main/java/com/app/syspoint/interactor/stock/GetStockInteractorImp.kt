package com.app.syspoint.interactor.stock

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GetStockInteractorImp: GetStockInteractor() {

    override fun executeGetStock() {
        super.executeGetStock()
        GlobalScope.launch {

        }
    }
}