package com.app.syspoint.documents

import com.app.syspoint.BuildConfig
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.Utils
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.models.CloseCash
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.database.dao.*

class CloseTicket: BaseTicket() {


    override fun template() {
        super.template()
        val stockId = StockDao().getCurrentStockId()

        val listaCorte: List<CorteBean> = SellsDao().getAllPartsGroupedClient()
        val mLidata: List<InventarioBean> = StockDao().list() as List<InventarioBean>
        val mListCharge: List<CloseCash> = PaymentDao().getAllConfirmedChargesToday(stockId)

        var ticket : String = when(BuildConfig.FLAVOR) {
            "donaqui" -> {
                buildDonAquiHeader()
            }
            else -> { // default SysPoint
                buildSyspointHeader()
            }
        }

        var totalCredito = 0.0
        var creditoCount = 0
        var totalContado = 0.0
        var contadoCount = 0
        var cliente = ""

        listaCorte.map { partida ->
            if (partida.clienteBean.cuenta.compareTo(cliente, ignoreCase = true) != 0) {
                cliente = partida.clienteBean.cuenta
                ticket += "$cliente ${partida.clienteBean.nombre_comercial}" + Constants.newLine
            }
            if (partida.tipoVenta == "Contado") {
                totalContado += partida.cantidad * partida.precio * (1 + partida.impuesto / 100)
                contadoCount++
            } else {
                totalCredito += partida.cantidad * partida.precio * (1 + partida.impuesto / 100)
                creditoCount++
            }

            ticket += partida.descripcion + Constants.newLine
            ticket += String.format(
                "%1$-5s  %2$11s  %3$10s",
                Utils.FDinero(partida.precio),
                partida.cantidad,
                Utils.FDinero(partida.cantidad * partida.precio * (1 + partida.impuesto / 100))
            ) + Constants.newLine
        }

        ticket += "           INVENTARIOS          " + Constants.newLine +
                  "================================" + Constants.newLine +
                  "PRODUCTO             " + Constants.newLine +
                  "INICIAL    VENTA    FINAL  " + Constants.newLine +
                  "================================" + Constants.newLine

        mLidata.map { inventory ->
            val stockHistoryDao = StockHistoryDao()
            val inventarioHistorialBean =
                stockHistoryDao.getInvatarioPorArticulo(inventory.articulo.articulo)
            val vendido = inventarioHistorialBean?.cantidad ?: 0
            val inicial = inventory.totalCantidad
            val final = inicial - vendido
            ticket += inventory.articulo.descripcion + Constants.newLine +
                    String.format(
                        "%1$-5s  %2$11s  %3$10s",
                        inicial,
                        vendido,
                        final
                    ) + Constants.newLine
        }

        ticket += "          VENTAS TOTALES        " + Constants.newLine +
                  "================================" + Constants.newLine
        ticket += "VENTAS DE CONTADO ($contadoCount)" + Constants.newLine
        ticket += Utils.FDinero(totalContado) + Constants.newLine + Constants.newLine

        ticket += "VENTAS A CRÉDITO ($creditoCount)" + Constants.newLine
        ticket += Utils.FDinero(totalCredito) + Constants.newLine+ Constants.newLine

        ticket += "            COBRANZAS           " + Constants.newLine +
                  "================================" + Constants.newLine
        ticket += "CLIENTE    TICKET      TOTAL    " + Constants.newLine
        mListCharge.map { charge ->
            ticket += String.format(
                "%1$-5s  %2$11s  %3$10s",
                charge.comertialName,
                charge.ticket,
                charge.abono
            ) + Constants.newLine
        }

        ticket += "================================" + Constants.newLine +
        "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine

        ticket += "FIRMA DEL VENDEDOR:           " + Constants.newLine +
                "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine +
                "                                " + Constants.newLine +
                "================================" + Constants.newLine +
                "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine

        ticket += "FIRMA DEL SUPERVISOR:           " + Constants.newLine +
                "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine +
                "                                " + Constants.newLine +
                "================================" + Constants.newLine +
                "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine
        document = ticket
    }

    override fun buildSyspointHeader(): String {
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }
        val sellers = if (vendedoresBean != null) "" + vendedoresBean.getNombre() + Constants.newLine else ""

        return  "     AGUA POINT S.A. DE C.V.    " + Constants.newLine +
                "     Calz. Aeropuerto 4912 A    " + Constants.newLine +
                "      San Rafael C.P. 80150     " + Constants.newLine +
                "        Culiacan, Sinaloa       " + Constants.newLine +
                "           APO170818QR6         " + Constants.newLine +
                "          (667) 744-9350        " + Constants.newLine +
                "        info@aguapoint.com      " + Constants.newLine +
                "         www.aguapoint.com      " + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                sellers +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                "          CORTE DE CAJA         " + Constants.newLine +
                "================================" + Constants.newLine +
                "CLIENTE / PRODUCTO             " + Constants.newLine +
                "PRECIO    CANTIDAD    IMPORTE  " + Constants.newLine +
                "================================" + Constants.newLine
    }

    override fun buildDonAquiHeader(): String {
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }

        val sellers = if (vendedoresBean != null) "" + vendedoresBean.getNombre() + Constants.newLine else ""

        return "         AGUAS DON AQUI         " + Constants.newLine +
                " Blvd. Manuel J. Clouthier 2755 " + Constants.newLine +
                "     Buenos Aires C.P. 80199    " + Constants.newLine +
                "        Culiacan, Sinaloa       " + Constants.newLine +
                "          HIMA9801022T8         " + Constants.newLine +
                "    Adalberto Higuera Mendez    " + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                sellers +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                "          CORTE DE CAJA         " + Constants.newLine +
                "================================" + Constants.newLine +
                "CLIENTE / PRODUCTO             " + Constants.newLine +
                "PRECIO    CANTIDAD    IMPORTE  " + Constants.newLine +
                "================================" + Constants.newLine
    }
}