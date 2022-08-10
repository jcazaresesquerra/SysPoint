package com.app.syspoint.domentos;

import android.app.Activity;

import com.app.syspoint.db.bean.AppBundle;
import com.app.syspoint.db.bean.CorteBean;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.bean.InventarioBean;
import com.app.syspoint.db.dao.InventarioDao;
import com.app.syspoint.db.dao.VentasDao;
import com.app.syspoint.utils.Utils;
import com.app.syspoint.utils.cache.CacheInteractor;

import java.util.List;

public class TicketCierre extends Documento {

    private InventarioBean inventarioBean;
    private List<CorteBean> listaCorte;
    private VentasDao ventasDao;
    private Activity mActivity;

    public void setInventarioBean(InventarioBean inventarioBean) {
        this.inventarioBean = inventarioBean;
    }

    public TicketCierre(Activity activity) {
        super(activity);
        mActivity = activity;
    }

    @Override
    public void template() {
        this.ventasDao = new VentasDao();
        this.listaCorte = ventasDao.getAllPartsGroupedClient();

        List<InventarioBean> mLidata;
        mLidata = (List<InventarioBean>) (List<?>) new InventarioDao().list();
        EmpleadoBean vendedoresBean = AppBundle.getUserBean();

        if (vendedoresBean == null) {
            vendedoresBean = new CacheInteractor(mActivity).getSeller();
        }

        String salto = "\n";
        String vendedores = vendedoresBean != null ? "" + vendedoresBean.getNombre() + salto : "";
        String ticket =
                "     AGUA POINT S.A. DE C.V.    " + salto +
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
                        "================================" + salto;
        double total = 0;
        String cliente = "";
        String clienteAnterior= "";
        for (CorteBean partida : listaCorte){
            //Si el cliente es cadena  vacia entonces
            if( partida.getClienteBean().getCuenta().compareToIgnoreCase(cliente)!=0) {

                //Guarda el cliente actual

                cliente = partida.getClienteBean().getCuenta();
                clienteAnterior = cliente;
                if (cliente == clienteAnterior) {
                    //Muestra lo en el reporte
                    ticket += cliente + " " + partida.getClienteBean().getNombre_comercial() + "\n";
                }

            }

             total += (partida.getCantidad() * partida.getPrecio()) * (1+ partida.getImpuesto() / 100);

            //Completa el documento
            ticket += partida.getDescripcion() + "\n";
            ticket += "" + String.format("%1$-5s %2$11s %3$10s %4$10s", Utils.FDinero(partida.getPrecio()),  partida.getCantidad(), Utils.FDinero(partida.getCantidad() * partida.getPrecio() * (1+ partida.getImpuesto() / 100)), "");

        }

        ticket += "================================" + salto + salto +
                // "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "SubTotal:", Utils.FDinero(ventasBean.getImporte()), "") + salto +
                // "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "     IVA:", Utils.FDinero(ventasBean.getImpuesto()), "") + salto +
                "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "   Total:", Utils.FDinero(total), "") + "" + salto +
                  "================================" + salto +
                "" + salto + salto + salto + salto;

        ticket += "FIRMA DEL VENDEDOR:           " + salto +
                "" + salto + salto + salto + salto + salto + salto +
                "                                " + salto +
                "================================" + salto +
                "" + salto + salto + salto + salto;

        ticket += "FIRMA DEL SUPERVISOR:           " + salto +
                "" + salto + salto + salto + salto + salto + salto +
                "                                " + salto +
                "================================" + salto +
                "" + salto + salto + salto + salto;
        documento = ticket;
    }
}
