package com.app.syspoint.ui.stock.activities;

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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.app.syspoint.R;
import com.app.syspoint.bluetooth.ConnectedThread;
import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.repository.cache.SharedPreferencesManager;
import com.app.syspoint.repository.objectBox.dao.PrinterDao;
import com.app.syspoint.repository.objectBox.dao.ProductDao;
import com.app.syspoint.repository.objectBox.dao.StockDao;
import com.app.syspoint.repository.objectBox.dao.StockHistoryDao;
import com.app.syspoint.repository.objectBox.entities.PrinterBox;
import com.app.syspoint.repository.objectBox.entities.ProductBox;
import com.app.syspoint.repository.objectBox.entities.StockBox;
import com.app.syspoint.repository.objectBox.entities.StockHistoryBox;
import com.app.syspoint.ui.bluetooth.BluetoothActivity;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

import timber.log.Timber;

public class FinalizaInventarioActivity extends AppCompatActivity {



    private final String TAG = FinalizaInventarioActivity.class.getSimpleName();

    //Connection bluetooth
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
    private TextView textViewStatus;

    private BluetoothAdapter mBTAdapter;
    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path
    private boolean isConnectada = false;
    private String templateTicket;
    Button btnConfirmaInventario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finaliza_inventario);
        initToolBar();
        textViewStatus = findViewById(R.id.tvStatusPrinterInventario);
        btnConfirmaInventario = findViewById(R.id.btnConfirmaInventario);

        btnConfirmaInventario.setOnClickListener(v -> {
            Timber.tag(TAG).d("btnConfirmaInventario -> click");

            int loadId = new CacheInteractor().getCurrentLoadId() + 1;
            new CacheInteractor().setLoadId(loadId);
            Utils.finishActivitiesFromStack();
            finish();
        });

        mBTAdapter = BluetoothAdapter.getDefaultAdapter();


        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            templateTicket = bundle.getString("ticket");

        }

        if (isConfigPrinter()) {
            if (!isBluetoothEnabled()) {
                //Pregunta si queremos activar el bluetooth
                Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetooth, 0);
            }else { initPrinter();
            }
        }else {
            Actividades.getSingleton(FinalizaInventarioActivity.this, BluetoothActivity.class).muestraActividad();
        }


        // Ask for location permission if not already allowed //esto lo comento para verificar si el ticket ya no suele salir grande
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

        Toolbar toolbar = findViewById(R.id.toolbar_finaliza_inventario);
        toolbar.setTitle("Inventario exitosa");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.purple_700));

        aplicaInventario();

    }

    private void aplicaInventario(){

        List<StockBox> mData = new StockDao().list();
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(this);
        int currentStockId = sharedPreferencesManager.getCurrentStockId();
        int currentLoadId = sharedPreferencesManager.getCurrentLoadId();

        for (StockBox item : mData){

            final ProductDao productDao = new ProductDao();
            final ProductBox productoBean = productDao.getProductoByArticulo(item.getArticulo().getTarget().getArticulo());

            if (productoBean != null){

                //Actualiza la existencia del articulo
                StockHistoryDao stockHistoryDao = new StockHistoryDao();
                StockHistoryBox inventarioHistorialBean =
                        stockHistoryDao.getInvatarioPorArticulo(item.articulo.getTarget().getArticulo());

                int vendido = inventarioHistorialBean != null ? inventarioHistorialBean.getCantidad() : 0;
                int inicial = item.getTotalCantidad();
                int total = inicial - vendido;

                productoBean.setExistencia(total);
                productDao.insertBox(productoBean);

                //Cambia el status a confirmado
                final StockDao stockDao = new StockDao();
                final StockBox inventarioBean = stockDao.getProductoByArticulo(productoBean.getArticulo());
                inventarioBean.setEstado("CO");
                stockDao.insertBox(inventarioBean);

            }
        }
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

                    textViewStatus.setText("Espere un momento...");

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
            Actividades.getSingleton(FinalizaInventarioActivity.this, BluetoothActivity.class).muestraActividad();
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
                Actividades.getSingleton(FinalizaInventarioActivity.this, BluetoothActivity.class).muestraActividad();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_termina_inventario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.imprime_inventario) {
            Timber.tag(TAG).d("print stock -> click");

            if (isConnectada == false) {
                initPrinter();
            }

            if(mConnectedThread != null) //First check to make sure thread created
                mConnectedThread.write(templateTicket);

        } else if (id == android.R.id.home) {
            Timber.tag(TAG).d("home -> click");
            return false;
        }  else if  (id ==  R.id.config_printer){
            Timber.tag(TAG).d("config printer -> click");
            Actividades.getSingleton(FinalizaInventarioActivity.this, BluetoothActivity.class).muestraActividad();
        }
        return super.onOptionsItemSelected(item);
    }

}