package com.app.syspoint.interactor.charge

import com.app.syspoint.models.Payment
import com.app.syspoint.repository.request.RequestCharge
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChargeInteractorImp: ChargeInteractor() {

    override fun executeGetCharge(onGetChargeListener: OnGetChargeListener) {
        super.executeGetCharge(onGetChargeListener)
        GlobalScope.launch {
            RequestCharge.requestGetCharge(onGetChargeListener)
        }
    }

    override fun executeGetChargeByClient(account: String, onGetChargeByClientListener: OnGetChargeByClientListener) {
        super.executeGetChargeByClient(account, onGetChargeByClientListener)
        GlobalScope.launch {
            RequestCharge.requestGetChargeByClient(account, onGetChargeByClientListener)
        }
    }

    override fun executeSaveCharge(charges: List<Payment>, onSaveChargeListener: OnSaveChargeListener) {
        super.executeSaveCharge(charges, onSaveChargeListener)
        GlobalScope.launch {
            RequestCharge.saveCharge(charges, onSaveChargeListener)
        }
    }

    override fun executeUpdateCharge(charges: List<Payment>, onUpdateChargeListener: OnUpdateChargeListener) {
        super.executeUpdateCharge(charges, onUpdateChargeListener)
        GlobalScope.launch {
            RequestCharge.updateCharge(charges, onUpdateChargeListener)
        }
    }
}