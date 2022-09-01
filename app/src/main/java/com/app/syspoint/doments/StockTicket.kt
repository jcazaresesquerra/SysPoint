package com.app.syspoint.doments

import android.app.Activity
import com.app.syspoint.repository.database.bean.AppBundle
import com.app.syspoint.repository.database.bean.InventarioBean
import com.app.syspoint.repository.database.dao.StockDao
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.cache.CacheInteractor

class StockTicket(activity: Activity): BaseTicket() {

    private val mActivity: Activity = activity

    override fun template() {
        super.template()

        val mLidata: List<InventarioBean> = StockDao().list() as List<*> as List<InventarioBean>

        //Obtiene el nombre del vendedor
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }

        val salto = "\n"
        val vendedor = if (vendedoresBean != null) "" + vendedoresBean.getNombre() + salto else ""
        var ticket = "     AGUA POINT S.A. DE C.V.    " + salto +
                "     Calz. Aeropuerto 4912 A    " + salto +
                "      San Rafael C.P. 80150     " + salto +
                "        Culiacan, Sinaloa       " + salto +
                "           APO170818QR6         " + salto +
                "          (667) 744-9350        " + salto +
                "        info@aguapoint.com      " + salto +
                "         www.aguapoint.com      " + salto +
                "" + salto +
                "" + salto +
                vendedor +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + salto +
                "" + salto +
                "" + salto +
                "           INVENTARIO          " + salto +
                "================================" + salto +
                "CONCEPTO / PRODUCTO             " + salto +
                "CANTIDAD     PRECIO     IMPORTE " + salto +
                "================================" + salto
        var total = 0.0
        for (items in mLidata) {
            total += items.precio * items.cantidad
            ticket += "" + items.articulo.descripcion + salto +
                    "" + String.format(
                "%1$-5s %2$11s %3$10s %4$10s",
                items.cantidad,
                Utils.FDinero(items.precio),
                Utils.FDinero(items.precio * items.cantidad),
                ""
            ) + salto
        }

        ticket += "================================" + salto + salto +  // "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "SubTotal:", Utils.FDinero(ventasBean.getImporte()), "") + salto +
                // "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "     IVA:", Utils.FDinero(ventasBean.getImpuesto()), "") + salto +
                "" + String.format(
            "%1$-5s %2$-10s %3$11s %4$10s",
            "",
            "   Total:",
            Utils.FDinero(total),
            ""
        ) + "" + salto +
                "================================" + salto +
                "" + salto + salto + salto + salto
        ticket += "FIRMA DEL VENDEDOR:           " + salto +
                "" + salto + salto + salto + salto + salto + salto +
                "                                " + salto +
                "================================" + salto +
                "" + salto + salto + salto + salto


        ticket += "FIRMA DEL SUPERVISOR:           " + salto +
                "" + salto + salto + salto + salto + salto + salto +
                "                                " + salto +
                "================================" + salto +
                "" + salto + salto + salto + salto

        document = ticket
    }
}