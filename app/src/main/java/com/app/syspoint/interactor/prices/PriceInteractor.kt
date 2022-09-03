package com.app.syspoint.interactor.prices

abstract class PriceInteractor {

    interface SendPricesListener {
        fun onSendPricesSuccess()
        fun onSendPricesError()
    }

    interface GetSpecialPricesListener {
        fun onGetSpecialPricesSuccess()
        fun onGetSpecialPricesError()
    }

    interface GetPricesByDateListener {
        fun onGetPricesByDateSuccess()
        fun onGetPricesByDateError()
    }

    interface GetPricesByClientListener {
        fun onGetPricesByClientSuccess()
        fun onGGetPricesByClientError()
    }

    open fun executeSendPrices(onSendPricesListener: SendPricesListener) {}
    open fun executeGetSpecialPrices(onGetSpecialPricesListener: GetSpecialPricesListener) {}
    open fun executeGetPricesByDate(onGetPricesByDateListener: GetPricesByDateListener) {}
    open fun executeGetPricesByClient(onGetPricesByClientListener: GetPricesByClientListener) {}
}