package com.app.syspoint.ui.ventas;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.app.syspoint.R;
import com.app.syspoint.db.VentasModelBean;
import com.app.syspoint.db.bean.AppBundle;
import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.ClientesRutaBean;
import com.app.syspoint.db.bean.CobranzaBean;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.bean.PartidasBean;
import com.app.syspoint.db.bean.PreciosEspecialesBean;
import com.app.syspoint.db.bean.ProductoBean;
import com.app.syspoint.db.bean.VentasBean;
import com.app.syspoint.db.dao.ClienteDao;
import com.app.syspoint.db.dao.ClientesRutaDao;
import com.app.syspoint.db.dao.CobranzaDao;
import com.app.syspoint.db.dao.PreciosEspecialesDao;
import com.app.syspoint.db.dao.ProductoDao;
import com.app.syspoint.db.dao.VentasDao;
import com.app.syspoint.db.dao.VentasModelDao;
import com.app.syspoint.domentos.TicketVenta;
import com.app.syspoint.http.ApiServices;
import com.app.syspoint.http.PointApi;
import com.app.syspoint.json.Cliente;
import com.app.syspoint.json.ClienteJson;
import com.app.syspoint.json.Precio;
import com.app.syspoint.json.PrecioEspecialJson;
import com.app.syspoint.json.RequestClients;
import com.app.syspoint.templates.ViewPDFActivity;
import com.app.syspoint.ui.PreCapturaActivity;
import com.app.syspoint.ui.productos.ListaProductosActivity;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.NetworkStateTask;
import com.app.syspoint.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VentasActivity extends AppCompatActivity {

    private AdapterItemsVenta mAdapter;
    private List<VentasModelBean> mData;
    private TextView textViewCliente;
    private TextView textViewNombre;
    private TextView textViewSubtotal;
    private TextView textViewImpuesto;
    private TextView textViewTotal;
    private String latidud;
    private String longitud;
    Activity activity;
    String tipoVenta = "SIN DEFINIR";

    String idCliente;

    RadioButton radio_contado;
    RadioButton radio_credito;
    private double saldo_credito = 0;
    private double credito = 0;
    private boolean is_credito = false;
    private double importe_venta = 0;
    private boolean isCreditMatriz = false;
    int countProducts = 0;
    private ImageView imageViewVentas, imageViewVisitas;
    private String cuentaMatriz = "";
    private String sucursalMatriz = "";
    private TextView textView_credito_cliente_vents_view;
    RelativeLayout rlprogress_venta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventas);
        rlprogress_venta = findViewById(R.id.rlprogress_venta);
        //donwloadCobranza();
        activity = this;
        this.initToolBar();
        this.deleteVentaTemp();
        this.initControls();
        this.initRecyclerView();
        locationStart();

    }




    private class getPreciosEspeciales extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            ClienteDao clienteDao = new ClienteDao();
            ClienteBean clienteBean = clienteDao.getClienteByCuenta(idCliente);

            RequestClients requestPrices = new RequestClients();
            requestPrices.setCuenta(clienteBean.getCuenta());

            Call<PrecioEspecialJson> preciosJson = ApiServices.getClientRestrofit().create(PointApi.class).getPreciosByClient(requestPrices);
            preciosJson.enqueue(new Callback<PrecioEspecialJson>() {
                @Override
                public void onResponse(Call<PrecioEspecialJson> call, Response<PrecioEspecialJson> response) {
                    if (response.isSuccessful()) {
                        for (Precio item : response.body().getPrecios()) {

                            //Para obtener los datos del cliente
                            final ClienteDao clienteDao = new ClienteDao();
                            final ClienteBean clienteBean = clienteDao.getClienteByCuenta(item.getCliente());
                            if (clienteBean == null) {
                                return;
                            }

                            //Para obtener los datos del producto
                            final ProductoDao productoDao = new ProductoDao();
                            final ProductoBean productoBean = productoDao.getProductoByArticulo(item.getArticulo());

                            if (productoBean == null) {
                                return;
                            }

                            final PreciosEspecialesDao preciosEspecialesDao = new PreciosEspecialesDao();
                            final PreciosEspecialesBean preciosEspecialesBean = preciosEspecialesDao.getPrecioEspeciaPorCliente(productoBean.getArticulo(), clienteBean.getCuenta());

                            //Si no hay precios especiales entonces crea un precio
                            if (preciosEspecialesBean == null) {

                                final PreciosEspecialesDao dao = new PreciosEspecialesDao();
                                final PreciosEspecialesBean bean = new PreciosEspecialesBean();
                                bean.setCliente(clienteBean.getCuenta());
                                bean.setArticulo(productoBean.getArticulo());
                                bean.setPrecio(item.getPrecio());
                                if (item.getActive() ==1){
                                    bean.setActive(true);
                                }else {
                                    bean.setActive(false);
                                }
                                dao.insert(bean);

                            } else {
                                preciosEspecialesBean.setCliente(clienteBean.getCuenta());
                                preciosEspecialesBean.setArticulo(productoBean.getArticulo());
                                preciosEspecialesBean.setPrecio(item.getPrecio());
                                if (item.getActive() ==1){
                                    preciosEspecialesBean.setActive(true);
                                }else {
                                    preciosEspecialesBean.setActive(false);
                                }
                                preciosEspecialesDao.save(preciosEspecialesBean);
                            }
                        }
                    }
                }


                @Override
                public void onFailure(Call<PrecioEspecialJson> call, Throwable t) {
                }
            });

            return null;
        }
    }

    private void initControls() {

        Intent intent = getIntent();
        idCliente = intent.getStringExtra(Actividades.PARAM_1);

        ClienteDao clienteDao = new ClienteDao();

        ClienteBean clienteBean = clienteDao.getClienteByCuenta(idCliente);
        CobranzaDao cobranzaDao1 = new CobranzaDao();
        double saldoCliente =  cobranzaDao1.getSaldoByCliente(clienteBean.getCuenta());
        clienteBean.setSaldo_credito(saldoCliente);
        clienteBean.setDate_sync(Utils.fechaActual());
        clienteDao.save(clienteBean);


        imageViewVentas = findViewById(R.id.img_btn_finish_sale);
        imageViewVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (existenPartidas()) {


                    if (radio_contado != null && radio_contado.isChecked() == false && radio_credito.isChecked() == false){
                        final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                        dialogo.setTitle("Tipo de venta")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("Debe de seleccionar el tipo de venta")
                                .setMessageColor(R.color.purple_700)
                                .setAnimationEnabled(false)
                                .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        dialogo.dismiss();
                                    }
                                })
                                .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        dialogo.dismiss();
                                    }
                                });
                        dialogo.setCancelable(false);
                        dialogo.show();
                        return;
                    }

                    if (tipoVenta.compareToIgnoreCase("SIN DEFINIR") == 0) {
                        final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                        dialogo.setTitle("Tipo de venta")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("Debe de seleccionar el tipo de venta")
                                .setMessageColor(R.color.purple_700)
                                .setAnimationEnabled(false)
                                .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        dialogo.dismiss();
                                    }
                                })
                                .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        dialogo.dismiss();
                                    }
                                });
                        dialogo.setCancelable(false);
                        dialogo.show();
                        return;
                    }

                    double saldo_disponible = 0;

                    ClienteDao clienteDao = new ClienteDao();
                    ClienteBean clienteBean = clienteDao.getClienteByCuenta(idCliente);

                    if (tipoVenta.compareToIgnoreCase("CREDITO") == 0) {
                        if (clienteBean != null) {
                            if (clienteBean.getIs_credito()) {
                                if (clienteBean.getMatriz() != null && clienteBean.getMatriz().length() > 0) {
                                    ClienteBean clienteMatriz = clienteDao.getClienteByCuenta(clienteBean.getMatriz());
                                    if (clienteMatriz != null) {
                                        isCreditMatriz = true;
                                        cuentaMatriz = clienteMatriz.getCuenta();
                                        sucursalMatriz = clienteMatriz.getNombre_comercial();
                                        saldo_disponible = clienteMatriz.getLimite_credito() - clienteMatriz.getSaldo_credito();
                                        if (saldo_disponible < importe_venta) {
                                            final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                                            dialogo.setTitle("Crédito insuficiente")
                                                    .setTitleColor(R.color.purple_500)
                                                    .setMessage("La matriz solo cuenta con un saldo " + Utils.FDinero(saldo_disponible) + "  para terminar la venta a crédito  ")
                                                    .setMessageColor(R.color.purple_700)
                                                    .setAnimationEnabled(false)
                                                    .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                                        @Override
                                                        public void onClick() {
                                                            dialogo.dismiss();
                                                        }
                                                    })
                                                    .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                                        @Override
                                                        public void onClick() {
                                                            dialogo.dismiss();
                                                        }
                                                    });
                                            dialogo.setCancelable(false);
                                            dialogo.show();
                                            return;
                                        }
                                    } else {
                                        if (validaCredito()) {
                                            saldo_disponible = credito - saldo_credito;
                                            if (saldo_disponible < importe_venta) {
                                                final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                                                dialogo.setTitle("Crédito insuficiente")
                                                        .setTitleColor(R.color.purple_500)
                                                        .setMessage("Este cliente solo cuenta con " + Utils.FDinero(saldo_disponible) + "  de credito disponible para ventas a credito")
                                                        .setMessageColor(R.color.purple_700)
                                                        .setAnimationEnabled(false)
                                                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                                            @Override
                                                            public void onClick() {
                                                                dialogo.dismiss();
                                                            }
                                                        })
                                                        .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                                            @Override
                                                            public void onClick() {
                                                                dialogo.dismiss();
                                                            }
                                                        });
                                                dialogo.setCancelable(false);
                                                dialogo.show();
                                                return;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }




                    final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                    dialogo.setTitle("Terminar")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("¿Desea terminal la venta?")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.ic_save_white, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            })
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {

                                    Utils.addActivity2Stack(activity);

                                    final ArrayList<PartidasBean> lista = new ArrayList<>();

                                    final VentasDao ventasDao = new VentasDao();
                                    final VentasBean ventasBean = new VentasBean();


                                    int ultimoFolio = ventasDao.getUltimoFolio();

                                    final ProductoDao productoDao = new ProductoDao();
                                    //Recorremos las partidas
                                    for (int x = 0; x < mData.size(); x++) {
                                        //Validamos si el articulo existe en la base de datos
                                        final ProductoBean productosBean = productoDao.getProductoByArticulo(mData.get(x).getArticulo());
                                        final PartidasBean partida = new PartidasBean();
                                        partida.setArticulo(productosBean);
                                        partida.setCantidad(mData.get(x).getCantidad());
                                        partida.setPrecio(mData.get(x).getPrecio());
                                        partida.setCosto(mData.get(x).getCosto());
                                        partida.setImpuesto(mData.get(x).getImpuesto());
                                        partida.setObserv(productosBean.getDescripcion());
                                        partida.setFecha(new Date());
                                        partida.setHora(Utils.getHoraActual());
                                        partida.setVenta(Long.valueOf(ultimoFolio));
                                        partida.setDescripcion(productosBean.getDescripcion());
                                        lista.add(partida);
                                    }

                                    //Le indicamos al sistema que el cliente ya se ah visitado
                                    final ClienteDao clienteDao = new ClienteDao();
                                    final ClienteBean clienteBean = clienteDao.getClienteByCuenta(idCliente);
                                    final String clienteID = String.valueOf(clienteBean.getId());
                                    clienteBean.setVisitado(1);
                                    clienteBean.setVisitasNoefectivas(0);
                                    clienteBean.setDate_sync(Utils.fechaActual());
                                    clienteDao.save(clienteBean);

                                    ProgressDialog progressDialog = new ProgressDialog(VentasActivity.this);
                                    progressDialog.setMessage("Espere un momento");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                                        progressDialog.dismiss();
                                        if (connected) testLoadClientes(String.valueOf(clienteBean.getId()));

                                        final ClientesRutaDao clientesRutaDao = new ClientesRutaDao();
                                        final ClientesRutaBean clientesRutaBean = clientesRutaDao.getClienteByCuentaCliente(idCliente);
                                        if (clientesRutaBean != null) {
                                            clientesRutaBean.setVisitado(1);
                                            clientesRutaDao.save(clientesRutaBean);
                                        }

                                        //Obtiene el nombre del vendedor
                                        final EmpleadoBean vendedoresBean = AppBundle.getUserBean();

                                        ventasBean.setTipo_doc("TIK");
                                        ventasBean.setFecha(Utils.fechaActual());
                                        ventasBean.setHora(Utils.getHoraActual());
                                        ventasBean.setCliente(clienteBean);
                                        ventasBean.setEmpleado(vendedoresBean);
                                        ventasBean.setImporte(Double.parseDouble(textViewSubtotal.getText().toString().replace(",", "")));
                                        ventasBean.setImpuesto(Double.parseDouble(textViewImpuesto.getText().toString().replace(",", "")));
                                        ventasBean.setDatos(clienteBean.getNombre_comercial());
                                        ventasBean.setEstado("CO");
                                        ventasBean.setCorte("N");
                                        ventasBean.setTemporal(1);
                                        ventasBean.setVenta(ultimoFolio);
                                        ventasBean.setLatidud(latidud);
                                        ventasBean.setLatidud(latidud);
                                        ventasBean.setSync(0);
                                        ventasBean.setTipo_venta(tipoVenta);
                                        ventasBean.setUsuario_cancelo("");
                                        if (cuentaMatriz.length() == 0) {
                                            ventasBean.setFactudado("");
                                        } else {
                                            ventasBean.setFactudado(cuentaMatriz);
                                        }
                                        ventasBean.setTicket(Utils.getHoraActual().replace(":", "") + Utils.getFechaRandom().replace("-", ""));

                                        //Creamos la venta
                                        ventasDao.creaVenta(ventasBean, lista);


                                        double totalVenta = Double.parseDouble(textViewSubtotal.getText().toString().replace(",", "")) + Double.parseDouble(textViewImpuesto.getText().toString().replace(",", ""));
                                        String ticketRamdom = ticketRamdom() + ventasBean.getFecha().replace("-", "") + "" + ventasBean.getHora().replace(":", "");
                                        if (tipoVenta.compareToIgnoreCase("CREDITO") == 0) {
                                            //Si la cobranza es de matriz entonces creamos la cobranza a matriz
                                            if (isCreditMatriz) {
                                                CobranzaBean cobranzaBean = new CobranzaBean();
                                                CobranzaDao cobranzaDao = new CobranzaDao();
                                                cobranzaBean.setCobranza(ticketRamdom);
                                                cobranzaBean.setCliente(cuentaMatriz);
                                                cobranzaBean.setImporte(totalVenta);
                                                cobranzaBean.setSaldo(totalVenta);
                                                cobranzaBean.setVenta(Integer.valueOf(ventasBean.getTicket()));
                                                cobranzaBean.setEstado("PE");
                                                cobranzaBean.setObservaciones("Se realiza la venta a crédito para sucursal \n " + clienteBean.getCuenta() + " " + clienteBean.getNombre_comercial() + " \n con cargo a Matriz " + cuentaMatriz + " " + sucursalMatriz + "\n" + ventasBean.getFecha() + " hora " + ventasBean.getHora());
                                                cobranzaBean.setFecha(ventasBean.getFecha());
                                                cobranzaBean.setHora(ventasBean.getHora());
                                                cobranzaBean.setEmpleado(vendedoresBean.getIdentificador());
                                                cobranzaBean.setAbono(false);
                                                cobranzaDao.save(cobranzaBean);

                                                //Actualizamos el documento de la venta con el de la cobranza
                                                ventasBean.setCobranza(ticketRamdom);
                                                ventasDao.save(ventasBean);

                                                //Actualizamos el saldo del cliente
                                                ClienteBean clienteMatriz = clienteDao.getClienteByCuenta(cuentaMatriz);
                                                double saldoNuevo = clienteMatriz.getSaldo_credito() + totalVenta;
                                                clienteMatriz.setSaldo_credito(saldoNuevo);
                                                clienteMatriz.setDate_sync(Utils.fechaActual());

                                                clienteDao.save(clienteMatriz);

                                                if (connected) testLoadClientes(String.valueOf(clienteMatriz.getId()));

                                            } else {
                                                CobranzaBean cobranzaBean = new CobranzaBean();
                                                CobranzaDao cobranzaDao = new CobranzaDao();
                                                cobranzaBean.setCobranza(ticketRamdom);
                                                cobranzaBean.setCliente(clienteBean.getCuenta());
                                                cobranzaBean.setImporte(totalVenta);
                                                cobranzaBean.setSaldo(totalVenta);
                                                cobranzaBean.setVenta(Integer.valueOf(ventasBean.getTicket()));
                                                cobranzaBean.setEstado("PE");
                                                cobranzaBean.setObservaciones("Venta a crédito " + ventasBean.getFecha() + " hora " + ventasBean.getHora());
                                                cobranzaBean.setFecha(ventasBean.getFecha());
                                                cobranzaBean.setHora(ventasBean.getHora());
                                                cobranzaBean.setEmpleado(vendedoresBean.getIdentificador());
                                                cobranzaDao.save(cobranzaBean);

                                                //Actualizamos el documento de la venta con el de la cobranza
                                                ventasBean.setCobranza(ticketRamdom);
                                                ventasDao.save(ventasBean);

                                                //Actualizamos el saldo del cliente
                                                double saldoNuevo = clienteBean.getSaldo_credito() + totalVenta;
                                                clienteBean.setSaldo_credito(saldoNuevo);
                                                clienteBean.setVisitasNoefectivas(0);
                                                clienteBean.setDate_sync(Utils.fechaActual());

                                                clienteDao.save(clienteBean);

                                                if (connected) testLoadClientes(clienteID);
                                            }
                                        }

                                        String ventaID = String.valueOf(ventasBean.getId());

                                        //Creamos el template del timbre
                                        TicketVenta ticketVenta = new TicketVenta(VentasActivity.this);
                                        ticketVenta.setVentasBean(ventasBean);
                                        ticketVenta.template();

                                        String ticket = ticketVenta.getDocumento();

                                        Intent intent = new Intent(VentasActivity.this, ViewPDFActivity.class);
                                        intent.putExtra("ticket", ticket);
                                        intent.putExtra("venta", ventaID);
                                        intent.putExtra("clienteID", clienteID);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                        dialogo.dismiss();
                                    }, VentasActivity.this).execute(), 100);
                                }
                            })
                            .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();

                                }
                            });
                    dialogo.setCancelable(false);
                    dialogo.show();

                } else {
                    final PrettyDialog dialogo = new PrettyDialog(VentasActivity.this);
                    dialogo.setTitle("Finalizar")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Debe de agregar un producto para finalizar la venta")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            })
                            .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            });
                    dialogo.setCancelable(false);
                    dialogo.show();
                }
            }
        });

        imageViewVisitas =

                findViewById(R.id.img_btn_finish_visita);
        imageViewVisitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClienteDao clienteDao = new ClienteDao();
                ClienteBean clienteBean = clienteDao.getClienteByCuenta(idCliente);
                HashMap<String, String> parametros = new HashMap<>();
                parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                parametros.put(Actividades.PARAM_2, clienteBean.getCalle());
                parametros.put(Actividades.PARAM_3, clienteBean.getNumero());
                parametros.put(Actividades.PARAM_4, clienteBean.getColonia());
                parametros.put(Actividades.PARAM_5, clienteBean.getNombre_comercial());
                parametros.put(Actividades.PARAM_6, clienteBean.getLatitud());
                parametros.put(Actividades.PARAM_7, clienteBean.getLongitud());
                Utils.addActivity2Stack(activity);
                Actividades.getSingleton(VentasActivity.this, PreCapturaActivity.class).muestraActividad(parametros);
            }
        });

        textViewCliente = findViewById(R.id.textView_cliente_venta_view);
        textViewNombre = findViewById(R.id.textView_cliente_nombre_venta_view);

        textViewSubtotal = findViewById(R.id.textView_subtotal_venta_view);

        textViewImpuesto = findViewById(R.id.textView_impuesto_venta_view);

        textViewTotal = findViewById(R.id.textView_total_venta_view);


        if (clienteBean != null) {
            is_credito = clienteBean.getIs_credito();
            saldo_credito = clienteBean.getSaldo_credito();
            credito = clienteBean.getLimite_credito();
            textViewNombre.setText(clienteBean.getNombre_comercial());

            if (clienteBean.getMatriz().compareToIgnoreCase("null") == 0) {
                textViewCliente.setText(clienteBean.getCuenta());
                textViewCliente.setText(clienteBean.getCuenta() + "(" + Utils.FDinero(clienteBean.getSaldo_credito()) + ")");
            } else {

                ClienteBean clienteMatriz = clienteDao.getClienteByCuenta(clienteBean.getMatriz());
                textViewCliente.setText(clienteBean.getCuenta() + "(" + Utils.FDinero(clienteMatriz.getSaldo_credito()) + ")");
            }
            if (clienteBean.getRecordatorio() == null || clienteBean.getRecordatorio() == "null" || clienteBean.getRecordatorio().isEmpty()) {
                testLoadClientes(String.valueOf(clienteBean.getId()));
            } else {
                if (clienteBean.getRecordatorio().compareToIgnoreCase("null") == 0 || clienteBean.getRecordatorio() == null) {

                } else {
                    showCustomDialog(clienteBean);
                }
            }
        }

        FloatingActionButton fb = findViewById(R.id.fbAddProductos);
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actividades.getSingleton(VentasActivity.this, ListaProductosActivity.class).muestraActividadForResult(Actividades.PARAM_INT_1);
            }
        });


        radio_contado = findViewById(R.id.radio_contado);
        radio_credito = findViewById(R.id.radio_credito);

        if (clienteBean.getIs_credito()) {
            tipoVenta = "CREDITO";
            radio_contado.setVisibility(View.VISIBLE);
            radio_credito.setVisibility(View.VISIBLE);
        } else {
            tipoVenta = "CONTADO";
            radio_contado.setVisibility(View.VISIBLE);
            radio_credito.setVisibility(View.GONE);
        }
        ProgressDialog progressDialog = new ProgressDialog(VentasActivity.this);
        progressDialog.setMessage("Espere un momento");
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
            progressDialog.dismiss();
            if (connected) new getPreciosEspeciales().execute();
        }, VentasActivity.this).execute(), 100);
    }

    private boolean validaCredito() {
        if (is_credito) {
            return true;
        }
        return false;
    }

    private void showCustomDialog(ClienteBean clienteBean) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_recordatorio);
        dialog.setCancelable(true);

        ClienteDao clienteDao = new ClienteDao();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_post = (EditText) dialog.findViewById(R.id.et_recordatorio);
        et_post.setEnabled(false);
        if (clienteBean.getRecordatorio() != "null" || !clienteBean.getRecordatorio().isEmpty() || clienteBean.getRecordatorio() != null) {
            et_post.setText(clienteBean.getRecordatorio());
        }
        ((AppCompatButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clienteBean.setRecordatorio("");
                clienteBean.setIs_recordatorio(true);
                clienteDao.save(clienteBean);

                ProgressDialog progressDialog = new ProgressDialog(VentasActivity.this);
                progressDialog.setMessage("Espere un momento");
                progressDialog.setCancelable(false);
                progressDialog.show();
                new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                    progressDialog.dismiss();
                    if (connected)
                        testLoadClientes(String.valueOf(clienteBean.getId()));
                }, VentasActivity.this).execute(), 100);

                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void testLoadClientes(String idCliente) {
        final ClienteDao clienteDao = new ClienteDao();
        List<ClienteBean> listaClientesDB = new ArrayList<>();
        listaClientesDB = clienteDao.getByIDCliente(idCliente);

        List<Cliente> listaClientes = new ArrayList<>();

        for (ClienteBean item : listaClientesDB) {
            Cliente cliente = new Cliente();
            cliente.setNombreComercial(item.getNombre_comercial());
            cliente.setCalle(item.getCalle());
            cliente.setNumero(item.getNumero());
            cliente.setColonia(item.getColonia());
            cliente.setCiudad(item.getCiudad());
            cliente.setCodigoPostal(item.getCodigo_postal());
            cliente.setFechaRegistro(item.getFecha_registro());
            cliente.setFechaBaja(item.getFecha_baja());
            cliente.setCuenta(item.getCuenta());
            cliente.setGrupo(item.getGrupo());
            cliente.setCategoria(item.getCategoria());
            if (item.getStatus() == false) {
                cliente.setStatus(0);
            } else {
                cliente.setStatus(1);
            }
            cliente.setConsec(item.getConsec());
            cliente.setRegion(item.getRegion());
            cliente.setSector(item.getSector());
            cliente.setRango(item.getRango());
            cliente.setSecuencia(item.getSecuencia());
            cliente.setPeriodo(item.getPeriodo());
            cliente.setRuta(item.getRuta());
            cliente.setLun(item.getLun());
            cliente.setMar(item.getMar());
            cliente.setMie(item.getMie());
            cliente.setJue(item.getJue());
            cliente.setVie(item.getVie());
            cliente.setSab(item.getSab());
            cliente.setDom(item.getDom());
            cliente.setLatitud("" + item.getLatitud());
            cliente.setLongitud("" + item.getLongitud());
            cliente.setPhone_contacto("" + item.getContacto_phone());
            cliente.setRecordatorio("" + item.getRecordatorio());
            cliente.setVisitas(item.getVisitasNoefectivas());
            if (item.getIs_credito()) {
                cliente.setIsCredito(1);
            } else {
                cliente.setIsCredito(0);
            }
            cliente.setSaldo_credito(item.getSaldo_credito());
            cliente.setLimite_credito(item.getLimite_credito());
            if (item.getMatriz() == "null" && item.getMatriz() == null) {
                cliente.setMatriz("null");
            } else {
                cliente.setMatriz(item.getMatriz());
            }
            listaClientes.add(cliente);
        }

        ClienteJson clienteRF = new ClienteJson();
        clienteRF.setClientes(listaClientes);
        String json = new Gson().toJson(clienteRF);
        Log.d("ClientesVentas", json);

        Call<ClienteJson> loadClientes = ApiServices.getClientRestrofit().create(PointApi.class).sendCliente(clienteRF);

        loadClientes.enqueue(new Callback<ClienteJson>() {
            @Override
            public void onResponse(Call<ClienteJson> call, Response<ClienteJson> response) {
                if (response.isSuccessful()) {
                }
            }

            @Override
            public void onFailure(Call<ClienteJson> call, Throwable t) {
            }
        });
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.radio_contado:
                if (checked)
                    tipoVenta = "CONTADO";
                break;
            case R.id.radio_credito:
                if (checked)
                    tipoVenta = "CREDITO";
                break;
        }
    }

    private void initRecyclerView() {

        mData = (List<VentasModelBean>) (List<?>) new VentasModelDao().list();

        final RecyclerView recyclerView = findViewById(R.id.recyclerView_ventas);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterItemsVenta(mData, new AdapterItemsVenta.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(int position) {

                VentasModelBean item = mData.get(position);

                final PrettyDialog dialog = new PrettyDialog(VentasActivity.this);
                dialog.setTitle("Eliminar")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Desea eliminar el articulo " + "\n" + item.getDescripcion())
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();
                            }
                        })
                        .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                VentasModelBean item = mData.get(position);
                                VentasModelDao dao = new VentasModelDao();
                                dao.delete(item);
                                mData = (List<VentasModelBean>) (List<?>) new VentasModelDao().list();
                                mAdapter.setItems(mData);
                                ocultaLinearLayouth();
                                calculaImportes();
                                dialog.dismiss();
                            }
                        })
                        .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();

                            }
                        });
                dialog.setCancelable(false);
                dialog.show();
                return false;
            }
        }, new AdapterItemsVenta.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                VentasModelBean item = mData.get(position);

                final Dialog dialogo = new Dialog(VentasActivity.this);
                dialogo.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
                dialogo.setContentView(R.layout.dialog_cantidad_venta);

                final EditText editTextCantidad = dialogo.findViewById(R.id.edittext_cantidad_venta_seleccionada_dialog);

                //Cuando el usuario da click en el boton aceptar
                final Button buttonAceptar = dialogo.findViewById(R.id.button_seleccionar_cantidad_venta_dialog);
                buttonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String cantidad = editTextCantidad.getText().toString();

                        if (cantidad == null) {
                            return;
                        }

                        int cantidadVenta = Integer.parseInt(cantidad);

                        if (cantidadVenta == 0) {
                            final PrettyDialog dialog = new PrettyDialog(VentasActivity.this);
                            dialog.setTitle("Precio")
                                    .setTitleColor(R.color.purple_500)
                                    .setMessage("El precio debe de ser mayor a cero")
                                    .setMessageColor(R.color.purple_700)
                                    .setAnimationEnabled(false)
                                    .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                        @Override
                                        public void onClick() {
                                            dialogo.dismiss();
                                        }
                                    })
                                    .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                        @Override
                                        public void onClick() {
                                            dialogo.dismiss();
                                        }
                                    });
                            dialog.setCancelable(false);
                            dialog.show();
                            return;
                        }

                        final VentasModelDao dao = new VentasModelDao();
                        item.setCantidad(cantidadVenta);
                        dao.save(item);

                        mData = (List<VentasModelBean>) (List<?>) new VentasModelDao().list();
                        mAdapter.setItems(mData);
                        ocultaLinearLayouth();
                        calculaImportes();
                        dialogo.dismiss();
                    }
                });

                dialogo.show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(editTextCantidad.getWindowToken(), 0);
                editTextCantidad.requestFocus();
                showKeyboards(VentasActivity.this);
            }
        });
        recyclerView.setAdapter(mAdapter);
        ocultaLinearLayouth();
        calculaImportes();
    }

    public static void showKeyboards(Activity activity) {
        if (activity != null) {
            activity.getWindow()
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_ventas);
        toolbar.setTitle("Venta");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_ventas, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch (item.getItemId()) {

            case android.R.id.home:

                final PrettyDialog dialog = new PrettyDialog(this);
                dialog.setTitle("Salir")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Desea salir de la venta")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();
                            }
                        })
                        .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {

                                deleteVentaTemp();
                                finish();
                                dialog.dismiss();
                            }
                        })
                        .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();

                            }
                        });
                dialog.setCancelable(false);
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean existenPartidas() {

        boolean valida = false;
        if (mData != null && mData.size() > 0) {
            valida = true;
        }
        return valida;
    }

    private String ticketRamdom() {
        char[] chars = "0123456789".toCharArray();
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder((10 + rnd.nextInt(900)) + Utils.getHoraActual().replace(":", ""));
        for (int i = 0; i < 5; i++)
            sb.append(chars[rnd.nextInt(chars.length)]);

        return sb.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED)
            return;

        String cantidad = data.getStringExtra(Actividades.PARAM_1);
        String articulo = data.getStringExtra(Actividades.PARAM_2);

        final ProductoDao productoDao = new ProductoDao();
        final ProductoBean productoBean = productoDao.getProductoByArticulo(articulo);

        if (productoBean == null) {
            return;
        }


        //Validamos si existe el producto
        if (validaProducto(productoBean.getArticulo())) {
            final PrettyDialog dialogo = new PrettyDialog(this);
            dialogo.setTitle("Existe")
                    .setTitleColor(R.color.purple_500)
                    .setMessage("El producto ingresado ya existe en la venta")
                    .setMessageColor(R.color.purple_700)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialogo.dismiss();
                        }
                    })
                    .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialogo.dismiss();
                        }
                    });
            dialogo.setCancelable(false);
            dialogo.show();
            return;
        }

        //TODO PERMITIMOS QUE EL USUARIO VENTA A PRECIO 0
        // if (productoBean.getPrecio() == 0) {
        //     final PrettyDialog dialogo = new PrettyDialog(this);
        //     dialogo.setTitle("Precio")
        //             .setTitleColor(R.color.purple_500)
        //             .setMessage("El precio debe de ser mayor a 0")
        //             .setMessageColor(R.color.purple_700)
        //             .setAnimationEnabled(false)
        //             .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
        //                 @Override
        //                 public void onClick() {
        //                     dialogo.dismiss();
        //                 }
        //             })
        //             .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
        //                 @Override
        //                 public void onClick() {
        //                     dialogo.dismiss();
        //                 }
        //             });
        //     dialogo.setCancelable(false);
        //     dialogo.show();
        //     return;
        // }

        int cantidadVendida = Integer.parseInt(cantidad);

        //Validamos los datos del cliente
        ClienteDao clienteDao = new ClienteDao();
        ClienteBean clienteBean = clienteDao.getClienteByCuenta(idCliente);

        //Validamos si hay precio especial del cliente
        final PreciosEspecialesDao preciosEspecialesDao = new PreciosEspecialesDao();
        final PreciosEspecialesBean preciosEspecialesBean = preciosEspecialesDao.getPrecioEspeciaPorCliente(productoBean.getArticulo(), clienteBean.getCuenta());

        //Hay precio especial entonces aplica el precio especial

        if (preciosEspecialesBean != null) {
            addItem(productoBean.getArticulo(), productoBean.getDescripcion(), preciosEspecialesBean.getPrecio(), productoBean.getCosto(), productoBean.getIva(), cantidadVendida);

        } else {//No apliques precio especial
            addItem(productoBean.getArticulo(), productoBean.getDescripcion(), productoBean.getPrecio(), productoBean.getCosto(), productoBean.getIva(), cantidadVendida);
        }
    }

    private void addItem(String articulo, String descripcion, double precio, double costo,
                         int impuesto, int cantidad) {

        final VentasModelBean item = new VentasModelBean();
        final VentasModelDao dao = new VentasModelDao();

        item.setArticulo(articulo);
        item.setDescripcion(descripcion);
        item.setCantidad(cantidad);
        item.setPrecio(precio);
        item.setCosto(costo);
        item.setImpuesto(impuesto);
        item.setObserv(descripcion);
        dao.insert(item);

        mData.add(item);
        mAdapter.setItems(mData);

        ocultaLinearLayouth();
        calculaImportes();
    }

    private boolean validaProducto(String articulo) {
        boolean existe = false;
        for (VentasModelBean item : mData) {
            if (item.getArticulo() == articulo) {
                existe = true;
                break;
            }
        }
        return existe;
    }

    private void ocultaLinearLayouth() {
        LinearLayout linearLayout = null;
        linearLayout = findViewById(R.id.empty_state_container);
        if (mData.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {

            linearLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            final PrettyDialog dialog = new PrettyDialog(this);
            dialog.setTitle("Salir")
                    .setTitleColor(R.color.purple_500)
                    .setMessage("Desea salir de la venta")
                    .setMessageColor(R.color.purple_700)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            deleteVentaTemp();
                            finish();
                            dialog.dismiss();
                        }
                    })
                    .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();

                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }

    private void deleteVentaTemp() {
        VentasModelDao dao = new VentasModelDao();
        dao.clear();
    }

    private void calculaImportes() {

        double subTotal = 0;
        double totalImpuesto = 0;

        /**
         * Obtenemos los totales
         **/
        for (int i = 0; i < mData.size(); i++) {
            subTotal += (mData.get(i).getPrecio() * mData.get(i).getCantidad());
            totalImpuesto += (mData.get(i).getPrecio() * mData.get(i).getCantidad()) * (mData.get(i).getImpuesto() / 100);
            countProducts = mData.get(i).getCantidad();
        }
        importe_venta = subTotal + totalImpuesto;
        String subtotalFormato = Utils.formatMoneyMX(subTotal);
        String impuestoFormato = Utils.formatMoneyMX(totalImpuesto);
        String totalFormato = Utils.formatMoneyMX(subTotal + totalImpuesto);

        if (subtotalFormato.startsWith("$ ."))
            subtotalFormato = "$ 0" + subtotalFormato.substring(2);

        if (impuestoFormato.startsWith("$ ."))
            impuestoFormato = "$ 0" + impuestoFormato.substring(2);

        if (totalFormato.startsWith("$ ."))
            totalFormato = "$ 0" + totalFormato.substring(2);

        //Contie el SubTotal de la venta
        this.textViewSubtotal.setText(subtotalFormato);

        //Contiene el total de los impuesto
        this.textViewImpuesto.setText(impuestoFormato);

        //Contiene el total de la venta
        this.textViewTotal.setText(totalFormato);

        if (existenPartidas()) {
            imageViewVentas.setVisibility(View.VISIBLE);
            imageViewVisitas.setVisibility(View.GONE);
        } else {
            imageViewVentas.setVisibility(View.GONE);
            imageViewVisitas.setVisibility(View.VISIBLE);
        }
    }

    //Apartir de aqui empezamos a obtener la direciones y coordenadas
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            /* permite realizar algunas configuraciones del movil para el permiso */
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, Local);
    }

    //* Aqui empieza la Clase Localizacion / se obtienen las coordenadas */
    public class Localizacion implements LocationListener {
        VentasActivity registrarClientesController;

        public VentasActivity getMainActivity() {
            return registrarClientesController;
        }

        public void setMainActivity(VentasActivity mainActivity) {
            this.registrarClientesController = mainActivity;
        }

        @Override
        public void onLocationChanged(Location location) {
            if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                try {
                    /*Geocodificacion- Proceso de conversión de coordenadas a direccion*/
                    Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> list = geocoder.getFromLocation(
                            location.getLatitude(), location.getLongitude(), 1);
                    if (!list.isEmpty()) {
                        latidud = "" + list.get(0).getLatitude();
                        longitud = "" + list.get(0).getLongitude();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            this.registrarClientesController.setLocation(location);
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(getApplicationContext(), "GPS Desactivado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(getApplicationContext(), "GPS Activado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    /* obtener la direccion*/
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                /*Geocodificacion- Proceso de conversión de coordenadas a direccion*/
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    // editTextDireccion.setText(DirCalle.getAddressLine(0));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }


}