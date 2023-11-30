package com.app.syspoint.repository.request.http;

import android.os.Build;
import android.os.Looper;

import com.app.syspoint.repository.objectBox.dao.SellsDao;
import com.app.syspoint.repository.objectBox.entities.PlayingBox;
import com.app.syspoint.repository.objectBox.entities.SellBox;
import com.app.syspoint.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

public class SincVentas extends Servicio {

    private final static String TAG = "SincVentas";

    private Response responseVentas;

    public SincVentas() throws Exception {
        super("saveSale");

        final SellsDao sellsDao = new SellsDao();
        List<SellBox> listaVentas = sellsDao.getListVentasByDate(Utils.fechaActual());

        HashMap<String, SellBox> listaVentasHash = new HashMap<>();

        for (SellBox item : listaVentas) {
            if (!listaVentasHash.containsKey(item.getTicket())) {
                listaVentasHash.put(item.getTicket(), item);
            }
        }

        final JSONArray jsonArrayVentas = new JSONArray();

        //Recorremos la lista de ventas
        for (SellBox items : listaVentasHash.values()){

            JSONObject jsonObjectVenta = new JSONObject();
            jsonObjectVenta.put("venta", ""+ items.getVenta());
            jsonObjectVenta.put("tipo_doc", ""+ items.getTipo_doc());
            jsonObjectVenta.put("fecha", ""+ items.getFecha());
            jsonObjectVenta.put("hora", "" + items.getHora());
            jsonObjectVenta.put("cliente", "" + items.getClient().getTarget().getCuenta());
            jsonObjectVenta.put("empleado", "" + items.getEmployee().getTarget().getIdentificador());
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

            JSONArray jsonArrayPartidas = new JSONArray();
            jsonObjectVenta.put("partidas", jsonArrayPartidas);

            //Recoremos los items de los productos
            for(PlayingBox detalle : items.getListaPartidas()){

                JSONObject jsonObjectPatidas = new JSONObject();
                jsonObjectPatidas.put("venta", "" + detalle.getVenta());
                jsonObjectPatidas.put("articulo", "" + detalle.getArticulo().getTarget().getArticulo());
                jsonObjectPatidas.put("cantidad", "" + detalle.getCantidad());
                jsonObjectPatidas.put("precio", "" + detalle.getPrecio());
                jsonObjectPatidas.put("impuesto", "" + detalle.getImpuesto());
                jsonObjectPatidas.put("observ", "" + detalle.getObserv());
                jsonObjectPatidas.put("fecha", "" + detalle.getFecha());
                jsonObjectPatidas.put("hora", "" + detalle.getHora());
                jsonArrayPartidas.put(jsonObjectPatidas);
            }

            jsonArrayVentas.put(jsonObjectVenta);

        }


        this.jsonObject.put("ventas", jsonArrayVentas);
        this.jsonObject.put("clientId", getEmployee() != null ? getEmployee().getClientId() : "tenet");
        String json = this.jsonObject.toString();

        boolean isUiThread = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) isUiThread = Looper.getMainLooper().isCurrentThread();
        else isUiThread = Thread.currentThread() == Looper.getMainLooper().getThread();

        Timber.tag(TAG).d("SincVentas -> json: %s -> isUIThread: %s", json, isUiThread);

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
