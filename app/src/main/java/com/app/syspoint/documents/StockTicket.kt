package com.app.syspoint.documents

import com.app.syspoint.repository.objectBox.dao.StockDao
import com.app.syspoint.repository.objectBox.entities.StockBox
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.Utils

class StockTicket: BaseTicket() {

    override fun template() {
        super.template()

        val stockData: List<StockBox> = StockDao().getCurrentStock()

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
            "donaqui" -> {
                buildDonAquiHeader()
            }
            else -> { // default SysPoint
                buildSyspointHeader()
            }
        }*/
        var total = 0.0
        for (items in stockData) {
            total += items.precio * items.lastCantidad
            ticket += "" + items.articulo!!.target.descripcion + Constants.NEW_LINE +
                    "" + String.format(
                "%1$-5s %2$11s %3$10s %4$10s",
                items.lastCantidad,
                Utils.FDinero(items.precio),
                Utils.FDinero(items.precio * items.lastCantidad),
                ""
            ) + Constants.NEW_LINE
        }

        ticket += "================================" + Constants.NEW_LINE + Constants.NEW_LINE +  // "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "SubTotal:", Utils.FDinero(ventasBean.getImporte()), "") + salto +
                // "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "     IVA:", Utils.FDinero(ventasBean.getImpuesto()), "") + salto +
                "" + String.format(
            "%1$-5s %2$-10s %3$11s %4$10s",
            "",
            "   Total:",
            Utils.FDinero(total),
            ""
        ) + "" + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE
        ticket += "FIRMA DEL VENDEDOR:           " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE +
                "                                " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE


        ticket += "FIRMA DEL SUPERVISOR:           " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE +
                "                                " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE + Constants.NEW_LINE

        document = ticket
    }

    override fun buildTenetHeader(): String {
        // get seller
        val employeeBox = getEmployee()

        val vendedor = if (employeeBox != null) "" + employeeBox.nombre + Constants.NEW_LINE else ""

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
                vendedor +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "           INVENTARIO          " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }


    override fun buildDonAquiHeader(): String {
        // get seller
        val employeeBox = getEmployee()

        val vendedor = if (employeeBox != null) "" + employeeBox.nombre + Constants.NEW_LINE else ""

        return  "            TOMAGUA             " + Constants.NEW_LINE +
                "   Vianey Arlyn Carrillo Guerra " + Constants.NEW_LINE +
                "       Alvaro Obregon 4113      " + Constants.NEW_LINE +
                "      Las Cucas C.P. 80018      " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "        RFC:CAGV951105KR3       " + Constants.NEW_LINE +
                "         (667) 320-4000         " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                vendedor +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "           INVENTARIO          " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }

    override fun buildNutriricaHeader(): String {
        // get seller
        val employeeBox = getEmployee()

        val vendedor = if (employeeBox != null) "" + employeeBox.nombre + Constants.NEW_LINE else ""

        return  "            Nutri Rica          " + Constants.NEW_LINE +
                "       Pedro de tovar #5460     " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "         (667) 455-9828         " + Constants.NEW_LINE +
                " ALEXI DE JESUS MENDEZ COYANTES " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                vendedor +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "           INVENTARIO          " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }

    override fun buildPruebasHeader(): String {
        // get seller
        val employeeBox = getEmployee()

        val vendedor = if (employeeBox != null) "" + employeeBox.nombre + Constants.NEW_LINE else ""

        return  "              SWTENET           " + Constants.NEW_LINE +
                "      Calz. Aeropuerto 4912-A   " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "         (667) 208-1920         " + Constants.NEW_LINE +
                " JESUS OSVALDO CAZARES ESQUERRA " + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                vendedor +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "" + Constants.NEW_LINE +
                "           INVENTARIO          " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE +
                "CONCEPTO / PRODUCTO             " + Constants.NEW_LINE +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.NEW_LINE +
                "================================" + Constants.NEW_LINE
    }
}