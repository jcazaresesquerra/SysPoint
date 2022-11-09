package com.app.syspoint.interactor.charge

import com.app.syspoint.models.Payment
import com.app.syspoint.models.json.PaymentJson
import com.app.syspoint.repository.database.bean.CobranzaBean
import retrofit2.Response

abstract class ChargeInteractor {
    interface OnGetChargeListener {
        fun onGetChargeSuccess(chargeList: List<CobranzaBean>)
        fun onGetChargeError()
    }

    interface OnGetChargeByClientListener {
        fun onGetChargeByClientSuccess(chargeByClientList: List<CobranzaBean>)
        fun onGetChargeByClientError()
    }

    interface OnGetChargeByEmployeeListener {
        fun onGetChargeByEmployeeSuccess(chargeByClientList: List<CobranzaBean>)
        fun onGetChargeByEmployeeError()
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
    open fun executeGetChargeByEmployee(id: String, onGetChargeByEmployeeListener: OnGetChargeByEmployeeListener) {}
    open fun executeGetChargeByClient(account: String, onGetChargeByClientListener: OnGetChargeByClientListener) {}
    open fun executeSaveCharge(charges: List<Payment>, onSaveChargeListener: OnSaveChargeListener) {}
    open fun executeUpdateCharge(charges: List<Payment>, onUpdateChargeListener: OnUpdateChargeListener) {}
}