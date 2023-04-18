package com.app.syspoint.viewmodel.viewPDF

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

class ViewPDFViewModel: ViewModel() {


    fun addProductosInventori(venta: Long) {
        viewModelScope.launch(Dispatchers.Default) {
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
                    }
                }

                upadteExistencias(venta)
            }
        }
    }

    //Actualiza las existencias del producto
    private fun upadteExistencias(venta: Long) {
        val sellsDao = SellsDao()
        val ventasBean = sellsDao.getVentaByInventario(venta)
        for (item in ventasBean!!.listaPartidas) {
            val productDao = ProductDao()
            val productoBean = productDao.getProductoByArticulo(item.articulo.target.articulo)
            if (productoBean != null) {
                productoBean.existencia = productoBean.existencia - item.cantidad
                productDao.insert(productoBean)
            }
        }
    }

    fun sync(venta: Long, clienteID: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            syncCloudVenta(venta)
            sincronizaCliente(clienteID)
            loadCobranza()
        }
    }

    private fun loadCobranza() {
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
        ChargeInteractorImp().executeSaveCharge(listaCobranza, object : OnSaveChargeListener {
            override fun onSaveChargeSuccess() {
                //Toast.makeText(getApplicationContext(), "Cobranza guardada correctamente", Toast.LENGTH_LONG).show();
            }

            override fun onSaveChargeError() {
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un problema al guardar la cobranza", Toast.LENGTH_LONG).show();
            }
        })
    }

    private fun sincronizaCliente(idCliente: Long) {
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
        ClientInteractorImp().executeSaveClient(listaClientes, object : SaveClientListener {
            override fun onSaveClientSuccess() {
                //Toast.makeText(getApplicationContext(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
            }

            override fun onSaveClientError() {
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
            }
        })
    }

    private fun syncCloudVenta(venta: Long) {
        try {
            val sincVentasByID = SincVentasByID(venta)
            sincVentasByID.setOnSuccess(object : ResponseOnSuccess() {
                @Throws(JSONException::class)
                override fun onSuccess(response: JSONArray) {
                }

                @Throws(Exception::class)
                override fun onSuccessObject(response: JSONObject) {
                }
            })
            sincVentasByID.setOnError(object : ResponseOnError() {
                override fun onError(error: ANError) {}
                override fun onError(error: String) {}
            })
            sincVentasByID.postObject()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}