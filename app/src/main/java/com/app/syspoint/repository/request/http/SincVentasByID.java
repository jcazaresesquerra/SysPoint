package com.app.syspoint.repository.request.http;

import android.app.Activity;

import com.app.syspoint.repository.database.bean.PartidasBean;
import com.app.syspoint.repository.database.bean.VentasBean;
import com.app.syspoint.repository.database.dao.SellsDao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SincVentasByID extends Servicio{

    private  Response responseVentas;

    public SincVentasByID (Activity activity, Long id) throws Exception {

        super(activity, "saveSale");

        final SellsDao sellsDao = new SellsDao();
        final List<VentasBean> listaVentas = sellsDao.getSincVentaByID(id);

        final JSONArray jsonArrayVentas = new JSONArray();


        //Recorremos la lista de ventas
        for (VentasBean items : listaVentas){

            final JSONObject jsonObjectVenta = new JSONObject();
            jsonObjectVenta.put("venta", ""+ items.getVenta());
            jsonObjectVenta.put("tipo_doc", ""+ items.getTipo_doc());
            jsonObjectVenta.put("fecha", ""+ items.getFecha());
            jsonObjectVenta.put("hora", "" + items.getHora());
            jsonObjectVenta.put("cliente", "" + items.getCliente().getCuenta());
            jsonObjectVenta.put("empleado", "" + items.getEmpleado().identificador);
            jsonObjectVenta.put("importe", "" + items.getImporte());
            jsonObjectVenta.put("impuesto", "" +items.getImpuesto());
            jsonObjectVenta.put("datos", "" + items.getDatos());
            jsonObjectVenta.put("latitud", "0.00000");
            jsonObjectVenta.put("longitud", "0.0000");
            jsonObjectVenta.put("almacen", "NO DEFINIDO");
            jsonObjectVenta.put("folio", items.getTicket());
            jsonObjectVenta.put("tipo_venta", items.getTipo_venta());
            jsonObjectVenta.put("estado", items.getEstado());
            if (items.getCobranza() == null){
                jsonObjectVenta.put("cobranza", "null");
            }else {
                jsonObjectVenta.put("cobranza", items.getCobranza());
            }
            //jsonObjectVenta.put("usuario_cancelo", items.getUsuario_cancelo());


            final JSONArray jsonArrayPartidas = new JSONArray();
            jsonObjectVenta.put("partidas", jsonArrayPartidas);

            //Recoremos los items de los productos
            for(PartidasBean detalle : items.getListaPartidas()){

            final JSONObject jsonObjectPatidas = new JSONObject();
                jsonObjectPatidas.put("venta", "" + detalle.getVenta());
                jsonObjectPatidas.put("articulo", "" + detalle.getArticulo().getArticulo());
                jsonObjectPatidas.put("cantidad", "" + detalle.getCantidad());
                jsonObjectPatidas.put("precio", "" + detalle.getPrecio());
                jsonObjectPatidas.put("costo", "" + detalle.getCosto());
                jsonObjectPatidas.put("impuesto", "" + detalle.getImpuesto());
                jsonObjectPatidas.put("observ", "" + detalle.getObserv());
                jsonObjectPatidas.put("fecha", "" + detalle.getFecha());
                jsonObjectPatidas.put("hora", "" + detalle.getHora());
                jsonArrayPartidas.put(jsonObjectPatidas);
            }

        jsonArrayVentas.put(jsonObjectVenta);

        }

        this.jsonObject.put("ventas", jsonArrayVentas);
        String json = this.jsonObject.toString();

        onSuccess = new ResponseOnSuccess() {
            @Override
            public void onSuccess(JSONArray response) throws JSONException {

            }

            @Override
            public void onSuccessObject(JSONObject response) throws Exception {
                if ( response.getString("result").compareToIgnoreCase("ok") == 0  ){
                    responseVentas.onComplete("ok");
                    responseVentas.onTransaction("ok");
                }
            }
        };
    }

    public void setResponseVentas(Response responseVentas) {
        this.responseVentas = responseVentas;
    }

    public abstract static class Response implements Runnable {

        @Override
        public void run() {
        }

        public abstract void onTransaction(final String transaction);

        public abstract void onComplete(final String transaction);
    }

}
