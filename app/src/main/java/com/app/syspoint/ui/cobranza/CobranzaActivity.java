package com.app.syspoint.ui.cobranza;

import static com.app.syspoint.utils.Utils.addActivity2Stack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.app.syspoint.R;
import com.app.syspoint.db.bean.AppBundle;
import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.CobdetBean;
import com.app.syspoint.db.bean.CobranzaBean;
import com.app.syspoint.db.bean.CobrosBean;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.dao.ClienteDao;
import com.app.syspoint.db.dao.CobranzaDao;
import com.app.syspoint.db.dao.CobranzaModelDao;
import com.app.syspoint.db.dao.CobrosDao;
import com.app.syspoint.domentos.TicketAbono;
import com.app.syspoint.http.ApiServices;
import com.app.syspoint.http.PointApi;
import com.app.syspoint.json.Cliente;
import com.app.syspoint.json.ClienteJson;
import com.app.syspoint.json.Cobranza;
import com.app.syspoint.json.CobranzaJson;
import com.app.syspoint.json.RequestCobranza;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.NetworkStateTask;
import com.app.syspoint.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CobranzaActivity extends AppCompatActivity {

    private TextView textViewClienteVenta;
    private TextView textViewNombreCliente;
    private TextView textView_subtotal_cobranza_view;
    private TextView textView_impuesto_cobranza_view;
    private TextView textView_total_cobranza_view;
    private TextView textView_cliente_saldo_cobranza_view;
    private String clienteGlobal;
    private AdapterCobranza mAdapter;
    private List<CobranzaModel> partidas;
    public static String id_cliente_seleccionado;
    double saldoCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cobranza);

        donwloadCobranza();
        initToolbar();
        initControls();
        initRecyclerViews();
        initParametros();
        eliminaPartidas();

    }

    private void eliminaPartidas() {

        List<CobranzaBean> listaDocumentosSeleccionados = new CobranzaDao().getDocumentosSeleccionados(CobranzaActivity.id_cliente_seleccionado);

        final CobranzaDao cobranzaDao = new CobranzaDao();
        for (CobranzaBean cobranzaItems : listaDocumentosSeleccionados) {
            final CobranzaBean cobranzaBean = cobranzaDao.getByCobranza(cobranzaItems.getCobranza());
            cobranzaBean.setIsCheck(false);
            cobranzaDao.save(cobranzaBean);
        }

        CobranzaModelDao dao = new CobranzaModelDao();
        dao.clear();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar_cobranza);
        toolbar.setTitle("Cobranza");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
        }
    }

    private void donwloadCobranza() {
        RequestCobranza requestCobranza = new RequestCobranza();
        requestCobranza.setCuenta(clienteGlobal);
        Call<CobranzaJson> getCobranza = ApiServices.getClientRestrofit().create(PointApi.class).getCobranzaByCliente(requestCobranza);
        getCobranza.enqueue(new Callback<CobranzaJson>() {
            @Override
            public void onResponse(Call<CobranzaJson> call, Response<CobranzaJson> response) {
                if (response.isSuccessful()) {
                    CobranzaDao cobranzaDao = new CobranzaDao();
                    for (Cobranza item : response.body().getCobranzas()) {

                        CobranzaBean cobranzaBean = cobranzaDao.getByCobranza(item.getCobranza());
                        if (cobranzaBean == null) {
                            final CobranzaBean cobranzaBean1 = new CobranzaBean();
                            final CobranzaDao cobranzaDao1 = new CobranzaDao();
                            cobranzaBean1.setCobranza(item.getCobranza());
                            cobranzaBean1.setCliente(item.getCuenta());
                            cobranzaBean1.setImporte(item.getImporte());
                            cobranzaBean1.setSaldo(item.getSaldo());
                            cobranzaBean1.setVenta(item.getVenta());
                            cobranzaBean1.setEstado(item.getEstado());
                            cobranzaBean1.setObservaciones(item.getObservaciones());
                            cobranzaBean1.setFecha(item.getFecha());
                            cobranzaBean1.setHora(item.getHora());
                            cobranzaBean1.setEmpleado(item.getIdentificador());
                            cobranzaBean1.setIsCheck(false);
                            cobranzaDao1.insert(cobranzaBean1);
                        } else {
                            cobranzaBean.setCobranza(item.getCobranza());
                            cobranzaBean.setCliente(item.getCuenta());
                            cobranzaBean.setImporte(item.getImporte());
                            cobranzaBean.setSaldo(item.getSaldo());
                            cobranzaBean.setVenta(item.getVenta());
                            cobranzaBean.setEstado(item.getEstado());
                            cobranzaBean.setObservaciones(item.getObservaciones());
                            cobranzaBean.setFecha(item.getFecha());
                            cobranzaBean.setHora(item.getHora());
                            cobranzaBean.setEmpleado(item.getIdentificador());
                            cobranzaBean.setIsCheck(false);
                            cobranzaDao.save(cobranzaBean);
                        }
                    }
                    //Toast.makeText(Ma, "Cobranza sincronizada", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CobranzaJson> call, Throwable t) {

            }
        });

    }

    private void initControls() {

        this.textViewClienteVenta = findViewById(R.id.textView_cliente_cobranza_view);
        this.textViewNombreCliente = findViewById(R.id.textView_cliente_nombre_cobranza_view);
        this.textView_subtotal_cobranza_view = findViewById(R.id.textView_subtotal_cobranza_view);
        this.textView_impuesto_cobranza_view = findViewById(R.id.textView_impuesto_cobranza_view);
        this.textView_total_cobranza_view = findViewById(R.id.textView_total_cobranza_view);
        this.textView_cliente_saldo_cobranza_view = findViewById(R.id.textView_cliente_saldo_cobranza_view);

        FloatingActionButton floatingActionButton = findViewById(R.id.fbAddDocumentos);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actividades.getSingleton(CobranzaActivity.this, ListaDocumentosCobranzaActivity.class).muestraActividadForResult(Actividades.PARAM_INT_1);
            }
        });

    }

    private void initParametros() {
        Intent intent = this.getIntent();
        this.clienteGlobal = intent.getStringExtra(Actividades.PARAM_1);
        CargarDatosCliente();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_cobranza, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();

                try {
                    //Eliminamos el cobro temporal para que no se guarde en memoria
                    final CobranzaModelDao dao = new CobranzaModelDao();
                    dao.clear();
                } catch (Exception e) {
                    //Excepcion.getSingleton(e).procesaExcepcion(activityGlobal);
                }
                return true;

            case R.id.terminarCobranza:
                TerminarCobranza();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void TerminarCobranza() {

        if (partidas.size() == 0) {
            final PrettyDialog dialogo = new PrettyDialog(CobranzaActivity.this);
            dialogo.setTitle("Sin documento")
                    .setTitleColor(R.color.purple_500)
                    .setMessage("No existen documentos por cobrar")
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


        final PrettyDialog dialogo = new PrettyDialog(CobranzaActivity.this);
        dialogo.setTitle("Terminar")
                .setTitleColor(R.color.purple_500)
                .setMessage("¿Desea terminal la cobranza?")
                .setMessageColor(R.color.purple_700)
                .setAnimationEnabled(false)
                .setIcon(R.drawable.ic_save_white, R.color.purple_500, new PrettyDialogCallback() {
                    @Override
                    public void onClick() {
                        dialogo.dismiss();
                    }
                }).addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
            @Override
            public void onClick() {

                dialogo.dismiss();
                Utils.addActivity2Stack(CobranzaActivity.this);


                    final CobrosDao cobrosDao = new CobrosDao();


                    final ClienteDao clientesDao = new ClienteDao();
                    final ClienteBean clienteBean = clientesDao.getClienteByCuenta(clienteGlobal);

                    if (clienteBean == null) {
                        final PrettyDialog dialogo = new PrettyDialog(CobranzaActivity.this);
                        dialogo.setTitle("Cliente")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("Cliente no encontrado")
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


                    //Obtiene el nombre del vendedor
                    final EmpleadoBean vendedoresBean = AppBundle.getUserBean();

                    if (vendedoresBean == null) {
                        final PrettyDialog dialogo = new PrettyDialog(CobranzaActivity.this);
                        dialogo.setTitle("Vendedor")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("Vendedor no encontrado")
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

                    final ArrayList<CobdetBean> lista = new ArrayList<>();
                    for (int x = 0; x < partidas.size(); x++) {

                        //Actualiza la cobranza
                        final CobranzaDao cobranzaDao = new CobranzaDao();
                        final CobranzaBean cobranzaBean = cobranzaDao.getByCobranza(partidas.get(x).getCobranza());

                        if (cobranzaBean != null) {
                            if (cobranzaBean.getSaldo() == partidas.get(x).getAcuenta()) {
                                cobranzaBean.setEstado("CO");
                                cobranzaBean.setSaldo(0);
                                cobranzaBean.setAbono(true);
                            } else {
                                cobranzaBean.setSaldo(cobranzaBean.getSaldo() - partidas.get(x).getAcuenta());
                                cobranzaBean.setAbono(true);
                            }
                            cobranzaBean.setFecha(Utils.fechaActual());
                            cobranzaDao.save(cobranzaBean);
                        }

                        final CobdetBean cobdetBean = new CobdetBean();
                        cobdetBean.setCobranza(partidas.get(x).getCobranza());
                        cobdetBean.setCliente(clienteBean);
                        cobdetBean.setFecha(Utils.fechaActual());
                        cobdetBean.setImporte(partidas.get(x).getAcuenta());
                        cobdetBean.setVenta(partidas.get(x).getVenta());
                        cobdetBean.setEmpleado(vendedoresBean);
                        cobdetBean.setAbono(0);
                        cobdetBean.setHora(Utils.getHoraActual());
                        cobdetBean.setSaldo(partidas.get(x).getSaldo());
                        lista.add(cobdetBean);
                    }

                    CobrosBean cobrosBean = new CobrosBean();
                    int folioCobranza = cobrosDao.getUltimoFolio();
                    cobrosBean.setCobro(folioCobranza);

                    //Creamos el encabezado de la venta
                    cobrosBean.setFecha(Utils.fechaActual());
                    cobrosBean.setHora(Utils.getHoraActual());
                    cobrosBean.setCliente(clienteBean);
                    cobrosBean.setEmpleado(vendedoresBean);
                    cobrosBean.setImporte(Double.parseDouble(textView_impuesto_cobranza_view.getText().toString().replace("$", "").replace(",", "").trim()));
                    cobrosBean.setEstado("CO");
                    cobrosBean.setTemporal(0);
                    cobrosBean.setSinc(0);

                    //Creamos el documento con la relacion de sus documentos
                    cobrosDao.CreaCobro(cobrosBean, lista);

                    ProgressDialog progressDialog = new ProgressDialog(CobranzaActivity.this);
                    progressDialog.setMessage("Espere un momento");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                        progressDialog.dismiss();
                        if (connected) {
                            try {
                                new loadAbonos().execute();

                                String ventaID = String.valueOf(cobrosBean.getId());

                                final CobranzaDao cobranzaDao = new CobranzaDao();
                                double nuevoSaldo = cobranzaDao.getTotalSaldoDocumentosCliente(clienteBean.getCuenta());

                                //Actualizamos el saldo del cliente
                                clienteBean.setSaldo_credito(nuevoSaldo);
                                clienteBean.setDate_sync(Utils.fechaActual());
                                clientesDao.save(clienteBean);

                                //Creamos el template del timbre
                                TicketAbono ticketAbono = new TicketAbono(CobranzaActivity.this);
                                ticketAbono.setCobrosBean(cobrosBean);
                                ticketAbono.template();

                                String ticket = ticketAbono.getDocumento();

                                testLoadClientes(String.valueOf(clienteBean.getId()));

                                //Elimina las partidas
                                final CobranzaModelDao dao = new CobranzaModelDao();
                                dao.clear();
                                Intent intent = new Intent(CobranzaActivity.this, ImprimeAbonoActivity.class);
                                intent.putExtra("ticket", ticket);
                                intent.putExtra("cobranza", ventaID);
                                intent.putExtra("clienteID", String.valueOf(clienteBean.getId()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (Exception e) {
                                    e.printStackTrace();
                            }
                        }
                        }, CobranzaActivity.this).execute(), 100);

            }
        }).addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
            @Override
            public void onClick() {
                dialogo.dismiss();

            }
        });
        dialogo.setCancelable(false);
        dialogo.show();
    }



    private class loadAbonos extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {

            final CobranzaDao cobranzaDao = new CobranzaDao();
            List<CobranzaBean> cobranzaBeanList = new ArrayList<>();
            cobranzaBeanList = cobranzaDao.getAbonosFechaActual(Utils.fechaActual());

            List<Cobranza> listaCobranza = new ArrayList<>();
            for (CobranzaBean item : cobranzaBeanList) {
                Cobranza cobranza = new Cobranza();
                cobranza.setCobranza(item.getCobranza());
                cobranza.setCuenta(item.getCliente());
                cobranza.setImporte(item.getImporte());
                cobranza.setSaldo(item.getSaldo());
                cobranza.setVenta(item.getVenta());
                cobranza.setEstado(item.getEstado());
                cobranza.setObservaciones(item.getObservaciones());
                cobranza.setFecha(item.getFecha());
                cobranza.setHora(item.getHora());
                cobranza.setIdentificador(item.getEmpleado());
                listaCobranza.add(cobranza);
            }

            CobranzaJson cobranzaJson = new CobranzaJson();
            cobranzaJson.setCobranzas(listaCobranza);
            String json = new Gson().toJson(cobranzaJson);
            Log.d("Sin Cobranza", json);

            Call<CobranzaJson> loadCobranza = ApiServices.getClientRestrofit().create(PointApi.class).updateCobranza(cobranzaJson);

            loadCobranza.enqueue(new Callback<CobranzaJson>() {
                @Override
                public void onResponse(Call<CobranzaJson> call, Response<CobranzaJson> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CobranzaActivity.this, "Cobranza sincroniza", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<CobranzaJson> call, Throwable t) {

                }
            });
            return null;
        }
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
        Log.d("ClientesCobranza", json);

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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == Activity.RESULT_CANCELED)
            return;

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case Actividades.PARAM_INT_1:

                if (validaDocumentoRepetido()) {
                    final PrettyDialog dialogo = new PrettyDialog(this);
                    dialogo.setTitle("Documento")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("El documento ya fue agregado")
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
                break;
        }


        /**
         *  Obtiene los parámetros de las otras vistas
         */
        String cobranzaSeleccionada = data.getStringExtra(Actividades.PARAM_1);
        String importeAcuenta = data.getStringExtra(Actividades.PARAM_2);

        try {

            final CobranzaDao cobranzaDao = new CobranzaDao();
            final CobranzaBean cobranzaBean = cobranzaDao.getByCobranza(cobranzaSeleccionada);

            int venta = cobranzaBean.getVenta();
            String cobranza = cobranzaBean.getCobranza();
            double importe = cobranzaBean.getImporte();
            double saldo = cobranzaBean.getSaldo();
            double acuenta = Double.parseDouble(importeAcuenta);
            String no_referen = "";

            //Todo creamos una nueva partida
            AddItems(venta, cobranza, importe, saldo, acuenta, no_referen);

        } catch (Exception e) {
            // Excepcion.getSingleton(e).procesaExcepcion(activityGlobal);
        }
    }

    private void AddItems(int venta, String cobranza, double importe, double saldo, double acuenta, String no_referen) {


        final CobranzaModel item = new CobranzaModel();
        final CobranzaModelDao dao = new CobranzaModelDao();
        item.setVenta(venta);
        item.setCobranza(cobranza);
        item.setImporte(importe);
        item.setSaldo(saldo);
        item.setAcuenta(acuenta);
        item.setNo_referen(no_referen);
        dao.insert(item);

        partidas.add(item);
        mAdapter.setItems(partidas);

        CalcularImportes();
    }

    private boolean validaDocumentoRepetido() {

        boolean repetido = false;

        for (CobranzaModel partidasItems : partidas) {

            final String producto = partidasItems.getCobranza();
            if (producto.compareTo(ListaDocumentosCobranzaActivity.documentoSeleccionado) == 0) {
                repetido = true;
                break;
            }
        }

        return repetido;
    }

    private void CalcularImportes() {

        double acuenta = 0;
        for (int i = 0; i < partidas.size(); i++) {
            acuenta += partidas.get(i).getAcuenta();
        }
        textView_impuesto_cobranza_view.setText(Utils.FDinero(acuenta));
        textView_total_cobranza_view.setText(Utils.FDinero((saldoCliente - acuenta)));

        ocultaLinearLayouth();
    }

    private void ocultaLinearLayouth() {
        LinearLayout linearLayout = null;
        linearLayout = findViewById(R.id.empty_state_container);
        if (partidas.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.INVISIBLE);
        }
    }

    private void CargarDatosCliente() {

        try {

            final ClienteDao clientesDao = new ClienteDao();
            final ClienteBean clientesBean = clientesDao.getClienteByCuenta(clienteGlobal);
            final CobranzaDao cobranzaDao = new CobranzaDao();

            if (clientesBean != null) {
                double saldoDocumentos = cobranzaDao.getTotalSaldoDocumentosCliente(clientesBean.getCuenta());
                clientesBean.setSaldo_credito(saldoDocumentos);
                clientesDao.save(clientesBean);
                testLoadClientes(String.valueOf(clientesBean.getId()));
                this.textViewClienteVenta.setText(clientesBean.getCuenta());
                this.textViewNombreCliente.setText(clientesBean.getNombre_comercial());
                this.id_cliente_seleccionado = clientesBean.getCuenta();
                this.textView_subtotal_cobranza_view.setText(Utils.FDinero(clientesBean.getSaldo_credito()));
                //this.textView_cliente_saldo_cobranza_view.setText(Formats.FDinero(saldoDocumentos));
                this.textView_cliente_saldo_cobranza_view.setText(Utils.FDinero(clientesBean.getSaldo_credito()));
                this.saldoCliente = clientesBean.getSaldo_credito();
            } else {
                //dialogo = new Dialogo(activityGlobal);
                //dialogo.setAceptar(true);
                //dialogo.setOnAceptarDissmis(true);
                //dialogo.setMensaje("Cliente no encontrado");
                //dialogo.show();
                //return ;
            }

        } catch (Exception e) {
            //Excepcion.getSingleton(e).procesaExcepcion(activityGlobal);
        }
    }


    private void initRecyclerViews() {

        partidas = new ArrayList<>();
        partidas = (List<CobranzaModel>) (List<?>) new CobranzaModelDao().list();

        /*** ----- Obtiene el recyclador ------ ****/
        final RecyclerView recyclerView = findViewById(R.id.recyclerView_cobranza);
        recyclerView.setHasFixedSize(true);

        /*** ----- Manejador ------ ****/
        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mAdapter = new AdapterCobranza(partidas, new AdapterCobranza.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(final int position) {
                CobranzaModel item = partidas.get(position);

                final PrettyDialog dialog = new PrettyDialog(CobranzaActivity.this);
                dialog.setTitle("Eliminar")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Desea eliminar el documento " + "\n" + item.getVenta())
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
                                CobranzaModelDao dao = new CobranzaModelDao();
                                dao.delete(item);
                                partidas = (List<CobranzaModel>) (List<?>) new CobranzaModelDao().list();
                                mAdapter.setItems(partidas);
                                CalcularImportes();
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
        });
        /*** ----- Dale memoria al adaptador ------ ****/
        recyclerView.setAdapter(mAdapter);
    }

    private void setData() {
        partidas = (List<CobranzaModel>) (List<?>) new CobranzaModelDao().list();
        mAdapter.setItems(partidas);
        CalcularImportes();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setData();
    }
}