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

public class RegistroClienteActivity extends AppCompatActivity {

    private Spinner spinner_status_registro_cliente;
    private Spinner spinner_ruta_registro_cliente;
    private EditText editText_nombre_registro_cliente;
    private EditText editText_calle_registro_cliente;
    private EditText editText_numero_registro_cliente;
    private EditText editText_colonia_registro_cliente;
    private EditText editText_ciudad_registro_cliente;
    private EditText editText_cp_registro_cliente;
    private EditText editText_fecha_alta_registro_cliente;
    private EditText inp_contacto_phone_registro_cliente;
    private EditText editText_no_cuenta_registro_cliente;
    private EditText inp_matriz_asignada_registro_cliente;
    private EditText et_registro_limite_credito;
    private EditText et_registro_saldo_credito;
    private EditText editTextLatitud;
    private EditText editTextLongitud;
    private CheckBox checkbor_lunes_registro_cliente;
    private CheckBox checkbor_martes_registro_cliente;
    private CheckBox checkbor_miercoles_registro_cliente;
    private CheckBox checkbor_jueves_registro_cliente;
    private CheckBox checkbor_viernes_registro_cliente;
    private CheckBox checkbor_sabado_registro_cliente;
    private CheckBox checkbor_domingo_registro_cliente;
    private CheckBox checkbox_registro_credito;
    private ImageButton img_fecha_alta_registro_cliente;
    private ImageButton img_search_cliente_registro_cliente;
    private ImageButton buttonLocation;
    private RelativeLayout rlprogress;

