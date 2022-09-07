package com.app.syspoint.documents

import android.app.Activity
import com.app.syspoint.repository.database.bean.AppBundle
import com.app.syspoint.repository.database.bean.CorteBean
import com.app.syspoint.repository.database.dao.SellsDao
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.cache.CacheInteractor

class CloseTicket(activity: Activity): BaseTicket() {

    private val mActivity: Activity = activity

    override fun template() {
        super.template()
        val listaCorte: List<CorteBean> = SellsDao().getAllPartsGroupedClient()

        //val mLidata: List<InventarioBean> = StockDao().list() as List<InventarioBean>
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }

        val salto = "\n"
        val vendedores = if (vendedoresBean != null) "" + vendedoresBean.getNombre() + salto else ""
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
                vendedores +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + salto +
                "" + salto +
                "" + salto +
                "          CORTE DE CAJA         " + salto +
                "================================" + salto +
                "CLIENTE / PRODUCTO             " + salto +
                "PRECIO    CANTIDAD    IMPORTE  " + salto +
                "================================" + salto
        var total = 0.0
        var cliente = ""

        for (partida in listaCorte) {
            //Si el cliente es cadena  vacia entonces
            if (partida.clienteBean.cuenta.compareTo(cliente, ignoreCase = true) != 0) {

                //Guarda el cliente actual
                cliente = partida.clienteBean.cuenta
                val clienteAnterior = cliente
                if (cliente == clienteAnterior) {
                    //Muestra lo en el reporte
                    ticket += """$cliente ${partida.clienteBean.nombre_comercial}
"""
                }
            }
            total += partida.cantidad * partida.precio * (1 + partida.impuesto / 100)

            //Completa el documento
            ticket += partida.descripcion + "\n"
            ticket += "" + String.format(
                "%1$-5s %2$11s %3$10s %4$10s",
                Utils.FDinero(partida.precio),
                partida.cantidad,
                Utils.FDinero(partida.cantidad * partida.precio * (1 + partida.impuesto / 100)),
                ""
            )
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