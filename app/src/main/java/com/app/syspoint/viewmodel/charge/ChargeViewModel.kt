package com.app.syspoint.viewmodel.charge

import android.os.Handler
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.database.dao.ChargesDao
import com.app.syspoint.repository.database.dao.ClientDao
import com.app.syspoint.repository.database.dao.PaymentDao
import com.app.syspoint.repository.database.dao.PaymentModelDao
import com.app.syspoint.ui.cobranza.CobranzaModel
import com.app.syspoint.ui.cobranza.ListaDocumentosCobranzaActivity
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.utils.Utils

class ChargeViewModel: ViewModel() {

    val chargeViewState = MutableLiveData<ChargeViewState>()

    fun setUpCharge() {
        val partidas = PaymentModelDao().list() as List<CobranzaModel?>
        chargeViewState.value = ChargeViewState.ChargeListLoaded(partidas)
    }

    fun downloadCharge(clientId: String) {
        if (clientId.isNotEmpty()) {
            //chargeViewState.value = ChargeViewState.LoadingStart
            ChargeInteractorImp().executeGetChargeByClient(clientId,
                object : OnGetChargeByClientListener {
                    override fun onGetChargeByClientSuccess(chargeByClientList: List<CobranzaBean>) {
                        chargeViewState.value = ChargeViewState.LoadingFinish
                    }

                    override fun onGetChargeByClientError() {
                        chargeViewState.value = ChargeViewState.LoadingFinish
                    }
                })
        } else {
            chargeViewState.value = ChargeViewState.LoadingFinish
        }
    }

