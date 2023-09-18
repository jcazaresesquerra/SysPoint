package com.app.syspoint.documents

import com.app.syspoint.repository.objectBox.dao.EmployeeDao
import com.app.syspoint.repository.objectBox.entities.CobrosBox
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.Utils

class DepositTicket: BaseTicket() {

    override fun template() {
        super.template()

        val cobrosBean = box as CobrosBox

        try {

            val employee = getEmployee()
            val clientId = employee?.clientId?:"tenet"

            var ticket : String = when(clientId) {
                Constants.NUTRIRICA_CLIENT_ID -> {
                    buildNutriricaHeader()
                }
                Constants.DON_AQUI_CLIENT_ID -> {
                    buildDonAquiHeader()
                }
                else -> { // default Tenet
                    buildTenetHeader()
                }
            }

            /*var ticket : String = when(BuildConfig.FLAVOR) {
                "donaqui" -> {
                    buildDonAquiHeader()
                }
                else -> { // default SysPoint
                    buildSyspointHeader()
                }
            }*/

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
                Utils.FDinero(cobrosBean.cliente.target.saldo_credito),
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
                Utils.FDinero(cobrosBean.cliente.target.saldo_credito - importeAbono),
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

    override fun buildTenetHeader(): String {
        val cobrosBox = box as CobrosBox

        val seller = if (cobrosBox?.empleado?.target != null) cobrosBox.empleado.target.nombre + Constants.NEW_LINE
        else {

            val employee = EmployeeDao().getEmployeeByID(cobrosBox.empleadoId)
            if (employee != null) employee.nombre + Constants.NEW_LINE
            else Constants.EMPTY_STRING + Constants.NEW_LINE
        }

        return  "     AGUA POINT S.A. DE C.V.    " + Constants.NEW_LINE +
                "     Calz. Aeropuerto 4912 A    " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "           APO170818QR6         " + Constants.NEW_LINE +
                "          (667) 744-9350        " + Constants.NEW_LINE +
                "        info@aguapoint.com      " + Constants.NEW_LINE +
                "         www.aguapoint.com      " + Constants.NEW_LINE + Constants.NEW_LINE +
                "COBRANZA:" + cobrosBox.cobro + Constants.NEW_LINE +
                "FECHA   :" + cobrosBox.fecha + Constants.NEW_LINE +
                "VENDEDOR:" + seller +
                "CLIENTE :" + cobrosBox.cliente.target.cuenta + Constants.NEW_LINE +
                "" + cobrosBox.cliente.target.nombre_comercial + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO          TICKET" + Constants.NEW_LINE +
                "SALDO/TIC    ABONO" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }

    override fun buildDonAquiHeader(): String {
        val cobrosBox = box as CobrosBox

        val seller = if (cobrosBox?.empleado?.target != null) cobrosBox.empleado.target.nombre + Constants.NEW_LINE
        else {
            val employee = EmployeeDao().getEmployeeByID(cobrosBox.empleadoId)
            if (employee != null) employee.nombre + Constants.NEW_LINE
            else Constants.EMPTY_STRING + Constants.NEW_LINE
        }

        return  "            Té Verdí            " + Constants.NEW_LINE +
                "  Mario Alain Urquidez Gonzalez " + Constants.NEW_LINE +
                "     Paulino Machorro #1881     " + Constants.NEW_LINE +
                "      Diaz Ordaz C.P. 80180     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "        RFC:UUGM910620TI9       " + Constants.NEW_LINE +
                "         (667) 142-8050         " + Constants.NEW_LINE + Constants.NEW_LINE +
                "COBRANZA:" + cobrosBox.cobro + Constants.NEW_LINE +
                "FECHA   :" + cobrosBox.fecha + Constants.NEW_LINE +
                "VENDEDOR:" + seller +
                "CLIENTE :" + cobrosBox.cliente.target.cuenta + Constants.NEW_LINE +
                "" + cobrosBox.cliente.target.nombre_comercial + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO          TICKET" + Constants.NEW_LINE +
                "SALDO/TIC    ABONO" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }

    override fun buildNutriricaHeader(): String {
        val cobrosBox = box as CobrosBox

        val seller = if (cobrosBox?.empleado?.target != null) cobrosBox.empleado.target.nombre + Constants.NEW_LINE
        else {
            val employee = EmployeeDao().getEmployeeByID(cobrosBox.empleadoId)
            if (employee != null) employee.nombre + Constants.NEW_LINE
            else Constants.EMPTY_STRING + Constants.NEW_LINE
        }

        val employeeBox = getEmployee()

        val sellers = if (employeeBox != null)
            employeeBox.nombre + Constants.NEW_LINE
        else Constants.EMPTY_STRING + Constants.NEW_LINE

        return  "         AGUAS DON AQUI         " + Constants.NEW_LINE +
                "    Manuel J. Clouthier #2755   " + Constants.NEW_LINE +
                "     Buenos Aires C.P. 80199    " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "         (667) 579-9656         " + Constants.NEW_LINE +
                "     ADALBERTO HIGUERA MENDEZ   " + Constants.NEW_LINE + Constants.NEW_LINE +
                "COBRANZA:" + cobrosBox.cobro + Constants.NEW_LINE +
                "FECHA   :" + cobrosBox.fecha + Constants.NEW_LINE +
                "VENDEDOR:" + seller +
                "CLIENTE :" + cobrosBox.cliente.target.cuenta + Constants.NEW_LINE +
                "" + cobrosBox.cliente.target.nombre_comercial + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO          TICKET" + Constants.NEW_LINE +
                "SALDO/TIC    ABONO" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }
}