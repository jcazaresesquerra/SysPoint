package com.app.syspoint.usecases

import com.app.syspoint.interactor.charge.ChargeInteractor
import com.app.syspoint.models.Resource
import com.app.syspoint.repository.database.bean.CobranzaBean
import com.app.syspoint.repository.request.RequestCharge
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class GetChargeUseCase {

    suspend operator fun invoke(): Flow<Resource<List<CobranzaBean>>> = callbackFlow {
        trySend(Resource.Loading)
        val callback = RequestCharge.requestGetCharge(object: ChargeInteractor.OnGetChargeListener {
            override fun onGetChargeSuccess(chargeList: List<CobranzaBean>) {
                trySend(Resource.Success(chargeList))
            }

            override fun onGetChargeError() {
                trySend(Resource.Error("Ha ocurrido un error al obtener cobranzas"))
            }
        })
        awaitClose {
            callback.cancel()
        }
    }
}