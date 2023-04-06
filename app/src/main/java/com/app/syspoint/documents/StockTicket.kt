package com.app.syspoint.documents

import com.app.syspoint.BuildConfig
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.repository.database.bean.AppBundle
import com.app.syspoint.repository.database.bean.InventarioBean
import com.app.syspoint.repository.database.dao.StockDao
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.Utils

class StockTicket: BaseTicket() {

    override fun template() {
        super.template()

        val stockData: List<InventarioBean> = StockDao().getCurrentStock()

        var ticket : String = when(BuildConfig.FLAVOR) {
            "donaqui" -> {
                buildDonAquiHeader()
            }
            else -> { // default SysPoint
                buildSyspointHeader()
            }
        }
        var total = 0.0
        for (items in stockData) {
            total += items.precio * items.lastCantidad
            ticket += "" + items.articulo.descripcion + Constants.NEW_LINE +
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

    override fun buildSyspointHeader(): String {
        // get seller
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }
        val vendedor = if (vendedoresBean != null) "" + vendedoresBean.getNombre() + Constants.NEW_LINE else ""

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
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }
        val vendedor = if (vendedoresBean != null) "" + vendedoresBean.getNombre() + Constants.NEW_LINE else ""

        return "         AGUAS DON AQUI         " + Constants.NEW_LINE +
                " Blvd. Manuel J. Clouthier 2755 " + Constants.NEW_LINE +
                "     Buenos Aires C.P. 80199    " + Constants.NEW_LINE +
                "        Culiacan, Sinaloa       " + Constants.NEW_LINE +
                "          HIMA9801022T8         " + Constants.NEW_LINE +
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
}