package com.app.syspoint.interactor

import com.app.syspoint.repository.request.RequestData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GetAllDataInteractorImp: GetAllDataInteractor() {

    override fun executeGetAllData(onGetAllDataListener: OnGetAllDataListener) {
        GlobalScope.launch {
            RequestData.requestAllData(onGetAllDataListener)
        }
    }

    override fun executeGetAllDataByDate(onGetAllDataByDateListener: OnGetAllDataByDateListener) {
        GlobalScope.launch {
            RequestData.requestAllDataByDate(onGetAllDataByDateListener)
        }
    }
}