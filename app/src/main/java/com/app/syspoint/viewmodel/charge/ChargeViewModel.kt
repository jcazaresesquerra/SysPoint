package com.app.syspoint.viewmodel.charge

import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.syspoint.documents.DepositTicket
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.interactor.charge.ChargeInteractor.OnGetChargeByClientListener
import com.app.syspoint.interactor.charge.ChargeInteractor.OnUpdateChargeListener
import com.app.syspoint.interactor.charge.ChargeInteractorImp
import com.app.syspoint.interactor.client.ClientInteractor.GetAllClientsListener
import com.app.syspoint.interactor.client.ClientInteractor.SaveClientListener
import com.app.syspoint.interactor.client.ClientInteractorImp
import com.app.syspoint.models.Client
import com.app.syspoint.models.Payment
import com.app.syspoint.models.sealed.ChargeViewState
import com.app.syspoint.repository.objectBox.AppBundle
import com.app.syspoint.repository.objectBox.dao.*
import com.app.syspoint.repository.objectBox.entities.*
import com.app.syspoint.ui.cobranza.ListaDocumentosCobranzaActivity
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val TAG = "ChargeViewModel"
class ChargeViewModel: ViewModel() {

    val chargeViewState = MutableLiveData<ChargeViewState>()

    fun setUpCharge() {
        val partidas = ChargeModelDao().list() as List<ChargeModelBox?>
        chargeViewState.value = ChargeViewState.ChargeListLoaded(partidas)
    }

