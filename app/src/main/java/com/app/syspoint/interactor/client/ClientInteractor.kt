package com.app.syspoint.interactor.client

import com.app.syspoint.models.Client
import com.app.syspoint.repository.database.bean.ClienteBean

abstract class ClientInteractor {

    interface GetAllClientsListener {
        fun onGetAllClientsSuccess(clientList: List<ClienteBean>)
        fun onGetAllClientsError()
    }

    interface GetClientByIdListener {
        fun onGetClientByIdSuccess()
        fun onGetClientByIdError()
    }

    interface SaveClientListener {
        fun onSaveClientSuccess()
        fun onSaveClientError()
    }

    open fun executeGetAllClients(onGetAllClientsListener: GetAllClientsListener) {}
    open fun executeGetClientById(onGetClientByIdListener: GetClientByIdListener) {}
    open fun executeSaveClient(clientList: List<Client>, onSaveClientListener: SaveClientListener) {}
}