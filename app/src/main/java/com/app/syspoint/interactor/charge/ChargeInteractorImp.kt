package com.app.syspoint.interactor.charge

import com.app.syspoint.models.Payment
import com.app.syspoint.repository.request.RequestCharge
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ChargeInteractorImp: ChargeInteractor() {

    override suspend fun executeGetCharge(onGetChargeListener: OnGetChargeListener) {
        super.executeGetCharge(onGetChargeListener)
        RequestCharge.requestGetCharge(onGetChargeListener)
    }

    @Synchronized
    override fun executeGetChargeByEmployee(id: String, onGetChargeByEmployeeListener: OnGetChargeByEmployeeListener) {
        super.executeGetChargeByEmployee(id, onGetChargeByEmployeeListener)
        GlobalScope.launch {
            RequestCharge.requestGetChargeByEmployee(id, onGetChargeByEmployeeListener)
        }
    }

    @Synchronized
    override fun executeGetChargeByClient(account: String, onGetChargeByClientListener: OnGetChargeByClientListener) {
        super.executeGetChargeByClient(account, onGetChargeByClientListener)
        GlobalScope.launch {
            RequestCharge.requestGetChargeByClient(account, onGetChargeByClientListener)
        }
    }

    @Synchronized
    override fun executeSaveCharge(charges: List<Payment>, onSaveChargeListener: OnSaveChargeListener) {
        super.executeSaveCharge(charges, onSaveChargeListener)
        GlobalScope.launch {
            RequestCharge.saveCharge(charges, onSaveChargeListener)
        }
    }

    @Synchronized
    override fun executeUpdateCharge(charges: List<Payment>, onUpdateChargeListener: OnUpdateChargeListener) {
        super.executeUpdateCharge(charges, onUpdateChargeListener)
        GlobalScope.launch {
            RequestCharge.updateCharge(charges, onUpdateChargeListener)
        }
    }
}