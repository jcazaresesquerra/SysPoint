package com.app.syspoint.interactor.charge

import com.app.syspoint.models.Payment
import com.app.syspoint.repository.objectBox.entities.ChargeBox

abstract class ChargeInteractor {
    interface OnGetChargeListener {
        fun onGetChargeSuccess(chargeList: List<ChargeBox>)
        fun onGetChargeError()
    }

    interface OnGetChargeByClientListener {
        fun onGetChargeByClientSuccess(chargeByClientList: List<ChargeBox>)
        fun onGetChargeByClientError()
    }

    interface OnGetChargeByEmployeeListener {
        fun onGetChargeByEmployeeSuccess(chargeByClientList: List<ChargeBox>)
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

    open suspend fun executeGetCharge(onGetChargeListener: OnGetChargeListener) {}
    open fun executeGetChargeByEmployee(id: String, onGetChargeByEmployeeListener: OnGetChargeByEmployeeListener) {}
    open fun executeGetChargeByClient(account: String, onGetChargeByClientListener: OnGetChargeByClientListener) {}
    open fun executeSaveCharge(charges: List<Payment>, onSaveChargeListener: OnSaveChargeListener) {}
    open fun executeUpdateCharge(charges: List<Payment>, onUpdateChargeListener: OnUpdateChargeListener) {}
}