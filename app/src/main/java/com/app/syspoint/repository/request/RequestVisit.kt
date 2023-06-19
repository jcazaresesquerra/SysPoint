package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.visit.VisitInteractor
import com.app.syspoint.models.Visit
import com.app.syspoint.models.json.VisitJson
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestVisit {
    companion object: BaseRequest() {
        fun saveVisit(visits: List<Visit>, onSaveVisitListener: VisitInteractor.OnSaveVisitListener) {
            val employee = getEmployee()
            val visitaJsonRF = VisitJson()
            visitaJsonRF.visits = visits
            visitaJsonRF.clientId = employee?.clientId?:"tenet"
            val json = Gson().toJson(visitaJsonRF)
            Log.d("SinEmpleados", json)

            val loadVisits = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).sendVisita(visitaJsonRF)

            loadVisits.enqueue(object: Callback<VisitJson> {
                override fun onResponse(call: Call<VisitJson>, response: Response<VisitJson>) {
                    if (response.isSuccessful) {
                        onSaveVisitListener.onSaveVisitSuccess()
                    } else {
                        val error = response.errorBody()!!.string()
                        onSaveVisitListener.onSaveVisitError()
                    }
                }

                override fun onFailure(call: Call<VisitJson>, t: Throwable) {
                    onSaveVisitListener.onSaveVisitError()
                }

            })

        }
    }
}