package com.app.syspoint.documents

import com.app.syspoint.repository.database.bean.CobrosBean
import com.app.syspoint.utils.Utils

class DepositTicket: BaseTicket() {

    override fun template() {
        super.template()

        val cobrosBean = bean as CobrosBean

        try {
            var importeAbono = 0.0
            //Contiene la línea
            val linea = "================================"

            //Contiene un salto
            val salto = "\n"
            var ticket = salto + linea + salto +
                    "     AGUA POINT S.A. DE C.V.    " + salto +
                    "     Calz. Aeropuerto 4912 A    " + salto +
                    "      San Rafael C.P. 80150     " + salto +
                    "        Culiacan, Sinaloa       " + salto +
                    "           APO170818QR6         " + salto +
                    "          (667) 744-9350        " + salto +
                    "        info@aguapoint.com      " + salto +
                    "         www.aguapoint.com      " + salto +
                    "COBRANZA:" + cobrosBean.cobro + salto +
                    "FECHA   :" + cobrosBean.fecha + salto +
                    "VENDEDOR:" + cobrosBean.empleado.getNombre() + salto +
                    "CLIENTE :" + cobrosBean.cliente.cuenta + salto +
                    "" + cobrosBean.cliente.nombre_comercial + salto +
                    "================================" + salto +
                    "CONCEPTO          TICKET" + salto +
                    "SALDO/TIC    ABONO" + salto +
                    "================================" + salto
            for (item in cobrosBean.listaPartidas) {
                importeAbono += item.importe
                ticket += "ABONO TICKET " + item.venta + salto +
                        "" + Utils.formatMoneyMX(item.saldo) + "    " + Utils.formatMoneyMX(item.importe) + salto
            }
            ticket += "================================" + salto +
                    "" + String.format(
                "%1$-5s %2$-10s %3$11s %4$8s",
                "",
                "TOTAL:",
                Utils.FDinero(importeAbono),
                ""
            ) + salto +
                    "================================" + salto +
                    "             SALDOS             " + salto +
                    "================================" + salto +
                    "" + String.format(
                "%1$-5s %2$-5s %3$5s %4$6s",
                "",
                "Saldo Anterior:",
                Utils.FDinero(cobrosBean.cliente.saldo_credito + importeAbono),
                ""
            ) + salto +
                    "" + String.format(
                "%1$-5s %2$-5s %3$5s %4$6s",
                "",
                "         Abono:",
                Utils.FDinero(importeAbono),
                ""
            ) + salto +
                    "" + String.format(
                "%1$-5s %2$-5s %3$5s %4$6s",
                "",
                "  Saldo Actual:",
                Utils.FDinero(cobrosBean.cliente.saldo_credito),
                ""
            ) + "" + salto +
                    "================================" + salto +
                    "    GRACIAS POR SU PREFERENCIA  " + salto +
                    "" + salto +
                    "" + salto +
                    "" + salto
            document = ticket
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}