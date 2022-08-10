package com.app.syspoint.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.utils.cache.CacheInteractor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.app.syspoint.R;
import com.app.syspoint.TipoVisitaModel;
import com.app.syspoint.db.AdapterTipoVisita;
import com.app.syspoint.db.bean.AppBundle;
import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.ClientesRutaBean;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.bean.VisitasBean;
import com.app.syspoint.db.dao.ClienteDao;
import com.app.syspoint.db.dao.ClientesRutaDao;
import com.app.syspoint.db.dao.VisitasDao;
import com.app.syspoint.http.ApiServices;
import com.app.syspoint.http.PointApi;
import com.app.syspoint.json.Cliente;
import com.app.syspoint.json.ClienteJson;
import com.app.syspoint.json.Visita;
import com.app.syspoint.json.VisitaJson;
import com.app.syspoint.ui.ventas.FinalizaPrecapturaActivity;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreCapturaActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleMap gMap;
    private GoogleApiClient googleApiClient;
    private Location lastKnownLocation;
    Spinner spinner_tipo_visita;
    Spinner spinner_resultado_visita;
    private static final int REQUEST_PERMISSION_LOCATION = 991;

    private String concepto_visita_seleccioando;
    private String tipo_inventario_seleccionado;
    private boolean isConnectada = false;

    private String nombre_comercial;
    private String direccion;
    private TextView tv_visita_nombre_comercial;
    private  TextView tv_direcccion_precaptura;
    private String idCuenta;
    private List<TipoVisitaModel> mData;
    private AdapterTipoVisita mAdapter;
    double latitud = 0;
    double longitud = 0;
    int countProducts = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_captura);


        mData = new ArrayList<>();
        this.initToolBar();
        this.loadSpinnerTipoVisita();

        tv_visita_nombre_comercial = findViewById(R.id.tv_visita_nombre_comercial);
        tv_direcccion_precaptura = findViewById(R.id.tv_direcccion_precaptura);

        Intent intent = getIntent();
        idCuenta  = intent.getStringExtra(Actividades.PARAM_1);
        nombre_comercial = intent.getStringExtra(Actividades.PARAM_5);
        tv_visita_nombre_comercial.setText("" + nombre_comercial);
        direccion =   intent.getStringExtra(Actividades.PARAM_2) + " " + intent.getStringExtra(Actividades.PARAM_3) + ", " +  intent.getStringExtra(Actividades.PARAM_4);

        tv_direcccion_precaptura.setText("" + direccion);

        latitud = Double.parseDouble(intent.getStringExtra(Actividades.PARAM_6));
        longitud = Double.parseDouble(intent.getStringExtra(Actividades.PARAM_7));

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapViewPre);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_precaptura);
        toolbar.setTitle("Precaptura");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
        }

    }

    void loadSpinnerTipoVisita(){

        mData.add(new TipoVisitaModel(1, "No Recibio", false));
        mData.add(new TipoVisitaModel(2, "Cerrado", false));
        mData.add(new TipoVisitaModel(3, "No Visitado",false));
        final RecyclerView recyclerView = findViewById(R.id.rv_tipo_visita);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterTipoVisita(mData, new AdapterTipoVisita.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                for (int i = 0; i < mData.size(); i++) {
                    TipoVisitaModel item = mData.get(i);
                    item.setSelected(false);
                }
                recyclerView.getAdapter().notifyDataSetChanged();

                if (position >= 0 && position < mData.size()) {
                    TipoVisitaModel item = mData.get(position);
                    concepto_visita_seleccioando = item.getName();
                    item.setSelected(true);
                } else {
                    Toast.makeText(PreCapturaActivity.this, "Ha ocurrido un error, intente nuevamente", Toast.LENGTH_LONG).show();
                }
                recyclerView.getAdapter().notifyDataSetChanged();

            }
        });

        recyclerView.setAdapter(mAdapter);


    }


    protected String[] getArrayString(final int id) {
        return this.getResources().getStringArray(id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_precaptura, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.finish_preventa:



                if (concepto_visita_seleccioando == null || concepto_visita_seleccioando.isEmpty() || concepto_visita_seleccioando == ""){
                    final PrettyDialog dialogo = new PrettyDialog(this);
                    dialogo.setTitle("Sin Concepto")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("No ha seleccionado el motivo de la visita")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.ic_save_white, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            }).addButton("OK", R.color.white, R.color.red_800, new PrettyDialogCallback() {
                        @Override
                        public void onClick() {
                            dialogo.dismiss();
                        }
                    });
                    dialogo.show();

                    return false;
                }




                final PrettyDialog dialogo = new PrettyDialog(this);
                dialogo.setTitle("Confirmar accion")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("¿Esta seguro de registrar esta accion?")
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

                                EmpleadoBean vendedoresBean = AppBundle.getUserBean();
                                if (vendedoresBean == null) {
                                    vendedoresBean = new CacheInteractor(PreCapturaActivity.this).getSeller();
                                }

                                //Le indicamos al sistema que el cliente ya se ah visitado
                                final ClienteDao clienteDao = new ClienteDao();
                                final ClienteBean clienteBean = clienteDao.getClienteByCuenta(idCuenta);
                                clienteBean.setVisitado(1);
                                clienteBean.setDate_sync(Utils.fechaActual());
                                clienteBean.setVisitasNoefectivas(clienteBean.getVisitasNoefectivas() + 1);
                                clienteDao.save(clienteBean);

                                final ClientesRutaDao clientesRutaDao = new ClientesRutaDao();
                                final ClientesRutaBean clientesRutaBean =  clientesRutaDao.getClienteByCuentaCliente(idCuenta);

                                if (clientesRutaBean != null) {
                                    clientesRutaBean.setVisitado(1);
                                    clientesRutaDao.save(clientesRutaBean);
                                }

                                sincronizaCliente(String.valueOf(clienteBean.getId()));

                                VisitasBean visitasBean = new VisitasBean();
                                VisitasDao visitasDao= new VisitasDao();

                                visitasBean.setMotivo_visita(concepto_visita_seleccioando);
                                visitasBean.setEmpleado(vendedoresBean);
                                visitasBean.setCliente(clienteBean);
                                visitasBean.setHora(Utils.getHoraActual());
                                visitasBean.setFecha(Utils.fechaActual());
                                visitasBean.setLatidud(clienteBean.getLatitud());
                                visitasBean.setLongitud(clienteBean.getLatitud());
                                visitasDao.insert(visitasBean);

                                loadVisitas();

                                HashMap<String, String> parametros = new HashMap<>();
                                parametros.put(Actividades.PARAM_1, concepto_visita_seleccioando);
                                parametros.put(Actividades.PARAM_2, tipo_inventario_seleccionado);
                                if (vendedoresBean != null) {
                                    parametros.put(Actividades.PARAM_3, vendedoresBean.getNombre());
                                }
                                parametros.put(Actividades.PARAM_4, Utils.fechaActual());
                                parametros.put(Actividades.PARAM_5, Utils.getHoraActual());
                                parametros.put(Actividades.PARAM_6, String.valueOf(latitud));
                                parametros.put(Actividades.PARAM_7, String.valueOf(longitud));
                                parametros.put(Actividades.PARAM_8, clienteBean.getCuenta());

                                Actividades.getSingleton(PreCapturaActivity.this, FinalizaPrecapturaActivity.class).muestraActividad(parametros);


                                dialogo.dismiss();

                                finish();
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



                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void loadVisitas(){

        final VisitasDao visitasDao = new VisitasDao();
        List<VisitasBean> visitasBeanListBean = new ArrayList<>();
        visitasBeanListBean =  visitasDao.getAllVisitasFechaActual(Utils.fechaActual());

        List<Visita> listaVisitas = new ArrayList<>();
        EmpleadoBean vendedoresBean = AppBundle.getUserBean();
        if (vendedoresBean == null) {
            vendedoresBean = new CacheInteractor(PreCapturaActivity.this).getSeller();
        }

        final ClienteDao clienteDao = new ClienteDao();

        for (VisitasBean item : visitasBeanListBean){
            Visita visita = new Visita();
            final ClienteBean clienteBean = clienteDao.getClienteByCuenta(item.getCliente().getCuenta());
            visita.setFecha(item.getFecha());
            visita.setHora(item.getHora());
            visita.setCuenta(clienteBean.getCuenta());
            visita.setLatidud(item.getLatidud());
            visita.setLongitud(item.getLongitud());
            visita.setMotivo_visita(item.getMotivo_visita());
            if (vendedoresBean != null) {
                visita.setIdentificador(vendedoresBean.getIdentificador());
            }
            listaVisitas.add(visita);
        }

        VisitaJson visitaJsonRF = new VisitaJson();
        visitaJsonRF.setVisitas(listaVisitas);
        String json = new Gson().toJson(visitaJsonRF);
        Log.d("SinEmpleados", json);

        Call<VisitaJson> loadVisitas = ApiServices.getClientRestrofit().create(PointApi.class).sendVisita(visitaJsonRF);

        loadVisitas.enqueue(new Callback<VisitaJson>() {
            @Override
            public void onResponse(Call<VisitaJson> call, Response<VisitaJson> response) {
                if(response.isSuccessful()){
                    Toast.makeText(PreCapturaActivity.this, "Visita sincroniza", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<VisitaJson> call, Throwable t) {

            }
        });
    }

    private void sincronizaCliente(String idCliente) {

        final ClienteDao clienteDao = new ClienteDao();
        List<ClienteBean> listaClientesDB = new ArrayList<>();
        listaClientesDB = clienteDao.getClientsByDay(Utils.fechaActual());

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
            cliente.setLatitud(item.getLatitud());
            cliente.setLongitud(item.getLongitud());
            cliente.setPhone_contacto(""+item.getContacto_phone());
            cliente.setRecordatorio(""+item.getRecordatorio());
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
        Log.d("SinEmpleados", json);

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
    public void onConnected(@Nullable Bundle bundle) {
        updateLastLocation(true);
    }

    @Override
    public void onConnectionSuspended(int i) {
        updateLastLocation(true);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        updateLastLocation(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        gMap.getUiSettings().setMapToolbarEnabled(true);
        updateLastLocation(true);
    }

    private void updateLastLocation(boolean move) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
            return;
        }
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        gMap.setMyLocationEnabled(false);

        if (lastKnownLocation != null) {
            if (move) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(latitud,longitud), 15f)
                );

                LatLng currentDriverPos = new LatLng(latitud,longitud);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(currentDriverPos);
                markerOptions.title(nombre_comercial);
                gMap.clear();
                gMap.animateCamera(CameraUpdateFactory.newLatLng(currentDriverPos));
                gMap.addMarker(markerOptions);
                gMap.animateCamera(CameraUpdateFactory.zoomTo(15f));
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation(true);
            }
        }
    }
    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}