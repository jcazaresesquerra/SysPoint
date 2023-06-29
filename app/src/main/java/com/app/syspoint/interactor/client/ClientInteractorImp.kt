package com.app.syspoint.interactor.client

import com.app.syspoint.models.Client
import com.app.syspoint.repository.request.RequestClient
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ClientInteractorImp: ClientInteractor() {


    @Synchronized
    override fun executeGetAllClients(onGetAllClientsListener: GetAllClientsListener) {
        super.executeGetAllClients(onGetAllClientsListener)
        GlobalScope.launch {
            //RequestClient.requestAllClients(onGetAllClientsListener)
        }
    }

    @Synchronized
    override fun executeGetAllClientsByDate(ruteByEmployee: String, day: Int, onGetAllClientsListener: GetAllClientsListener) {
        super.executeGetAllClientsByDate(ruteByEmployee, day, onGetAllClientsListener)
        GlobalScope.launch {
            //RequestClient.requestGetAllClientsByDate(ruteByEmployee, day, onGetAllClientsListener)
        }
    }

    @Synchronized
    override fun executeGetAllClientsAndLastSellByRute(ruteByEmployee: String, day: Int, onGetAllClientsListener: GetAllClientsListener) {
        super.executeGetAllClientsAndLastSellByRute(ruteByEmployee, day, onGetAllClientsListener)
        GlobalScope.launch {
            RequestClient.requestGetAllClientsAndLastSellByRute(ruteByEmployee, day, onGetAllClientsListener)
        }
    }

    @Synchronized
    override fun executeGetClientById(clientId: String, onGetClientByIdListener: GetClientByIdListener) {
        super.executeGetClientById(clientId, onGetClientByIdListener)
        GlobalScope.launch {
            RequestClient.requestClientById(clientId, onGetClientByIdListener)
        }
    }

    @Synchronized
    override fun executeSaveClient(clientList: List<Client>, onSaveClientListener: SaveClientListener) {
        super.executeSaveClient(clientList, onSaveClientListener)
        GlobalScope.launch {
            RequestClient.saveClients(clientList, onSaveClientListener)
        }
    }

    @Synchronized
    override fun executeFindClient(clientName: String, onFindClientListener: FindClientListener) {
        super.executeFindClient(clientName, onFindClientListener)
        GlobalScope.launch {
            RequestClient.findClient(clientName, onFindClientListener)
        }
    }

    @Synchronized
    override fun executeGetClientByAccount(account: String, onGetClientByAccount: GetClientByAccount) {
        super.executeGetClientByAccount(account, onGetClientByAccount)
        GlobalScope.launch {
            RequestClient.requestGetClientByAccount(account, onGetClientByAccount)
        }
    }

    @Synchronized
    override fun executeGetLasClient(onGetLasCliente: GetLastClient) {
        super.executeGetLasClient(onGetLasCliente)
        GlobalScope.launch {
            RequestClient.getLastCLient(onGetLasCliente)
        }
    }
}