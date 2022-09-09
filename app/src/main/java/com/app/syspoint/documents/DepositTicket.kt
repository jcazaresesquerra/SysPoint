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
                ticket += "ABONO TICKET " + item.venta + Constants.newLine +
                        "" + Utils.formatMoneyMX(item.saldo) + "    " + Utils.formatMoneyMX(item.importe) + Constants.newLine
            }
            ticket += "================================" + Constants.newLine +
                    "" + String.format(
                "%1$-5s %2$-10s %3$11s %4$8s",
                "",
                "TOTAL:",
                Utils.FDinero(importeAbono),
                ""
            ) + Constants.newLine +
                    "================================" + Constants.newLine +
                    "             SALDOS             " + Constants.newLine +
                    "================================" + Constants.newLine +
                    "" + String.format(
                "%1$-5s %2$-5s %3$5s %4$6s",
                "",
                "Saldo Anterior:",
                Utils.FDinero(cobrosBean.cliente.saldo_credito + importeAbono),
                ""
            ) + Constants.newLine +
                    "" + String.format(
                "%1$-5s %2$-5s %3$5s %4$6s",
                "",
                "         Abono:",
                Utils.FDinero(importeAbono),
                ""
            ) + Constants.newLine +
                    "" + String.format(
                "%1$-5s %2$-5s %3$5s %4$6s",
                "",
                "  Saldo Actual:",
                Utils.FDinero(cobrosBean.cliente.saldo_credito),
                ""
            ) + "" + Constants.newLine +
                    "================================" + Constants.newLine +
                    "    GRACIAS POR SU PREFERENCIA  " + Constants.newLine +
                    "" + Constants.newLine +
                    "" + Constants.newLine +
                    "" + Constants.newLine
            document = ticket
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun buildSyspointHeader(): String {
        val cobrosBean = bean as CobrosBean
        return Constants.newLine + Constants.line + Constants.newLine +
                "     AGUA POINT S.A. DE C.V.    " + Constants.newLine +
                "     Calz. Aeropuerto 4912 A    " + Constants.newLine +
                "      San Rafael C.P. 80150     " + Constants.newLine +
                "        Culiacan, Sinaloa       " + Constants.newLine +
                "           APO170818QR6         " + Constants.newLine +
                "          (667) 744-9350        " + Constants.newLine +
                "        info@aguapoint.com      " + Constants.newLine +
                "         www.aguapoint.com      " + Constants.newLine +
                "COBRANZA:" + cobrosBean.cobro + Constants.newLine +
                "FECHA   :" + cobrosBean.fecha + Constants.newLine +
                "VENDEDOR:" + cobrosBean.empleado.getNombre() + Constants.newLine +
                "CLIENTE :" + cobrosBean.cliente.cuenta + Constants.newLine +
                "" + cobrosBean.cliente.nombre_comercial + Constants.newLine +
                "================================" + Constants.newLine +
                "CONCEPTO          TICKET" + Constants.newLine +
                "SALDO/TIC    ABONO" + Constants.newLine +
                "================================" + Constants.newLine
    }

    override fun buildDonAquiHeader(): String {
        val cobrosBean = bean as CobrosBean
        return Constants.newLine + Constants.line + Constants.newLine +
                "         AGUAS DON AQUI         " + Constants.newLine +
                " Blvd. Manuel J. Clouthier 2755 " + Constants.newLine +
                "     Buenos Aires C.P. 80199    " + Constants.newLine +
                "        Culiacan, Sinaloa       " + Constants.newLine +
                "          HIMA9801022T8         " + Constants.newLine +
                "    Adalberto Higuera Mendez    " + Constants.newLine +
                "COBRANZA:" + cobrosBean.cobro + Constants.newLine +
                "FECHA   :" + cobrosBean.fecha + Constants.newLine +
                "VENDEDOR:" + cobrosBean.empleado.getNombre() + Constants.newLine +
                "CLIENTE :" + cobrosBean.cliente.getCuenta() + Constants.newLine +
                "" + cobrosBean.getCliente().nombre_comercial + Constants.newLine +
                "================================" + Constants.newLine +
                "CONCEPTO          TICKET" + Constants.newLine +
                "SALDO/TIC    ABONO" + Constants.newLine +
                "================================" + Constants.newLine
    }
}