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

    interface FindClientListener {
        fun onFindClientSuccess(clientList: List<ClienteBean>)
        fun onFindClientError()
    }

    interface GetClientByAccount {
        fun onGetClientSuccess()
        fun onGetClientError()
    }

    open fun executeGetAllClients(onGetAllClientsListener: GetAllClientsListener) {}
    open fun executeGetAllClientsByDate(ruteByEmployee: String, day: Int, onGetAllClientsListener: GetAllClientsListener) {}
    open fun executeGetClientById(clientId: String, onGetClientByIdListener: GetClientByIdListener) {}
    open fun executeSaveClient(clientList: List<Client>, onSaveClientListener: SaveClientListener) {}
    open fun executeFindClient(clientName: String, onFindClientListener: FindClientListener) {}
    open fun executeGetClientByAccount(account: String, onGetClientByAccount: GetClientByAccount) {}
}