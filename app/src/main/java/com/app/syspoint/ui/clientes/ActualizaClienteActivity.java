package com.app.syspoint.ui.clientes;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.app.syspoint.interactor.client.ClientInteractor;
import com.app.syspoint.interactor.client.ClientInteractorImp;
import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.ClienteBean;
import com.app.syspoint.repository.database.dao.ClientDao;
import com.app.syspoint.models.Client;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActualizaClienteActivity extends AppCompatActivity {


    Spinner spinner_grupo_actualiza_cliente;
    Spinner spinner_categoria_actualiza_cliente;
    Spinner spinner_status_actualiza_cliente;

    Spinner spinner_region_actualiza_cliente;
    Spinner spinner_sector_actualiza_cliente;
    Spinner spinner_ruta_actualiza_cliente;
    Spinner spinner_periodo_actualiza_cliente;

    EditText editText_nombre_actualiza_cliente;
    EditText editText_calle_actualiza_cliente;
    EditText editText_numero_actualiza_cliente;
    EditText editText_colonia_actualiza_cliente;
    EditText editText_ciudad_actualiza_cliente;
    EditText editText_cp_actualiza_cliente;
    EditText inp_contacto_phone_actualiza_cliente;
    EditText editText_fecha_alta_actualiza_cliente;
    ImageButton img_fecha_alta_actualiza_cliente;
    EditText editText_fecha_baja_actualiza_cliente;
    ImageButton img_fecha_baja_actualiza_cliente;
    EditText editText_no_cuenta_actualiza_cliente;
    EditText et_registro_actualiza_limite_credito;
    EditText et_registro_actualiza_credito;
    String status_seleccionado;
    String categoria_seleccionada;
    String grupo_seleccionado;
    String region_seleccionado;
    String sector_seleccionado;
    String ruta_seleccionado;
    String periodo_seleccionado;

    String statusSeleccionadoDB;
    String categoriaSeleccionadaDB;
    String grupoSeleccionadoDB;
    String regionSeleccionadoDB;
    String sectorSeleccionadoDB;
    String rutaSeleccionadoDB;
    String periodoSeleccionadoDB;

    private List<String> listaCamposValidos;
    private int mYear, mMonth, mDay;
    int no_cuenta = 0;
    String clienteGlobal;


    EditText inp_ruta_actualiza_cliente;
    EditText inp_secuencia_actualiza_cliente;
    CheckBox checkbor_lunes_actualiza_cliente;
    CheckBox checkbor_martes_actualiza_cliente;
    CheckBox checkbor_miercoles_actualiza_cliente;
    CheckBox checkbor_jueves_actualiza_cliente;
    CheckBox checkbor_viernes_actualiza_cliente;
    CheckBox checkbor_sabado_actualiza_cliente;
    CheckBox checkbor_domingo_actualiza_cliente;
    CheckBox et_actualiza_credito;
    private RelativeLayout rlprogress;

    EditText editTextLatitud;
    EditText editTextLongitud;
    ImageButton buttonLocation;
    ImageButton img_search_cliente_actualiza_cliente;
    boolean isLocation = false;
    EditText inp_matriz_asignada_actualiza_cliente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualiza_cliente);
        rlprogress = findViewById(R.id.rlprogress_cliente_actualiza);
        this.initToolBar();

        this.initControls();
        this.getData();
        this.loadSpinnerCategoria();
        this.loadSpinnerStatus();
        this.loadSpinnerRegion();
        this.loadSpinnerSector();
        this.loadSpinnerRuta();
        this.loadSpinnerPeriodo();
        this.loadSpinnerGrupo();
    }

    private void getData() {

        Intent intent = getIntent();
        clienteGlobal = intent.getStringExtra(Actividades.PARAM_1);

        ClientDao clientDao = new ClientDao();
        ClienteBean clienteBean = clientDao.getClientByAccount(clienteGlobal);

        if (clienteBean != null) {
            editText_nombre_actualiza_cliente.setText(clienteBean.getNombre_comercial());
            editText_calle_actualiza_cliente.setText(clienteBean.getCalle());
            editText_numero_actualiza_cliente.setText(clienteBean.getNumero());
            editText_colonia_actualiza_cliente.setText(clienteBean.getColonia());
            editText_ciudad_actualiza_cliente.setText(clienteBean.getCiudad());
            editText_cp_actualiza_cliente.setText("" + clienteBean.getCodigo_postal());
            editText_no_cuenta_actualiza_cliente.setText(clienteBean.getCuenta());
            editText_fecha_alta_actualiza_cliente.setText(clienteBean.getFecha_registro());
            editText_fecha_baja_actualiza_cliente.setText(clienteBean.getFecha_baja());
            inp_contacto_phone_actualiza_cliente.setText(""+ clienteBean.getContacto_phone());
            if (clienteBean.getStatus() == false){
                statusSeleccionadoDB = "Activo";
            }else {
                statusSeleccionadoDB = "InActivo";
            }

            categoriaSeleccionadaDB = clienteBean.getCategoria();
            grupoSeleccionadoDB =  clienteBean.getGrupo();
            regionSeleccionadoDB = clienteBean.getRegion();
            sectorSeleccionadoDB = clienteBean.getSector();
            rutaSeleccionadoDB =  clienteBean.getRango();
            periodoSeleccionadoDB = String.valueOf(clienteBean.getPeriodo());




            if (clienteBean.getLun() == 1){
                checkbor_lunes_actualiza_cliente.setChecked(true);
            }
            if (clienteBean.getMar() == 1){
                checkbor_martes_actualiza_cliente.setChecked(true);
            }
            if (clienteBean.getMie() == 1){
                checkbor_miercoles_actualiza_cliente.setChecked(true);
            }
            if (clienteBean.getJue() == 1){
                checkbor_jueves_actualiza_cliente.setChecked(true);
            }
            if (clienteBean.getVie() == 1){
                checkbor_viernes_actualiza_cliente.setChecked(true);
            }
            if (clienteBean.getSab() == 1){
                checkbor_sabado_actualiza_cliente.setChecked(true);
            }
            if (clienteBean.getDom() == 1){
                checkbor_domingo_actualiza_cliente.setChecked(true);
            }

            editTextLatitud.setText(clienteBean.getLatitud());
            editTextLongitud.setText(clienteBean.getLongitud());

            inp_matriz_asignada_actualiza_cliente.setText(clienteBean.getMatriz());
            et_registro_actualiza_limite_credito.setText(""+clienteBean.getLimite_credito());
            et_registro_actualiza_credito.setText("" +clienteBean.getSaldo_credito());

            if (clienteBean.getIs_credito()){
                et_actualiza_credito.setChecked(true);
            }else {
                et_actualiza_credito.setChecked(false);

            }


        }

    }

    void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_actualiza_cliente);
        toolbar.setTitle("Actualiza cliente");
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
        menuInflater.inflate(R.menu.menu_actualiza_cliente, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.actualizaUbicacionCliente:
                isLocation = false;
                Actividades.getSingleton(ActualizaClienteActivity.this, MapsClienteActivity.class).muestraActividadForResult(300);
                return true;

            case R.id.actualizaCliente:

                if (validaCampos()) {
                    if (validaCliente()) {

                        final PrettyDialog dialog = new PrettyDialog(this);
                        dialog.setTitle("Actualizar")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("Desea actualizar el cliente")
                                .setMessageColor(R.color.purple_700)
                                .setAnimationEnabled(false)
                                .setIcon(R.drawable.ic_save_white, R.color.purple_500, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        dialog.dismiss();
                                    }
                                })
                                .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        actualizaCliente();
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

                    }
                } else {

                    StringBuilder campos = new StringBuilder();
                    for (String validItem : listaCamposValidos) {
                        campos.append(validItem).append("\n");
                    }

                    final PrettyDialog dialog = new PrettyDialog(this);
                    dialog.setTitle("Campos requeridos")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Debe de completar los campos requeridos " + "\n" + campos)
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            })
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.light_blue_700, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });

                    dialog.setCancelable(false);
                    dialog.show();
                }

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void initControls() {


        inp_matriz_asignada_actualiza_cliente = findViewById(R.id.inp_matriz_asignada_actualiza_cliente);

        img_search_cliente_actualiza_cliente = findViewById(R.id.img_search_cliente_actualiza_cliente);
        img_search_cliente_actualiza_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actividades.getSingleton(ActualizaClienteActivity.this, ListaClientesActivity.class).muestraActividadForResult(200);
            }
        });
        et_registro_actualiza_limite_credito  = findViewById(R.id.et_registro_actualiza_limite_credito);
        et_registro_actualiza_credito  = findViewById(R.id.et_registro_actualiza_credito);
        et_actualiza_credito  = findViewById(R.id.et_actualiza_credito);

        editText_nombre_actualiza_cliente = findViewById(R.id.inp_nombre_actualiza_cliente);
        editText_calle_actualiza_cliente = findViewById(R.id.inp_calle_actualiza_cliente);
        editText_numero_actualiza_cliente = findViewById(R.id.inp_numero_actualiza_cliente);
        editText_colonia_actualiza_cliente = findViewById(R.id.inp_colonia_actualiza_cliente);
        editText_ciudad_actualiza_cliente = findViewById(R.id.inp_ciudad_actualiza_cliente);
        editText_cp_actualiza_cliente = findViewById(R.id.inp_cp_actualiza_cliente);
        editText_no_cuenta_actualiza_cliente = findViewById(R.id.inp_no_cuenta_actualiza_cliente);
        inp_contacto_phone_actualiza_cliente = findViewById(R.id.inp_contacto_phone_actualiza_cliente);
        editText_fecha_alta_actualiza_cliente = findViewById(R.id.inp_fecha_alta_actualiza_cliente);
        img_fecha_alta_actualiza_cliente = findViewById(R.id.img_fecha_alta_actualiza_cliente);

        editText_fecha_baja_actualiza_cliente = findViewById(R.id.inp_fecha_baja_actualiza_cliente);
        img_fecha_baja_actualiza_cliente = findViewById(R.id.img_fecha_baja_actualiza_cliente);

        img_fecha_alta_actualiza_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFechaactualiza();
            }
        });

        img_fecha_baja_actualiza_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateFechaBaja();
            }
        });

        inp_ruta_actualiza_cliente = findViewById(R.id.inp_ruta_actualiza_cliente);
        inp_secuencia_actualiza_cliente = findViewById(R.id.inp_secuencia_actualiza_cliente);
        checkbor_lunes_actualiza_cliente = findViewById(R.id.checkbor_lunes_actualiza_cliente);
        checkbor_martes_actualiza_cliente = findViewById(R.id.checkbor_martes_actualiza_cliente);
        checkbor_miercoles_actualiza_cliente = findViewById(R.id.checkbor_miercoles_actualiza_cliente);
        checkbor_jueves_actualiza_cliente = findViewById(R.id.checkbor_jueves_actualiza_cliente);
        checkbor_viernes_actualiza_cliente = findViewById(R.id.checkbor_viernes_actualiza_cliente);
        checkbor_sabado_actualiza_cliente = findViewById(R.id.checkbor_sabado_actualiza_cliente);
        checkbor_domingo_actualiza_cliente = findViewById(R.id.checkbor_domingo_actualiza_cliente);

        editTextLatitud = findViewById(R.id.et_actualiza_latitud);
        editTextLongitud = findViewById(R.id.et_actualiza_longitud);
        buttonLocation = findViewById(R.id.btn_actualiza_location);


        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLocation = true;
                locationStart();
            }
        });

    }

    private void dateFechaactualiza() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        if (dayOfMonth < 9) {
                            editText_fecha_alta_actualiza_cliente.setText("0" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        } else {
                            editText_fecha_alta_actualiza_cliente.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    private void dateFechaBaja() {

        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        if (dayOfMonth < 9) {
                            editText_fecha_baja_actualiza_cliente.setText("0" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        } else {
                            editText_fecha_baja_actualiza_cliente.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }


                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

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
        ActualizaClienteActivity registrarClientesController;

        public ActualizaClienteActivity getMainActivity() {
            return registrarClientesController;
        }

        public void setMainActivity(ActualizaClienteActivity mainActivity) {
            this.registrarClientesController = mainActivity;
        }

        @Override
        public void onLocationChanged(Location location) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (location.getLatitude() != 0.0 && location.getLongitude() != 0.0) {
                        try {
                            /*Geocodificacion- Proceso de conversión de coordenadas a direccion*/
                            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                            List<Address> list = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1);
                            if (!list.isEmpty()) {

                                String address = list.get(0).getAddressLine(0);
                                String cityName = list.get(0).getLocality();
                                String stateName = list.get(0).getAdminArea();
                                String codigo = list.get(0).getPostalCode();


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!isLocation) {
                                            editText_calle_actualiza_cliente.setText(address);
                                            editText_cp_actualiza_cliente.setText(codigo);
                                            editText_colonia_actualiza_cliente.setText(cityName);
                                            editText_ciudad_actualiza_cliente.setText(stateName);
                                            editTextLatitud.setText("" +list.get(0).getLatitude());
                                            editTextLongitud.setText(""+ list.get(0).getLongitude());
                                        }else {
                                            editTextLatitud.setText("" +list.get(0).getLatitude());
                                            editTextLongitud.setText(""+ list.get(0).getLongitude());
                                        }
                                    }
                                });

                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();

            registrarClientesController.setLocation(location);

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                //Obtener la direccion de la calle a partir de la latitud y la longitud
                if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
                    try {
                        /*Geocodificacion- Proceso de conversión de coordenadas a direccion*/
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
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
        }).start();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
            }
        }
    }

    private void loadSpinnerGrupo() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.grupo);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_grupo_actualiza_cliente = findViewById(R.id.spinner_grupo_actualiza_cliente);
        spinner_grupo_actualiza_cliente.setAdapter(adapter);
        spinner_grupo_actualiza_cliente.setSelection(arrayList.indexOf(grupoSeleccionadoDB));
        spinner_grupo_actualiza_cliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                grupo_seleccionado = spinner_grupo_actualiza_cliente.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerCategoria() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.categoria);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_categoria_actualiza_cliente = findViewById(R.id.spinner_categoria_actualiza_cliente);
        spinner_categoria_actualiza_cliente.setAdapter(adapter);
        spinner_categoria_actualiza_cliente.setSelection(arrayList.indexOf(categoriaSeleccionadaDB));
        spinner_categoria_actualiza_cliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categoria_seleccionada = spinner_categoria_actualiza_cliente.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerStatus() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.status_producto);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_status_actualiza_cliente = findViewById(R.id.spinner_status_actualiza_cliente);
        spinner_status_actualiza_cliente.setAdapter(adapter);
        spinner_status_actualiza_cliente.setSelection(arrayList.indexOf(statusSeleccionadoDB));
        spinner_status_actualiza_cliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status_seleccionado = spinner_status_actualiza_cliente.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerRegion() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.region);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_region_actualiza_cliente = findViewById(R.id.spinner_region_actualiza_cliente);
        spinner_region_actualiza_cliente.setAdapter(adapter);
        spinner_region_actualiza_cliente.setSelection(arrayList.indexOf(regionSeleccionadoDB));
        spinner_region_actualiza_cliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                region_seleccionado = spinner_region_actualiza_cliente.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerSector() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.ruteo_sector);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_sector_actualiza_cliente = findViewById(R.id.spinner_sector_actualiza_cliente);
        spinner_sector_actualiza_cliente.setAdapter(adapter);
        spinner_sector_actualiza_cliente.setSelection(arrayList.indexOf(sectorSeleccionadoDB));
        spinner_sector_actualiza_cliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sector_seleccionado = spinner_sector_actualiza_cliente.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerRuta() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.ruteo_rango_rutas);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_ruta_actualiza_cliente = findViewById(R.id.spinner_rango_actualiza_cliente);
        spinner_ruta_actualiza_cliente.setAdapter(adapter);
        spinner_ruta_actualiza_cliente.setSelection(arrayList.indexOf(rutaSeleccionadoDB));
        spinner_ruta_actualiza_cliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ruta_seleccionado = spinner_ruta_actualiza_cliente.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerPeriodo() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.ruteo_periodo);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_periodo_actualiza_cliente = findViewById(R.id.spinner_periodo_actualiza_cliente);
        spinner_periodo_actualiza_cliente.setAdapter(adapter);
        spinner_periodo_actualiza_cliente.setSelection(arrayList.indexOf(periodoSeleccionadoDB));
        spinner_periodo_actualiza_cliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                periodo_seleccionado = spinner_periodo_actualiza_cliente.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    protected String[] getArrayString(final int id) {
        return this.getResources().getStringArray(id);
    }

    private boolean validaCampos() {

        listaCamposValidos = new ArrayList<>();

        boolean valida = true;

        String nombre = editText_nombre_actualiza_cliente.getText().toString();

        String calle = editText_calle_actualiza_cliente.getText().toString();

        String numero = editText_numero_actualiza_cliente.getText().toString();

        String colonia = editText_colonia_actualiza_cliente.getText().toString();
        String ciudad = editText_ciudad_actualiza_cliente.getText().toString();

        String cp = editText_cp_actualiza_cliente.getText().toString();


        if (nombre.isEmpty()) {
            valida = false;
            listaCamposValidos.add("nombre");
        }

        if (calle.isEmpty()) {
            valida = false;
            listaCamposValidos.add("ciudad");
        }

        if (numero.isEmpty()) {
            valida = false;
            listaCamposValidos.add("numero");
        }


        if (numero.isEmpty()) {
            valida = false;
            listaCamposValidos.add("numero");
        }

        if (numero.isEmpty()) {
            valida = false;
            listaCamposValidos.add("numero");
        }

        if (colonia.isEmpty()) {
            valida = false;
            listaCamposValidos.add("colonia");
        }
        if (ciudad.isEmpty()) {
            valida = false;
            listaCamposValidos.add("ciudad");
        }
        if (cp.isEmpty()) {
            valida = false;
            listaCamposValidos.add("C.P");
        }

        return valida;
    }

    private boolean validaCliente() {

        boolean valida = true;

        ClientDao dao = new ClientDao();
        ClienteBean bean = dao.getClientByAccount(editText_no_cuenta_actualiza_cliente.getText().toString());

        valida = bean != null;
        return valida;
    }

    String idCliente;
    private void actualizaCliente() {

        String cp = editText_cp_actualiza_cliente.getText().toString();
        String sec = inp_secuencia_actualiza_cliente.getText().toString();

        if (cp != null && !cp.isEmpty() && sec != null && !sec.isEmpty() && periodo_seleccionado != null && !periodo_seleccionado.isEmpty()) {
            ClientDao dao = new ClientDao();
            ClienteBean bean = dao.getClientByAccount(editText_no_cuenta_actualiza_cliente.getText().toString());
            bean.setNombre_comercial(editText_nombre_actualiza_cliente.getText().toString());
            bean.setCalle(editText_calle_actualiza_cliente.getText().toString());
            bean.setNumero(editText_numero_actualiza_cliente.getText().toString());
            bean.setColonia(editText_colonia_actualiza_cliente.getText().toString());
            bean.setCiudad(editText_ciudad_actualiza_cliente.getText().toString());
            bean.setCodigo_postal(Integer.parseInt(cp));
            bean.setFecha_registro(editText_fecha_alta_actualiza_cliente.getText().toString());
            bean.setFecha_baja(editText_fecha_baja_actualiza_cliente.getText().toString());
            bean.setCuenta(editText_no_cuenta_actualiza_cliente.getText().toString());
            bean.setGrupo(grupo_seleccionado);
            bean.setCategoria(categoria_seleccionada);
            bean.setStatus(status_seleccionado.compareToIgnoreCase("Activo") == 0);
            bean.setConsec(no_cuenta);
            bean.setRegion(region_seleccionado);
            bean.setSector(sector_seleccionado);
            bean.setRango(ruta_seleccionado);
            bean.setRuta(inp_ruta_actualiza_cliente.getText().toString());
            bean.setSecuencia(Integer.parseInt(sec));
            bean.setPeriodo(Integer.parseInt(periodo_seleccionado));
            if (checkbor_lunes_actualiza_cliente.isChecked()) {
                bean.setLun(1);
            } else {
                bean.setLun(0);
            }
            if (checkbor_martes_actualiza_cliente.isChecked()) {
                bean.setMar(1);
            } else {
                bean.setMar(0);
            }
            if (checkbor_miercoles_actualiza_cliente.isChecked()) {
                bean.setMie(1);
            } else {
                bean.setMie(0);
            }
            if (checkbor_jueves_actualiza_cliente.isChecked()) {
                bean.setJue(1);
            } else {
                bean.setJue(0);
            }
            if (checkbor_viernes_actualiza_cliente.isChecked()) {
                bean.setVie(1);
            } else {
                bean.setVie(0);
            }
            if (checkbor_sabado_actualiza_cliente.isChecked()) {
                bean.setSab(1);
            } else {
                bean.setSab(0);
            }
            if (checkbor_domingo_actualiza_cliente.isChecked()) {
                bean.setDom(1);
            } else {
                bean.setDom(0);
            }


            bean.setLatitud(editTextLatitud.getText().toString());
            bean.setLongitud(editTextLongitud.getText().toString());
            bean.setContacto_phone(inp_contacto_phone_actualiza_cliente.getText().toString());
            if (et_actualiza_credito.isChecked()) {
                bean.setIs_credito(true);
            } else {
                bean.setIs_credito(false);
            }


            bean.setLimite_credito(Double.parseDouble(et_registro_actualiza_limite_credito.getText().toString().replace("$", "").replace(" ", "")));
            bean.setSaldo_credito(Double.parseDouble(et_registro_actualiza_credito.getText().toString().replace("$", "").replace(" ", "")));
            bean.setMatriz(inp_matriz_asignada_actualiza_cliente.getText().toString());
            bean.setDate_sync(Utils.fechaActual());
            dao.save(bean);
            idCliente = String.valueOf(bean.getId());

            if (!Utils.isNetworkAvailable(getApplication())) {
                //showDialogNotConnectionInternet();
            } else {
                testLoadClientes(idCliente);
            }
        } else {
            Toast.makeText(ActualizaClienteActivity.this, "Necesita llenar todos los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogNotConnectionInternet() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testLoadClientes(idCliente);
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void testLoadClientes(String idCliente){
        progressshow();
        final ClientDao clientDao = new ClientDao();
        List<ClienteBean> listaClientesDB = new ArrayList<>();
        listaClientesDB =  clientDao.getByIDClient(idCliente);

        List<Client> listaClientes = new ArrayList<>();

        for (ClienteBean item : listaClientesDB){
            Client cliente = new Client();
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
            cliente.setLatitud("" +item.getLatitud());
            cliente.setLongitud(""+ item.getLongitud());
            cliente.setPhone_contacto(""+item.getContacto_phone());
            cliente.setRecordatorio(""+item.getRecordatorio());
            cliente.setVisitas(item.getVisitasNoefectivas());
            if (item.getIs_credito()){
                cliente.setCredito(1);
            }else{
                cliente.setCredito(0);
            }
            cliente.setSaldo_credito(item.getSaldo_credito());
            cliente.setLimite_credito(item.getLimite_credito());

            if (item.getMatriz() == "null" && item.getMatriz() == null) {
                cliente.setMatriz("null");
            }else{
                cliente.setMatriz(item.getMatriz());
            }
            listaClientes.add(cliente);
        }

        new ClientInteractorImp().executeSaveClient(listaClientes, new ClientInteractor.SaveClientListener() {
            @Override
            public void onSaveClientSuccess() {
                progresshide();
                //Toast.makeText(getApplicationContext(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onSaveClientError() {
                progresshide();
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    public void progressshow() {
        rlprogress.setVisibility(View.VISIBLE);
    }

    public void progresshide() {
        rlprogress.setVisibility(View.GONE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED)
            return;


        if (requestCode == 200){
            String cuenta = data.getStringExtra(Actividades.PARAM_1);
            inp_matriz_asignada_actualiza_cliente.setText(""+cuenta);
        }else if(requestCode == 300) {
            String numero = data.getStringExtra(Actividades.PARAM_1);
            String calle = data.getStringExtra(Actividades.PARAM_2);
            String localidad = data.getStringExtra(Actividades.PARAM_3);
            String colonia = data.getStringExtra(Actividades.PARAM_4);
            String estado = data.getStringExtra(Actividades.PARAM_5);
            String pais = data.getStringExtra(Actividades.PARAM_6);
            String cp = data.getStringExtra(Actividades.PARAM_7);
            String lat = data.getStringExtra(Actividades.PARAM_8);
            String lng = data.getStringExtra(Actividades.PARAM_9);

            editText_calle_actualiza_cliente.setText("" +calle);
            editText_cp_actualiza_cliente.setText("" +cp);
            editText_colonia_actualiza_cliente.setText("" + localidad);
            editText_ciudad_actualiza_cliente.setText(""+estado);
            editText_numero_actualiza_cliente.setText("" + numero);
            editTextLatitud.setText("" +lat);
            editTextLongitud.setText("" + lng);
        }

    }
}