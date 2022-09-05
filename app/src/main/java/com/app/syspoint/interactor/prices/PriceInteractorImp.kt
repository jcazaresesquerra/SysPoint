package com.app.syspoint.interactor.prices

import com.app.syspoint.models.Price
import com.app.syspoint.repository.request.RequestPrice
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PriceInteractorImp: PriceInteractor() {

    override fun executeGetPricesByClient(client: String, onGetPricesByClientListener: GetPricesByClientListener) {
        super.executeGetPricesByClient(client, onGetPricesByClientListener)
        GlobalScope.launch {
            RequestPrice.requestPricesByClient(client, onGetPricesByClientListener)
        }
    }

    override fun executeGetPricesByDate(onGetPricesByDateListener: GetPricesByDateListener) {
        super.executeGetPricesByDate(onGetPricesByDateListener)
    }

    override fun executeGetSpecialPrices(onGetSpecialPricesListener: GetSpecialPricesListener) {
        super.executeGetSpecialPrices(onGetSpecialPricesListener)
        GlobalScope.launch {
            RequestPrice.requestAllPrices(onGetSpecialPricesListener)
        }
    }

    override fun executeSendPrices(priceList: List<Price>, onSendPricesListener: SendPricesListener) {
        super.executeSendPrices(priceList, onSendPricesListener)
        GlobalScope.launch {
            RequestPrice.requestSavePrice(priceList, onSendPricesListener)
        }
    }
}