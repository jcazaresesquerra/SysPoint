package com.app.syspoint.ui.ventas;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.google.gson.Gson;
import com.app.syspoint.R;
import com.app.syspoint.bluetooth.BluetoothActivity;
import com.app.syspoint.bluetooth.ConnectedThread;
import com.app.syspoint.db.bean.AppBundle;
import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.bean.PrinterBean;
import com.app.syspoint.db.bean.VisitasBean;
import com.app.syspoint.db.dao.ClienteDao;
import com.app.syspoint.db.dao.PrinterDao;
import com.app.syspoint.db.dao.VisitasDao;
import com.app.syspoint.http.ApiServices;
import com.app.syspoint.http.PointApi;
import com.app.syspoint.json.Cliente;
import com.app.syspoint.json.ClienteJson;
import com.app.syspoint.json.Visita;
import com.app.syspoint.json.VisitaJson;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinalizaPrecapturaActivity extends AppCompatActivity {



    private final String TAG = FinalizaPrecapturaActivity.class.getSimpleName();

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
    public String templateTicket;
    private String motivoVisita;
    private String vendedor;
    private String hora;
    private String fecha;
    private String latitud;
    private String longitud;
    private String cuenta_cliente;
    private boolean isConnectada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finaliza_precaptura);

        initToolBar();


        textViewStatus = findViewById(R.id.tvStatusPrinterVisita);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            templateTicket = bundle.getString(Actividades.PARAM_1);
            motivoVisita = bundle.getString(Actividades.PARAM_2);
            vendedor = bundle.getString(Actividades.PARAM_3);
            hora = bundle.getString(Actividades.PARAM_4);
            fecha = bundle.getString(Actividades.PARAM_5);
            latitud = bundle.getString(Actividades.PARAM_6);
            longitud = bundle.getString(Actividades.PARAM_7);
            cuenta_cliente = bundle.getString(Actividades.PARAM_8);


        }


        Button button = findViewById(R.id.btnConfirmaVisita);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.finishActivitiesFromStack();
                finish();
            }
        });

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();

        if (isConfigPrinter()) {
            if (!isBluetoothEnabled()) {
                //Pregunta si queremos activar el bluetooth
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }else {
                initPrinter();
            }
        }else {
            Actividades.getSingleton(FinalizaPrecapturaActivity.this, BluetoothActivity.class).muestraActividad();
        }


        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);



        mHandler = new Handler(Looper.myLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try{
                        readMessage = new String((byte[]) msg.obj, "UTF-8" );
                    }catch (UnsupportedEncodingException e){
                        e.printStackTrace();
                    }
                    textViewStatus.setText(readMessage);
                }
                if (msg.what == CONNECTING_STATUS){
                    if (msg.arg1 == 1){
                        //textViewStatus.setTextColor(Color.GREEN);
                        textViewStatus.setText("Puede imprimir el documento dando click en la parte superior");
                    }else {
                        //textViewStatus.setTextColor(Color.RED);
                        textViewStatus.setText("¡Dispositivo Bluetooth no encontrado!");
                        initPrinter();
                    }
                }
            }
        };
    }

    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_finaliza_visita);
        toolbar.setTitle("Visita exitosa");
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
        menuInflater.inflate(R.menu.menu_pdf_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_print) {
            if (isConnectada == false) {
                initPrinter();
            }

            if(mConnectedThread != null) //First check to make sure thread created
                // mConnectedThread.printTicketVisita("Hola");
                mConnectedThread.printTicketVisita(templateTicket, motivoVisita, vendedor, hora, fecha);


        } else if (id == android.R.id.home) {

            return true;
        }else if  (id ==  R.id.action_settings){
            Actividades.getSingleton(FinalizaPrecapturaActivity.this, BluetoothActivity.class).muestraActividad();

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
            cliente.setPhone_contacto(""+item.getContacto_phone());
            cliente.setRecordatorio(""+item.getRecordatorio());
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

                    if(!mBTAdapter.isEnabled()) {
                        Toast.makeText(getBaseContext(), "Bluetooth no encendido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    textViewStatus.setText("Conectando....");

                    // Spawn a new thread to avoid blocking the GUI one
                    new Thread()
                    {
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
                            if(!fail) {
                                mConnectedThread = new ConnectedThread(mBTSocket, mHandler);
                                mConnectedThread.start();

                                mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, establecida.getName())
                                        .sendToTarget();
                            }
                        }
                    }.start();
                }

            }
        }else {
            Actividades.getSingleton(FinalizaPrecapturaActivity.this, BluetoothActivity.class).muestraActividad();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
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
        if(mConnectedThread != null)
            mConnectedThread.cancel();

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
                Actividades.getSingleton(FinalizaPrecapturaActivity.this, BluetoothActivity.class).muestraActividad();
            }
        }
    }

    private void loadVisitas(){

        final VisitasDao visitasDao = new VisitasDao();
        List<VisitasBean> visitasBeanListBean = new ArrayList<>();
        visitasBeanListBean =  visitasDao.getAllVisitasFechaActual(Utils.fechaActual());

        List<Visita> listaVisitas = new ArrayList<>();
        for (VisitasBean item : visitasBeanListBean){
            Visita visita = new Visita();
            visita.setFecha(item.getFecha());
            visita.setHora(item.getHora());

            final ClienteDao clienteDao = new ClienteDao();
            final ClienteBean clienteBean = clienteDao.getClienteByCuenta(cuenta_cliente);
            visita.setCuenta(clienteBean.getCuenta());
            visita.setLatidud(item.getLatidud());
            visita.setLongitud(item.getLongitud());
            visita.setMotivo_visita(item.getMotivo_visita());
            //Obtiene el nombre del vendedor
            final EmpleadoBean vendedoresBean = AppBundle.getUserBean();
            visita.setIdentificador(vendedoresBean.getIdentificador());

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
                    //Toast.makeText(FinalizaPrecapturaActivity.this, "Visita Sincronizada", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<VisitaJson> call, Throwable t) {

            }
        });
    }

}