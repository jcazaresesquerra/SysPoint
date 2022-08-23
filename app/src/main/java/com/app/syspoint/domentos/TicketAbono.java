package com.app.syspoint.domentos;

import android.app.Activity;
import android.util.Log;

import com.app.syspoint.db.bean.CobdetBean;
import com.app.syspoint.db.bean.CobrosBean;
import com.app.syspoint.utils.Utils;

public class TicketAbono extends Documento{

    private CobrosBean cobrosBean;

    public void setCobrosBean(CobrosBean cobrosBean) {
        this.cobrosBean = cobrosBean;
    }

    public TicketAbono(Activity activity) {
        super(activity);
    }

    @Override
    public void template() {
        try {

            double importeAbono = 0;
            //Contiene la línea
            String linea = "================================";

            //Contiene un salto
            String salto = "\n";

            String ticket =
                    salto + linea + salto +
                            "         AGUAS DON AQUI         " + salto +
                            " Blvd. Manuel J. Clouthier 2755 " + salto +
                            "     Buenos Aires C.P. 80199    " + salto +
                            "        Culiacan, Sinaloa       " + salto +
                            "          HIMA9801022T8         " + salto +
                            "    Adalberto Higuera Mendez    " + salto +
                            "COBRANZA:" + cobrosBean.getCobro() + salto +
                            "FECHA   :" + cobrosBean.getFecha() + salto +
                            "VENDEDOR:" + cobrosBean.getEmpleado().getNombre() + salto +
                            "CLIENTE :" + cobrosBean.getCliente().getCuenta() + salto +
                            "" + cobrosBean.getCliente().getNombre_comercial() + salto +
                            "================================" + salto +
                            "CONCEPTO          TICKET" + salto +
                            "SALDO/TIC    ABONO" + salto +
                            "================================" + salto;

            for (CobdetBean item: cobrosBean.getListaPartidas()){
                importeAbono += item.getImporte();
                ticket += "ABONO TICKET " + item.getVenta() + salto +
                        "" + Utils.formatMoneyMX(item.getSaldo()) + "    " + Utils.formatMoneyMX( item.getImporte()) + salto;
            }

            ticket += "================================" + salto +
                    "" + String.format("%1$-5s %2$-10s %3$11s %4$8s", "", "TOTAL:", Utils.FDinero( importeAbono), "") + salto +
                    "================================" + salto +
                    "             SALDOS             " + salto +
                    "================================" + salto +
                    "" + String.format("%1$-5s %2$-5s %3$5s %4$6s", "", "Saldo Anterior:", Utils.FDinero(cobrosBean.getCliente().getSaldo_credito() + importeAbono), "") + salto +
                    "" + String.format("%1$-5s %2$-5s %3$5s %4$6s", "", "         Abono:", Utils.FDinero(importeAbono), "") + salto +
                    "" + String.format("%1$-5s %2$-5s %3$5s %4$6s", "", "  Saldo Actual:", Utils.FDinero( cobrosBean.getCliente().getSaldo_credito()), "") + "" + salto +
                    "================================" + salto +
                    "    GRACIAS POR SU PREFERENCIA  "+ salto +
                    "" + salto +
                    "" + salto +
                    "" + salto;


            Log.d("Ticket", ticket);
            documento = ticket;
        } catch (Exception e){

        }
    }
}
