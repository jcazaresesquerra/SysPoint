package com.app.syspoint.viewmodel.viewPDF

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androidnetworking.error.ANError
import com.app.syspoint.interactor.charge.ChargeInteractor.OnSaveChargeListener
import com.app.syspoint.interactor.charge.ChargeInteractorImp
import com.app.syspoint.interactor.client.ClientInteractor.SaveClientListener
import com.app.syspoint.interactor.client.ClientInteractorImp
import com.app.syspoint.models.Client
import com.app.syspoint.models.Payment
import com.app.syspoint.repository.objectBox.dao.*
import com.app.syspoint.repository.objectBox.entities.StockBox
import com.app.syspoint.repository.objectBox.entities.StockHistoryBox
import com.app.syspoint.repository.request.http.Servicio.ResponseOnError
import com.app.syspoint.repository.request.http.Servicio.ResponseOnSuccess
import com.app.syspoint.repository.request.http.SincVentasByID
import com.app.syspoint.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import timber.log.Timber

private const val TAG = "ViewPDFViewModel"

class ViewPDFViewModel: ViewModel() {

    fun addProductosInventori(venta: Long) {
        Timber.tag(TAG).d("addProductosInventori $venta")
        viewModelScope.launch(Dispatchers.IO) {
            val sellsDao = SellsDao()
            val ventasBean = sellsDao.getVentaByInventario(venta)

            if (ventasBean != null) {

                //Contiene las partidas de la venta
                for (item in ventasBean.listaPartidas) {

                    //Consultamos a la base de datos si existe el producto
                    val productDao = ProductDao()
                    val productoBean = productDao.getProductoByArticulo(item.articulo.target.articulo)

                    //Si no existe en el inventario creamos el producto
                    if (productoBean != null) {

                        //Si existe entonces creamos el inser en estado PE
                        val stockDao = StockDao()
                        val inventarioBean = stockDao.getProductoByArticulo(item.articulo.target.articulo)

                        //Si no existe se deja como pendiente
                        if (inventarioBean == null) {
                            val bean = StockBox()
                            val dao = StockDao()
                            bean.articulo.target = productoBean
                            bean.cantidad = 0
                            bean.estado = "PE"
                            bean.precio = item.precio
                            bean.fecha = Utils.fechaActual()
                            bean.hora = Utils.getHoraActual()
                            bean.impuesto = item.impuesto
                            bean.articulo_clave = productoBean.articulo
                            dao.insert(bean)
                            val stockHistoryDao = StockHistoryDao()
                            val inventarioHistorialBean = stockHistoryDao.getInvatarioPorArticulo(productoBean.articulo)
                            if (inventarioHistorialBean != null) {
                                inventarioHistorialBean.cantidad = inventarioHistorialBean.cantidad + item.cantidad
                                stockHistoryDao.insert(inventarioHistorialBean)
                            } else {
                                val invBean = StockHistoryBox()
                                val invDao = StockHistoryDao()
                                invBean.articulo.target = productoBean
                                invBean.articulo_clave = productoBean.articulo
                                invBean.cantidad = item.cantidad
                                invDao.insert(invBean)
                            }
                        } else {
                            //Si existe entonces actualizamos los datos
                            val stockHistoryDao = StockHistoryDao()
                            val inventarioHistorialBean = stockHistoryDao.getInvatarioPorArticulo(productoBean.articulo)

                            //Si existe entonces actualizamos las cantidades
                            if (inventarioHistorialBean != null) {
                                inventarioHistorialBean.cantidad = inventarioHistorialBean.cantidad + item.cantidad
                                stockHistoryDao.insert(inventarioHistorialBean)
                            } else {
                                //Creamos el historial del Inventario
                                val invBean = StockHistoryBox()
                                val invDao = StockHistoryDao()
                                invBean.articulo.target = productoBean
                                invBean.articulo_clave = productoBean.articulo
                                invBean.cantidad = item.cantidad
                                invDao.insert(invBean)
                            }
                        }

                        productoBean.existencia = productoBean.existencia - item.cantidad

                        Timber.tag(TAG).d("insert product: $productoBean")
                        productDao.insert(productoBean)
                    }
                }

            }
        }
    }


    fun sync(venta: Long, clienteID: Long) {
        Timber.tag(TAG).d("Sync $venta $clienteID")
        viewModelScope.launch(Dispatchers.IO) {
            syncCloudVenta(venta)
            sincronizaCliente(clienteID)
            loadCobranza()
        }
    }


    private fun loadCobranza() {
        Timber.tag(TAG).d("Load cobranza")
        val cobranzaBeanList = ChargeDao().getCobranzaFechaActual(Utils.fechaActual())
        val listaCobranza: MutableList<Payment> = ArrayList()
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
            listaCobranza.add(cobranza)
        }
        Timber.tag(TAG).d("Cobranzas: $cobranzaBeanList")
        ChargeInteractorImp().executeSaveCharge(listaCobranza, object : OnSaveChargeListener {
            override fun onSaveChargeSuccess() {
                Timber.tag(TAG).d("executeSaveCharge success")
                //Toast.makeText(getApplicationContext(), "Cobranza guardada correctamente", Toast.LENGTH_LONG).show();
            }

            override fun onSaveChargeError() {
                Timber.tag(TAG).d("executeSaveCharge error")
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un problema al guardar la cobranza", Toast.LENGTH_LONG).show();
            }
        })
    }

    private fun sincronizaCliente(idCliente: Long) {
        Timber.tag(TAG).d("sincronizaCliente: $idCliente")
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
            if (item.matriz === "null" && item.matriz == null && item.matriz!!.isEmpty()) {
                client.matriz = ""
            } else {
                client.matriz = item.matriz
            }
            listaClientes.add(client)
        }

        Timber.tag(TAG).d("Clients: $listaClientes")
        ClientInteractorImp().executeSaveClient(listaClientes, object : SaveClientListener {
            override fun onSaveClientSuccess() {
                Timber.tag(TAG).d("executeSaveCharge success")
            }

            override fun onSaveClientError() {
                Timber.tag(TAG).d("executeSaveCharge error")
            }
        })
    }

    private fun syncCloudVenta(venta: Long) {
        try {
            Timber.tag(TAG).d("syncCloudVenta $venta")
            val sincVentasByID = SincVentasByID(venta)
            sincVentasByID.setOnSuccess(object : ResponseOnSuccess() {
                @Throws(JSONException::class)
                override fun onSuccess(response: JSONArray) {
                    Timber.tag(TAG).d("SincVentasByID Send sell success")
                }

                @Throws(Exception::class)
                override fun onSuccessObject(response: JSONObject) {
                    Timber.tag(TAG).d( "SincVentasByID Send sell successObject")
                }
            })
            sincVentasByID.setOnError(object : ResponseOnError() {
                override fun onError(error: ANError) {
                    Timber.tag(TAG).d( "SincVentasByID Send sell error ANR")
                }
                override fun onError(error: String) {
                    Timber.tag(TAG).d( "SincVentasByID Send sell error")
                }
            })
            sincVentasByID.postObject()
        } catch (e: Exception) {
            e.printStackTrace()
            Timber.tag(TAG).e(e)
        }
    }
}