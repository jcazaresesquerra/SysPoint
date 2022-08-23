package com.app.syspoint.interactor

import android.widget.Toast
import com.app.syspoint.db.bean.*
import com.app.syspoint.db.dao.*
import com.app.syspoint.http.ApiServices
import com.app.syspoint.http.Data
import com.app.syspoint.http.PointApi
import com.app.syspoint.json.*
import com.app.syspoint.repository.request.RequestData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class GetAllDataInteractorImp: GetAllDataInteractor() {

    override fun executeGetAllData(onGetAllDataListener: OnGetAllDataListener) {
        GlobalScope.launch {
            RequestData.requestAllData(onGetAllDataListener)
        }
    }

    override fun executeGetAllDataByDate(onGetAllDataByDateListener: OnGetAllDataByDateListener) {

    }
}