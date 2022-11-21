package com.app.syspoint.ui.templates;

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

import com.androidnetworking.error.ANError;
import com.app.syspoint.interactor.charge.ChargeInteractor;
import com.app.syspoint.interactor.charge.ChargeInteractorImp;
import com.app.syspoint.interactor.client.ClientInteractor;
import com.app.syspoint.interactor.client.ClientInteractorImp;
import com.app.syspoint.R;
import com.app.syspoint.ui.bluetooth.BluetoothActivity;
import com.app.syspoint.bluetooth.ConnectedThread;
import com.app.syspoint.repository.database.bean.ClienteBean;
import com.app.syspoint.repository.database.bean.CobranzaBean;
import com.app.syspoint.repository.database.bean.InventarioBean;
import com.app.syspoint.repository.database.bean.InventarioHistorialBean;
import com.app.syspoint.repository.database.bean.PartidasBean;
import com.app.syspoint.repository.database.bean.PrinterBean;
import com.app.syspoint.repository.database.bean.ProductoBean;
import com.app.syspoint.repository.database.bean.VentasBean;
import com.app.syspoint.repository.database.dao.ClientDao;
import com.app.syspoint.repository.database.dao.PaymentDao;
import com.app.syspoint.repository.database.dao.StockDao;
import com.app.syspoint.repository.database.dao.StockHistoryDao;
import com.app.syspoint.repository.database.dao.PrinterDao;
import com.app.syspoint.repository.database.dao.ProductDao;
import com.app.syspoint.repository.database.dao.SellsDao;
import com.app.syspoint.repository.request.http.Servicio;
import com.app.syspoint.repository.request.http.SincVentasByID;
import com.app.syspoint.models.Client;
import com.app.syspoint.models.Payment;
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
    int venta;
    String clienteID;
    public String templateTicket;
    private boolean isConnectada = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        initToolBar();


        textViewStatus = findViewById(R.id.tvStatusPrinter);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            templateTicket = bundle.getString("ticket");
            venta = Integer.parseInt( bundle.getString("venta"));
            clienteID = bundle.getString("clienteID");
        }

        addProductosInventori();
        upadteExistencias();


        ClientDao clientDao = new ClientDao();
        ClienteBean clienteBean = clientDao.getClient(String.valueOf(clienteID));

        if (clienteBean != null){
            clienteBean.setVisitado(1);
            clienteBean.setVisitasNoefectivas(0);
            clienteBean.setDate_sync(Utils.fechaActual());
            clientDao.save(clienteBean);

            PaymentDao paymentDao = new PaymentDao();
            try {
                double saldoCliente = paymentDao.getTotalSaldoDocumentosCliente(clienteBean.getCuenta());
                clienteBean.setSaldo_credito(saldoCliente);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Sincroniza la venta con el servidor
        if (Utils.isNetworkAvailable(getApplication())){
            syncCloudVenta(venta);
            sincronizaCliente(clienteID);
            loadCobranza();
        }

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
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.finishActivitiesFromStack();
                finish();
            }
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


    public void loadCobranza() {

            final PaymentDao paymentDao = new PaymentDao();
            List<CobranzaBean> cobranzaBeanList = new ArrayList<>();
            cobranzaBeanList = paymentDao.getCobranzaFechaActual(Utils.fechaActual());

            List<Payment> listaCobranza = new ArrayList<>();
            for (CobranzaBean item : cobranzaBeanList) {
                Payment cobranza = new Payment();
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

            new ChargeInteractorImp().executeSaveCharge(listaCobranza, new ChargeInteractor.OnSaveChargeListener() {
                @Override
                public void onSaveChargeSuccess() {
                    //Toast.makeText(getApplicationContext(), "Cobranza guardada correctamente", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onSaveChargeError() {
                    //Toast.makeText(getApplicationContext(), "Ha ocurrido un problema al guardar la cobranza", Toast.LENGTH_LONG).show();
                }
            });
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


    private void sincronizaCliente(String idCliente) {



        final ClientDao clientDao = new ClientDao();
        List<ClienteBean> listaClientesDB = new ArrayList<>();
        listaClientesDB = clientDao.getByIDClient(idCliente);

        List<Client> listaClientes = new ArrayList<>();

        for (ClienteBean item : listaClientesDB) {
            Client client = new Client();
            client.setNombreComercial(item.getNombre_comercial());
            client.setCalle(item.getCalle());
            client.setNumero(item.getNumero());
            client.setColonia(item.getColonia());
            client.setCiudad(item.getCiudad());
            client.setCodigoPostal(item.getCodigo_postal());
            client.setFechaRegistro(item.getFecha_registro());
            client.setCuenta(item.getCuenta());
            client.setStatus(item.getStatus()? 1 : 0);
            client.setConsec(item.getConsec());
            client.setRango(item.getRango());
            client.setLun(item.getLun());
            client.setMar(item.getMar());
            client.setMie(item.getMie());
            client.setJue(item.getJue());
            client.setVie(item.getVie());
            client.setSab(item.getSab());
            client.setDom(item.getDom());
            client.setLatitud(item.getLatitud());
            client.setLongitud(item.getLongitud());
            client.setPhone_contacto(""+item.getContacto_phone());
            client.setRecordatorio(""+item.getRecordatorio());
            client.setVisitas(item.getVisitasNoefectivas());
            if (item.getMatriz()== "null" && item.getMatriz() == null && item.getMatriz().length() == 0) {
                client.setMatriz("");
            }else{
                client.setMatriz(item.getMatriz());
            }
            listaClientes.add(client);
        }

        new ClientInteractorImp().executeSaveClient(listaClientes, new ClientInteractor.SaveClientListener() {
            @Override
            public void onSaveClientSuccess() {
                //Toast.makeText(getApplicationContext(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveClientError() {
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mConnectedThread != null)
            mConnectedThread.cancel();

    }

    private void syncCloudVenta(Integer venta){

        try{
            final SincVentasByID sincVentasByID = new SincVentasByID(Long.parseLong(String.valueOf(venta)));

            sincVentasByID.setOnSuccess(new Servicio.ResponseOnSuccess() {
                @Override
                public void onSuccess(JSONArray response) throws JSONException {
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

        }catch (Exception e){

        }
    }

    //Actualiza las existencias del producto
    private void upadteExistencias(){

        final SellsDao sellsDao = new SellsDao();
        final VentasBean ventasBean = sellsDao.getVentaByInventario(venta);

        for (PartidasBean item : ventasBean.getListaPartidas()){

            final ProductDao productDao = new ProductDao();
            final ProductoBean productoBean = productDao.getProductoByArticulo(item.getArticulo().getArticulo());

            if (productoBean != null){
                productoBean.setExistencia(productoBean.getExistencia() - item.getCantidad());
                productDao.save(productoBean);
            }

        }

    }
    private void addProductosInventori(){

        final SellsDao sellsDao = new SellsDao();
        final VentasBean ventasBean = sellsDao.getVentaByInventario(venta);

        if (ventasBean == null) {
            Log.d("SysPoint", "Ha ocurrido un error, intente nuevamente addProductosInventori");
            return;
        }

        //Contiene las partidas de la venta
        for (PartidasBean item : ventasBean.getListaPartidas()){

            //Consultamos a la base de datos si existe el producto
            final ProductDao productDao = new ProductDao();
            final ProductoBean productoBean = productDao.getProductoByArticulo(item.getArticulo().getArticulo());

            //Si no existe en el inventario creamos el producto
            if (productoBean != null){

                //Si existe entonces creamos el inser en estado PE
                StockDao stockDao = new StockDao();
                InventarioBean inventarioBean = stockDao.getProductoByArticulo(item.getArticulo().getArticulo());

                //Si no existe se deja como pendiente
                if (inventarioBean == null){
                    InventarioBean bean = new InventarioBean();
                    StockDao dao = new StockDao();
                    bean.setArticulo(productoBean);
                    bean.setCantidad(0);
                    bean.setEstado("PE");
                    bean.setPrecio(item.getPrecio());
                    bean.setFecha(Utils.fechaActual());
                    bean.setHora(Utils.getHoraActual());
                    bean.setImpuesto(item.getImpuesto());
                    bean.setArticulo_clave(productoBean.getArticulo());
                    dao.insert(bean);

                    final StockHistoryDao stockHistoryDao = new StockHistoryDao();
                    final InventarioHistorialBean inventarioHistorialBean = stockHistoryDao.getInvatarioPorArticulo(productoBean.getArticulo());

                    if (inventarioHistorialBean != null){
                        inventarioHistorialBean.setCantidad(inventarioHistorialBean.getCantidad() + item.getCantidad());
                        stockHistoryDao.save(inventarioHistorialBean);
                    }else {
                        final InventarioHistorialBean invBean = new InventarioHistorialBean();
                        final StockHistoryDao invDao = new StockHistoryDao();
                        invBean.setArticulo(productoBean);
                        invBean.setArticulo_clave(productoBean.getArticulo());
                        invBean.setCantidad(item.getCantidad());
                        invDao.insert(invBean);
                    }
                }else {
                    //Si existe entonces actualizamos los datos
                    final StockHistoryDao stockHistoryDao = new StockHistoryDao();
                    final InventarioHistorialBean inventarioHistorialBean = stockHistoryDao.getInvatarioPorArticulo(productoBean.getArticulo());

                    //Si existe entonces actualizamos las cantidades
                    if (inventarioHistorialBean != null){
                        inventarioHistorialBean.setCantidad(inventarioHistorialBean.getCantidad() + item.getCantidad());
                        stockHistoryDao.save(inventarioHistorialBean);
                    }else {
                        //Creamos el historial del Inventario
                        final InventarioHistorialBean invBean = new InventarioHistorialBean();
                        final StockHistoryDao invDao = new StockHistoryDao();
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
                Actividades.getSingleton(ViewPDFActivity.this, BluetoothActivity.class).muestraActividad();
            }
        }
    }
}