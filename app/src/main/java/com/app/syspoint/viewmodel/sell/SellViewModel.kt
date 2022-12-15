package com.app.syspoint.viewmodel.sell

import android.os.Handler
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.syspoint.documents.SellTicket
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.interactor.charge.ChargeInteractor.OnGetChargeByClientListener
import com.app.syspoint.interactor.charge.ChargeInteractorImp
import com.app.syspoint.interactor.client.ClientInteractor
import com.app.syspoint.interactor.client.ClientInteractor.SaveClientListener
import com.app.syspoint.interactor.client.ClientInteractorImp
import com.app.syspoint.interactor.prices.PriceInteractor.GetPricesByClientListener
import com.app.syspoint.interactor.prices.PriceInteractorImp
import com.app.syspoint.models.Client
import com.app.syspoint.models.enum.SellType
import com.app.syspoint.models.sealed.SellViewState
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.database.dao.*
import com.app.syspoint.utils.Actividades
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*

class SellViewModel: ViewModel() {

    val sellViewState = MutableLiveData<SellViewState>()
    private val partidas = MutableLiveData<List<VentasModelBean?>>()
    val partidasEspeciales = MutableLiveData<List<PreciosEspecialesBean?>>()
    private val sellImport = MutableLiveData<Double>()
    private val latitude = MutableLiveData<Double>()
    private val longitude = MutableLiveData<Double>()

    init {
        sellImport.value = 0.0
    }
    val mutex = Mutex()

