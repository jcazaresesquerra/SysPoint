package com.app.syspoint.documents

import com.app.syspoint.BuildConfig
import com.app.syspoint.repository.database.bean.CobrosBean
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.Utils

class DepositTicket: BaseTicket() {

    override fun template() {
        super.template()

        val cobrosBean = bean as CobrosBean

        try {

            var ticket : String = when(BuildConfig.FLAVOR) {
                "donaqui" -> {
                    buildDonAquiHeader()
                }
                else -> { // default SysPoint
                    buildSyspointHeader()
                }
            }

            var importeAbono = 0.0

            for (item in cobrosBean.listaPartidas) {
                importeAbono += item.importe
                ticket += "ABONO TICKET " + item.venta + Constants.NEW_LINE +
                        "" + Utils.formatMoneyMX(item.saldo) + "    " + Utils.formatMoneyMX(item.importe) + Constants.NEW_LINE
            }
            ticket += "================================" + Constants.NEW_LINE +
                    "" + String.format(
                "%1$-5s %2$-10s %3$11s %4$8s",
                "",
                "TOTAL:",
                Utils.FDinero(importeAbono),
                ""
            ) + Constants.NEW_LINE +
                    "================================" + Constants.NEW_LINE +
                    "             SALDOS             " + Constants.NEW_LINE +
                    "================================" + Constants.NEW_LINE +
                    "" + String.format(
                "%1$-5s %2$-5s %3$5s %4$6s",
                "",
                "Saldo Anterior:",
                Utils.FDinero(cobrosBean.cliente.saldo_credito + importeAbono),
                ""
            ) + Constants.NEW_LINE +
                    "" + String.format(
                "%1$-5s %2$-5s %3$5s %4$6s",
                "",
                "         Abono:",
                Utils.FDinero(importeAbono),
                ""
            ) + Constants.NEW_LINE +
                    "" + String.format(
                "%1$-5s %2$-5s %3$5s %4$6s",
                "",
                "  Saldo Actual:",
                Utils.FDinero(cobrosBean.cliente.saldo_credito),
                ""
            ) + "" + Constants.NEW_LINE +
                    "================================" + Constants.NEW_LINE +
                    "    GRACIAS POR SU PREFERENCIA  " + Constants.NEW_LINE +
                    "" + Constants.NEW_LINE +
                    "" + Constants.NEW_LINE +
                    "" + Constants.NEW_LINE
            document = ticket
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun buildSyspointHeader(): String {
        val cobrosBean = bean as CobrosBean
        return Constants.NEW_LINE + Constants.line + Constants.NEW_LINE +
                "     AGUA POINT S.A. DE C.V.    " + Constants.NEW_LINE +
                "     Calz. Aeropuerto 4912 A    " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "           APO170818QR6         " + Constants.NEW_LINE +
                "          (667) 744-9350        " + Constants.NEW_LINE +
                "        info@aguapoint.com      " + Constants.NEW_LINE +
                "         www.aguapoint.com      " + Constants.NEW_LINE +
                "COBRANZA:" + cobrosBean.cobro + Constants.NEW_LINE +
                "FECHA   :" + cobrosBean.fecha + Constants.NEW_LINE +
                "VENDEDOR:" + cobrosBean.empleado.getNombre() + Constants.NEW_LINE +
                "CLIENTE :" + cobrosBean.cliente.cuenta + Constants.NEW_LINE +
                "" + cobrosBean.cliente.nombre_comercial + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO          TICKET" + Constants.NEW_LINE +
                "SALDO/TIC    ABONO" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }

    override fun buildDonAquiHeader(): String {
        val cobrosBean = bean as CobrosBean
        return Constants.NEW_LINE + Constants.line + Constants.NEW_LINE +
                "         AGUAS DON AQUI         " + Constants.NEW_LINE +
                " Blvd. Manuel J. Clouthier 2755 " + Constants.NEW_LINE +
                "     Buenos Aires C.P. 80199    " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "          HIMA9801022T8         " + Constants.NEW_LINE +
                "    Adalberto Higuera Mendez    " + Constants.NEW_LINE +
                "COBRANZA:" + cobrosBean.cobro + Constants.NEW_LINE +
                "FECHA   :" + cobrosBean.fecha + Constants.NEW_LINE +
                "VENDEDOR:" + cobrosBean.empleado.getNombre() + Constants.NEW_LINE +
                "CLIENTE :" + cobrosBean.cliente.getCuenta() + Constants.NEW_LINE +
                "" + cobrosBean.getCliente().nombre_comercial + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO          TICKET" + Constants.NEW_LINE +
                "SALDO/TIC    ABONO" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }
}