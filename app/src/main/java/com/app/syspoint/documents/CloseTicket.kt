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
            Constants.DON_AQUI_FLAVOR_TAG -> {
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
        var cliente = Constants.EMPTY_STRING

        listaCorte.map { partida ->
            if (partida.clienteBean.cuenta.compareTo(cliente, ignoreCase = true) != 0) {
                cliente = partida.clienteBean.cuenta
                ticket += "$cliente ${partida.clienteBean.nombre_comercial}" + Constants.NEW_LINE
            }
            if (partida.tipoVenta == Constants.CONTADO) {
                totalContado += partida.cantidad * partida.precio * (1 + partida.impuesto / 100)
                contadoCount++
            } else {
                totalCredito += partida.cantidad * partida.precio * (1 + partida.impuesto / 100)
                creditoCount++
            }

            ticket += partida.descripcion + Constants.NEW_LINE
            ticket += String.format(
                "%1$-5s  %2$11s  %3$10s",
                Utils.FDinero(partida.precio),
                partida.cantidad,
                Utils.FDinero(partida.cantidad * partida.precio * (1 + partida.impuesto / 100))
            ) + Constants.NEW_LINE
        }

        ticket += "           INVENTARIOS          " + Constants.NEW_LINE +
                  "================================" + Constants.NEW_LINE +
                  "PRODUCTO             " + Constants.NEW_LINE +
                  "INICIAL    VENTA    FINAL  " + Constants.NEW_LINE +
                  "================================" + Constants.NEW_LINE

        mLidata.map { inventory ->
            val stockHistoryDao = StockHistoryDao()
            val inventarioHistorialBean =
                stockHistoryDao.getInvatarioPorArticulo(inventory.articulo.articulo)
            val vendido = inventarioHistorialBean?.cantidad ?: 0
            val inicial = inventory.totalCantidad
            val final = inicial - vendido
            ticket += inventory.articulo.descripcion + Constants.NEW_LINE +
                    String.format(
                        "%1$-5s  %2$11s  %3$10s",
                        inicial,
                        vendido,
                        final
                    ) + Constants.NEW_LINE
        }

        ticket += "          VENTAS TOTALES        " + Constants.NEW_LINE +
                  "================================" + Constants.NEW_LINE
        ticket += "VENTAS DE CONTADO ($contadoCount)" + Constants.NEW_LINE
        ticket += Utils.FDinero(totalContado) + Constants.NEW_LINE + Constants.NEW_LINE

        ticket += "VENTAS A CRÉDITO ($creditoCount)" + Constants.NEW_LINE
        ticket += Utils.FDinero(totalCredito) + Constants.NEW_LINE+ Constants.NEW_LINE

        if (mListCharge.isNotEmpty()) {
            ticket += "            COBRANZAS           " + Constants.NEW_LINE +
                    "================================" + Constants.NEW_LINE
            ticket += "CLIENTE    TICKET      TOTAL    " + Constants.NEW_LINE
            var totalChargeAmount = 0.0
            mListCharge.map { charge ->
                totalChargeAmount += charge.abono
                ticket += String.format(
                    "%1$-5s  %2$11s  %3$10s",
                    charge.comertialName,
                    charge.ticket,
                    charge.abono
                ) + Constants.NEW_LINE
            }

            ticket += "================================" + Constants.NEW_LINE

            ticket += " Cobranza (${mListCharge.size}) " + Constants.NEW_LINE +
                    String.format(" %1$-5s", Utils.FDinero(totalChargeAmount))
        }

        ticket += Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE


        ticket += "FIRMA DEL VENDEDOR:           " + Constants.NEW_LINE +
                Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE +
                "                                " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE

        ticket += "FIRMA DEL SUPERVISOR:           " + Constants.NEW_LINE +
                Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE +
                "                                " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE
        document = ticket
    }

    override fun buildSyspointHeader(): String {
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) vendedoresBean = CacheInteractor().getSeller()

        val sellers = if (vendedoresBean != null)
            vendedoresBean.getNombre() + Constants.NEW_LINE
        else Constants.EMPTY_STRING + Constants.NEW_LINE

        return  "     AGUA POINT S.A. DE C.V.    " + Constants.NEW_LINE +
                "     Calz. Aeropuerto 4912 A    " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "           APO170818QR6         " + Constants.NEW_LINE +
                "          (667) 744-9350        " + Constants.NEW_LINE +
                "        info@aguapoint.com      " + Constants.NEW_LINE +
                "         www.aguapoint.com      " + Constants.NEW_LINE +
                Constants.NEW_LINE + Constants.NEW_LINE +
                sellers +
                Utils.fechaActual() + " " + Utils.getHoraActual() + Constants.EMPTY_STRING +
                Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE +
                "          CORTE DE CAJA         " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CLIENTE / PRODUCTO             " + Constants.NEW_LINE +
                "PRECIO    CANTIDAD    IMPORTE  " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }

    override fun buildDonAquiHeader(): String {
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) vendedoresBean = CacheInteractor().getSeller()

        val sellers = if (vendedoresBean != null)
            vendedoresBean.getNombre() + Constants.NEW_LINE
        else Constants.EMPTY_STRING + Constants.NEW_LINE

        return "         AGUAS DON AQUI         " + Constants.NEW_LINE +
                " Blvd. Manuel J. Clouthier 2755 " + Constants.NEW_LINE +
                "     Buenos Aires C.P. 80199    " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "          HIMA9801022T8         " + Constants.NEW_LINE +
                "    Adalberto Higuera Mendez    " + Constants.NEW_LINE +
                Constants.NEW_LINE + Constants.NEW_LINE +
                sellers +
                Utils.fechaActual() + " " + Utils.getHoraActual() + Constants.EMPTY_STRING +
                Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE +
                "          CORTE DE CAJA         " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CLIENTE / PRODUCTO             " + Constants.NEW_LINE +
                "PRECIO    CANTIDAD    IMPORTE  " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }
}