package com.app.syspoint.models.sealed

import com.app.syspoint.models.enums.SellType
import com.app.syspoint.repository.objectBox.entities.SellModelBox
import java.util.HashMap

sealed class SellViewState {
    data class SellsLoaded(val data: List<SellModelBox?>): SellViewState()
    data class SellsRefresh(val data: List<SellModelBox?>): SellViewState()
    data class ClientsLoaded(val clientName: String, val account: String, val saldoCredito: String): SellViewState()
    data class ItemAdded(val data: List<SellModelBox?>): SellViewState()
    data class ComputedImports(val totalFormat: String, val subtotalFormat: String, val importFormat: String): SellViewState()
    data class ChargeByClientLoaded(val account: String, val saldo: Double): SellViewState()
    data class PrecatureParamsCreated(val params: HashMap<String, String>): SellViewState()
    data class ClientType(val clientType: SellType): SellViewState()
    data class PrecatureFinished(val ticket: String, val sellId: Long, val clientId: Long, val account: String): SellViewState()
    data class NotEnoughCredit(val saldo: Double, val isMatriz: Boolean): SellViewState()
    data class ShowScheduler(val clientId: Long, val recordatorio: String): SellViewState()
    object FinishPreSell: SellViewState()
    object LoadingStart: SellViewState()
    object LoadingFinish: SellViewState()
    object NotInternetConnection: SellViewState()
}