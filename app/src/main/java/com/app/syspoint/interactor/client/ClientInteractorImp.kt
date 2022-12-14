package com.app.syspoint.interactor.client

import com.app.syspoint.models.Client
import com.app.syspoint.repository.request.RequestClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ClientInteractorImp: ClientInteractor() {

    override fun executeGetAllClients(onGetAllClientsListener: GetAllClientsListener) {
        super.executeGetAllClients(onGetAllClientsListener)
        GlobalScope.launch {
            RequestClient.requestAllClients(onGetAllClientsListener)
        }
    }

    override fun executeGetClientById(clientId: String, onGetClientByIdListener: GetClientByIdListener) {
        super.executeGetClientById(clientId, onGetClientByIdListener)
        GlobalScope.launch {
            RequestClient.requestClientById(clientId, onGetClientByIdListener)
        }
    }

    override fun executeSaveClient(clientList: List<Client>, onSaveClientListener: SaveClientListener) {
        super.executeSaveClient(clientList, onSaveClientListener)
        GlobalScope.launch {
            RequestClient.saveClients(clientList, onSaveClientListener)
        }
    }
}