package com.app.syspoint.documents

import android.app.Activity
import com.app.syspoint.repository.database.bean.VentasBean
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.cache.CacheInteractor

class SellTicket(activity: Activity): BaseTicket() {

    private var mActivity: Activity = activity

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

        val salto = "\n"
        var vendedor = if (ventasBean.empleado != null) "Vendedor:" + ventasBean.empleado
            .getNombre() + salto else ""

        if (ventasBean.empleado == null) {
            val empleadoBean = CacheInteractor().getSeller()
            vendedor =
                if (empleadoBean != null) "Vendedor:" + empleadoBean.getNombre() + salto else ""
        }


        var ticket = "     AGUA POINT S.A. DE C.V.    " + salto +
                "     Calz. Aeropuerto 4912 A    " + salto +
                "      San Rafael C.P. 80150     " + salto +
                "        Culiacan, Sinaloa       " + salto +
                "           APO170818QR6         " + salto +
                "          (667) 744-9350        " + salto +
                "        info@aguapoint.com      " + salto +
                "         www.aguapoint.com      " + salto +
                "" + salto +
                "" + salto +
                "(" + ventasBean.cliente.cuenta + ")  " + ventasBean.cliente.nombre_comercial + salto + vendedor +
                "" + ventasBean.fecha + " " + ventasBean.hora + "" + salto +
                "FOLIO FINAL:         " + ventasBean.ticket + salto +
                "" + salto +
                "" + salto +
                "          NOTA DE VENTA         " + salto +
                "================================" + salto +
                "CONCEPTO / PRODUCTO             " + salto +
                "CANTIDAD     PRECIO     IMPORTE " + salto +
                "================================" + salto
        //val importeTotalVenta: Double = ventasBean.importe + ventasBean.impuesto
        for (items in ventasBean.listaPartidas) {
            ticket += "" + items.articulo.descripcion + salto +
                    "" + String.format(
                "%1$-5s %2$11s %3$10s %4$10s",
                items.cantidad,
                Utils.FDinero(items.precio),
                Utils.FDinero(items.precio * items.cantidad),
                ""
            ) + salto
        }
        ticket += "================================$salto$salto"

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
        ) + salto +
                "" + String.format(
            "%1$-5s %2$-10s %3$11s %4$10s",
            "",
            "       IVA:",
            Utils.FDinero(ventasBean.impuesto),
            ""
        ) + salto +
                "" + String.format(
            "%1$-5s %2$-10s %3$11s %4$10s",
            "",
            "     Total:",
            Utils.FDinero(ventasBean.importe + ventasBean.impuesto),
            ""
        ) + "" + salto
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
        ticket += "================================$salto"
        if (ventasBean.tipo_venta.compareTo("CREDITO", ignoreCase = true) == 0) {
            ticket += "FIRMA DE CONFORMIDAD:           " + salto +
                    "" + salto + salto + salto + salto +
                    "                                " + salto +
                    "================================" + salto +
                    "" + ventasBean.fecha + " " + ventasBean.hora + "" + salto +
                    "FOLIO FINAL:         " + ventasBean.ticket + salto +
                    "" + salto + salto + salto + salto
        } else if (ventasBean.tipo_venta.compareTo("CONTADO", ignoreCase = true) == 0) {
            ticket += "" + ventasBean.fecha + " " + ventasBean.hora + "" + salto +
                    "FOLIO FINAL:         " + ventasBean.ticket + salto +
                    "" + salto + salto + salto + salto
        }


        document = ticket
    }
}