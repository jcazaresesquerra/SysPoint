package com.app.syspoint.ui.ventas;

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

import com.androidnetworking.error.ANError;
import com.app.syspoint.interactor.charge.ChargeInteractor;
import com.app.syspoint.interactor.charge.ChargeInteractorImp;
import com.app.syspoint.interactor.client.ClientInteractor;
import com.app.syspoint.interactor.client.ClientInteractorImp;
import com.app.syspoint.interactor.employee.GetEmployeeInteractor;
import com.app.syspoint.interactor.employee.GetEmployeesInteractorImp;
import com.app.syspoint.interactor.prices.PriceInteractor;
import com.app.syspoint.interactor.prices.PriceInteractorImp;
import com.app.syspoint.interactor.visit.VisitInteractor;
import com.app.syspoint.interactor.visit.VisitInteractorImp;
import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.R;
import com.app.syspoint.models.Employee;
import com.app.syspoint.models.Payment;
import com.app.syspoint.models.Price;
import com.app.syspoint.repository.objectBox.dao.ChargeDao;
import com.app.syspoint.repository.objectBox.dao.ClientDao;
import com.app.syspoint.repository.objectBox.dao.EmployeeDao;
import com.app.syspoint.repository.objectBox.dao.PrinterDao;
import com.app.syspoint.repository.objectBox.dao.RoutingDao;
import com.app.syspoint.repository.objectBox.dao.SpecialPricesDao;
import com.app.syspoint.repository.objectBox.dao.VisitsDao;
import com.app.syspoint.repository.objectBox.entities.ChargeBox;
import com.app.syspoint.repository.objectBox.entities.ClientBox;
import com.app.syspoint.repository.objectBox.entities.EmployeeBox;
import com.app.syspoint.repository.objectBox.entities.PrinterBox;
import com.app.syspoint.repository.objectBox.entities.RoutingBox;
import com.app.syspoint.repository.objectBox.entities.SpecialPricesBox;
import com.app.syspoint.repository.objectBox.entities.VisitsBox;
import com.app.syspoint.repository.request.http.Servicio;
import com.app.syspoint.repository.request.http.SincVentas;
import com.app.syspoint.ui.bluetooth.BluetoothActivity;
import com.app.syspoint.bluetooth.ConnectedThread;
import com.app.syspoint.repository.objectBox.AppBundle;
import com.app.syspoint.models.Client;
import com.app.syspoint.models.Visit;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.NetworkStateTask;
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
                new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                    if (connected) {
                        getClientsByRute();

                        saveVentas();
                        saveCobranza();
                        saveAbonos();
                        saveVisitas();
                        //saveClientes();
                        savePreciosEspeciales();
                    }
                }).execute(), 100);

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
                mConnectedThread.printTicketVisit(templateTicket, motivoVisita, vendedor, hora, fecha);


        } else if (id == android.R.id.home) {

            return true;
        }else if  (id ==  R.id.action_settings){
            Actividades.getSingleton(FinalizaPrecapturaActivity.this, BluetoothActivity.class).muestraActividad();

        }

        return super.onOptionsItemSelected(item);
    }


    private void sincronizaCliente(Long idCliente) {
        final ClientDao clientDao = new ClientDao();
        List<ClientBox> listaClientesDB = new ArrayList<>();
        listaClientesDB = clientDao.getByIDClient(idCliente);

        List<Client> listaClientes = new ArrayList<>();

        for (ClientBox item : listaClientesDB) {
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
            cliente.setPhone_contacto(""+item.getContacto_phone());
            cliente.setRecordatorio(""+item.getRecordatorio());
            cliente.setVisitas(item.getVisitasNoefectivas());
            listaClientes.add(cliente);
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
            final PrinterBox establecida = existeImpresora.getImpresoraEstablecida();

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

        final VisitsDao visitsDao = new VisitsDao();
        List<VisitsBox> visitBoxList =  visitsDao.getVisitsByCurrentDay(Utils.fechaActual());

        List<Visit> listaVisitas = new ArrayList<>();
        for (VisitsBox item : visitBoxList){
            Visit visita = new Visit();
            visita.setFecha(item.getFecha());
            visita.setHora(item.getHora());

            final ClientDao clientDao = new ClientDao();
            final ClientBox clientBox = clientDao.getClientByAccount(cuenta_cliente);
            visita.setCuenta(clientBox.getCuenta());
            visita.setLatidud(item.getLatidud());
            visita.setLongitud(item.getLongitud());
            visita.setMotivo_visita(item.getMotivo_visita());
            //Obtiene el nombre del vendedor
            EmployeeBox employeeBox = AppBundle.getUserBox();

            if (employeeBox == null) {
                employeeBox = new CacheInteractor().getSeller();
            }

            if (employeeBox != null) {
                visita.setIdentificador(employeeBox.getIdentificador());
            }

            listaVisitas.add(visita);
        }

        new VisitInteractorImp().executeSaveVisit(listaVisitas, new VisitInteractor.OnSaveVisitListener() {
            @Override
            public void onSaveVisitSuccess() {
                //Toast.makeText(getApplicationContext(), "Visita sincroniza", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveVisitError() {
                //Toast.makeText(getApplicationContext(), "Ha ocurrido un error al registrar la visita", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateCobranzas() {
        EmployeeBox employeeBox = AppBundle.getUserBox();

        if (employeeBox != null) {
            new ChargeInteractorImp().executeGetChargeByEmployee(employeeBox.getIdentificador(), new ChargeInteractor.OnGetChargeByEmployeeListener() {
                @Override
                public void onGetChargeByEmployeeSuccess(@NonNull List<ChargeBox> chargeByClientList) {
                    Log.d("SysPoint", "charge updated");
                }
                @Override
                public void onGetChargeByEmployeeError() {
                    Log.d("SysPoint", "error when charge update");
                }
            });
        }
    }

    private void getClientsByRute() {

        RoutingDao routingDao = new RoutingDao();
        RoutingBox routingBox = routingDao.getRutaEstablecida();

        if (routingBox != null) {
            EmployeeBox vendedoresBean = AppBundle.getUserBox();
            String ruta = routingBox.getRuta() != null && !routingBox.getRuta().isEmpty() ? routingBox.getRuta(): vendedoresBean.getRute();
            new ClientInteractorImp().executeGetAllClientsByDate(ruta, routingBox.getDia(), new ClientInteractor.GetAllClientsListener() {
                @Override
                public void onGetAllClientsSuccess(@NonNull List<ClientBox> clientList) {
                    saveClientes();
                }

                @Override
                public void onGetAllClientsError() {
                    saveClientes();
                    //loadRuta();
                    //Toast.makeText(requireActivity(), "Ha ocurrido un error. Conectate a internet para cambiar de ruta u obtener los clientes", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveVentas() {
        try {
            final SincVentas sincVentas = new SincVentas();

            sincVentas.setOnSuccess(new Servicio.ResponseOnSuccess() {
                @Override
                public void onSuccess(JSONArray response) throws JSONException {
                }

                @Override
                public void onSuccessObject(JSONObject response) throws Exception {

                }
            });

            sincVentas.setOnError(new Servicio.ResponseOnError() {
                @Override
                public void onError(ANError error) {

                }

                @Override
                public void onError(String error) {

                }
            });

            sincVentas.postObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveVisitas() {

        final VisitsDao visitsDao = new VisitsDao();
        List<VisitsBox> visitsBoxList = visitsDao.getVisitsByCurrentDay(Utils.fechaActual());
        final ClientDao clientDao = new ClientDao();
        EmployeeBox employeeBox = AppBundle.getUserBox();

        if (employeeBox == null) {
            employeeBox = new CacheInteractor().getSeller();
        }

        List<Visit> visitList = new ArrayList<>();
        for (VisitsBox item : visitsBoxList) {
            Visit visita = new Visit();
            visita.setFecha(item.getFecha());
            visita.setHora(item.getHora());
            final ClientBox clienteBean = clientDao.getClientByAccount(item.getCliente().getTarget().getCuenta());
            visita.setCuenta(clienteBean.getCuenta());
            visita.setLatidud(item.getLatidud());
            visita.setLongitud(item.getLongitud());
            visita.setMotivo_visita(item.getMotivo_visita());
            if (employeeBox != null) {
                visita.setIdentificador(employeeBox.getIdentificador());
            } else {
                Log.e(TAG, "employeeBox is null");
            }

            visitList.add(visita);
        }

        new VisitInteractorImp().executeSaveVisit(visitList, new VisitInteractor.OnSaveVisitListener() {
            @Override
            public void onSaveVisitSuccess() {
                //Toast.makeText(requireActivity(), "Visita registrada correctamente", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveVisitError() {
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al registrar la visita", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveCobranza() {
        ChargeDao chargeDao = new ChargeDao();
        List<ChargeBox> cobranzaList = chargeDao.getCobranzaFechaActual(Utils.fechaActual());

        List<Payment> listaCobranza = new ArrayList<>();
        for (ChargeBox item : cobranzaList) {
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
                //Toast.makeText(requireActivity(), "Cobranza guardada correctamente", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveChargeError() {
                //Toast.makeText(requireActivity(), "Ha ocurrido un problema al guardar la cobranza", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void saveAbonos() {

        ChargeDao chargeDao = new ChargeDao();
        List<ChargeBox> cobranzaList = chargeDao.getAbonosFechaActual(Utils.fechaActual());
        List<Payment> listaCobranza = new ArrayList<>();

        for (ChargeBox item : cobranzaList) {
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
            cobranza.setUpdatedAt(item.getUpdatedAt());
            listaCobranza.add(cobranza);
        }

        new ChargeInteractorImp().executeUpdateCharge(listaCobranza, new ChargeInteractor.OnUpdateChargeListener() {
            @Override
            public void onUpdateChargeSuccess() {
                //Toast.makeText(requireActivity(), "Cobranza actualizada correctamente", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onUpdateChargeError() {
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al actualizar la cobranza", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void savePreciosEspeciales() {

        final SpecialPricesDao dao = new SpecialPricesDao();

        List<SpecialPricesBox> listaDB = dao.getPreciosBydate(Utils.fechaActual());


        //Contiene la lista de lo que se envia al servidor
        final List<Price> listaPreciosServidor = new ArrayList<>();

        //Contien la lista de precios especiales locales
        for (SpecialPricesBox items : listaDB) {

            final Price precio = new Price();
            if (items.getActive()) {
                precio.setActive(1);
            } else {
                precio.setActive(0);
            }

            precio.setArticulo(items.getArticulo());
            precio.setCliente(items.getCliente());
            precio.setPrecio(items.getPrecio());
            listaPreciosServidor.add(precio);

        }

        new PriceInteractorImp().executeSendPrices(listaPreciosServidor, new PriceInteractor.SendPricesListener() {
            @Override
            public void onSendPricesSuccess() {
                //Toast.makeText(requireActivity(), "Sincronizacion de lista de precios exitosa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSendPricesError() {
                //Toast.makeText(requireActivity(), "Error al sincronizar la lista de precios intente mas tarde", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void saveClientes() {

        final ClientDao clientDao = new ClientDao();
        List<ClientBox> clientListDB = clientDao.getClientsByDay(Utils.fechaActual());

        List<Client> clientList = new ArrayList<>();

        for (ClientBox item : clientListDB) {
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
            client.setPhone_contacto("" + item.getContacto_phone());
            client.setRecordatorio("" + item.getRecordatorio());
            client.setVisitas(item.getVisitasNoefectivas());
            if (item.isCredito()) {
                client.setCredito(1);
            } else {
                client.setCredito(0);
            }
            client.setSaldo_credito(item.getSaldo_credito());
            client.setLimite_credito(item.getLimite_credito());
            if (item.getMatriz() == null || (item.getMatriz() != null && item.getMatriz().equals("null"))) {
                client.setMatriz("null");
            } else {
                client.setMatriz(item.getMatriz());
            }
            client.setUpdatedAt(item.getUpdatedAt());

            clientList.add(client);
        }

        new ClientInteractorImp().executeSaveClient(clientList, new ClientInteractor.SaveClientListener() {
            @Override
            public void onSaveClientSuccess() {
                //Toast.makeText(requireActivity(), "Sincronizacion de clientes exitosa", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveClientError() {
                //Toast.makeText(requireActivity(), "Ha ocurrido un error al sincronizar los clientes", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void testLoadEmpleado(Long id){
        final EmployeeDao employeeDao = new EmployeeDao();
        List<EmployeeBox> listaEmpleadosDB = new ArrayList<>();
        listaEmpleadosDB =  employeeDao.getEmployeeById(id);

        List<Employee> listEmpleados = new ArrayList<>();
        for (EmployeeBox item : listaEmpleadosDB){
            Employee empleado = new Employee();
            empleado.setNombre(item.getNombre());
            if (item.getDireccion().isEmpty()){
                empleado.setDireccion("-");
            }else{
                empleado.setDireccion(item.getDireccion());
            }
            empleado.setEmail(item.getEmail());
            if (item.getTelefono().isEmpty()){
                empleado.setTelefono("-");
            }else{
                empleado.setTelefono(item.getTelefono());
            }

            if (item.getFecha_nacimiento().isEmpty()){
                empleado.setFechaNacimiento("-");
            }else{
                empleado.setFechaNacimiento(item.getFecha_nacimiento());
            }

            if (item.getFecha_ingreso().isEmpty()){
                empleado.setFechaIngreso("-");
            }else{
                empleado.setFechaIngreso(item.getFecha_ingreso());
            }

            empleado.setContrasenia(item.getContrasenia());
            empleado.setIdentificador(item.getIdentificador());
            empleado.setStatus(item.getStatus()? 1 : 0);

            if (item.getPath_image() == null || item.getPath_image().isEmpty()){
                empleado.setPathImage("");
            }else {
                empleado.setPathImage(item.getPath_image());
            }

            if (!item.getRute().isEmpty()) {
                empleado.setRute(item.getRute());
            } else  {
                empleado.setRute("");
            }

            listEmpleados.add(empleado);
        }

        new GetEmployeesInteractorImp().executeSaveEmployees(listEmpleados, new GetEmployeeInteractor.SaveEmployeeListener() {
            @Override
            public void onSaveEmployeeSuccess() {
                //progresshide();
                //Toast.makeText(ActualizarEmpleadoActivity.this, "Empleados sincronizados", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSaveEmployeeError() {
                //progresshide();
                //Toast.makeText(ActualizarEmpleadoActivity.this, "Ha ocurrido un error al sincronizar los empleados", Toast.LENGTH_LONG).show();
            }
        });
    }

}