    fun loadClientData(clientId: String) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                val clientesDao = ClientDao()
                val clientesBean = clientesDao.getClientByAccount(clientId)
                val chargeDao = ChargeDao()
                if (clientesBean != null) {
                    chargeViewState.postValue(ChargeViewState.LoadingStart)
                    ChargeInteractorImp().executeGetChargeByClient(clientesBean.cuenta!!, object : OnGetChargeByClientListener {
                        override fun onGetChargeByClientSuccess(chargeByClientList: List<ChargeBox>) {
                            val saldoCliente = chargeDao.getSaldoByCliente(clientesBean.cuenta!!)
                            //chargeViewState.postValue(ChargeViewState.LoadingFinish)
                            chargeViewState.postValue(ChargeViewState.ChargeLoaded(clientId, saldoCliente))
                            Log.d(TAG, "ChargeViewState.ChargeLoaded(clientId, saldoCliente)")
                            downloadCharge(clientId)
                        }

                        override fun onGetChargeByClientError() {
                            Log.d(TAG, "downloadCharge(clientId) onGetChargeByClientError")
                            downloadCharge(clientId)
                            //chargeViewState.postValue(ChargeViewState.LoadingFinish)
                        }
                    })

                    val saldoDocumentos = chargeDao.getSaldoByCliente(clientesBean.cuenta!!)
                    clientesBean.saldo_credito = saldoDocumentos
                    clientesDao.insert(clientesBean)
                    testLoadClientes(clientesBean.id)
                    chargeViewState.postValue(ChargeViewState.ClientLoaded(clientesBean))
                    Log.d(TAG, "ChargeViewState.ClientLoaded(clientesBean)")
                } else {
                    Log.d(TAG, "downloadCharge(clientId)")
                    downloadCharge(clientId)
                    //dialogo = new Dialogo(activityGlobal);
                    //dialogo.setAceptar(true);
                    //dialogo.setOnAceptarDissmis(true);
                    //dialogo.setMensaje("Cliente no encontrado");
                    //dialogo.show();
                    //return ;
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun downloadCharge(clientId: String) {
        viewModelScope.launch {
            if (clientId.isNotEmpty()) {
                //chargeViewState.value = ChargeViewState.LoadingStart
                ChargeInteractorImp().executeGetChargeByClient(clientId,
                    object : OnGetChargeByClientListener {
                        override fun onGetChargeByClientSuccess(chargeByClientList: List<ChargeBox>) {
                            Log.d(TAG, "hideLoading onGetChargeByClientSuccess")
                            chargeViewState.value = ChargeViewState.LoadingFinish
                        }

                        override fun onGetChargeByClientError() {
                            Log.d(TAG, "hideLoading onGetChargeByClientError")

                            chargeViewState.value = ChargeViewState.LoadingFinish
                        }
                    })
            } else {
                Log.d(TAG, "hideLoading !clientId.isNotEmpty()")
                chargeViewState.value = ChargeViewState.LoadingFinish
            }
        }
    }

    fun endCharge() {
        val dao = ChargeModelDao()
        val charges = dao.list() as List<ChargeModelBox?>
        if (charges.isEmpty()) {
            chargeViewState.value = ChargeViewState.EndChargeWithoutDocument
        } else {
            chargeViewState.value = ChargeViewState.EndChargeWithDocument

        }
    }

    fun handleEndChargeWithDocument(clientId: String, import: String) {
        val chargesDao = CobrosDao()
        val clientesDao = ClientDao()
        val clienteBean = clientesDao.getClientByAccount(clientId)
        if (clienteBean == null) {
            chargeViewState.value = ChargeViewState.UserNotFound
            return
        }


        //Obtiene el nombre del vendedor
        var vendedoresBean = AppBundle.getUserBox()
        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }
        if (vendedoresBean == null) {
            chargeViewState.value = ChargeViewState.SellerNotFound
            return
        }

        val dao = ChargeModelDao()
        val charges = dao.list() as List<ChargeModelBox?>
        val lista = ArrayList<CobdetBox>()
        val currentStockId = CacheInteractor().getCurrentStockId()

        charges.map { charge->
            if (charge != null) {
                //Actualiza la cobranza
                val chargeDao = ChargeDao()
                val chargeBox = chargeDao.getByCobranza(charge.cobranza)
                if (chargeBox != null) {
                    if (chargeBox.saldo == charge.acuenta) {
                        chargeBox.estado = "CO"
                        chargeBox.saldo = 0.0
                        chargeBox.abono = true
                        chargeBox.acuenta = charge.acuenta
                    } else {
                        chargeBox.saldo = chargeBox.saldo!! - charge.acuenta
                        chargeBox.abono = true
                        chargeBox.acuenta = charge.acuenta

                    }
                    chargeBox.fecha = Utils.fechaActual()
                    chargeBox.updatedAt = Utils.fechaActualHMS()
                    chargeBox.stockId = currentStockId
                    chargeDao.insert(chargeBox)
                }
                val cobdetBox = CobdetBox()
                cobdetBox.cobranza = charge.cobranza
                cobdetBox.cliente.target = clienteBean
                cobdetBox.fecha = Utils.fechaActual()
                cobdetBox.importe = charge.acuenta
                cobdetBox.venta = charge.venta
                cobdetBox.empleado.target = vendedoresBean
                cobdetBox.abono = 0
                cobdetBox.hora = Utils.getHoraActual()
                cobdetBox.saldo = charge.saldo
                lista.add(cobdetBox)
            }
        }
        val cobrosBean = CobrosBox()
        val folioCobranza = chargesDao.getUltimoFolio()
        cobrosBean.cobro = folioCobranza

        //Creamos el encabezado de la venta
        cobrosBean.fecha = Utils.fechaActual()
        cobrosBean.hora = Utils.getHoraActual()
        cobrosBean.cliente!!.target = clienteBean
        cobrosBean.empleado!!.target = vendedoresBean
        cobrosBean.importe = import.replace("$", "")
                .replace(",", "")
                .trim { it <= ' ' }
                .toDouble()
        cobrosBean.estado = "CO"
        cobrosBean.temporal = 0
        cobrosBean.sinc = 0

        //Creamos el documento con la relacion de sus documentos
        chargesDao.createCharge(cobrosBean, lista)

        chargeViewState.value = ChargeViewState.LoadingStart
        Handler().postDelayed({
            NetworkStateTask { connected: Boolean ->
                chargeViewState.postValue(ChargeViewState.LoadingFinish)
                try {
                    if (connected) {
                        saveClientAbono(clientId, cobrosBean)
                    } else {
                        chargeViewState.postValue(ChargeViewState.NotInternetConnection)
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }.execute()
        }, 100)
    }

    private fun saveClientAbono(clientId: String, cobrosBean: CobrosBox) {
        val clientesDao = ClientDao()
        val clienteBean = clientesDao.getClientByAccount(clientId)
        saveAbono()
        val ventaID = cobrosBean.id
        val nuevoSaldo = ChargeDao().getSaldoByCliente(clienteBean!!.cuenta!!)

        //Actualizamos el saldo del cliente
        clienteBean.saldo_credito = nuevoSaldo
        clienteBean.date_sync = Utils.fechaActual()
        clientesDao.insert(clienteBean)

        //Creamos el template del timbre
        val depositTicket = DepositTicket()
        depositTicket.box = cobrosBean
        depositTicket.template()
        val ticket = depositTicket.document
        testLoadClientes(clienteBean.id)

        //Elimina las partidas
        val dao = ChargeModelDao()
        dao.clear()

        chargeViewState.value = ChargeViewState.ClientSaved(ticket, ventaID!!, clienteBean.id.toString())
    }

    fun getTaxes(clientId: String) {
        Log.d(TAG, "getTaxes start")
        val dao = ChargeModelDao()
        val charges = dao.list() as List<ChargeModelBox?>

        var acuenta = 0.0
        for (i in charges.indices) {
            acuenta += charges[i]?.acuenta ?: 0.0
        }

        val clientBean = ClientDao().getClientByAccount(clientId)
        val totalAmount = clientBean?.saldo_credito ?: 0.0

        chargeViewState.value = ChargeViewState.ComputedTaxes(totalAmount, acuenta, charges.isEmpty())
        Log.d(TAG, "getTaxes finish")
    }

    fun deletePartida(charge: ChargeModelBox?){
        Log.d(TAG, "deletePartida start")
        val dao = ChargeModelDao()
        dao.delete(charge!!.id!!)
        val charges = dao.list() as List<ChargeModelBox?>
        chargeViewState.value = ChargeViewState.ChargeListRefresh(charges)
        Log.d(TAG, "deletePartida finish ChargeViewState.ChargeListRefresh(charges)")

    }

    fun deletePartidas(clientId: String) {
        viewModelScope.launch {
            Log.d(TAG, "deletePartidas start")
            val chargeDao = ChargeDao()
            val selectedDocumentList = chargeDao.getDocumentosSeleccionados(clientId)
            for (chargeItems in selectedDocumentList) {
                val chargeBean = chargeDao.getByCobranza(chargeItems.cobranza)
                chargeBean!!.isCheck = false
                chargeDao.insert(chargeBean)
            }

            val dao = ChargeModelDao()
            dao.clear()
            Log.d(TAG, "deletePartidas finish")
        }
    }

    fun validaDocumentoRepetido(): Boolean {
        val dao = ChargeModelDao()
        val charges = dao.list() as List<ChargeModelBox?>
        var repetido = false
        for (partidasItems in charges) {
            if (partidasItems != null) {
                val producto = partidasItems.cobranza
                if (producto!!.compareTo(ListaDocumentosCobranzaActivity.documentoSeleccionado) == 0) {
                    repetido = true
                    break
                }
            }
        }
        return repetido
    }

    private fun testLoadClientes(idCliente: Long) {
        val clientDao = ClientDao()
        val listaClientesDB = clientDao.getByIDClient(idCliente)
        val listaClientes: MutableList<Client> = java.util.ArrayList()
        for (item in listaClientesDB) {
            val cliente = Client()
            cliente.nombreComercial = item.nombre_comercial
            cliente.calle = item.calle
            cliente.numero = item.numero
            cliente.colonia = item.colonia
            cliente.ciudad = item.ciudad
            cliente.codigoPostal = item.codigo_postal
            cliente.fechaRegistro = item.fecha_registro
            cliente.cuenta = item.cuenta
            cliente.status = if (item.status) 1 else 0
            cliente.consec = item.consec
            cliente.rango = item.rango
            cliente.lun = item.lun
            cliente.mar = item.mar
            cliente.mie = item.mie
            cliente.jue = item.jue
            cliente.vie = item.vie
            cliente.sab = item.sab
            cliente.dom = item.dom
            cliente.latitud = "" + item.latitud
            cliente.longitud = "" + item.longitud
            cliente.phone_contacto = "" + item.contacto_phone
            cliente.recordatorio = "" + item.recordatorio
            cliente.visitas = item.visitasNoefectivas
            cliente.isCredito = if (item.isCredito) 1 else 0
            cliente.saldo_credito = item.saldo_credito
            cliente.limite_credito = item.limite_credito
            if (item.matriz === "null" && item.matriz == null) {
                cliente.matriz = "null"
            } else {
                cliente.matriz = item.matriz
            }
            listaClientes.add(cliente)
        }
        ClientInteractorImp().executeSaveClient(listaClientes, object : SaveClientListener {
            override fun onSaveClientSuccess() {
                //Toast.makeText(getApplicationContext(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
            }

            override fun onSaveClientError() {
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
            }
        })

        val routingDao = RoutingDao()
        val ruteoBean = routingDao.getRutaEstablecida()

        if (ruteoBean != null) {
            ClientInteractorImp().executeGetAllClientsByDate(ruteoBean.ruta!!, ruteoBean.dia, object : GetAllClientsListener {
                override fun onGetAllClientsSuccess(clientList: List<ClientBox>) {}
                override fun onGetAllClientsError() {}
            })
        }
    }

    private fun saveAbono() {
        val cobranzaBeanList = ChargeDao().getAbonosFechaActual(Utils.fechaActual())
        val listaCobranza: MutableList<Payment> = java.util.ArrayList()
        for (item in cobranzaBeanList) {
            val cobranza = Payment()
            cobranza.cobranza = item.cobranza
            cobranza.cuenta = item.cliente
            cobranza.importe = item.importe
            cobranza.saldo = item.saldo!!
            cobranza.venta = item.venta
            cobranza.estado = item.estado
            cobranza.observaciones = item.observaciones
            cobranza.fecha = item.fecha
            cobranza.hora = item.hora
            cobranza.identificador = item.empleado
            cobranza.updatedAt = item.updatedAt
            listaCobranza.add(cobranza)
        }
        ChargeInteractorImp().executeUpdateCharge(listaCobranza, object : OnUpdateChargeListener {
            override fun onUpdateChargeSuccess() {
                //Toast.makeText(getApplicationContext(), "Cobranza sincroniza", Toast.LENGTH_LONG).show();
            }

            override fun onUpdateChargeError() {
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al actualizar la cobranza", Toast.LENGTH_LONG).show();
            }
        })
    }

    fun createCharge(cobranzaSeleccionada: String?, importeAcuenta: String?, clientId: String) {
        Log.d(TAG, "createCharge start")
        if (cobranzaSeleccionada != null && importeAcuenta != null) {
            val cobranzaBean = ChargeDao().getByCobranza(cobranzaSeleccionada)

            val venta = cobranzaBean?.venta ?: 0L
            val cobranza = cobranzaBean?.cobranza ?: ""
            val importe = cobranzaBean?.importe ?: 0.0
            val saldo = cobranzaBean?.saldo ?: 0.0
            val acuenta = importeAcuenta.toDouble()
            val no_referen = ""

            val item = ChargeModelBox()
            val dao = ChargeModelDao()
            item.venta = venta
            item.cobranza = cobranza
            item.importe = importe
            item.saldo = saldo
            item.acuenta = acuenta
            item.no_referen = no_referen
            dao.insert(item)

            val partidas = arrayListOf<ChargeModelBox?>()
            partidas.add(item)

            chargeViewState.value = ChargeViewState.ChargeListRefresh(partidas)
            getTaxes(clientId)
        }
        Log.d(TAG, "createCharge finish")
    }
}