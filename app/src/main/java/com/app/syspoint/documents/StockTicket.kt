package com.app.syspoint.documents

import com.app.syspoint.BuildConfig
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.repository.objectBox.AppBundle
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

        return "         AGUAS DON AQUI         " + Constants.NEW_LINE +
                " Blvd. Manuel J. Clouthier 2755 " + Constants.NEW_LINE +
                "     Buenos Aires C.P. 80199    " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "          HIMA9801022T8         " + Constants.NEW_LINE +
                "         (667) 579-9656         " + Constants.NEW_LINE +
                "    Adalberto Higuera Mendez    " + Constants.NEW_LINE +
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

        return  "            NUTRIRICA           " + Constants.NEW_LINE +
                "       Pedro de Tovar 5460      " + Constants.NEW_LINE +
                "      San Rafael C.P. 80150     " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "         (667) 455-9828         " + Constants.NEW_LINE +
                " Alexi De Jesus Mendez Coyantes " + Constants.NEW_LINE +
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