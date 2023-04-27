package com.app.syspoint.ui.templates;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.lifecycle.ViewModelProvider;

import com.app.syspoint.R;
import com.app.syspoint.repository.objectBox.dao.ChargeDao;
import com.app.syspoint.repository.objectBox.dao.ClientDao;
import com.app.syspoint.repository.objectBox.dao.PrinterDao;
import com.app.syspoint.repository.objectBox.entities.ClientBox;
import com.app.syspoint.repository.objectBox.entities.PrinterBox;
import com.app.syspoint.ui.bluetooth.BluetoothActivity;
import com.app.syspoint.bluetooth.ConnectedThread;
import com.app.syspoint.ui.cobranza.CobranzaActivity;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.NetworkStateTask;
import com.app.syspoint.utils.Utils;
import com.app.syspoint.viewmodel.viewPDF.ViewPDFViewModel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.UUID;


public class ViewPDFActivity extends AppCompatActivity {

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
    Long venta;
    Long clienteID;
    String account;
    public String templateTicket;
    private boolean isConnectada = false;

    private ViewPDFViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        initToolBar();

        viewModel = new ViewModelProvider(this).get(ViewPDFViewModel.class);

        textViewStatus = findViewById(R.id.tvStatusPrinter);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            templateTicket = bundle.getString("ticket");
            venta = bundle.getLong("venta", 0);
            clienteID = bundle.getLong("clienteID");
            account = bundle.getString("account");
        }

        viewModel.addProductosInventori(venta);

        ClientDao clientDao = new ClientDao();
        ClientBox clienteBean = clientDao.getClientByAccount(String.valueOf(account));

        if (clienteBean != null){
            clienteBean.setVisitado(1);
            clienteBean.setVisitasNoefectivas(0);
            clienteBean.setDate_sync(Utils.fechaActual());
            clientDao.insertBox(clienteBean);

            ChargeDao chargeDao = new ChargeDao();
            try {
                double saldoCliente = chargeDao.getSaldoByCliente(clienteBean.getCuenta());

                float saldoClient = (float) saldoCliente;

                if (clienteBean.getMatriz() == null || clienteBean.getMatriz().isEmpty() || clienteBean.getMatriz() == "null") {
                    saldoClient = (float) clienteBean.getSaldo_credito();
                } else {
                    ClientBox clientMatriz = clientDao.getClientByAccount(clienteBean.getMatriz());
                    if (clientMatriz != null) {
                        saldoClient = (float) clientMatriz.getSaldo_credito();
                    }
                }

                Button buttonDoCharge = findViewById(R.id.btnCobranza);
                if (saldoClient > 0) {
                    buttonDoCharge.setVisibility(View.VISIBLE);
                    buttonDoCharge.setText(getString(R.string.do_charge_text_button, saldoClient));

                    buttonDoCharge.setOnClickListener(v -> {
                        Utils.finishActivitiesFromStack();
                        finish();
                        HashMap<String, String> parametros = new HashMap<>();
                        if (clienteBean.getMatriz() == null || clienteBean.getMatriz().isEmpty() || clienteBean.getMatriz() == "null") {
                            parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                        } else {
                            ClientBox clientMatriz = clientDao.getClientByAccount(clienteBean.getMatriz());
                            if (clientMatriz != null) {
                                parametros.put(Actividades.PARAM_1, clientMatriz.getCuenta());
                            } else {
                                parametros.put(Actividades.PARAM_1, clienteBean.getCuenta());
                            }
                        }
                        Actividades.getSingleton(this, CobranzaActivity.class).muestraActividad(parametros);
                    });
                }
                else buttonDoCharge.setVisibility(View.GONE);

                clienteBean.setSaldo_credito(saldoCliente);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Sincroniza la venta con el servidor
        new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
            if (connected) {
               viewModel.sync(venta, clienteID);
            }
        }).execute(), 100);

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
            Actividades.getSingleton(ViewPDFActivity.this, BluetoothActivity.class).muestraActividad();
        }


        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        Button button = findViewById(R.id.btnConfirmaVenta);
        button.setOnClickListener(v -> {
            Utils.finishActivitiesFromStack();
            finish();
        });

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
                Actividades.getSingleton(ViewPDFActivity.this, BluetoothActivity.class).muestraActividad();
            }
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    private void initToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar_preview);
        toolbar.setTitle("Venta exitosa");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));

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
                mConnectedThread.write(templateTicket);


        } else if (id == android.R.id.home) {

            return true;
        }else if  (id ==  R.id.action_settings){
            Actividades.getSingleton(ViewPDFActivity.this, BluetoothActivity.class).muestraActividad();
        }

        return super.onOptionsItemSelected(item);
    }

    private void initPrinter() {

        PrinterDao existeImpresora = new PrinterDao();

        int existe = existeImpresora.existeConfiguracionImpresora();

        if (existe > 0) {
            final PrinterBox establecida = existeImpresora.getImpresoraEstablecida();

            if (establecida != null) {
                isConnectada = true;

                if (establecida != null) {

                    if(!mBTAdapter.isEnabled()) {
                        Toast.makeText(getBaseContext(), "Bluetooth no encendido", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    textViewStatus.setText("Conectado....");

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
            Actividades.getSingleton(ViewPDFActivity.this, BluetoothActivity.class).muestraActividad();
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

        return existe > 0;
    }

    public boolean isBluetoothEnabled() {
        return mBTAdapter != null && mBTAdapter.isEnabled();
    }
}