    fun loadClientData(clientId: String) {
        try {
            val clientesDao = ClientDao()
            val clientesBean = clientesDao.getClientByAccount(clientId)
            val paymentDao = PaymentDao()
            if (clientesBean != null) {
                chargeViewState.postValue(ChargeViewState.LoadingStart)
                ChargeInteractorImp().executeGetChargeByClient(clientesBean.cuenta, object : OnGetChargeByClientListener {
                    override fun onGetChargeByClientSuccess(chargeByClientList: List<CobranzaBean>) {
                        val paymentDao1 = PaymentDao()
                        val saldoCliente = paymentDao1.getSaldoByCliente(clientesBean.cuenta)
                        //chargeViewState.postValue(ChargeViewState.LoadingFinish)
                        chargeViewState.postValue(ChargeViewState.ChargeLoaded(clientId, saldoCliente))

                        downloadCharge(clientId)
                    }

                    override fun onGetChargeByClientError() {
                        downloadCharge(clientId)
                        //chargeViewState.postValue(ChargeViewState.LoadingFinish)
                    }
                })

                val saldoDocumentos = paymentDao.getTotalSaldoDocumentosCliente(clientesBean.cuenta)
                clientesBean.saldo_credito = saldoDocumentos
                clientesDao.save(clientesBean)
                testLoadClientes(clientesBean.id.toString())
                chargeViewState.value = ChargeViewState.ClientLoaded(clientesBean)
            } else {
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

    fun endCharge() {
        val dao = PaymentModelDao()
        val charges = dao.list() as List<CobranzaModel?>
        if (charges.isEmpty()) {
            chargeViewState.value = ChargeViewState.EndChargeWithoutDocument
        } else {
            chargeViewState.value = ChargeViewState.EndChargeWithDocument

        }
    }

    fun handleEndChargeWithDocument(clientId: String, import: String) {
        val chargesDao = ChargesDao()
        val clientesDao = ClientDao()
        val clienteBean = clientesDao.getClientByAccount(clientId)
        if (clienteBean == null) {
            chargeViewState.value = ChargeViewState.UserNotFound
            return
        }


        //Obtiene el nombre del vendedor
        var vendedoresBean = AppBundle.getUserBean()
        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }
        if (vendedoresBean == null) {
            chargeViewState.value = ChargeViewState.SellerNotFound
            return
        }

        val dao = PaymentModelDao()
        val charges = dao.list() as List<CobranzaModel?>

        val lista = ArrayList<CobdetBean>()
        for (x in charges.indices) {

            val charge = charges[x]
            if (charge != null) {
                //Actualiza la cobranza
                val paymentDao = PaymentDao()
                val cobranzaBean = paymentDao.getByCobranza(charge.cobranza)
                if (cobranzaBean != null) {
                    if (cobranzaBean.saldo == charge.acuenta) {
                        cobranzaBean.estado = "CO"
                        cobranzaBean.saldo = 0.0
                        cobranzaBean.abono = true
                    } else {
                        cobranzaBean.saldo = cobranzaBean.saldo - charge.acuenta
                        cobranzaBean.abono = true
                    }
                    cobranzaBean.fecha = Utils.fechaActual()
                    paymentDao.save(cobranzaBean)
                }
                val cobdetBean = CobdetBean()
                cobdetBean.cobranza = charge.getCobranza()
                cobdetBean.cliente = clienteBean
                cobdetBean.fecha = Utils.fechaActual()
                cobdetBean.importe = charge.acuenta
                cobdetBean.venta = charge.venta
                cobdetBean.empleado = vendedoresBean
                cobdetBean.abono = 0
                cobdetBean.hora = Utils.getHoraActual()
                cobdetBean.saldo = charge.saldo
                lista.add(cobdetBean)
            }
        }
        val cobrosBean = CobrosBean()
        val folioCobranza = chargesDao.getUltimoFolio()
        cobrosBean.cobro = folioCobranza

        //Creamos el encabezado de la venta
        cobrosBean.fecha = Utils.fechaActual()
        cobrosBean.hora = Utils.getHoraActual()
        cobrosBean.cliente = clienteBean
        cobrosBean.empleado = vendedoresBean
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

    private fun saveClientAbono(clientId: String, cobrosBean: CobrosBean) {
        val clientesDao = ClientDao()
        val clienteBean = clientesDao.getClientByAccount(clientId)
        saveAbono()
        val ventaID = cobrosBean.id.toString()
        val paymentDao = PaymentDao()
        val nuevoSaldo = paymentDao.getTotalSaldoDocumentosCliente(clienteBean!!.cuenta)

        //Actualizamos el saldo del cliente
        clienteBean.saldo_credito = nuevoSaldo
        clienteBean.date_sync = Utils.fechaActual()
        clientesDao.save(clienteBean)

        //Creamos el template del timbre
        val depositTicket = DepositTicket()
        depositTicket.bean = cobrosBean
        depositTicket.template()
        val ticket = depositTicket.document
        testLoadClientes(clienteBean.id.toString())

        //Elimina las partidas
        val dao = PaymentModelDao()
        dao.clear()

        chargeViewState.value = ChargeViewState.ClientSaved(ticket, ventaID, clienteBean.id.toString())
    }

    fun getTaxes(clientId: String) {
        val dao = PaymentModelDao()
        val charges = dao.list() as List<CobranzaModel?>

        var acuenta = 0.0
        for (i in charges.indices) {
            acuenta += charges[i]?.acuenta ?: 0.0
        }

        val clientBean = ClientDao().getClientByAccount(clientId)
        val totalAmount = clientBean?.saldo_credito ?: 0.0

        chargeViewState.value = ChargeViewState.ComputedTaxes(totalAmount, acuenta, charges.isEmpty())
    }

    fun deletePartida(charge: CobranzaModel?){
        val dao = PaymentModelDao()
        dao.delete(charge)
        val charges = dao.list() as List<CobranzaModel?>
        chargeViewState.value = ChargeViewState.ChargeListRefresh(charges)
    }

    fun deletePartidas(clientId: String) {
        val paymentDao = PaymentDao()
        val selectedDocumentList = paymentDao.getDocumentosSeleccionados(clientId)
        for (chargeItems in selectedDocumentList) {
            val chargeBean = paymentDao.getByCobranza(chargeItems.cobranza)
            chargeBean!!.isCheck = false
            paymentDao.save(chargeBean)
        }

        val dao = PaymentModelDao()
        dao.clear()
    }

    fun validaDocumentoRepetido(): Boolean {
        val dao = PaymentModelDao()
        val charges = dao.list() as List<CobranzaModel?>
        var repetido = false
        for (partidasItems in charges) {
            if (partidasItems != null) {
                val producto = partidasItems.cobranza
                if (producto.compareTo(ListaDocumentosCobranzaActivity.documentoSeleccionado) == 0) {
                    repetido = true
                    break
                }
            }
        }
        return repetido
    }

    private fun testLoadClientes(idCliente: String) {
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
            cliente.fechaBaja = item.fecha_baja
            cliente.cuenta = item.cuenta
            cliente.grupo = item.grupo
            cliente.categoria = item.categoria
            if (item.status) {
                cliente.status = 1
            } else {
                cliente.status = 0
            }
            cliente.consec = item.consec
            cliente.region = item.region
            cliente.sector = item.sector
            cliente.rango = item.rango
            cliente.secuencia = item.secuencia
            cliente.periodo = item.periodo
            cliente.ruta = item.ruta
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
            if (item.is_credito) {
                cliente.isCredito = 1
            } else {
                cliente.isCredito = 0
            }
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
        ClientInteractorImp().executeGetAllClients(object : GetAllClientsListener {
            override fun onGetAllClientsSuccess(clientList: List<ClienteBean>) {}
            override fun onGetAllClientsError() {}
        })
    }

    private fun saveAbono() {
        val paymentDao = PaymentDao()
        val cobranzaBeanList = paymentDao.getAbonosFechaActual(Utils.fechaActual())
        val listaCobranza: MutableList<Payment> = java.util.ArrayList()
        for (item in cobranzaBeanList) {
            val cobranza = Payment()
            cobranza.cobranza = item.cobranza
            cobranza.cuenta = item.cliente
            cobranza.importe = item.importe
            cobranza.saldo = item.saldo
            cobranza.venta = item.venta
            cobranza.estado = item.estado
            cobranza.observaciones = item.observaciones
            cobranza.fecha = item.fecha
            cobranza.hora = item.hora
            cobranza.identificador = item.empleado
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
        if (cobranzaSeleccionada != null && importeAcuenta != null) {
            val paymentDao = PaymentDao();
            val cobranzaBean = paymentDao.getByCobranza(cobranzaSeleccionada);

            val venta = cobranzaBean?.venta ?: 0
            val cobranza = cobranzaBean?.cobranza ?: ""
            val importe = cobranzaBean?.importe ?: 0.0
            val saldo = cobranzaBean?.saldo ?: 0.0
            val acuenta = importeAcuenta.toDouble()
            val no_referen = ""

            val item = CobranzaModel()
            val dao = PaymentModelDao()
            item.venta = venta
            item.cobranza = cobranza
            item.importe = importe
            item.saldo = saldo
            item.acuenta = acuenta
            item.no_referen = no_referen
            dao.insert(item)

            val partidas = arrayListOf<CobranzaModel?>()
            partidas.add(item)

            chargeViewState.value = ChargeViewState.ChargeListRefresh(partidas)
            getTaxes(clientId)
        }
    }
}