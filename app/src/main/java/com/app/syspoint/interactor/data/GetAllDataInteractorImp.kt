package com.app.syspoint.interactor.data

import com.app.syspoint.repository.request.RequestData
import kotlinx.coroutines.*

class GetAllDataInteractorImp: GetAllDataInteractor() {

    override suspend fun executeGetAllData(onGetAllDataListener: OnGetAllDataListener) {
        withContext(Dispatchers.IO) {
            RequestData.requestAllData(onGetAllDataListener)
        }
    }

    override fun executeGetAllDataByDate(onGetAllDataByDateListener: OnGetAllDataByDateListener) {
        GlobalScope.launch {
            RequestData.requestAllDataByDate(onGetAllDataByDateListener)
        }
    }
}