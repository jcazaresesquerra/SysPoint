package com.app.syspoint.models.sealed

import com.app.syspoint.repository.database.bean.ClientesRutaBean
import com.app.syspoint.repository.database.bean.CobranzaBean
import com.app.syspoint.repository.database.bean.RuteoBean

sealed class HomeViewState {
    data class ClientRuteDefined(val clientRute: List<ClientesRutaBean>): HomeViewState()
    data class UpdateRute(val ruteBean: RuteoBean): HomeViewState()
    data class RuteLoaded(val data: List<ClientesRutaBean?>): HomeViewState()
    object CreateRute: HomeViewState()
    object GettingUpdates: HomeViewState()
    object ErrorWhileGettingData: HomeViewState()
}

sealed class GetChargeViewState {
    data class GetChargeSuccess(val charges: List<CobranzaBean>): GetChargeViewState()
    data class GetChargeError(val error: String): GetChargeViewState()
    object LoadingStart: GetChargeViewState()
    object LoadingFinish: GetChargeViewState()
}

sealed class SetRuteViewState {
    object Loading: SetRuteViewState()
    data class RuteDefined(val clientRute: List<ClientesRutaBean>): SetRuteViewState()
    object RuteDefinedWithOutClients: SetRuteViewState()
}

sealed class HomeLoadingViewState {
    object LoadingStart: HomeLoadingViewState()
    object LoadingFinish: HomeLoadingViewState()
}

sealed class RequestingDataViewState {
    object RequestingDataStart: RequestingDataViewState()
    object RequestingDataFinish: RequestingDataViewState()
}
