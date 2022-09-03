package com.app.syspoint.interactor.charge

abstract class ChargeInteractor {
    interface OnGetChargeListener {
        fun onGetChargeSuccess()
        fun onGetChargeError()
    }

    interface OnGetChargeByClientListener {
        fun onGetChargeByClientSuccess()
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
    open fun executeGetChargeByClient(onGetChargeByClientListener: OnGetChargeByClientListener) {}
    open fun executeSaveCharge(onSaveChargeListener: OnSaveChargeListener) {}
    open fun executeUpdateCharge(onUpdateChargeListener: OnUpdateChargeListener) {}
}