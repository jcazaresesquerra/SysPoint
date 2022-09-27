package com.app.syspoint.models.sealed

import com.app.syspoint.repository.database.bean.ClienteBean
import com.app.syspoint.ui.cobranza.CobranzaModel

sealed class ChargeViewState {
    object NotInternetConnection: ChargeViewState()
    object LoadingStart: ChargeViewState()
    object LoadingFinish: ChargeViewState()
    object UserNotFound: ChargeViewState()
    object SellerNotFound: ChargeViewState()
    object EndChargeWithoutDocument: ChargeViewState()
    object EndChargeWithDocument: ChargeViewState()
    data class ChargeCreated(val venta: Int, val cobranza: String, val importe: Double, val saldo: Double, val acuenta: Double, val no_referen: String): ChargeViewState()
    data class ChargeLoaded(val clientId: String, val saldoCiente: Double): ChargeViewState()
    data class ChargeListLoaded(val charges: List<CobranzaModel?>): ChargeViewState()
    data class ChargeListRefresh(val charges: List<CobranzaModel?>): ChargeViewState()
    data class ClientLoaded(val clientBean: ClienteBean): ChargeViewState()
    data class ComputedTaxes(val totalAmount: Double, val restAmount: Double, val show: Boolean): ChargeViewState()
    data class ClientSaved(val ticket: String, val sellId: String, val clientId: String): ChargeViewState()
}