package com.app.syspoint.models.sealed

import com.app.syspoint.repository.objectBox.entities.RoutingBox
import com.app.syspoint.repository.objectBox.entities.RuteClientBox

sealed class HomeViewState {
    data class ClientRuteDefined(val clientRute: List<RuteClientBox>): HomeViewState()
    data class UpdateRute(val ruteBean: RoutingBox): HomeViewState()
    data class RuteLoaded(val data: List<RuteClientBox?>): HomeViewState()
    object CreateRute: HomeViewState()
    object GettingUpdates: HomeViewState()
    object ErrorWhileGettingData: HomeViewState()
}

sealed class GetChargeViewState {
    object LoadingStart: GetChargeViewState()
    object LoadingFinish: GetChargeViewState()
}

sealed class SetRuteViewState {
    object Loading: SetRuteViewState()
    data class RuteDefined(val clientRute: List<RuteClientBox>): SetRuteViewState()
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
