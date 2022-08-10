package com.app.syspoint.domentos;

import android.app.Activity;

import com.app.syspoint.db.bean.AppBundle;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.bean.InventarioBean;
import com.app.syspoint.db.dao.InventarioDao;
import com.app.syspoint.utils.Utils;
import com.app.syspoint.utils.cache.CacheInteractor;

import java.util.List;

public class TicketInventario  extends Documento{


    private InventarioBean inventarioBean;
    private Activity mActicity;

    public void setVentasBean(InventarioBean inventarioBean) {
        this.inventarioBean = inventarioBean;
    }

    public TicketInventario(Activity activity) {
        super(activity);
        mActicity = activity;
    }



    @Override
    public void template() {
        List<InventarioBean> mLidata;
        mLidata = (List<InventarioBean>) (List<?>) new InventarioDao().list();

        //Obtiene el nombre del vendedor
        EmpleadoBean vendedoresBean = AppBundle.getUserBean();

        if (vendedoresBean == null) {
            vendedoresBean = new CacheInteractor(mActicity).getSeller();
        }

        String salto = "\n";
        String vendedor = vendedoresBean != null ? "" + vendedoresBean.getNombre() + salto : "";
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
                        vendedor +
                        "" + Utils.fechaActual() + " " + Utils.getHoraActual() + "" + salto +
                        "" + salto +
                        "" + salto +
                        "           INVENTARIO          " + salto +
                        "================================" + salto +
                        "CONCEPTO / PRODUCTO             " + salto +
                        "CANTIDAD     PRECIO     IMPORTE " + salto +
                        "================================" + salto;
        double total = 0;
        for (InventarioBean items : mLidata) {
            total+= items.getPrecio() * items.getCantidad();
            ticket += "" + items.getArticulo().getDescripcion() + salto +
                    "" + String.format("%1$-5s %2$11s %3$10s %4$10s", items.getCantidad(), Utils.FDinero(items.getPrecio()), Utils.FDinero(items.getPrecio() * items.getCantidad()), "") + salto;
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
