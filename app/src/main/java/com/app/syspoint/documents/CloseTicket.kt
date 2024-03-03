package com.app.syspoint.documents

import com.app.syspoint.models.CloseCash
import com.app.syspoint.repository.objectBox.dao.ChargeDao
import com.app.syspoint.repository.objectBox.dao.SellsDao
import com.app.syspoint.repository.objectBox.dao.StockDao
import com.app.syspoint.repository.objectBox.dao.StockHistoryDao
import com.app.syspoint.repository.objectBox.entities.CashCloseBox
import com.app.syspoint.repository.objectBox.entities.StockBox
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.Utils

class CloseTicket: BaseTicket() {

    override fun template() {
        super.template()
        val stockId = StockDao().getCurrentStockId()

        val listaCorte: List<CashCloseBox> = SellsDao().getAllPartsGroupedClient()
        val mLidata: List<StockBox> = StockDao().list()
        val mListCharge: List<CloseCash> = ChargeDao().getAllConfirmedChargesToday(stockId)

        val employee = getEmployee()
        val clientId = employee?.clientId?:"tenet"

        var ticket : String = when(clientId) {
            Constants.NUTRIRICA_CLIENT_ID -> {
                buildNutriricaHeader()
            }
            Constants.DON_AQUI_CLIENT_ID -> {
                buildDonAquiHeader()
            }
            Constants.PRUEBAS_CLIENT_ID -> {
                buildPruebasHeader()
            }
            else -> { // default Tenet
                buildTenetHeader()
            }
        }

        /*var ticket : String = when(BuildConfig.FLAVOR) {
            Constants.DON_AQUI_FLAVOR_TAG -> {
                buildDonAquiHeader()
            }
            else -> { // default SysPoint
                buildSyspointHeader()
            }
        }*/

        var totalCredito = 0.0
        var creditoCount = 0
        var totalContado = 0.0
        var contadoCount = 0
        var cliente = Constants.EMPTY_STRING

        val listaCorteDistricted = listaCorte.distinctBy {
            it.sellId
        }.map { partida ->
            if (partida.tipoVenta == Constants.CONTADO) {
                contadoCount++
            } else {
                creditoCount++
            }
        }

        listaCorte.map { partida ->
            if (partida.client.target.cuenta!!.compareTo(cliente, ignoreCase = true) != 0) {
                cliente = partida.client.target.cuenta!!
                ticket += partida.client.target.nombre_comercial + Constants.NEW_LINE
            }
            ticket += partida.descripcion + Constants.NEW_LINE

            /*if (partida.tipoVenta == Constants.CONTADO) {
                totalContado += partida.cantidad * (partida.precio * (1 + partida.impuesto / 100))
            } else {
                totalCredito += partida.cantidad * (partida.precio * (1 + partida.impuesto / 100))
            }*/

            ticket += String.format(
                "%1$-5s  %2$11s  %3$10s",
                Utils.FDinero(partida.precio),
                partida.cantidad,
                Utils.FDinero(partida.cantidad * (partida.precio * (1 + partida.impuesto / 100)))
            ) + Constants.NEW_LINE
        }


        listaCorte.map { partida ->
            if (partida.tipoVenta == Constants.CONTADO) {
                totalContado += partida.cantidad * (partida.precio * (1 + partida.impuesto / 100))
            } else {
                totalCredito += partida.cantidad * (partida.precio * (1 + partida.impuesto / 100))
            }
        }

        ticket += "           INVENTARIOS          " + Constants.NEW_LINE +
                  "================================" + Constants.NEW_LINE +
                  "PRODUCTO             " + Constants.NEW_LINE +
                  "INICIAL    VENTA    FINAL  " + Constants.NEW_LINE +
                  "================================" + Constants.NEW_LINE

        mLidata.map { inventory ->
            val stockHistoryDao = StockHistoryDao()
            val inventarioHistorialBean =
                stockHistoryDao.getInvatarioPorArticulo(inventory.articulo.target.articulo)
            val vendido = inventarioHistorialBean?.cantidad ?: 0
            val inicial = inventory.totalCantidad
            val final = inicial - vendido
            ticket += inventory.articulo.target.descripcion + Constants.NEW_LINE +
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

        ticket += "VENTAS A CREDITO ($creditoCount)" + Constants.NEW_LINE
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
                    Utils.FDinero(charge.abono)
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

    override fun buildTenetHeader(): String {
        val employeeBox = getEmployee()

        val sellers = if (employeeBox != null)
            employeeBox.nombre + Constants.NEW_LINE
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
        val employeeBox = getEmployee()

        val sellers = if (employeeBox != null)
            employeeBox.nombre + Constants.NEW_LINE
        else Constants.EMPTY_STRING + Constants.NEW_LINE

        return  "            TOMAGUA             " + Constants.NEW_LINE +
                "   Vianey Arlyn Carrillo Guerra " + Constants.NEW_LINE +
                "       Alvaro Obregon 4113      " + Constants.NEW_LINE +
                "      Las Cucas C.P. 80018      " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "        RFC:CAGV951105KR3       " + Constants.NEW_LINE +
                "         (667) 320-4000         " + Constants.NEW_LINE +
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

    override fun buildNutriricaHeader(): String {
        val employeeBox = getEmployee()

        val sellers = if (employeeBox != null)
            employeeBox.nombre + Constants.NEW_LINE
        else Constants.EMPTY_STRING + Constants.NEW_LINE

        return  "            Nutri Rica          " + Constants.NEW_LINE +
                "       Pedro de tovar #5460     " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "         (667) 455-9828         " + Constants.NEW_LINE +
                " ALEXI DE JESUS MENDEZ COYANTES " + Constants.NEW_LINE +
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

    override fun buildPruebasHeader(): String {
        val employeeBox = getEmployee()

        val sellers = if (employeeBox != null)
            employeeBox.nombre + Constants.NEW_LINE
        else Constants.EMPTY_STRING + Constants.NEW_LINE

        return  "              SWTENET           " + Constants.NEW_LINE +
                "      Calz. Aeropuerto 4912-A   " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "         (667) 208-1920         " + Constants.NEW_LINE +
                " JESUS OSVALDO CAZARES ESQUERRA " + Constants.NEW_LINE +
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