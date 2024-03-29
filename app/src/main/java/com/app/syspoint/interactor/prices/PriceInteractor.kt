package com.app.syspoint.interactor.prices

import com.app.syspoint.models.Price
import com.app.syspoint.models.json.SpecialPriceJson
import com.app.syspoint.repository.objectBox.entities.SpecialPricesBox
import retrofit2.Response

abstract class PriceInteractor {

    interface SendPricesListener {
        fun onSendPricesSuccess()
        fun onSendPricesError()
    }

    interface GetSpecialPricesListener {
        fun onGetSpecialPricesSuccess(priceList: List<SpecialPricesBox>)
        fun onGetSpecialPricesError()
    }

    interface GetPricesByDateListener {
        fun onGetPricesByDateSuccess(response: Response<SpecialPriceJson>)
        fun onGetPricesByDateError()
    }

    interface GetPricesByClientListener {
        fun onGetPricesByClientSuccess(pricesByClientList: List<SpecialPricesBox>)
        fun onGGetPricesByClientError()
    }

    open fun executeSendPrices(priceList: List<Price>, onSendPricesListener: SendPricesListener) {}
    open fun executeGetSpecialPrices(onGetSpecialPricesListener: GetSpecialPricesListener) {}
    open fun executeGetPricesByDate(onGetPricesByDateListener: GetPricesByDateListener) {}
    open fun executeGetPricesByClient(client: String, onGetPricesByClientListener: GetPricesByClientListener) {}
}