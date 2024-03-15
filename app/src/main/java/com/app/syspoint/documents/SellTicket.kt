package com.app.syspoint.documents

import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.repository.objectBox.dao.EmployeeDao
import com.app.syspoint.repository.objectBox.dao.SessionDao
import com.app.syspoint.repository.objectBox.entities.SellBox
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.Utils

class SellTicket: BaseTicket() {

    override fun template() {
        super.template()

        val ventasBean = box as SellBox

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


        /*var ticket : String = when(BuildConfig.FLAVOR) {
            "donaqui" -> {
                buildDonAquiHeader()
            }
            else -> { // default SysPoint
                buildSyspointHeader()
            }
        }*/

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

        //val importeTotalVenta: Double = ventasBean.importe + ventasBean.impuesto









        for (items in ventasBean.listaPartidas) {
            ticket += "" + items.articulo.target.descripcion + Constants.NEW_LINE +
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
            "        IVA:",
            Utils.FDinero(ventasBean.impuesto),
            ""
        ) + Constants.NEW_LINE +
                "" + String.format(
            "%1$-5s %2$-10s %3$11s %4$10s",
            "",
            "      Total:",
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
        if (ventasBean.tipo_venta!!.compareTo("CREDITO", ignoreCase = true) == 0) {
            ticket += "FIRMA DE CONFORMIDAD:           " + Constants.NEW_LINE +
                    "" + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE +
                    "                                " + Constants.NEW_LINE +
                    "================================" + Constants.NEW_LINE +
                    "" + ventasBean.fecha + " " + ventasBean.hora + "" + Constants.NEW_LINE +
                    "FOLIO FINAL:         " + ventasBean.ticket + Constants.NEW_LINE +
                    "" + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE
        } else if (ventasBean.tipo_venta!!.compareTo("CONTADO", ignoreCase = true) == 0) {
            ticket += "" + ventasBean.fecha + " " + ventasBean.hora + "" + Constants.NEW_LINE +
                    "FOLIO FINAL:         " + ventasBean.ticket + Constants.NEW_LINE +
                    "" + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE
        }


        document = ticket
    }

    override fun buildTenetHeader(): String {
        val sellBox = box as SellBox

        val vendedor = if (sellBox?.employee?.target != null) {
            "Vendedor:" + sellBox.employee.target.nombre + Constants.NEW_LINE
        } else {
            val employeeBox = CacheInteractor().getSeller()
            val sessionBox = SessionDao().getUserSession()
            val employee = EmployeeDao().getEmployeeByID(sessionBox?.empleadoId ?: sellBox.empleadoId)
            if (employeeBox != null) "Vendedor:" + employeeBox.nombre + Constants.NEW_LINE
            else if (employee != null) "Vendedor:" + employee.nombre + Constants.NEW_LINE
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
                "(" + sellBox.client.target.cuenta + ")  " + sellBox.client.target.nombre_comercial + Constants.NEW_LINE + vendedor +
                "" + sellBox.fecha + " " + sellBox.hora + "" + Constants.NEW_LINE +
                "FOLIO FINAL:         " + sellBox.ticket + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "          NOTA DE VENTA         " + Constants.NEW_LINE +
                "             " + sellBox.tipo_venta?.toUpperCase() +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }

    override fun buildTomaguaHeader(): String {
        val sellBox = box as SellBox

        val vendedor = if (sellBox?.employee?.target != null) {
            "Vendedor:" + sellBox.employee.target.nombre + Constants.NEW_LINE
        } else {
            val employeeBox = CacheInteractor().getSeller()
            val sessionBox = SessionDao().getUserSession()
            val employee = EmployeeDao().getEmployeeByID(sessionBox?.empleadoId ?: sellBox.empleadoId)
            if (employeeBox != null) "Vendedor:" + employeeBox.nombre + Constants.NEW_LINE
            else if (employee != null) "Vendedor:" + employee.nombre + Constants.NEW_LINE
            else ""
        }

        return  "            TOMAGUA             " + Constants.NEW_LINE +
                "   Vianey Arlyn Carrillo Guerra " + Constants.NEW_LINE +
                "       Alvaro Obregon 4113      " + Constants.NEW_LINE +
                "      Las Cucas C.P. 80018      " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "        RFC:CAGV951105KR3       " + Constants.NEW_LINE +
                "         (667) 320-4000         " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "(" + sellBox.client.target.cuenta + ")  " + sellBox.client.target.nombre_comercial + Constants.NEW_LINE + vendedor +
                "" + sellBox.fecha + " " + sellBox.hora + "" + Constants.NEW_LINE +
                "FOLIO FINAL:         " + sellBox.ticket + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "          NOTA DE VENTA         " + Constants.NEW_LINE +
                "             " + sellBox.tipo_venta?.toUpperCase() +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }

    override fun buildNutriricaHeader(): String {

        val sellBox = box as SellBox

        val vendedor = if (sellBox?.employee?.target != null) {
            "Vendedor:" + sellBox.employee.target.nombre + Constants.NEW_LINE
        } else {
            val employeeBox = CacheInteractor().getSeller()
            val sessionBox = SessionDao().getUserSession()
            val employee = EmployeeDao().getEmployeeByID(sessionBox?.empleadoId ?: sellBox.empleadoId)
            if (employeeBox != null) "Vendedor:" + employeeBox.nombre + Constants.NEW_LINE
            else if (employee != null) "Vendedor:" + employee.nombre + Constants.NEW_LINE
            else ""
        }

        return  "            Nutri Rica          " + Constants.NEW_LINE +
                "       Pedro de tovar #5460     " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "         (667) 455-9828         " + Constants.NEW_LINE +
                " ALEXI DE JESUS MENDEZ COYANTES " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "(" + sellBox.client.target.cuenta + ")  " + sellBox.client.target.nombre_comercial + Constants.NEW_LINE + vendedor +
                "" + sellBox.fecha + " " + sellBox.hora + "" + Constants.NEW_LINE +
                "FOLIO FINAL:         " + sellBox.ticket + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "          NOTA DE VENTA         " + Constants.NEW_LINE +
                "             " + sellBox.tipo_venta?.toUpperCase() +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }
    override fun buildPruebasHeader(): String {

        val sellBox = box as SellBox

        val vendedor = if (sellBox?.employee?.target != null) {
            "Vendedor:" + sellBox.employee.target.nombre + Constants.NEW_LINE
        } else {
            val employeeBox = CacheInteractor().getSeller()
            val sessionBox = SessionDao().getUserSession()
            val employee = EmployeeDao().getEmployeeByID(sessionBox?.empleadoId ?: sellBox.empleadoId)
            if (employeeBox != null) "Vendedor:" + employeeBox.nombre + Constants.NEW_LINE
            else if (employee != null) "Vendedor:" + employee.nombre + Constants.NEW_LINE
            else ""
        }

        return  "              SWTENET           " + Constants.NEW_LINE +
                "      Calz. Aeropuerto 4912-A   " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "         (667) 208-1920         " + Constants.NEW_LINE +
                " JESUS OSVALDO CAZARES ESQUERRA " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "(" + sellBox.client.target.cuenta + ")  " + sellBox.client.target.nombre_comercial + Constants.NEW_LINE + vendedor +
                "" + sellBox.fecha + " " + sellBox.hora + "" + Constants.NEW_LINE +
                "FOLIO FINAL:         " + sellBox.ticket + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "          NOTA DE VENTA         " + Constants.NEW_LINE +
                "             " + sellBox.tipo_venta?.toUpperCase() +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }

    override fun buildTewaiHeader(): String {

        val sellBox = box as SellBox

        val vendedor = if (sellBox?.employee?.target != null) {
            "Vendedor:" + sellBox.employee.target.nombre + Constants.NEW_LINE
        } else {
            val employeeBox = CacheInteractor().getSeller()
            val sessionBox = SessionDao().getUserSession()
            val employee = EmployeeDao().getEmployeeByID(sessionBox?.empleadoId ?: sellBox.empleadoId)
            if (employeeBox != null) "Vendedor:" + employeeBox.nombre + Constants.NEW_LINE
            else if (employee != null) "Vendedor:" + employee.nombre + Constants.NEW_LINE
            else ""
        }

        return  "             TE WAI             " + Constants.NEW_LINE +
                "     Benito Juarez #344 PTE     " + Constants.NEW_LINE +
                "        Centro C.P. 80000       " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "        RFC: HWA160603KW7       " + Constants.NEW_LINE +
                "         (667) 390-0701         " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "(" + sellBox.client.target.cuenta + ")  " + sellBox.client.target.nombre_comercial + Constants.NEW_LINE + vendedor +
                "" + sellBox.fecha + " " + sellBox.hora + "" + Constants.NEW_LINE +
                "FOLIO FINAL:         " + sellBox.ticket + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "          NOTA DE VENTA         " + Constants.NEW_LINE +
                "             " + sellBox.tipo_venta?.toUpperCase() +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }


    override fun buildParatyHeader(): String {

        val sellBox = box as SellBox

        val vendedor = if (sellBox?.employee?.target != null) {
            "Vendedor:" + sellBox.employee.target.nombre + Constants.NEW_LINE
        } else {
            val employeeBox = CacheInteractor().getSeller()
            val sessionBox = SessionDao().getUserSession()
            val employee = EmployeeDao().getEmployeeByID(sessionBox?.empleadoId ?: sellBox.empleadoId)
            if (employeeBox != null) "Vendedor:" + employeeBox.nombre + Constants.NEW_LINE
            else if (employee != null) "Vendedor:" + employee.nombre + Constants.NEW_LINE
            else ""
        }

        return  "             PARATY             " + Constants.NEW_LINE +
                "  Franco Aaron Flores Castillo  " + Constants.NEW_LINE +
                "      Ciudades Hermanas 722     " + Constants.NEW_LINE +
                "  Lomas de Guadalupe C.P. 80250 " + Constants.NEW_LINE +
                "          FOCF851204CN6         " + Constants.NEW_LINE +
                "          (667) 481-8105        " + Constants.NEW_LINE +
                "    parati.aguacln@gmail.com    " + Constants.NEW_LINE +
                "          www.paraty.com        " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "(" + sellBox.client.target.cuenta + ")  " + sellBox.client.target.nombre_comercial + Constants.NEW_LINE + vendedor +
                "" + sellBox.fecha + " " + sellBox.hora + "" + Constants.NEW_LINE +
                "FOLIO FINAL:         " + sellBox.ticket + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "          NOTA DE VENTA         " + Constants.NEW_LINE +
                "             " + sellBox.tipo_venta?.toUpperCase() +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }

}