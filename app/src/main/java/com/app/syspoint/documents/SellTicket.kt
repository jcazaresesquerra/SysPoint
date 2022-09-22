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
            ticket += "" + items.articulo.descripcion + Constants.newLine +
                    "" + String.format(
                "%1$-5s %2$11s %3$10s %4$10s",
                items.cantidad,
                Utils.FDinero(items.precio),
                Utils.FDinero(items.precio * items.cantidad),
                ""
            ) + Constants.newLine
        }
        ticket += "================================" + Constants.newLine + Constants.newLine

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
        ) + Constants.newLine +
                "" + String.format(
            "%1$-5s %2$-10s %3$11s %4$10s",
            "",
            "       IVA:",
            Utils.FDinero(ventasBean.impuesto),
            ""
        ) + Constants.newLine +
                "" + String.format(
            "%1$-5s %2$-10s %3$11s %4$10s",
            "",
            "     Total:",
            Utils.FDinero(ventasBean.importe + ventasBean.impuesto),
            ""
        ) + "" + Constants.newLine
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
        ticket += "================================" + Constants.newLine
        if (ventasBean.tipo_venta.compareTo("CREDITO", ignoreCase = true) == 0) {
            ticket += "FIRMA DE CONFORMIDAD:           " + Constants.newLine +
                    "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine +
                    "                                " + Constants.newLine +
                    "================================" + Constants.newLine +
                    "" + ventasBean.fecha + " " + ventasBean.hora + "" + Constants.newLine +
                    "FOLIO FINAL:         " + ventasBean.ticket + Constants.newLine +
                    "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine
        } else if (ventasBean.tipo_venta.compareTo("CONTADO", ignoreCase = true) == 0) {
            ticket += "" + ventasBean.fecha + " " + ventasBean.hora + "" + Constants.newLine +
                    "FOLIO FINAL:         " + ventasBean.ticket + Constants.newLine +
                    "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine
        }


        document = ticket
    }

    override fun buildSyspointHeader(): String {
        val ventasBean = bean as VentasBean

        val vendedor = if (ventasBean.empleado != null) {
            "Vendedor:" + ventasBean.empleado.getNombre() + Constants.newLine
        } else {
            val empleadoBean = CacheInteractor().getSeller()
            if (empleadoBean != null) "Vendedor:" + empleadoBean.getNombre() + Constants.newLine
            else ""
        }

        return "     AGUA POINT S.A. DE C.V.    " + Constants.newLine +
                "     Calz. Aeropuerto 4912 A    " + Constants.newLine +
                "      San Rafael C.P. 80150     " + Constants.newLine +
                "        Culiacan, Sinaloa       " + Constants.newLine +
                "           APO170818QR6         " + Constants.newLine +
                "          (667) 744-9350        " + Constants.newLine +
                "        info@aguapoint.com      " + Constants.newLine +
                "         www.aguapoint.com      " + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                "(" + ventasBean.cliente.cuenta + ")  " + ventasBean.cliente.nombre_comercial + Constants.newLine + vendedor +
                "" + ventasBean.fecha + " " + ventasBean.hora + "" + Constants.newLine +
                "FOLIO FINAL:         " + ventasBean.ticket + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                "          NOTA DE VENTA         " + Constants.newLine +
                "================================" + Constants.newLine +
                "CONCEPTO / PRODUCTO             " + Constants.newLine +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.newLine +
                "================================" + Constants.newLine
    }

    override fun buildDonAquiHeader(): String {
        val ventasBean = bean as VentasBean

        val vendedor = if (ventasBean.empleado != null) {
            "Vendedor:" + ventasBean.empleado.getNombre() + Constants.newLine
        } else {
            val empleadoBean = CacheInteractor().getSeller()
            if (empleadoBean != null) "Vendedor:" + empleadoBean.getNombre() + Constants.newLine
            else ""
        }

        return "         AGUAS DON AQUI         " + Constants.newLine +
                " Blvd. Manuel J. Clouthier 2755 " + Constants.newLine +
                "     Buenos Aires C.P. 80199    " + Constants.newLine +
                "        Culiacan, Sinaloa       " + Constants.newLine +
                "          HIMA9801022T8         " + Constants.newLine +
                "    Adalberto Higuera Mendez    " + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                "(" + ventasBean.cliente.cuenta + ")  " + ventasBean.cliente.nombre_comercial + Constants.newLine +
                vendedor +
                "" + ventasBean.fecha + " " + ventasBean.hora + "" + Constants.newLine +
                "FOLIO FINAL:         " + ventasBean.ticket + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                "          NOTA DE VENTA         " + Constants.newLine +
                "================================" + Constants.newLine +
                "CONCEPTO / PRODUCTO             " + Constants.newLine +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.newLine +
                "================================" + Constants.newLine
    }
}