package com.app.syspoint.viewmodel.sell


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.syspoint.App
import com.app.syspoint.documents.SellTicket
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.interactor.charge.ChargeInteractor.OnGetChargeByClientListener
import com.app.syspoint.interactor.charge.ChargeInteractorImp
import com.app.syspoint.interactor.client.ClientInteractor.SaveClientListener
import com.app.syspoint.interactor.client.ClientInteractorImp
import com.app.syspoint.interactor.prices.PriceInteractor.GetPricesByClientListener
import com.app.syspoint.interactor.prices.PriceInteractorImp
import com.app.syspoint.models.Client
import com.app.syspoint.models.enums.SellType
import com.app.syspoint.models.sealed.SellViewState
import com.app.syspoint.repository.objectBox.AppBundle
import com.app.syspoint.repository.objectBox.dao.*
import com.app.syspoint.repository.objectBox.entities.*
import com.app.syspoint.utils.Actividades
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.*

class SellViewModel: ViewModel() {

    val sellViewState = MutableLiveData<SellViewState>()
    private val partidas = MutableLiveData<List<SellModelBox?>>()
    val partidasEspeciales = MutableLiveData<List<SpecialPricesBox?>>()
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
            val data = SellsModelDao().list() as List<SellModelBox?>
            partidas.postValue(data)
            sellViewState.postValue(SellViewState.SellsLoaded(data))
        }
    }

    @Synchronized
    fun refreshSellData() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = SellsModelDao().list() as List<SellModelBox?>
            partidas.postValue(data)
            sellViewState.postValue(SellViewState.SellsRefresh(data))
        }
    }

    @Synchronized
    fun setUpClientType(clientId: String?) {
        viewModelScope.launch(Dispatchers.Main) {
            val clienteBean = ClientDao().getClientByAccount(clientId)

            val clientType = if (clienteBean != null && clienteBean.isCredito) {
                SellType.CREDITO
            } else {
                SellType.CONTADO
            }
            sellViewState.value = (SellViewState.ClientType(clientType))
        }
    }

    @Synchronized
    fun setUpChargeByClient(clientId: String?) {
        sellViewState.value = SellViewState.LoadingStart

            NetworkStateTask { connected: Boolean ->
                if (connected) {
                    viewModelScope.launch(Dispatchers.IO) {
                        val clientDao = ClientDao()
                        val clienteBean = clientDao.getClientByAccount(clientId.toString())

                        ChargeInteractorImp().executeGetChargeByClient(
                            clienteBean!!.cuenta!!,
                            object : OnGetChargeByClientListener {
                                override fun onGetChargeByClientSuccess(chargeByClientList: List<ChargeBox>) {
                                    val saldoCliente =
                                        ChargeDao().getSaldoByCliente(clienteBean.cuenta!!)
                                    clienteBean.saldo_credito = saldoCliente
                                    clienteBean.date_sync = Utils.fechaActual()
                                    clientDao.insertBox(clienteBean)
                                    val saldo: Double =
                                        if (clienteBean.matriz == null || (clienteBean.matriz != null && clienteBean.matriz!!.compareTo(
                                                "null",
                                                ignoreCase = true
                                            ) == 0)
                                        ) {
                                            clienteBean.saldo_credito
                                        } else {
                                            val client =
                                                clientDao.getClientByAccount(clienteBean.matriz)
                                            client?.saldo_credito ?: 0.0
                                        }
                                    //sellViewState.postValue(SellViewState.LoadingFinish)
                                    sellViewState.postValue(
                                        SellViewState.ChargeByClientLoaded(
                                            clienteBean.cuenta!!,
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
                    }
                } else {
                    proceedWithoutInternet(clientId)
                }
            }.execute()

    }

    fun setUpPricesByClient(clientId: String?) {
        viewModelScope.launch(Dispatchers.IO) {
            PriceInteractorImp().executeGetPricesByClient(clientId!!, object : GetPricesByClientListener {
                override fun onGetPricesByClientSuccess(pricesByClientList: List<SpecialPricesBox>) {
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
                    clienteBean.nombre_comercial!!,
                    clienteBean.cuenta!!,
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

    fun updateSaldo(clientId: String?) {
        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientByAccount(clientId)
        if (clienteBean != null) {
            val saldoCliente = ChargeDao().getSaldoByCliente(clienteBean.cuenta!!)
            clienteBean.saldo_credito = saldoCliente
            clienteBean.date_sync = Utils.fechaActual()
            clientDao.insertBox(clienteBean)
        }
    }

    fun submitSchedule(clientId: Long) {
        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientById(clientId)
        if (clienteBean != null) {
            clienteBean.recordatorio = ""
            clienteBean.isRecordatorio = true
            clienteBean.updatedAt = Utils.fechaActualHMS()
            clientDao.insertBox(clienteBean)

            testLoadClientes(clientId.toString())
        }
    }

    @Synchronized
    fun computeImports() {
        viewModelScope.launch(Dispatchers.Default) {
            val sells: List<SellModelBox?> =
                if (partidas.value.isNullOrEmpty())
                    SellsModelDao().list() as List<SellModelBox?>
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
                impuesto: Int, cantidad: Int): ArrayList<SellModelBox?> {

        val item = SellModelBox()
        val dao = SellsModelDao()
        item.articulo = articulo
        item.descripcion = descripcion
        item.cantidad = cantidad
        item.precio = precio
        item.impuesto = impuesto.toDouble()
        item.observ = descripcion
        dao.insert(item)
        val sells = partidas.value as ArrayList<SellModelBox?>
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
                params[Actividades.PARAM_1] = it.cuenta!!
                params[Actividades.PARAM_2] = it.calle!!
                params[Actividades.PARAM_3] = it.numero!!
                params[Actividades.PARAM_4] = it.colonia!!
                params[Actividades.PARAM_5] = it.nombre_comercial!!
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
                if (clienteBean.isCredito && !clienteBean.matriz.isNullOrEmpty()) {
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
    fun finishPrecature(clientId: String?, sellType: SellType, subtota: String, import: String) {
        sellViewState.value = SellViewState.LoadingStart

        viewModelScope.launch(Dispatchers.IO) {
            val lista = ArrayList<PlayingBox>()
            val sellsDao = SellsDao()
            val sellBox = SellBox()
            val ultimoFolio = sellsDao.getUltimoFolio()
            val productDao = ProductDao()
            //Recorremos las partidas
            partidas.value?.let {
                for (partida in it) {
                    //Validamos si el articulo existe en la base de datos
                    partida?.let {
                        val productsBox = productDao.getProductoByArticulo(partida.articulo)
                        val partidaBean = PlayingBox()
                        partidaBean.articulo.target = productsBox
                        partidaBean.cantidad = partida.cantidad
                        partidaBean.precio = partida.precio
                        partidaBean.impuesto = partida.impuesto
                        partidaBean.observ = productsBox!!.descripcion
                        partidaBean.fecha = Date()
                        partidaBean.hora = Utils.getHoraActual()
                        partidaBean.venta = java.lang.Long.valueOf(ultimoFolio.toLong())
                        partidaBean.descripcion = productsBox.descripcion
                        lista.add(partidaBean)
                    }
                }
            }

            //Le indicamos al sistema que el cliente ya se ah visitado
            val clientDao1 = ClientDao()
            val clienteBean1 = clientDao1.getClientByAccount(clientId.toString())
            var clienteID = clienteBean1!!.id
            val account = clienteBean1.cuenta.toString()

            clienteBean1.visitado = 1
            clienteBean1.visitasNoefectivas = 0
            clienteBean1.date_sync = Utils.fechaActual()
            clientDao1.insertBox(clienteBean1)

            val routingDao = RoutingDao()
            val ruteoBean = routingDao.getRutaEstablecida()
            val ruteClientDao = RuteClientDao()
            val clientesRutaBean =
                if (ruteoBean != null) ruteClientDao.getClienteByCuentaCliente(clientId.toString(), ruteoBean.dia, ruteoBean.ruta!!)
                else ruteClientDao.getClienteByCuentaCliente(clientId.toString())
            if (clientesRutaBean != null) {
                clientesRutaBean.visitado = 1
                ruteClientDao.insert(clientesRutaBean)

                saveTempRuteClient(clientesRutaBean)
            }

            //Obtiene el nombre del vendedor
            val employeeBox = getEmployee()
            val sessionBox = SessionDao().getUserSession()


            sellBox.tipo_doc = "TIK"
            sellBox.fecha = Utils.fechaActual()
            sellBox.hora = Utils.getHoraActual()
            sellBox.clienteId = clienteID
            sellBox.empleadoId = employeeBox?.id ?: (sessionBox?.id?: -1)
            sellBox.client.target = clienteBean1
            sellBox.employee.target = employeeBox
            sellBox.importe = subtota.replace(",", "").toDouble()
            sellBox.impuesto = import.replace(",", "").toDouble()
            sellBox.datos = clienteBean1.nombre_comercial
            sellBox.estado = "CO"
            sellBox.corte = "N"
            sellBox.temporal = 1
            sellBox.venta = ultimoFolio
            sellBox.latidud = latitude.value.toString()
            sellBox.longitud = longitude.value.toString()
            sellBox.sync = 0
            sellBox.tipo_venta = sellType.value
            sellBox.usuario_cancelo = ""
            sellBox.listaPartidas.addAll(lista)

            var clienteMatriz: ClientBox? = null
            if (!clienteBean1.matriz.isNullOrEmpty()) {
                clienteMatriz = clientDao1.getClientByAccount(clienteBean1.matriz)
                clienteMatriz?.let {
                    sellBox.factudado = clienteMatriz.cuenta
                }
            }

            sellBox.ticket = Utils.getHoraActual().replace(":", "") + Utils.getFechaRandom().replace("-", "")
            val totalVenta: Double = subtota.replace(",", "").toDouble() +
                    import.replace(",", "").toDouble()

            val ticketRamdom: String = ticketRamdom() + sellBox.fecha!!.replace("-", "") + "" + sellBox.hora!!.replace(":", "")

            if (sellType == SellType.CREDITO) {
                //Si la cobranza es de matriz entonces creamos la cobranza a matriz

                val isCreditMatriz = clienteBean1.isCredito
                        && !clienteBean1.matriz.isNullOrEmpty()
                        && clienteMatriz != null
                val stockId = StockDao().getCurrentStockId()

                val paymentBox = App.mBoxStore!!.boxFor(ChargeBox::class.java)

                if (isCreditMatriz) {
                    val cobranzaBean = ChargeBox()

                    cobranzaBean.cobranza = ticketRamdom
                    cobranzaBean.cliente = clienteMatriz?.cuenta ?: clienteID.toString()
                    cobranzaBean.importe = totalVenta
                    cobranzaBean.saldo = totalVenta
                    cobranzaBean.acuenta = 0.0
                    cobranzaBean.venta = sellBox.ticket!!.toLong()
                    cobranzaBean.estado = "PE"
                    cobranzaBean.observaciones = "Se realiza la venta a crédito para sucursal ${clienteBean1.cuenta} ${clienteBean1.nombre_comercial} con cargo a Matriz ${clienteMatriz?.cuenta} ${clienteMatriz?.nombre_comercial} ${sellBox.fecha} hora ${sellBox.hora}"
                    cobranzaBean.fecha = sellBox.fecha
                    cobranzaBean.updatedAt = Utils.fechaActualHMS_()
                    cobranzaBean.hora = sellBox.hora
                    cobranzaBean.stockId = stockId
                    if (employeeBox != null) {
                        cobranzaBean.empleado =
                            employeeBox.identificador
                    }
                    cobranzaBean.abono = false
                    paymentBox.put(cobranzaBean)

                    //Actualizamos el documento de la venta con el de la cobranza
                    sellBox.cobranza = ticketRamdom
                    //ventasDao.save(ventasBean);

                    //Actualizamos el saldo del cliente
                    val saldoNuevo = clienteMatriz!!.saldo_credito + totalVenta
                    clienteMatriz.saldo_credito = saldoNuevo
                    clienteMatriz.date_sync = Utils.fechaActual()
                    clientDao1.insert(clienteMatriz)
                    clienteID = clienteMatriz.id
                } else {
                    val cobranzaBean = ChargeBox()
                    cobranzaBean.cobranza = ticketRamdom
                    cobranzaBean.cliente = clienteBean1.cuenta
                    cobranzaBean.importe = totalVenta
                    cobranzaBean.saldo = totalVenta
                    cobranzaBean.acuenta = 0.0
                    cobranzaBean.venta = sellBox.ticket!!.toLong()
                    cobranzaBean.estado = "PE"
                    cobranzaBean.observaciones = "Venta a crédito " + sellBox.fecha + " hora " + sellBox.hora
                    cobranzaBean.fecha = sellBox.fecha
                    cobranzaBean.updatedAt = Utils.fechaActualHMS_()
                    cobranzaBean.hora = sellBox.hora
                    cobranzaBean.stockId = stockId

                    if (employeeBox != null) {
                        cobranzaBean.empleado =
                            employeeBox.identificador
                    }
                    paymentBox.put(cobranzaBean)

                    //Actualizamos el documento de la venta con el de la cobranza
                    sellBox.cobranza = ticketRamdom
                    //ventasDao.save(ventasBean);

                    //Actualizamos el saldo del cliente
                    val saldoNuevo =
                        clienteBean1.saldo_credito + totalVenta
                    clienteBean1.saldo_credito = saldoNuevo
                    clienteBean1.visitasNoefectivas = 0
                    clienteBean1.date_sync =
                        Utils.fechaActual()
                    clientDao1.insertBox(clienteBean1)
                    clienteID = clienteBean1.id
                }
                testLoadClientes(clienteID.toString())
            }
            sellsDao.insert(sellBox)



            //Creamos la venta
            sellsDao.creaVenta(sellBox, lista)
            val ventaID = sellBox.venta

            //Creamos el template del timbre
            val sellTicket = SellTicket()
            sellTicket.box = sellBox
            sellTicket.template()
            val ticket = sellTicket.document
            Log.d("SellViewModel", ticket)

            sellViewState.postValue(SellViewState.LoadingFinish)
            sellViewState.postValue(SellViewState.PrecatureFinished(ticket, ventaID, clienteID, account))
        }
    }

    @Synchronized
    fun testLoadClientes(idCliente: String) {
        sellViewState.postValue(SellViewState.LoadingStart)
        viewModelScope.launch(Dispatchers.IO) {
            val clientDao = ClientDao()
            val listaClientesDB = clientDao.getByIDClient(idCliente.toLong())
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
                client.updatedAt = item.updatedAt
                client.visitas = item.visitasNoefectivas
                client.isCredito = if (item.isCredito) 1 else 0
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


    private fun proceedWithoutInternet(clientId: String?) {
        val clientDao = ClientDao()
        val clienteBean = clientDao.getClientByAccount(clientId)
        clienteBean?.let {
            val saldoCliente = ChargeDao().getSaldoByCliente(clienteBean.cuenta!!)
            clienteBean.saldo_credito = saldoCliente
            clienteBean.date_sync = Utils.fechaActual()
            clientDao.insertBox(clienteBean)
            val saldo: Double =
                if (clienteBean.matriz == null || (clienteBean.matriz != null && clienteBean.matriz!!.compareTo("null", ignoreCase = true) == 0)) {
                    clienteBean.saldo_credito
                } else {
                    val client =
                        clientDao.getClientByAccount(clienteBean.matriz)
                    client?.saldo_credito ?: 0.0
                }
            //sellViewState.postValue(SellViewState.LoadingFinish)
            sellViewState.value =
                SellViewState.ChargeByClientLoaded(
                    clienteBean.cuenta!!,
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
                    clienteBean.nombre_comercial!!,
                    clienteBean.cuenta!!,
                    saldoCLient
                )
        }

        val data = SellsModelDao().list() as List<SellModelBox?>
        partidas.value = (data)
        sellViewState.value = (SellViewState.SellsRefresh(data))

        val sells = SellsModelDao().list() as List<SellModelBox?>

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

    private fun getEmployee(): EmployeeBox? {
        var vendedoresBean = AppBundle.getUserBox()
        if (vendedoresBean == null) {
            val sessionBox = SessionDao().getUserSession()
            vendedoresBean = if (sessionBox != null) {
                EmployeeDao().getEmployeeByID(sessionBox.empleadoId)
            } else {
                CacheInteractor().getSeller()
            }
        }
        return vendedoresBean
    }

    private fun saveTempRuteClient(ruteClientBox: RuteClientBox) {
        val tempRuteClientDao = TempRuteClientDao()
        val tempRuteClientBox = TempRuteClientBox()
        tempRuteClientBox.id = ruteClientBox.id
        tempRuteClientBox.nombre_comercial = ruteClientBox.nombre_comercial
        tempRuteClientBox.calle = ruteClientBox.calle
        tempRuteClientBox.numero = ruteClientBox.numero
        tempRuteClientBox.colonia = ruteClientBox.colonia
        tempRuteClientBox.cuenta = ruteClientBox.cuenta
        tempRuteClientBox.rango = ruteClientBox.rango
        tempRuteClientBox.lun = ruteClientBox.lun
        tempRuteClientBox.mar = ruteClientBox.mar
        tempRuteClientBox.mie = ruteClientBox.mie
        tempRuteClientBox.jue = ruteClientBox.jue
        tempRuteClientBox.vie = ruteClientBox.vie
        tempRuteClientBox.sab = ruteClientBox.sab
        tempRuteClientBox.dom = ruteClientBox.dom
        tempRuteClientBox.lunOrder = ruteClientBox.lunOrder
        tempRuteClientBox.marOrder = ruteClientBox.marOrder
        tempRuteClientBox.mieOrder = ruteClientBox.mieOrder
        tempRuteClientBox.jueOrder = ruteClientBox.jueOrder
        tempRuteClientBox.vieOrder = ruteClientBox.vieOrder
        tempRuteClientBox.sabOrder = ruteClientBox.sabOrder
        tempRuteClientBox.domOrder = ruteClientBox.domOrder
        tempRuteClientBox.order = ruteClientBox.order
        tempRuteClientBox.visitado = ruteClientBox.visitado
        tempRuteClientBox.latitud = ruteClientBox.latitud
        tempRuteClientBox.longitud = ruteClientBox.longitud
        tempRuteClientBox.phone_contact = ruteClientBox.phone_contact
        tempRuteClientBox.status = ruteClientBox.status
        tempRuteClientBox.isCredito = ruteClientBox.isCredito
        tempRuteClientBox.recordatorio = ruteClientBox.recordatorio
        tempRuteClientBox.isRecordatorio = ruteClientBox.isRecordatorio
        tempRuteClientBox.date_sync = ruteClientBox.date_sync
        tempRuteClientBox.updatedAt = ruteClientBox.updatedAt
        tempRuteClientBox.ventaClientId = ruteClientBox.ventaClientId
        tempRuteClientBox.ventaFecha = ruteClientBox.ventaFecha
        tempRuteClientBox.ventaCreatedAt = ruteClientBox.ventaCreatedAt
        tempRuteClientBox.ventaUpdatedAt = ruteClientBox.ventaUpdatedAt

        tempRuteClientDao.insertBox(tempRuteClientBox)
    }

}