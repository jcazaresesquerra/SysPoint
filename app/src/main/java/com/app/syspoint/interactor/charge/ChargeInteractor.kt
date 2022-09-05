package com.app.syspoint.interactor.charge

import com.app.syspoint.models.Payment
import com.app.syspoint.models.json.PaymentJson
import retrofit2.Response

abstract class ChargeInteractor {
    interface OnGetChargeListener {
        fun onGetChargeSuccess(response: Response<PaymentJson>)
        fun onGetChargeError()
    }

    interface OnGetChargeByClientListener {
        fun onGetChargeByClientSuccess(response: Response<PaymentJson>)
        fun onGetChargeByClientError()
    }

    interface OnSaveChargeListener {
        fun onSaveChargeSuccess()
        fun onSaveChargeError()
    }

    interface OnUpdateChargeListener {
        fun onUpdateChargeSuccess()
        fun onUpdateChargeError()
    }

    open fun executeGetCharge(onGetChargeListener: OnGetChargeListener) {}
    open fun executeGetChargeByClient(account: String, onGetChargeByClientListener: OnGetChargeByClientListener) {}
    open fun executeSaveCharge(charges: List<Payment>, onSaveChargeListener: OnSaveChargeListener) {}
    open fun executeUpdateCharge(charges: List<Payment>, onUpdateChargeListener: OnUpdateChargeListener) {}
}