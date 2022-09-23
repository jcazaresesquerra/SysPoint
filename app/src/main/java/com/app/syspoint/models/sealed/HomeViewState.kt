package com.app.syspoint.models.sealed

import com.app.syspoint.repository.database.bean.ClientesRutaBean
import com.app.syspoint.repository.database.bean.RuteoBean

sealed class HomeViewState {
    data class ClientRuteDefined(val clientRute: List<ClientesRutaBean>): HomeViewState()
    data class UpdateRute(val ruteBean: RuteoBean): HomeViewState()
    object CreateRute: HomeViewState()
    object LoadingStart: HomeViewState()
    object GettingUpdates: HomeViewState()
    object LoadingFinish: HomeViewState()
    object ErrorWhileGettingData: HomeViewState()
}
