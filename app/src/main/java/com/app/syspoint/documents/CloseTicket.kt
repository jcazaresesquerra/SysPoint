package com.app.syspoint.documents

import android.app.Activity
import com.app.syspoint.BuildConfig
import com.app.syspoint.repository.database.bean.AppBundle
import com.app.syspoint.repository.database.bean.Bean
import com.app.syspoint.repository.database.bean.CorteBean
import com.app.syspoint.repository.database.bean.EmpleadoBean
import com.app.syspoint.repository.database.dao.SellsDao
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.Utils
import com.app.syspoint.utils.cache.CacheInteractor

class CloseTicket(activity: Activity): BaseTicket() {

    private val mActivity: Activity = activity

    override fun template() {
        super.template()
        val listaCorte: List<CorteBean> = SellsDao().getAllPartsGroupedClient()

        //val mLidata: List<InventarioBean> = StockDao().list() as List<InventarioBean>

        var ticket : String = when(BuildConfig.FLAVOR) {
            "donaqui" -> {
                buildDonAquiHeader()
            }
            else -> { // default SysPoint
                buildSyspointHeader()
            }
        }


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
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }
        val sellers = if (vendedoresBean != null) "" + vendedoresBean.getNombre() + Constants.newLine else ""

        return  "     AGUA POINT S.A. DE C.V.    " + Constants.newLine +
                "     Calz. Aeropuerto 4912 A    " + Constants.newLine +
                "      San Rafael C.P. 80150     " + Constants.newLine +
                "        Culiacan, Sinaloa       " + Constants.newLine +
                "           APO170818QR6         " + Constants.newLine +
                "          (667) 744-9350        " + Constants.newLine +
                "        info@aguapoint.com      " + Constants.newLine +
                "         www.aguapoint.com      " + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                sellers +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                "          CORTE DE CAJA         " + Constants.newLine +
                "================================" + Constants.newLine +
                "CLIENTE / PRODUCTO             " + Constants.newLine +
                "PRECIO    CANTIDAD    IMPORTE  " + Constants.newLine +
                "================================" + Constants.newLine
    }

    override fun buildDonAquiHeader(): String {
        var vendedoresBean = AppBundle.getUserBean()

        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }

        val sellers = if (vendedoresBean != null) "" + vendedoresBean.getNombre() + Constants.newLine else ""

        return "         AGUAS DON AQUI         " + Constants.newLine +
                " Blvd. Manuel J. Clouthier 2755 " + Constants.newLine +
                "     Buenos Aires C.P. 80199    " + Constants.newLine +
                "        Culiacan, Sinaloa       " + Constants.newLine +
                "          HIMA9801022T8         " + Constants.newLine +
                "    Adalberto Higuera Mendez    " + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                sellers +
                "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + Constants.newLine +
                "" + Constants.newLine +
                "" + Constants.newLine +
                "          CORTE DE CAJA         " + Constants.newLine +
                "================================" + Constants.newLine +
                "CLIENTE / PRODUCTO             " + Constants.newLine +
                "PRECIO    CANTIDAD    IMPORTE  " + Constants.newLine +
                "================================" + Constants.newLine
    }
}