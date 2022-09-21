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

    override fun executeGetClientById(onGetClientByIdListener: GetClientByIdListener) {
        super.executeGetClientById(onGetClientByIdListener)
        GlobalScope.launch {
            RequestClient.requestClientById()
        }
    }

    override fun executeSaveClient(clientList: List<Client>, onSaveClientListener: SaveClientListener) {
        super.executeSaveClient(clientList, onSaveClientListener)
        GlobalScope.launch {
            RequestClient.saveClients(clientList, onSaveClientListener)
        }
    }
}