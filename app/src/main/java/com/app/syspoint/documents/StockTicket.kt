package com.app.syspoint.documents

import com.app.syspoint.BuildConfig
import com.app.syspoint.repository.database.bean.AppBundle
import com.app.syspoint.repository.database.bean.InventarioBean
import com.app.syspoint.repository.database.dao.StockDao
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.Utils
import com.app.syspoint.interactor.cache.CacheInteractor

class StockTicket: BaseTicket() {

    override fun template() {
        super.template()

        val stockData: List<InventarioBean> = StockDao().list() as List<InventarioBean>

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
            total += items.precio * items.cantidad
            ticket += "" + items.articulo.descripcion + Constants.newLine +
                    "" + String.format(
                "%1$-5s %2$11s %3$10s %4$10s",
                items.cantidad,
                Utils.FDinero(items.precio),
                Utils.FDinero(items.precio * items.cantidad),
                ""
            ) + Constants.newLine
        }

        ticket += "================================" + Constants.newLine + Constants.newLine +  // "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "SubTotal:", Utils.FDinero(ventasBean.getImporte()), "") + salto +
                // "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "     IVA:", Utils.FDinero(ventasBean.getImpuesto()), "") + salto +
                "" + String.format(
            "%1$-5s %2$-10s %3$11s %4$10s",
            "",
            "   Total:",
            Utils.FDinero(total),
            ""
        ) + "" + Constants.newLine +
                "================================" + Constants.newLine +
                "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine
        ticket += "FIRMA DEL VENDEDOR:           " + Constants.newLine +
                "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine +
                "                                " + Constants.newLine +
                "================================" + Constants.newLine +
                "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine


        ticket += "FIRMA DEL SUPERVISOR:           " + Constants.newLine +
                "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine +
                "                                " + Constants.newLine +
                "================================" + Constants.newLine +
                "" + Constants.newLine + Constants.newLine + Constants.newLine + Constants.newLine

        document = ticket
    }

    override fun buildSyspointHeader(): String {
        // get seller
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }
        val vendedor = if (vendedoresBean != null) "" + vendedoresBean.getNombre() + Constants.newLine else ""

        return "     AGUA POINT S.A. DE C.V.    " + Constants.newLine +
                "     Calz. Aeropuerto 4912 A    " + Constants.newLine +
                "      San Rafael C.P. 80150     " + Constants.newLine +
                "        Culiacan, Sinaloa       " + Constants.newLine +
                "           APO170818QR6         " + Constants.newLine +
                "          (667) 744-9350        " + Constants.newLine +
                "        info@aguapoint.com      " + Constants.newLine +
                "         www.aguapoint.com      " + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                vendedor +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                "           INVENTARIO          " + Constants.newLine +
                "================================" + Constants.newLine +
                "CONCEPTO / PRODUCTO             " + Constants.newLine +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.newLine +
                "================================" + Constants.newLine
    }

    override fun buildDonAquiHeader(): String {
        // get seller
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }
        val vendedor = if (vendedoresBean != null) "" + vendedoresBean.getNombre() + Constants.newLine else ""

        return "         AGUAS DON AQUI         " + Constants.newLine +
                " Blvd. Manuel J. Clouthier 2755 " + Constants.newLine +
                "     Buenos Aires C.P. 80199    " + Constants.newLine +
                "        Culiacan, Sinaloa       " + Constants.newLine +
                "          HIMA9801022T8         " + Constants.newLine +
                "    Adalberto Higuera Mendez    " + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                vendedor +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                "           INVENTARIO          " + Constants.newLine +
                "================================" + Constants.newLine +
                "CONCEPTO / PRODUCTO             " + Constants.newLine +
                "CANTIDAD     PRECIO     IMPORTE " + Constants.newLine +
                "================================" + Constants.newLine
    }
}