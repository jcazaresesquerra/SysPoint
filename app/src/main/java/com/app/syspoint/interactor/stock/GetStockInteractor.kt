package com.app.syspoint.interactor.stock

abstract class GetStockInteractor {
    interface OnGetStockListener {
        fun onGerStockSuccess()
        fun onGetStockError()
    }

    open fun executeGetStock() {}
}