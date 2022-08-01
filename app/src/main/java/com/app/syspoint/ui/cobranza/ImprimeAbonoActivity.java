package com.app.syspoint.ui.cobranza;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.app.syspoint.R;
import com.app.syspoint.bluetooth.BluetoothActivity;
import com.app.syspoint.bluetooth.ConnectedThread;
import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.CobranzaBean;
import com.app.syspoint.db.bean.InventarioBean;
import com.app.syspoint.db.bean.InventarioHistorialBean;
import com.app.syspoint.db.bean.PartidasBean;
import com.app.syspoint.db.bean.PrinterBean;
import com.app.syspoint.db.bean.ProductoBean;
import com.app.syspoint.db.bean.VentasBean;
import com.app.syspoint.db.dao.ClienteDao;
import com.app.syspoint.db.dao.CobranzaDao;
import com.app.syspoint.db.dao.InventarioDao;
import com.app.syspoint.db.dao.InventarioHistorialDao;
import com.app.syspoint.db.dao.PrinterDao;
import com.app.syspoint.db.dao.ProductoDao;
import com.app.syspoint.db.dao.VentasDao;
import com.app.syspoint.http.ApiServices;
import com.app.syspoint.http.PointApi;
import com.app.syspoint.http.Servicio;
import com.app.syspoint.http.SincVentasByID;
import com.app.syspoint.json.Cliente;
import com.app.syspoint.json.ClienteJson;
import com.app.syspoint.json.Cobranza;
import com.app.syspoint.json.CobranzaJson;
import com.app.syspoint.templates.ViewPDFActivity;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ImprimeAbonoActivity extends AppCompatActivity {

    private final String TAG = ViewPDFActivity.class.getSimpleName();

    //Connection bluetooth
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private BluetoothAdapter mBTAdapter;
    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path


    private TextView textViewStatus;
    int venta;
    String clienteID;
    public String templateTicket;
    private boolean isConnectada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imprime_abono);
        initToolBar();
        textViewStatus = findViewById(R.id.tvStatusPrinterCobro);
        new loadAbonos().execute();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            templateTicket = bundle.getString("ticket");
            venta = Integer.parseInt(bundle.getString("cobranza"));
            clienteID = bundle.getString("clienteID");
        }

        //Sincroniza la venta con el servidor
        if (Utils.isNetworkAvailable(getApplication())) {
            syncCloudVenta(venta);
            sincronizaCliente(clienteID);
        }

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if (isConfigPrinter()) {
            if (!isBluetoothEnabled()) {
                //Pregunta si queremos activar el bluetooth
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            } else {
                initPrinter();
            }
        } else {
            Actividades.getSingleton(ImprimeAbonoActivity.this, BluetoothActivity.class).muestraActividad();
        }


        // Ask for location permission if not already allowed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        Button button = findViewById(R.id.btnConfirmaAbono);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.finishActivitiesFromStack();
                finish();
            }
        });

        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MESSAGE_READ) {
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    textViewStatus.setText(readMessage);
                }
                if (msg.what == CONNECTING_STATUS) {
                    if (msg.arg1 == 1) {
                        //textViewStatus.setTextColor(Color.GREEN);
                        textViewStatus.setText("Puede imprimir el documento dando click en la parte superior");
                    } else {
                        //textViewStatus.setTextColor(Color.RED);
                        textViewStatus.setText("¡Dispositivo Bluetooth no encontrado!");
                        initPrinter();
                    }
                }
            }
        };

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
                        Toast.makeText(ImprimeAbonoActivity.this, "Cobranza sincroniza", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<CobranzaJson> call, Throwable t) {

                }
            });
            return null;
        }
    }
    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_cobranza_imprime);
        toolbar.setTitle("Cobranza exitosa");
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
        menuInflater.inflate(R.menu.menu_abono_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_print_abono) {
            if (isConnectada == false) {
                initPrinter();
            }

            if (mConnectedThread != null) //First check to make sure thread created
                // mConnectedThread.printTicketVisita("Hola");
                mConnectedThread.write(templateTicket);


        } else if (id == android.R.id.home) {

            return true;
        } else if (id == R.id.action_settings_abono) {
            Actividades.getSingleton(ImprimeAbonoActivity.this, BluetoothActivity.class).muestraActividad();
        }

        return super.onOptionsItemSelected(item);
    }


    private void sincronizaCliente(String idCliente) {


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
            cliente.setLatitud(item.getLatitud());
            cliente.setLongitud(item.getLongitud());
            cliente.setPhone_contacto("" + item.getContacto_phone());
            cliente.setRecordatorio("" + item.getRecordatorio());
            cliente.setVisitas(item.getVisitasNoefectivas());
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

    private void initPrinter() {

        PrinterDao existeImpresora = new PrinterDao();

        int existe = existeImpresora.existeConfiguracionImpresora();

        if (existe > 0) {
            final PrinterBean establecida = existeImpresora.getImpresoraEstablecida();

            if (establecida != null) {
                isConnectada = true;

                if (establecida != null) {

                    if (!mBTAdapter.isEnabled()) {
                        Toast.makeText(getBaseContext(), "Bluetooth no encendido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    textViewStatus.setText("Conectado....");

                    // Spawn a new thread to avoid blocking the GUI one
                    new Thread() {
                        @Override
                        public void run() {
                            boolean fail = false;

                            BluetoothDevice device = mBTAdapter.getRemoteDevice(establecida.getAddress());

                            try {
                                mBTSocket = createBluetoothSocket(device);
                            } catch (IOException e) {
                                fail = true;
                                Toast.makeText(getBaseContext(), "Falló la creación de socket", Toast.LENGTH_SHORT).show();
                            }
                            // Establish the Bluetooth socket connection.
                            try {
                                mBTSocket.connect();
                            } catch (IOException e) {
                                try {
                                    fail = true;
                                    mBTSocket.close();
                                    mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                            .sendToTarget();
                                } catch (IOException e2) {
                                    //insert code to deal with this
                                    Toast.makeText(getBaseContext(), "Falló la creación de socket", Toast.LENGTH_SHORT).show();
                                }
                            }
                            if (!fail) {
                                mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                                mConnectedThread.start();

                                mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, establecida.getName())
                                        .sendToTarget();
                            }
                        }
                    }.start();
                }

            }
        } else {
            Actividades.getSingleton(ImprimeAbonoActivity.this, BluetoothActivity.class).muestraActividad();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e);
        }
        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }

    private boolean isConfigPrinter() {

        PrinterDao existeImpresora = new PrinterDao();
        int existe = existeImpresora.existeConfiguracionImpresora();

        if (existe > 0) {
            return true;
        }

        return false;
    }

    public boolean isBluetoothEnabled() {
        return mBTAdapter != null && mBTAdapter.isEnabled();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mConnectedThread != null)
            mConnectedThread.cancel();

    }

    private void syncCloudVenta(Integer venta) {

        try {
            final SincVentasByID sincVentasByID = new SincVentasByID(this, Long.parseLong(String.valueOf(venta)));

            sincVentasByID.setOnSuccess(new Servicio.ResponseOnSuccess() {
                @Override
                public void onSuccess(JSONArray response) throws JSONException {
                    Toast.makeText(ImprimeAbonoActivity.this, "Venta sincronizada exitosamente", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSuccessObject(JSONObject response) throws Exception {

                }
            });

            sincVentasByID.setOnError(new Servicio.ResponseOnError() {
                @Override
                public void onError(ANError error) {

                }

                @Override
                public void onError(String error) {

                }
            });

            sincVentasByID.postObject();

        } catch (Exception e) {

        }
    }

    //Actualiza las existencias del producto
    private void upadteExistencias() {

        final VentasDao ventasDao = new VentasDao();
        final VentasBean ventasBean = ventasDao.getVentaByInventario(venta);

        for (PartidasBean item : ventasBean.getListaPartidas()) {

            final ProductoDao productoDao = new ProductoDao();
            final ProductoBean productoBean = productoDao.getProductoByArticulo(item.getArticulo().getArticulo());

            if (productoBean != null) {
                productoBean.setExistencia(productoBean.getExistencia() - item.getCantidad());
                productoDao.save(productoBean);
            }

        }

    }

    private void addProductosInventori() {

        final VentasDao ventasDao = new VentasDao();
        final VentasBean ventasBean = ventasDao.getVentaByInventario(venta);

        //Contiene las partidas de la venta
        for (PartidasBean item : ventasBean.getListaPartidas()) {

            //Consultamos a la base de datos si existe el producto
            final ProductoDao productoDao = new ProductoDao();
            final ProductoBean productoBean = productoDao.getProductoByArticulo(item.getArticulo().getArticulo());

            //Si no existe en el inventario creamos el producto
            if (productoBean != null) {

                //Si existe entonces creamos el inser en estado PE
                InventarioDao inventarioDao = new InventarioDao();
                InventarioBean inventarioBean = inventarioDao.getProductoByArticulo(item.getArticulo().getArticulo());

                if (inventarioBean == null) {
                    InventarioBean bean = new InventarioBean();
                    InventarioDao dao = new InventarioDao();
                    bean.setArticulo(productoBean);
                    bean.setCantidad(0);
                    bean.setEstado("PE");
                    bean.setPrecio(item.getPrecio());
                    bean.setFecha(Utils.fechaActual());
                    bean.setHora(Utils.getHoraActual());
                    bean.setImpuesto(item.getImpuesto());
                    bean.setArticulo_clave(productoBean.getArticulo());
                    dao.insert(bean);

                    final InventarioHistorialDao inventarioHistorialDao = new InventarioHistorialDao();
                    final InventarioHistorialBean inventarioHistorialBean = inventarioHistorialDao.getInvatarioPorArticulo(productoBean.getArticulo());

                    if (inventarioHistorialBean != null) {
                        inventarioHistorialBean.setCantidad(inventarioHistorialBean.getCantidad() + item.getCantidad());
                        inventarioHistorialDao.save(inventarioHistorialBean);
                    } else {
                        final InventarioHistorialBean invBean = new InventarioHistorialBean();
                        final InventarioHistorialDao invDao = new InventarioHistorialDao();
                        invBean.setArticulo(productoBean);
                        invBean.setArticulo_clave(productoBean.getArticulo());
                        invBean.setCantidad(item.getCantidad());
                        invDao.insert(invBean);
                    }
                } else {
                    final InventarioHistorialDao inventarioHistorialDao = new InventarioHistorialDao();
                    final InventarioHistorialBean inventarioHistorialBean = inventarioHistorialDao.getInvatarioPorArticulo(productoBean.getArticulo());

                    if (inventarioHistorialBean != null) {
                        inventarioHistorialBean.setCantidad(inventarioHistorialBean.getCantidad() + item.getCantidad());
                        inventarioHistorialDao.save(inventarioHistorialBean);
                    } else {
                        final InventarioHistorialBean invBean = new InventarioHistorialBean();
                        final InventarioHistorialDao invDao = new InventarioHistorialDao();
                        invBean.setArticulo(productoBean);
                        invBean.setArticulo_clave(productoBean.getArticulo());
                        invBean.setCantidad(item.getCantidad());
                        invDao.insert(invBean);
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isAutomatico = BluetoothActivity.isPrimeraVez;

        if (isAutomatico) {
            if (isConfigPrinter()) {
                if (!isBluetoothEnabled()) {
                    //Pregunta si queremos activar el bluetooth
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                } else {
                    initPrinter();
                }
            } else {
                Actividades.getSingleton(ImprimeAbonoActivity.this, BluetoothActivity.class).muestraActividad();
            }
        }
    }
}