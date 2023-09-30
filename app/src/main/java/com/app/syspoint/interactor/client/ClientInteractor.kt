package com.app.syspoint.interactor.client

import com.app.syspoint.models.Client
import com.app.syspoint.repository.objectBox.entities.ClientBox

abstract class ClientInteractor {

    interface GetAllClientsListener {
        fun onGetAllClientsSuccess(clientList: List<ClientBox>)
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
        fun onFindClientSuccess(clientList: List<ClientBox>)
        fun onFindClientError()
    }

    interface GetClientByAccount {
        fun onGetClientSuccess()
        fun onGetClientError()
    }

    interface GetLastClient {
        fun onGetLastClientSuccess(client: ClientBox)
        fun onGetLastClientError()
    }
    open fun executeGetAllClients(onGetAllClientsListener: GetAllClientsListener) {}
    @Deprecated("this fucntion needs to be replaced by executeGetAllClientsAndLastSellByRute")
    open fun executeGetAllClientsByDate(ruteByEmployee: String, day: Int, onGetAllClientsListener: GetAllClientsListener) {}
    open fun executeGetAllClientsAndLastSellByRute(ruteByEmployee: String, day: Int, onGetAllClientsListener: GetAllClientsListener) {}
    open fun executeGetClientById(ids: List<String?>, onGetClientByIdListener: GetClientByIdListener) {}
    open fun executeSaveClient(clientList: List<Client>, onSaveClientListener: SaveClientListener) {}
    open fun executeFindClient(clientName: String, onFindClientListener: FindClientListener) {}
    open fun executeGetClientByAccount(account: String, onGetClientByAccount: GetClientByAccount) {}
    open fun executeGetLasClient(onGetLasClient: GetLastClient) {}
}