    @Synchronized
    fun setUpSells() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = SellsModelDao().list() as List<VentasModelBean?>
            partidas.postValue(data)
            sellViewState.postValue(SellViewState.SellsLoaded(data))
        }
    }

    @Synchronized
    fun refreshSellData() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = SellsModelDao().list() as List<VentasModelBean?>
            partidas.postValue(data)
            sellViewState.postValue(SellViewState.SellsRefresh(data))
        }
    }

    @Synchronized
    fun setUpClientType(clientId: String) {
        viewModelScope.launch(Dispatchers.Main) {
            val clienteBean = ClientDao().getClientByAccount(clientId)

            val clientType = if (clienteBean != null && clienteBean.is_credito) {
                SellType.CREDITO
            } else {
                SellType.CONTADO
            }
            sellViewState.value = (SellViewState.ClientType(clientType))
        }
    }

    @Synchronized
    fun setUpChargeByClient(clientId: String) {
        sellViewState.value = SellViewState.LoadingStart

        viewModelScope.launch(Dispatchers.Default) {
            NetworkStateTask { connected: Boolean ->
                if (connected) {
                    val clientDao = ClientDao()
                    val clienteBean = clientDao.getClientByAccount(clientId)

                    ChargeInteractorImp().executeGetChargeByClient(
                        clienteBean!!.cuenta,
                        object : OnGetChargeByClientListener {
                            override fun onGetChargeByClientSuccess(chargeByClientList: List<CobranzaBean>) {
                                val paymentDao1 = PaymentDao()
                                val saldoCliente = paymentDao1.getSaldoByCliente(clienteBean.cuenta)
                                clienteBean.saldo_credito = saldoCliente
                                clienteBean.date_sync = Utils.fechaActual()
                                clientDao.save(clienteBean)
                                val saldo: Double =
                                    if (clienteBean.matriz == null || (clienteBean.matriz != null && clienteBean.matriz.compareTo("null", ignoreCase = true) == 0)) {
                                        clienteBean.saldo_credito
                                    } else {
                                        val client =
                                            clientDao.getClientByAccount(clienteBean.matriz)
                                        client?.saldo_credito ?: 0.0
                                    }
                                //sellViewState.postValue(SellViewState.LoadingFinish)
                                sellViewState.postValue(
                                    SellViewState.ChargeByClientLoaded(
                                        clienteBean.cuenta,
                                        saldo
                                    )
                                )
                                setUpPricesByClient(clientId)
                            }

                            override fun onGetChargeByClientError() {
                                //sellViewState.postValue(SellViewState.LoadingFinish)
                                setUpPricesByClient(clientId)
                            }
                        })
                } else {
                    proceedWithoutInternet(clientId)
                }
            }.execute()
        }
    }

    fun setUpPricesByClient(clientId: String) {
        PriceInteractorImp().executeGetPricesByClient(clientId, object : GetPricesByClientListener {
            override fun onGetPricesByClientSuccess(pricesByClientList: List<PreciosEspecialesBean>) {
                partidasEspeciales.value = pricesByClientList
                //sellViewState.postValue(SellViewState.LoadingFinish)
                loadClients(clientId)
            }

            override fun onGGetPricesByClientError() {
                loadClients(clientId)
                //sellViewState.postValue(SellViewState.LoadingFinish)
            }
        })
    }

    @Synchronized
    fun loadClients(clientId: String) {
        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientByAccount(clientId)

        if (clienteBean != null) {
            sellViewState.postValue(SellViewState.LoadingFinish)

            val saldo = if (clienteBean.matriz.isNullOrEmpty() || clienteBean.matriz == "null") {
                Utils.FDinero(clienteBean.saldo_credito)
            } else {
                val clientMatriz = clientDao.getClientByAccount(clienteBean.matriz)
                Utils.FDinero(clientMatriz?.saldo_credito ?: 0.0)
            }
            sellViewState.postValue(
                SellViewState.ClientsLoaded(
                    clienteBean.nombre_comercial,
                    clienteBean.cuenta,
                    saldo
                )
            )
        }
        /*if (clienteBean != null) {
            ClientInteractorImp().executeGetAllClients(object: ClientInteractor.GetAllClientsListener {
                override fun onGetAllClientsSuccess(clientList: List<ClienteBean>) {
                    sellViewState.postValue(SellViewState.LoadingFinish)
                    val newClientBean = clientDao.getClientByAccount(clientId)
                    if (newClientBean != null) {
                        val saldo = if (newClientBean.matriz.isNullOrEmpty() || newClientBean.matriz == "null") {
                            Utils.FDinero(newClientBean.saldo_credito)
                        } else {
                            val clientMatriz = clientDao.getClientByAccount(newClientBean.matriz)
                            Utils.FDinero(clientMatriz?.saldo_credito ?: 0.0)
                        }
                        sellViewState.postValue(SellViewState.ClientsLoaded(newClientBean.nombre_comercial, newClientBean.cuenta, saldo))
                    } else {
                        val saldo = if (clienteBean.matriz.isNullOrEmpty() || clienteBean.matriz == "null") {
                            Utils.FDinero(clienteBean.saldo_credito)
                        } else {
                            val clientMatriz = clientDao.getClientByAccount(clienteBean.matriz)
                            Utils.FDinero(clientMatriz?.saldo_credito ?: 0.0)
                        }
                        sellViewState.postValue(SellViewState.ClientsLoaded(clienteBean.nombre_comercial, clienteBean.cuenta, saldo))
                    }
                }

                override fun onGetAllClientsError() {
                    sellViewState.postValue(SellViewState.LoadingFinish)

                    val saldo = if (clienteBean.matriz.isNullOrEmpty() || clienteBean.matriz == "null") {
                        Utils.FDinero(clienteBean.saldo_credito)
                    } else {
                        val clientMatriz = clientDao.getClientByAccount(clienteBean.matriz)
                        Utils.FDinero(clientMatriz?.saldo_credito ?: 0.0)
                    }
                    sellViewState.postValue(SellViewState.ClientsLoaded(clienteBean.nombre_comercial, clienteBean.cuenta, saldo))
                }
            })
        }*/
    }

    fun updateSaldo(clientId: String) {
        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientByAccount(clientId)
        if (clienteBean != null) {
            val paymentDao1 = PaymentDao()
            val saldoCliente = paymentDao1.getSaldoByCliente(clienteBean.cuenta)
            clienteBean.saldo_credito = saldoCliente
            clienteBean.date_sync = Utils.fechaActual()
            clientDao.save(clienteBean)
        }
    }

    fun submitSchedule(clientId: String) {
        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientById(clientId)
        if (clienteBean != null) {
            clienteBean.recordatorio = ""
            clienteBean.is_recordatorio = true
            clientDao.save(clienteBean)

            testLoadClientes(clientId)
        }
    }

    @Synchronized
    fun computeImports() {
        viewModelScope.launch(Dispatchers.Default) {
            val sells: List<VentasModelBean?> =
                if (partidas.value.isNullOrEmpty())
                    SellsModelDao().list() as List<VentasModelBean?>
                else partidas.value!!

            var subTotal = 0.0
            var totalImpuesto = 0.0
            /**
             * Obtenemos los totales
             */
            for (sell in sells) {
                sell?.let {
                    subTotal += sell.precio * sell.cantidad
                    totalImpuesto += sell.precio * sell.cantidad * (sell.impuesto / 100)
                    val countProducts = sell.cantidad
                }
            }
            sellImport.postValue(subTotal + totalImpuesto)
            var subtotalFormato = Utils.formatMoneyMX(subTotal)
            var impuestoFormato = Utils.formatMoneyMX(totalImpuesto)
            var totalFormato = Utils.formatMoneyMX(subTotal + totalImpuesto)
            if (subtotalFormato.startsWith("$ .")) subtotalFormato =
                "$ 0" + subtotalFormato.substring(2)
            if (impuestoFormato.startsWith("$ .")) impuestoFormato =
                "$ 0" + impuestoFormato.substring(2)
            if (totalFormato.startsWith("$ .")) totalFormato = "$ 0" + totalFormato.substring(2)

            sellViewState.postValue(
                SellViewState.ComputedImports(
                    totalFormato,
                    subtotalFormato,
                    impuestoFormato
                )
            )
        }
    }

    fun addItem(articulo: String, descripcion: String, precio: Double,
                impuesto: Int, cantidad: Int): ArrayList<VentasModelBean?> {

        val item = VentasModelBean()
        val dao = SellsModelDao()
        item.articulo = articulo
        item.descripcion = descripcion
        item.cantidad = cantidad
        item.precio = precio
        item.impuesto = impuesto.toDouble()
        item.observ = descripcion
        dao.insert(item)
        val sells = partidas.value as ArrayList<VentasModelBean?>
        sells.add(item)

        partidas.value = sells
        //sellViewState.postValue(SellViewState.ItemAdded(sells))
        return sells
    }

    @Synchronized
    fun createPrecatureParams(clientId: String) {
        viewModelScope.launch {
            val clientDao = ClientDao()
            val clientBean = clientDao.getClientByAccount(clientId)
            val params = HashMap<String, String>()
            clientBean?.let {
                params[Actividades.PARAM_1] = it.cuenta
                params[Actividades.PARAM_2] = it.calle
                params[Actividades.PARAM_3] = it.numero
                params[Actividades.PARAM_4] = it.colonia
                params[Actividades.PARAM_5] = it.nombre_comercial
                params[Actividades.PARAM_6] = it.latitud ?: ""
                params[Actividades.PARAM_7] = it.longitud ?: ""

                sellViewState.postValue(SellViewState.PrecatureParamsCreated(params))
            }
        }
    }

    fun existenPartidas(): Boolean {
        return partidas.value?.isNotEmpty() ?: false
    }

    fun validaProducto(articulo: String): Boolean {
        var existe = false
        partidas.value?.let {
            for (item in it) {
                if (item != null && item.articulo == articulo) {
                    existe = true
                    break
                }
            }
        }

        return existe
    }

    fun clearSells() {
        viewModelScope.launch {
            val dao = SellsModelDao()
            dao.clear()
        }
    }

    fun setLocation(latitud: Double, longitud: Double) {
        latitude.postValue(latitud)
        longitude.postValue(longitud)
    }

    private fun ticketRamdom(): String {
        val chars = "0123456789".toCharArray()
        val rnd = Random()
        val sb = StringBuilder(
            (10 + rnd.nextInt(900)).toString() + Utils.getHoraActual().replace(":", "")
        )
        for (i in 0..4) sb.append(chars[rnd.nextInt(chars.size)])
        return sb.toString()
    }

    @Synchronized
    fun checkUserCredit(clientId: String, sellType: SellType, subtota: String, import: String) {
        viewModelScope.launch {
            val clientDao = ClientDao()
            val clienteBean = clientDao.getClientByAccount(clientId)
            var saldoDisponible = 0.0

            clienteBean?.let {
                if (clienteBean.is_credito && !clienteBean.matriz.isNullOrEmpty()) {
                    val clienteMatriz = clientDao.getClientByAccount(clienteBean.matriz)

                    if (clienteMatriz != null) {
                        saldoDisponible = clienteMatriz.limite_credito - clienteMatriz.saldo_credito

                        if (saldoDisponible < (sellImport.value?:0.0)) {
                            sellViewState.postValue(SellViewState.NotEnoughCredit(saldoDisponible, true))
                            return@launch
                        }
                    } else {
                        saldoDisponible = clienteBean.limite_credito - clienteBean.saldo_credito

                        if (saldoDisponible < (sellImport.value?: 0.0)) {
                            sellViewState.postValue(SellViewState.NotEnoughCredit(saldoDisponible, false))
                            return@launch
                        }
                    }
                }
                sellViewState.postValue(SellViewState.FinishPreSell)
            }
        }
    }

    @Synchronized
    fun finishPrecature(clientId: String, sellType: SellType, subtota: String, import: String) {
        sellViewState.value = SellViewState.LoadingStart

        viewModelScope.launch {
            val lista = ArrayList<PartidasBean>()
            val sellsDao = SellsDao()
            val ventasBean = VentasBean()
            val ultimoFolio = sellsDao.getUltimoFolio()
            val productDao = ProductDao()
            //Recorremos las partidas
            partidas.value?.let {
                for (partida in it) {
                    //Validamos si el articulo existe en la base de datos
                    partida?.let {
                        val productosBean = productDao.getProductoByArticulo(partida.articulo)
                        val partidaBean = PartidasBean()
                        partidaBean.articulo = productosBean
                        partidaBean.cantidad = partida.cantidad
                        partidaBean.precio = partida.precio
                        partidaBean.impuesto = partida.impuesto
                        partidaBean.observ = productosBean!!.descripcion
                        partidaBean.fecha = Date()
                        partidaBean.hora = Utils.getHoraActual()
                        partidaBean.venta = java.lang.Long.valueOf(ultimoFolio.toLong())
                        partidaBean.descripcion = productosBean.descripcion
                        lista.add(partidaBean)
                    }
                }
            }

            //Le indicamos al sistema que el cliente ya se ah visitado
            val clientDao1 = ClientDao()
            val clienteBean1 = clientDao1.getClientByAccount(clientId)
            var clienteID = clienteBean1!!.id.toString()
            var account = clienteBean1.cuenta.toString()

            clienteBean1.visitado = 1
            clienteBean1.visitasNoefectivas = 0
            clienteBean1.date_sync = Utils.fechaActual()
            clientDao1.save(clienteBean1)

            val ruteClientDao = RuteClientDao()
            val clientesRutaBean =
                ruteClientDao.getClienteByCuentaCliente(clientId)
            if (clientesRutaBean != null) {
                clientesRutaBean.visitado = 1
                ruteClientDao.save(clientesRutaBean)
            }

            //Obtiene el nombre del vendedor
            var vendedoresBean = AppBundle.getUserBean()
            if (vendedoresBean == null) {
                vendedoresBean = CacheInteractor().getSeller()
            }
            ventasBean.tipo_doc = "TIK"
            ventasBean.fecha = Utils.fechaActual()
            ventasBean.hora = Utils.getHoraActual()
            ventasBean.cliente = clienteBean1
            ventasBean.empleado = vendedoresBean
            ventasBean.importe = subtota.replace(",", "").toDouble()
            ventasBean.impuesto = import.replace(",", "").toDouble()
            ventasBean.datos = clienteBean1.nombre_comercial
            ventasBean.estado = "CO"
            ventasBean.corte = "N"
            ventasBean.temporal = 1
            ventasBean.venta = ultimoFolio
            ventasBean.latidud = latitude.value.toString()
            ventasBean.longitud = longitude.value.toString()
            ventasBean.sync = 0
            ventasBean.tipo_venta = sellType.value
            ventasBean.usuario_cancelo = ""

            var clienteMatriz: ClienteBean? = null
            if (!clienteBean1.matriz.isNullOrEmpty()) {
                clienteMatriz = clientDao1.getClientByAccount(clienteBean1.matriz)
                clienteMatriz?.let {
                    ventasBean.factudado = clienteMatriz.cuenta
                }
            }

            ventasBean.ticket = Utils.getHoraActual().replace(":", "") + Utils.getFechaRandom().replace("-", "")
            val totalVenta: Double = subtota.replace(",", "").toDouble() +
                    import.replace(",", "").toDouble()

            val ticketRamdom: String = ticketRamdom() + ventasBean.fecha.replace("-", "") + "" + ventasBean.hora.replace(":", "")

            if (sellType == SellType.CREDITO) {
                //Si la cobranza es de matriz entonces creamos la cobranza a matriz

                val isCreditMatriz = clienteBean1.is_credito
                        && clienteBean1.matriz != null
                        && clienteBean1.matriz.isNotEmpty()
                        && clienteMatriz != null

                if (isCreditMatriz) {
                    val cobranzaBean = CobranzaBean()
                    val paymentDao = PaymentDao()
                    cobranzaBean.cobranza = ticketRamdom
                    cobranzaBean.cliente = clienteMatriz?.cuenta ?: clienteID
                    cobranzaBean.importe = totalVenta
                    cobranzaBean.saldo = totalVenta
                    cobranzaBean.venta = ventasBean.ticket.toLong()
                    cobranzaBean.estado = "PE"
                    cobranzaBean.observaciones = "Se realiza la venta a crédito para sucursal ${clienteBean1.cuenta} ${clienteBean1.nombre_comercial} con cargo a Matriz ${clienteMatriz?.cuenta} ${clienteMatriz?.nombre_comercial} ${ventasBean.fecha} hora ${ventasBean.hora}"
                    cobranzaBean.fecha = ventasBean.fecha
                    cobranzaBean.hora = ventasBean.hora
                    if (vendedoresBean != null) {
                        cobranzaBean.empleado =
                            vendedoresBean.getIdentificador()
                    }
                    cobranzaBean.abono = false
                    paymentDao.save(cobranzaBean)

                    //Actualizamos el documento de la venta con el de la cobranza
                    ventasBean.cobranza = ticketRamdom
                    //ventasDao.save(ventasBean);

                    //Actualizamos el saldo del cliente
                    val saldoNuevo = clienteMatriz!!.saldo_credito + totalVenta
                    clienteMatriz.saldo_credito = saldoNuevo
                    clienteMatriz.date_sync = Utils.fechaActual()
                    clientDao1.save(clienteMatriz)
                    clienteID = clienteMatriz.id.toString()
                } else {
                    val cobranzaBean = CobranzaBean()
                    val paymentDao = PaymentDao()
                    cobranzaBean.cobranza = ticketRamdom
                    cobranzaBean.cliente = clienteBean1.cuenta
                    cobranzaBean.importe = totalVenta
                    cobranzaBean.saldo = totalVenta
                    cobranzaBean.venta = ventasBean.ticket.toLong()
                    cobranzaBean.estado = "PE"
                    cobranzaBean.observaciones = "Venta a crédito " + ventasBean.fecha + " hora " + ventasBean.hora
                    cobranzaBean.fecha = ventasBean.fecha
                    cobranzaBean.hora = ventasBean.hora
                    if (vendedoresBean != null) {
                        cobranzaBean.empleado =
                            vendedoresBean.getIdentificador()
                    }
                    paymentDao.save(cobranzaBean)

                    //Actualizamos el documento de la venta con el de la cobranza
                    ventasBean.cobranza = ticketRamdom
                    //ventasDao.save(ventasBean);

                    //Actualizamos el saldo del cliente
                    val saldoNuevo =
                        clienteBean1.saldo_credito + totalVenta
                    clienteBean1.saldo_credito = saldoNuevo
                    clienteBean1.visitasNoefectivas = 0
                    clienteBean1.date_sync =
                        Utils.fechaActual()
                    clientDao1.save(clienteBean1)
                    clienteID = clienteBean1.id.toString()
                }
                testLoadClientes(clienteID)
            }
            sellsDao.save(ventasBean)


            //Creamos la venta
            sellsDao.creaVenta(ventasBean, lista)
            val ventaID = ventasBean.venta.toString()

            //Creamos el template del timbre
            val sellTicket = SellTicket()
            sellTicket.bean = ventasBean
            sellTicket.template()
            val ticket = sellTicket.document

            sellViewState.postValue(SellViewState.LoadingFinish)
            sellViewState.postValue(SellViewState.PrecatureFinished(ticket, ventaID, clienteID, account))
        }
    }

    @Synchronized
    fun testLoadClientes(idCliente: String) {
        sellViewState.value = SellViewState.LoadingStart
        viewModelScope.launch {
            val clientDao = ClientDao()
            val listaClientesDB = clientDao.getByIDClient(idCliente)
            val listaClientes: MutableList<Client> = ArrayList()
            for (item in listaClientesDB) {
                val client = Client()
                client.nombreComercial = item.nombre_comercial
                client.calle = item.calle
                client.numero = item.numero
                client.colonia = item.colonia
                client.ciudad = item.ciudad
                client.codigoPostal = item.codigo_postal
                client.fechaRegistro = item.fecha_registro
                client.cuenta = item.cuenta
                client.status = if (item.status) 1 else 0
                client.consec = item.consec
                client.rango = item.rango
                client.lun = item.lun
                client.mar = item.mar
                client.mie = item.mie
                client.jue = item.jue
                client.vie = item.vie
                client.sab = item.sab
                client.dom = item.dom
                client.latitud = item.latitud
                client.longitud = item.longitud
                client.phone_contacto = item.contacto_phone
                client.recordatorio = item.recordatorio
                client.visitas = item.visitasNoefectivas
                client.isCredito = if (item.is_credito) 1 else 0
                client.saldo_credito = item.saldo_credito
                client.limite_credito = item.limite_credito
                client.matriz = if (item.matriz == "null" && item.matriz == null) "null" else item.matriz

                listaClientes.add(client)
            }

            //sellViewState.value = SellViewState.LoadingStart
            NetworkStateTask { connected: Boolean ->
                sellViewState.postValue(SellViewState.LoadingFinish)
                if (connected) {
                    ClientInteractorImp().executeSaveClient(
                        listaClientes,
                        object : SaveClientListener {
                            override fun onSaveClientSuccess() {
                                //Toast.makeText(getApplicationContext(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
                            }

                            override fun onSaveClientError() {
                                //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
                            }
                        })
                } else {
                    sellViewState.postValue(SellViewState.NotInternetConnection)
                }
            }.execute()
        }
    }


    private fun proceedWithoutInternet(clientId: String) {
        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientByAccount(clientId)
        val paymentDao1 = PaymentDao()
        clienteBean?.let {
            val saldoCliente = paymentDao1.getSaldoByCliente(clienteBean.cuenta)
            clienteBean.saldo_credito = saldoCliente
            clienteBean.date_sync = Utils.fechaActual()
            clientDao.save(clienteBean)
            val saldo: Double =
                if (clienteBean.matriz == null || (clienteBean.matriz != null && clienteBean.matriz.compareTo("null", ignoreCase = true) == 0)) {
                    clienteBean.saldo_credito
                } else {
                    val client =
                        clientDao.getClientByAccount(clienteBean.matriz)
                    client?.saldo_credito ?: 0.0
                }
            //sellViewState.postValue(SellViewState.LoadingFinish)
            sellViewState.value =
                SellViewState.ChargeByClientLoaded(
                    clienteBean.cuenta,
                    saldo
                )

            val saldoCLient = if (clienteBean.matriz.isNullOrEmpty() || clienteBean.matriz == "null") {
                Utils.FDinero(clienteBean.saldo_credito)
            } else {
                val clientMatriz = clientDao.getClientByAccount(clienteBean.matriz)
                Utils.FDinero(clientMatriz?.saldo_credito ?: 0.0)
            }
            sellViewState.value =
                SellViewState.ClientsLoaded(
                    clienteBean.nombre_comercial,
                    clienteBean.cuenta,
                    saldoCLient
                )
        }

        val data = SellsModelDao().list() as List<VentasModelBean?>
        partidas.value = (data)
        sellViewState.value = (SellViewState.SellsRefresh(data))

        val sells = SellsModelDao().list() as List<VentasModelBean?>

        var subTotal = 0.0
        var totalImpuesto = 0.0
        /**
         * Obtenemos los totales
         */
        for (sell in sells) {
            sell?.let {
                subTotal += sell.precio * sell.cantidad
                totalImpuesto += sell.precio * sell.cantidad * (sell.impuesto / 100)
                val countProducts = sell.cantidad
            }
        }

        sellImport.value = (subTotal + totalImpuesto)
        var subtotalFormato = Utils.formatMoneyMX(subTotal)
        var impuestoFormato = Utils.formatMoneyMX(totalImpuesto)
        var totalFormato = Utils.formatMoneyMX(subTotal + totalImpuesto)
        if (subtotalFormato.startsWith("$ .")) subtotalFormato =
            "$ 0" + subtotalFormato.substring(2)
        if (impuestoFormato.startsWith("$ .")) impuestoFormato =
            "$ 0" + impuestoFormato.substring(2)
        if (totalFormato.startsWith("$ .")) totalFormato = "$ 0" + totalFormato.substring(2)

        sellViewState. value = (
                SellViewState.ComputedImports(
                    totalFormato,
                    subtotalFormato,
                    impuestoFormato
                )
                )

        sellViewState.value = SellViewState.LoadingFinish
        sellViewState.value = (SellViewState.NotInternetConnection)
    }

}