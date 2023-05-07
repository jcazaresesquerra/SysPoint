package com.app.syspoint.models.sealed

import com.app.syspoint.repository.objectBox.entities.ClientBox

sealed class GetClientsByRuteViewState {
    data class GetClientsByRuteSuccess(val clients: List<ClientBox>): GetClientsByRuteViewState()
    data class GetClientsByRuteError(val message: String): GetClientsByRuteViewState()
}