    private List<String> listaCamposValidos;
    private String status_seleccionado;
    private String ruta_seleccionado;
    private String idCliente;
    private int no_cuenta = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_cliente);
        rlprogress = findViewById(R.id.rlprogress_cliente_registro);
        initToolBar();
        this.initControls();
        this.loadSpinnerStatus();
        this.loadSpinnerRuta();
        this.loadConsecCuenta();

        //locationStart();

        editText_fecha_alta_registro_cliente.setText(Utils.fechaActualPicker());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_CANCELED) return;

        if (requestCode == 200){
            String cuenta = data.getStringExtra(Actividades.PARAM_1);
            inp_matriz_asignada_registro_cliente.setText(cuenta);
        }else if(requestCode == 300) {
            String numero = data.getStringExtra(Actividades.PARAM_1);
            String calle = data.getStringExtra(Actividades.PARAM_2);
            String localidad = data.getStringExtra(Actividades.PARAM_3);
            String colonia = data.getStringExtra(Actividades.PARAM_5);
            String cp = data.getStringExtra(Actividades.PARAM_7);
            String lat = data.getStringExtra(Actividades.PARAM_8);
            String lng = data.getStringExtra(Actividades.PARAM_9);

            editText_calle_registro_cliente.setText(calle);
            editText_cp_registro_cliente.setText(cp);
            editText_colonia_registro_cliente.setText(localidad);
            editText_ciudad_registro_cliente.setText(colonia);
            editText_numero_registro_cliente.setText(numero);
            editTextLatitud.setText(lat);
            editTextLongitud.setText(lng);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_registro_cliente, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;

            case R.id.ubicaciobCliente:
                Actividades.getSingleton(RegistroClienteActivity.this, MapsClienteActivity.class).muestraActividadForResult(300);
                return true;

            case R.id.guardaCliente:

                if (validaCampos()) {
                    if (!validaCliente()) {

                        final PrettyDialog dialog = new PrettyDialog(this);
                        dialog.setTitle("Registrar")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("Desea registar el cliente")
                                .setMessageColor(R.color.purple_700)
                                .setAnimationEnabled(false)
                                .setIcon(R.drawable.ic_save_white, R.color.purple_500, () -> dialog.dismiss())
                                .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, () -> {
                                    registraCliente();
                                    dialog.dismiss();
                                })
                                .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, () -> dialog.dismiss());
                        dialog.setCancelable(false);
                        dialog.show();

                    } else {
                        final PrettyDialog dialog = new PrettyDialog(this);
                        dialog.setTitle("Exitente")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("Ya existe un registro con el numero de cuenta  " + editText_no_cuenta_registro_cliente.getText().toString())
                                .setMessageColor(R.color.purple_700)
                                .setAnimationEnabled(false)
                                .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, () -> dialog.dismiss())
                                .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.light_blue_800, () -> dialog.dismiss());

                        dialog.setCancelable(false);
                        dialog.show();
                    }
                } else {

                    StringBuilder campos = new StringBuilder();
                    for (String validItem : listaCamposValidos) {
                        campos.append(validItem).append("\n");
                    }

                    final PrettyDialog dialog = new PrettyDialog(this);
                    dialog.setTitle("Campor requeridos")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Debe de completar los campos requeridos " + "\n" + campos)
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, () -> dialog.dismiss())
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.light_blue_700, () -> dialog.dismiss());

                    dialog.setCancelable(false);
                    dialog.show();
                }


            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar_registro_cliente);
        toolbar.setTitle("Registro cliente");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));
        }
    }

    private void loadConsecCuenta() {

        final ClientDao clientDao = new ClientDao();
        no_cuenta = clientDao.getLastConsec();
        String consectivo = "";
        if (no_cuenta < 10) {
            consectivo = "00000" + no_cuenta;
        } else if (no_cuenta >= 10 && no_cuenta <= 99) {
            consectivo = "0000" + no_cuenta;
        } else if (no_cuenta >= 100 && no_cuenta <= 999) {
            consectivo = "000" + no_cuenta;
        } else if (no_cuenta >= 1000 && no_cuenta <= 9999) {
            consectivo = "00" + no_cuenta;
        } else if (no_cuenta >= 10000 && no_cuenta <= 99999) {
            consectivo = "0" + no_cuenta;
        } else {
            consectivo = "" + no_cuenta;
        }

        editText_no_cuenta_registro_cliente.setText(consectivo);
    }

    private void initControls() {

        img_search_cliente_registro_cliente = findViewById(R.id.img_search_cliente_registro_cliente);
        img_search_cliente_registro_cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Actividades.getSingleton(RegistroClienteActivity.this, ListaClientesActivity.class).muestraActividadForResult(200);

            }
        });

        editText_nombre_registro_cliente = findViewById(R.id.inp_nombre_registro_cliente);
        editText_calle_registro_cliente = findViewById(R.id.inp_calle_registro_cliente);
        editText_numero_registro_cliente = findViewById(R.id.inp_numero_registro_cliente);
        editText_colonia_registro_cliente = findViewById(R.id.inp_colonia_registro_cliente);
        editText_ciudad_registro_cliente = findViewById(R.id.inp_ciudad_registro_cliente);
        editText_cp_registro_cliente = findViewById(R.id.inp_cp_registro_cliente);
        editText_no_cuenta_registro_cliente = findViewById(R.id.inp_no_cuenta_registro_cliente);
        inp_contacto_phone_registro_cliente = findViewById(R.id.inp_contacto_phone_registro_cliente);
        editText_fecha_alta_registro_cliente = findViewById(R.id.inp_fecha_alta_registro_cliente);
        img_fecha_alta_registro_cliente = findViewById(R.id.img_fecha_alta_registro_cliente);
        et_registro_limite_credito = findViewById(R.id.et_registro_limite_credito);
        et_registro_saldo_credito  = findViewById(R.id.et_registro_limite_credito);
        checkbox_registro_credito = findViewById(R.id.checkbox_registro_credito);
        inp_matriz_asignada_registro_cliente = findViewById(R.id.inp_matriz_asignada_registro_cliente);
        img_fecha_alta_registro_cliente.setOnClickListener(v -> dateFechaRegistro());

        checkbor_lunes_registro_cliente = findViewById(R.id.checkbor_lunes_registro_cliente);
        checkbor_martes_registro_cliente = findViewById(R.id.checkbor_martes_registro_cliente);
        checkbor_miercoles_registro_cliente = findViewById(R.id.checkbor_miercoles_registro_cliente);
        checkbor_jueves_registro_cliente = findViewById(R.id.checkbor_jueves_registro_cliente);
        checkbor_viernes_registro_cliente = findViewById(R.id.checkbor_viernes_registro_cliente);
        checkbor_sabado_registro_cliente = findViewById(R.id.checkbor_sabado_registro_cliente);
        checkbor_domingo_registro_cliente = findViewById(R.id.checkbor_domingo_registro_cliente);

        editTextLatitud = findViewById(R.id.et_registro_latitud);
        editTextLongitud = findViewById(R.id.et_registro_longitud);
        buttonLocation = findViewById(R.id.btn_registro_location);

        checkbox_registro_credito = findViewById(R.id.checkbox_registro_credito);
        et_registro_limite_credito = findViewById(R.id.et_registro_limite_credito);
        et_registro_saldo_credito = findViewById(R.id.et_registro_saldo_credito);

        buttonLocation.setOnClickListener(v -> {
            //locationStart();
        });

    }

    private void dateFechaRegistro() {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    if (dayOfMonth < 9) {
                        editText_fecha_alta_registro_cliente.setText("0" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    } else {
                        editText_fecha_alta_registro_cliente.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
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
        RegistroClienteActivity registrarClientesController;

        public RegistroClienteActivity getMainActivity() {
            return registrarClientesController;
        }

        public void setMainActivity(RegistroClienteActivity mainActivity) {
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

                       // if (!isLocation) {
                       //     String address = list.get(0).getAddressLine(0);
                       //     String cityName = list.get(0).getLocality();
                       //     String stateName = list.get(0).getAdminArea();
                       //     String codigo = list.get(0).getPostalCode();
//
                       //    if(editText_calle_registro_cliente.getText().toString().isEmpty()){
                       //         editText_calle_registro_cliente.setText(address);
                       //     }
//
                       //     editText_cp_registro_cliente.setText(codigo);
                       //     editText_colonia_registro_cliente.setText(cityName);
                       //     editText_ciudad_registro_cliente.setText(stateName);
                       //     editTextLatitud.setText("" + list.get(0).getLatitude());
                       //     editTextLongitud.setText("" + list.get(0).getLongitude());
                       // } else {
                       //     editTextLatitud.setText("" + list.get(0).getLatitude());
                       //     editTextLongitud.setText("" + list.get(0).getLongitude());
                       // }
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

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
            }
        }
    }

    private void loadSpinnerStatus() {

        //Obtiene el array de las unidades de medida
        String[] array = getArrayString(R.array.status_producto);

        //Obtiene la lista de Strings
        List<String> arrayList = Utils.convertArrayStringListString(array);

        //Creamos el adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_status_registro_cliente = findViewById(R.id.spinner_status_registro_cliente);
        spinner_status_registro_cliente.setAdapter(adapter);
        spinner_status_registro_cliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                status_seleccionado = spinner_status_registro_cliente.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadSpinnerRuta() {
        String[] array = getArrayString(R.array.ruteo_rango_rutas);
        List<String> arrayList = Utils.convertArrayStringListString(array);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_status_producto, arrayList);
        spinner_ruta_registro_cliente = findViewById(R.id.spinner_rango_registro_cliente);
        spinner_ruta_registro_cliente.setAdapter(adapter);
        spinner_ruta_registro_cliente.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ruta_seleccionado = spinner_ruta_registro_cliente.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    protected String[] getArrayString(final int id) {
        return this.getResources().getStringArray(id);
    }

    private boolean validaCampos() {
        boolean valida = true;
        listaCamposValidos = new ArrayList<>();

        String nombre = editText_nombre_registro_cliente.getText().toString();
        String calle = editText_calle_registro_cliente.getText().toString();
        String numero = editText_numero_registro_cliente.getText().toString();
        String colonia = editText_colonia_registro_cliente.getText().toString();
        String ciudad = editText_ciudad_registro_cliente.getText().toString();
        String cp = editText_cp_registro_cliente.getText().toString();

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
        ClientDao dao = new ClientDao();
        ClienteBean bean = dao.getClientByAccount(editText_no_cuenta_registro_cliente.getText().toString());
        return bean != null;
    }

    private void registraCliente() {
        String numero = inp_contacto_phone_registro_cliente.getText().toString();
        if (numero != null && !numero.isEmpty() && !numero.equals("null")) {
            final ClienteBean clienteBean = new ClienteBean();
            final ClientDao clientDao = new ClientDao();
            clienteBean.setNombre_comercial(editText_nombre_registro_cliente.getText().toString());
            clienteBean.setCalle(editText_calle_registro_cliente.getText().toString());
            clienteBean.setNumero(editText_numero_registro_cliente.getText().toString());
            clienteBean.setColonia(editText_colonia_registro_cliente.getText().toString());
            clienteBean.setCiudad(editText_ciudad_registro_cliente.getText().toString());
            clienteBean.setCodigo_postal(Integer.parseInt(editText_cp_registro_cliente.getText().toString()));
            clienteBean.setFecha_registro(editText_fecha_alta_registro_cliente.getText().toString());
            clienteBean.setCuenta(editText_no_cuenta_registro_cliente.getText().toString());
            clienteBean.setStatus(status_seleccionado.compareToIgnoreCase("Activo") == 0);
            clienteBean.setConsec(no_cuenta);
            clienteBean.setRango(ruta_seleccionado);

            clienteBean.setLun(checkbor_lunes_registro_cliente.isChecked() ? 1 : 0);
            clienteBean.setMar(checkbor_martes_registro_cliente.isChecked() ? 1 : 0);
            clienteBean.setMie(checkbor_miercoles_registro_cliente.isChecked() ? 1 : 0);
            clienteBean.setJue(checkbor_jueves_registro_cliente.isChecked() ? 1 : 0);
            clienteBean.setVie(checkbor_viernes_registro_cliente.isChecked() ? 1 : 0);
            clienteBean.setSab(checkbor_sabado_registro_cliente.isChecked() ? 1 : 0);
            clienteBean.setDom(checkbor_domingo_registro_cliente.isChecked() ? 1 : 0);

            clienteBean.setContacto_phone(inp_contacto_phone_registro_cliente.getText().toString());
            clienteBean.setLatitud(editTextLatitud.getText().toString());
            clienteBean.setLongitud(editTextLongitud.getText().toString());
            clienteBean.setIs_credito(checkbox_registro_credito.isChecked());

            String limite = et_registro_limite_credito.getText().toString();
            if (limite.isEmpty()) {
                clienteBean.setLimite_credito(0.00);
            } else {
                clienteBean.setLimite_credito(Double.parseDouble(et_registro_limite_credito.getText().toString()));
            }

            if (checkbox_registro_credito.isChecked()) {
                clienteBean.setMatriz(inp_matriz_asignada_registro_cliente.getText().toString());
                clienteBean.setIs_credito(true);
            } else {
                clienteBean.setIs_credito(false);
                clienteBean.setMatriz("null");
            }

            clienteBean.setSaldo_credito(0.00);
            clienteBean.setDate_sync(Utils.fechaActual());

            clientDao.insert(clienteBean);

            idCliente = String.valueOf(clienteBean.getId());
            if (!Utils.isNetworkAvailable(getApplication())) {
                //showDialogNotConnectionInternet();
            } else {
                testLoadClientes(idCliente);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Debe introducir un telefono de contacto",Toast.LENGTH_SHORT).show();
        }
    }

    private void showDialogNotConnectionInternet() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_warning);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(v -> {
            testLoadClientes(idCliente);
            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void testLoadClientes(String idCliente) {
        progressshow();

        ClientDao clientDao = new ClientDao();
        List<ClienteBean> listaClientesDB = clientDao.getByIDClient(idCliente);
        List<Client> listaClientes = new ArrayList<>();

        for (ClienteBean item : listaClientesDB) {
            Client cliente = new Client();
            cliente.setNombreComercial(item.getNombre_comercial());
            cliente.setCalle(item.getCalle());
            cliente.setNumero(item.getNumero());
            cliente.setColonia(item.getColonia());
            cliente.setCiudad(item.getCiudad());
            cliente.setCodigoPostal(item.getCodigo_postal());
            cliente.setFechaRegistro(item.getFecha_registro());
            cliente.setCuenta(item.getCuenta());
            cliente.setStatus(item.getStatus()? 1 : 0);
            cliente.setConsec(item.getConsec());
            cliente.setRango(item.getRango());
            cliente.setLun(item.getLun());
            cliente.setMar(item.getMar());
            cliente.setMie(item.getMie());
            cliente.setJue(item.getJue());
            cliente.setVie(item.getVie());
            cliente.setSab(item.getSab());
            cliente.setDom(item.getDom());
            cliente.setLatitud(item.getLatitud());
            cliente.setLongitud(item.getLongitud());
            cliente.setPhone_contacto(item.getContacto_phone());
            cliente.setRecordatorio(item.getRecordatorio() != null? item.getRecordatorio() : "");
            cliente.setVisitas(item.getVisitasNoefectivas());
            cliente.setCredito(item.getIs_credito()? 1 : 0);

            cliente.setSaldo_credito(item.getSaldo_credito());
            cliente.setLimite_credito(item.getLimite_credito());
            if (item.getMatriz().equals("null") || item.getMatriz() == null) {
                cliente.setMatriz("null");
            } else{
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
}