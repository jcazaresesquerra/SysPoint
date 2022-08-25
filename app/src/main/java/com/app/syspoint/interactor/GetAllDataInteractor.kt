package com.app.syspoint.interactor

abstract class GetAllDataInteractor {
    interface OnGetAllDataListener {
        fun onGetAllDataSuccess()
        fun onGetAllDataError()
    }

    interface OnGetAllDataByDateListener {
        fun onGetAllDataByDateSuccess()
        fun onGetAllDataByDateError()
    }

    open fun executeGetAllData(onGetAllDataListener: OnGetAllDataListener) {}
    open fun executeGetAllDataByDate(onGetAllDataByDateListener: OnGetAllDataByDateListener) {}
}