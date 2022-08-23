package com.app.syspoint.interactor

import com.app.syspoint.http.Data
import retrofit2.Response

abstract class GetAllDataInteractor {
    interface OnGetAllDataListener {
        fun onGetAllDataSuccess()
        fun onGetAllDataError()
    }

    interface OnGetAllDataByDateListener {
        fun onGetAllDataByDateSuccess(response: Response<Data?>?)
        fun onGetAllDataByDateError()
    }

    open fun executeGetAllData(onGetAllDataListener: OnGetAllDataListener) {}
    open fun executeGetAllDataByDate(onGetAllDataByDateListener: OnGetAllDataByDateListener) {}
}