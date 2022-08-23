package com.app.syspoint.domentos;

import android.app.Activity;
import android.util.Log;

import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.bean.PartidasBean;
import com.app.syspoint.db.bean.VentasBean;
import com.app.syspoint.db.dao.ClienteDao;
import com.app.syspoint.utils.Utils;
import com.app.syspoint.utils.cache.CacheInteractor;

public class TicketVenta extends Documento {

    private VentasBean ventasBean;
    private Activity mActivity;

    public TicketVenta(Activity activity) {
        super(activity);
        mActivity = activity;
    }

    public void setVentasBean(VentasBean ventasBean) {
        this.ventasBean = ventasBean;
    }

    @Override
    public void template() {
        double saldoCreditodo = 0;
        boolean facturaMatriz = false;
        double importeTotalVenta;
        if (ventasBean.getTipo_venta().compareToIgnoreCase("CREDITO") == 0) {

            if  (ventasBean.getFactudado().length() > 0){
                ClienteDao clienteDao = new ClienteDao();
                ClienteBean clienteMatriz = clienteDao.getClienteByCuenta(ventasBean.getFactudado());
                if (clienteMatriz != null) {
                    saldoCreditodo = clienteMatriz.getSaldo_credito();
                    facturaMatriz = true;
                }
            }else {
                saldoCreditodo = ventasBean.getCliente().getSaldo_credito();
                facturaMatriz = false;
            }
        }

        String salto = "\n";
        String vendedor = ventasBean.getEmpleado() != null ? ("Vendedor:" + ventasBean.getEmpleado().getNombre() + salto) : "";

        if (ventasBean.getEmpleado() == null) {
            EmpleadoBean empleadoBean = new CacheInteractor(mActivity).getSeller();
            vendedor = empleadoBean != null ? ("Vendedor:" + empleadoBean.getNombre() + salto) : "";
        }


        String ticket =
                        "         AGUAS DON AQUI         " + salto +
                        " Blvd. Manuel J. Clouthier 2755 " + salto +
                        "     Buenos Aires C.P. 80199    " + salto +
                        "        Culiacan, Sinaloa       " + salto +
                        "          HIMA9801022T8         " + salto +
                        "    Adalberto Higuera Mendez    " + salto +
                        "" + salto +
                        "" + salto +
                        "(" + ventasBean.getCliente().getCuenta() + ")  " + ventasBean.getCliente().getNombre_comercial() + salto +
                        vendedor +
                        "" + ventasBean.getFecha() + " " + ventasBean.getHora() + "" + salto +
                        "FOLIO FINAL:         " + ventasBean.getTicket() + salto +
                        "" + salto +
                        "" + salto +
                        "          NOTA DE VENTA         " + salto +
                        "================================" + salto +
                        "CONCEPTO / PRODUCTO             " + salto +
                        "CANTIDAD     PRECIO     IMPORTE " + salto +
                        "================================" + salto;
        importeTotalVenta = (ventasBean.getImporte() + ventasBean.getImpuesto());
        for (PartidasBean items : ventasBean.getListaPartidas()) {
            ticket += "" + items.getArticulo().getDescripcion() + salto +
                    "" + String.format("%1$-5s %2$11s %3$10s %4$10s", items.getCantidad(), Utils.FDinero(items.getPrecio()), Utils.FDinero(items.getPrecio() * items.getCantidad()), "") + salto;
        }
        ticket += "================================" + salto + salto;

/*
        if (ventasBean.getTipo_venta().compareToIgnoreCase("CREDITO") == 0) {

            if (facturaMatriz) {
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", "SALDO ANTERI0R:", Utils.FDinero(saldoCreditodo - importeTotalVenta), "") + salto;
            }else {
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", "SALDO ANTERI0R:", Utils.FDinero(saldoCreditodo - importeTotalVenta), "") + salto;
            }


        }
*/
        ticket += "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "   SubTotal:", Utils.FDinero(ventasBean.getImporte()), "") + salto +
                "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "       IVA:", Utils.FDinero(ventasBean.getImpuesto()), "") + salto +
                "" + String.format("%1$-5s %2$-10s %3$11s %4$10s", "", "     Total:", Utils.FDinero(ventasBean.getImporte() + ventasBean.getImpuesto()), "") + "" + salto;
/*
        if (ventasBean.getTipo_venta().compareToIgnoreCase("CREDITO") == 0) {
            if (facturaMatriz){
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", " SALDO ACTUAL:", Utils.FDinero(saldoCreditodo), "") + salto + salto;
            }else{
                ticket += "" + String.format("%1$-5s %2$-10s %3$5s %4$5s", "", " SALDO ACTUAL:", Utils.FDinero(saldoCreditodo), "") + salto + salto;
            }
        }
*/
        ticket += "================================" + salto;
        if (ventasBean.getTipo_venta().compareToIgnoreCase("CREDITO") == 0) {
            ticket += "FIRMA DE CONFORMIDAD:           " + salto +
                    "" + salto + salto + salto + salto +
                    "                                " + salto +
                    "================================" + salto +
                    "" + ventasBean.getFecha() + " " + ventasBean.getHora() + "" + salto +
                    "FOLIO FINAL:         " + ventasBean.getTicket() + salto +
                    "" + salto + salto + salto + salto;
        } else if ((ventasBean.getTipo_venta().compareToIgnoreCase("CONTADO") == 0)) {
            ticket += "" + ventasBean.getFecha() + " " + ventasBean.getHora() + "" + salto +
                    "FOLIO FINAL:         " + ventasBean.getTicket() + salto +
                    "" + salto + salto + salto + salto;
        }

        Log.d("Ticket", ticket);
        documento = ticket;

    }
}
