package com.app.syspoint.models.sealed

import com.app.syspoint.repository.database.bean.ClienteBean

sealed class GetClientsByRuteViewState {
    data class GetClientsByRuteSuccess(val clients: List<ClienteBean>): GetClientsByRuteViewState()
    data class GetClientsByRuteError(val message: String): GetClientsByRuteViewState()
}