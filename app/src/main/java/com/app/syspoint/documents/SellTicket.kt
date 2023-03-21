package com.app.syspoint.documents

import com.app.syspoint.BuildConfig
import com.app.syspoint.repository.database.bean.VentasBean
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.Utils
import com.app.syspoint.interactor.cache.CacheInteractor

class SellTicket: BaseTicket() {

    override fun template() {
        super.template()

        val ventasBean = bean as VentasBean

        /*var saldoCreditodo = 0.0
        var facturaMatriz = false
        if (ventasBean.tipo_venta.compareTo("CREDITO", ignoreCase = true) == 0) {
            if (ventasBean.factudado.isNotEmpty()) {
                val clientDao = ClientDao()
                val clienteMatriz = clientDao.getClientByAccount(ventasBean.factudado)
                if (clienteMatriz != null) {
                    saldoCreditodo = clienteMatriz.saldo_credito
                    facturaMatriz = true
                }
            } else {
                saldoCreditodo = ventasBean.cliente.saldo_credito
                facturaMatriz = false
            }
        }*/


        var ticket : String = when(BuildConfig.FLAVOR) {
            "donaqui" -> {
                buildDonAquiHeader()
            }
            else -> { // default SysPoint
                buildSyspointHeader()
            }
        }

        //val importeTotalVenta: Double = ventasBean.importe + ventasBean.impuesto
        for (items in ventasBean.listaPartidas) {
            ticket += "" + items.articulo.descripcion + Constants.NEW_LINE +
                    "" + String.format(
                "%1$-5s %2$11s %3$10s %4$10s",
                items.cantidad,
                Utils.FDinero(items.precio),
                Utils.FDinero(items.precio * items.cantidad),
                ""
            ) + Constants.NEW_LINE
        }
        ticket += "================================" + Constants.NEW_LINE + Constants.NEW_LINE

/*
        if (ventasBean.getTipo_venta().compareToIgnoreCase("CREDITO") == 0) {

            if (facturaMatriz) {
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", "SALDO ANTERI0R:", Utils.FDinero(saldoCreditodo - importeTotalVenta), "") + salto;
            }else {
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", "SALDO ANTERI0R:", Utils.FDinero(saldoCreditodo - importeTotalVenta), "") + salto;
            }


        }
*/

/*
        if (ventasBean.getTipo_venta().compareToIgnoreCase("CREDITO") == 0) {

            if (facturaMatriz) {
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", "SALDO ANTERI0R:", Utils.FDinero(saldoCreditodo - importeTotalVenta), "") + salto;
            }else {
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", "SALDO ANTERI0R:", Utils.FDinero(saldoCreditodo - importeTotalVenta), "") + salto;
            }


        }*/
        ticket += "" + String.format(
            "%1$-5s %2$-10s %3$11s %4$10s",
            "",
            "   SubTotal:",
            Utils.FDinero(ventasBean.importe),
            ""
        ) + Constants.NEW_LINE +
                "" + String.format(
            "%1$-5s %2$-10s %3$11s %4$10s",
            "",
            "       IVA:",
            Utils.FDinero(ventasBean.impuesto),
            ""
        ) + Constants.NEW_LINE +
                "" + String.format(
            "%1$-5s %2$-10s %3$11s %4$10s",
            "",
            "     Total:",
            Utils.FDinero(ventasBean.importe + ventasBean.impuesto),
            ""
        ) + "" + Constants.NEW_LINE
        /*if (ventasBean.getTipo_venta().compareToIgnoreCase("CREDITO") == 0) {
            if (facturaMatriz){
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", " SALDO ACTUAL:", Utils.FDinero(saldoCreditodo), "") + salto + salto;
            }else{
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", " SALDO ACTUAL:", Utils.FDinero(saldoCreditodo), "") + salto + salto;
            }
        }*/
        /*if (ventasBean.getTipo_venta().compareToIgnoreCase("CREDITO") == 0) {
            if (facturaMatriz){
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", " SALDO ACTUAL:", Utils.FDinero(saldoCreditodo), "") + salto + salto;
            }else{
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", " SALDO ACTUAL:", Utils.FDinero(saldoCreditodo), "") + salto + salto;
            }
        }*/
        ticket += "================================" + Constants.NEW_LINE
        if (ventasBean.tipo_venta.compareTo("CREDITO", ignoreCase = true) == 0) {
            ticket += "FIRMA DE CONFORMIDAD:           " + Constants.NEW_LINE +
                    "" + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE +
                    "                                " + Constants.NEW_LINE +
                    "================================" + Constants.NEW_LINE +
                    "" + ventasBean.fecha + " " + ventasBean.hora + "" + Constants.NEW_LINE +
                    "FOLIO FINAL:         " + ventasBean.ticket + Constants.NEW_LINE +
                    "" + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE
        } else if (ventasBean.tipo_venta.compareTo("CONTADO", ignoreCase = true) == 0) {
            ticket += "" + ventasBean.fecha + " " + ventasBean.hora + "" + Constants.NEW_LINE +
                    "FOLIO FINAL:         " + ventasBean.ticket + Constants.NEW_LINE +
                    "" + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE
        }


        document = ticket
    }

    override fun buildSyspointHeader(): String {
        val ventasBean = bean as VentasBean

        val vendedor = if (ventasBean.empleado != null) {
            "Vendedor:" + ventasBean.empleado.getNombre() + Constants.NEW_LINE
        } else {
            val empleadoBean = CacheInteractor().getSeller()
            if (empleadoBean != null) "Vendedor:" + empleadoBean.getNombre() + Constants.NEW_LINE
            else ""
        }

        return "     AGUA POINT S.A. DE C.V.    " + Constants.NEW_LINE +
                "     Calz. Aeropuerto 4912 A    " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "           APO170818QR6         " + Constants.NEW_LINE +
                "          (667) 744-9350        " + Constants.NEW_LINE +
                "        info@aguapoint.com      " + Constants.NEW_LINE +
                "         www.aguapoint.com      " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "(" + ventasBean.cliente.cuenta + ")  " + ventasBean.cliente.nombre_comercial + Constants.NEW_LINE + vendedor +
                "" + ventasBean.fecha + " " + ventasBean.hora + "" + Constants.NEW_LINE +
                "FOLIO FINAL:         " + ventasBean.ticket + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "          NOTA DE VENTA         " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }

    override fun buildDonAquiHeader(): String {
        val ventasBean = bean as VentasBean

        val vendedor = if (ventasBean.empleado != null) {
            "Vendedor:" + ventasBean.empleado.getNombre() + Constants.NEW_LINE
        } else {
            val empleadoBean = CacheInteractor().getSeller()
            if (empleadoBean != null) "Vendedor:" + empleadoBean.getNombre() + Constants.NEW_LINE
            else ""
        }

        return "         AGUAS DON AQUI         " + Constants.NEW_LINE +
                " Blvd. Manuel J. Clouthier 2755 " + Constants.NEW_LINE +
                "     Buenos Aires C.P. 80199    " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "          HIMA9801022T8         " + Constants.NEW_LINE +
                "    Adalberto Higuera Mendez    " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "(" + ventasBean.cliente.cuenta + ")  " + ventasBean.cliente.nombre_comercial + Constants.NEW_LINE +
                vendedor +
                "" + ventasBean.fecha + " " + ventasBean.hora + "" + Constants.NEW_LINE +
                "FOLIO FINAL:         " + ventasBean.ticket + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "          NOTA DE VENTA         " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }
}