package com.app.syspoint.interactor.data

abstract class GetAllDataInteractor {
    interface OnGetAllDataListener {
        fun onGetAllDataSuccess()
        fun onGetAllDataError()
    }

    interface OnGetAllDataByDateListener {
        fun onGetAllDataByDateSuccess()
        fun onGetAllDataByDateError()
    }

    open suspend fun executeGetAllData(onGetAllDataListener: OnGetAllDataListener) {}
    open fun executeGetAllDataByDate(onGetAllDataByDateListener: OnGetAllDataByDateListener) {}
}