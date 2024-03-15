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
                Constants.TOMAGUA_CLIENT_ID -> {
                    buildTomaguaHeader()
                }
                Constants.PRUEBAS_CLIENT_ID -> {
                    buildPruebasHeader()
                }
                Constants.PARATY_CLIENT_ID -> {
                    buildParatyHeader()
                }
                Constants.TEWAI_CLIENT_ID -> {
                    buildTewaiHeader()
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

    override fun buildTomaguaHeader(): String {
        val cobrosBox = box as CobrosBox

        val seller = if (cobrosBox?.empleado?.target != null) cobrosBox.empleado.target.nombre + Constants.NEW_LINE
        else {
            val employee = EmployeeDao().getEmployeeByID(cobrosBox.empleadoId)
            if (employee != null) employee.nombre + Constants.NEW_LINE
            else Constants.EMPTY_STRING + Constants.NEW_LINE
        }

        return  "            TOMAGUA             " + Constants.NEW_LINE +
                "   Vianey Arlyn Carrillo Guerra " + Constants.NEW_LINE +
                "       Alvaro Obregon 4113      " + Constants.NEW_LINE +
                "      Las Cucas C.P. 80018      " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "        RFC:CAGV951105KR3       " + Constants.NEW_LINE +
                "         (667) 320-4000         " + Constants.NEW_LINE + Constants.NEW_LINE +
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

        return  "            Nutri Rica          " + Constants.NEW_LINE +
                "       Pedro de tovar #5460     " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "         (667) 455-9828         " + Constants.NEW_LINE +
                " ALEXI DE JESUS MENDEZ COYANTES " + Constants.NEW_LINE + Constants.NEW_LINE +
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

    override fun buildPruebasHeader(): String {
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

        return  "              SWTENET           " + Constants.NEW_LINE +
                "      Calz. Aeropuerto 4912-A   " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "         (667) 208-1920         " + Constants.NEW_LINE +
                " JESUS OSVALDO CAZARES ESQUERRA " + Constants.NEW_LINE + Constants.NEW_LINE +
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

    override fun buildParatyHeader(): String {
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

        return  "             PARATY             " + Constants.NEW_LINE +
                "  Franco Aaron Flores Castillo  " + Constants.NEW_LINE +
                "      Ciudades Hermanas 722     " + Constants.NEW_LINE +
                "  Lomas de Guadalupe C.P. 80250 " + Constants.NEW_LINE +
                "          FOCF851204CN6         " + Constants.NEW_LINE +
                "          (667) 481-8105        " + Constants.NEW_LINE +
                "    parati.aguacln@gmail.com    " + Constants.NEW_LINE +
                "          www.paraty.com        " + Constants.NEW_LINE + Constants.NEW_LINE +
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

    override fun buildTewaiHeader(): String {
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

        return  "             TE WAI             " + Constants.NEW_LINE +
                "     Benito Juarez #344 PTE     " + Constants.NEW_LINE +
                "        Centro C.P. 80000       " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "        RFC: HWA160603KW7       " + Constants.NEW_LINE +
                "         (667) 390-0701         " + Constants.NEW_LINE + Constants.NEW_LINE +
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