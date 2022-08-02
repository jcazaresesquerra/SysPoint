package com.app.syspoint.ui.ventas;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.error.ANError;
import com.google.gson.Gson;
import com.app.syspoint.R;
import com.app.syspoint.bluetooth.BluetoothActivity;
import com.app.syspoint.bluetooth.ConnectedThread;
import com.app.syspoint.db.bean.AppBundle;
import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.CobranzaBean;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.bean.InventarioHistorialBean;
import com.app.syspoint.db.bean.PartidasBean;
import com.app.syspoint.db.bean.PrinterBean;
import com.app.syspoint.db.bean.ProductoBean;
import com.app.syspoint.db.bean.RolesBean;
import com.app.syspoint.db.bean.VentasBean;
import com.app.syspoint.db.dao.ClienteDao;
import com.app.syspoint.db.dao.CobranzaDao;
import com.app.syspoint.db.dao.InventarioHistorialDao;
import com.app.syspoint.db.dao.PrinterDao;
import com.app.syspoint.db.dao.ProductoDao;
import com.app.syspoint.db.dao.RolesDao;
import com.app.syspoint.db.dao.VentasDao;
import com.app.syspoint.domentos.TicketVenta;
import com.app.syspoint.http.ApiServices;
import com.app.syspoint.http.PointApi;
import com.app.syspoint.http.Servicio;
import com.app.syspoint.http.SincVentasByID;
import com.app.syspoint.json.Cliente;
import com.app.syspoint.json.ClienteJson;
import com.app.syspoint.json.Cobranza;
import com.app.syspoint.json.CobranzaJson;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListaVentasFragment extends Fragment {

    protected static final String TAG = "TAG";

    //Connection bluetooth
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;

    private Handler mHandler; // Our main handler that will receive callback notifications
    private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private List<VentasBean> mData;
    private AdapterListaVentas mAdapter;
    private LinearLayout lyt_empleados;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_lista_ventas, container, false);

        setHasOptionsMenu(true);

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
            Actividades.getSingleton(getActivity(), BluetoothActivity.class).muestraActividad();
        }


        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


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
                    //textViewStatus.setText(readMessage);
                }
                if (msg.what == CONNECTING_STATUS){
                    if (msg.arg1 == 1){
                        //textViewStatus.setTextColor(Color.GREEN);
                        //textViewStatus.setText("Puede imprimir el documento dando click en la parte superior");
                    }else {
                        //textViewStatus.setTextColor(Color.RED);
                        //textViewStatus.setText("¡Dispositivo Bluetooth no encontrado!");
                        initPrinter();
                    }
                }
            }
        };

        lyt_empleados = root.findViewById(R.id.lyt_ventas);
        initRecyclerView(root);

        return root;
    }

    private void initRecyclerView(View root) {

        mData = new ArrayList<>();
        mData = (List<VentasBean>) (List<?>) new VentasDao().getListVentasByDate(Utils.fechaActual());

        if (mData.size() > 0) {
            lyt_empleados.setVisibility(View.GONE);
        } else {
            lyt_empleados.setVisibility(View.VISIBLE);
        }

        final RecyclerView recyclerView = root.findViewById(R.id.recyclerView_lista_ventas);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterListaVentas(mData, new AdapterListaVentas.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                showSelecctionFunction(position);
            }
        });
        recyclerView.setAdapter(mAdapter);

    }

    private void showSelecctionFunction(int position) {

        final VentasBean venta = mData.get(position);


        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setIcon(R.drawable.logo);
        builderSingle.setTitle("Seleccionar opción");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line);
        arrayAdapter.add("Reimprimir");
        arrayAdapter.add("Cancelar");
        arrayAdapter.add("Subir comprobante");
        /*
        if (venta.getTipo_venta().compareToIgnoreCase("CREDITO") == 0){

        }
*/
        builderSingle.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);

                String identificador = "";

                //Obtiene el nombre del vendedor
                final EmpleadoBean vendedoresBean = AppBundle.getUserBean();


                if (vendedoresBean != null){
                    identificador = vendedoresBean.getIdentificador();
                }
                final RolesDao rolesDao = new RolesDao();
                final RolesBean rolesBean = rolesDao.getRolByEmpleado(identificador, "Ventas");


                if (strName == null ||  strName.compareToIgnoreCase("Subir comprobante") == 0 ) {
                    HashMap<String, String> parametros = new HashMap<>();
                    parametros.put(Actividades.PARAM_1, String.valueOf(venta.getCobranza()));
                    Actividades.getSingleton(getContext(), CApturaComprobanteActivity.class).muestraActividad(parametros);
                    return;
                }


                if (strName == null ||  strName.compareToIgnoreCase("Cancelar") == 0 ) {

                    if (rolesBean != null){
                        if (!rolesBean.getActive()){
                            Toast.makeText(getContext(), "No tienes privilegios para esta area", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }


                    if (venta.getEstado().compareToIgnoreCase("CA") == 0){
                        final PrettyDialog dialog2 = new PrettyDialog(getContext());
                        dialog2.setTitle("Venta cancelada")
                                .setTitleColor(R.color.purple_500)
                                .setMessage("La venta ya fue cancelada anteriormente" + venta.getId())
                                .setMessageColor(R.color.purple_700)
                                .setAnimationEnabled(false)
                                .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        dialog2.dismiss();
                                    }
                                })
                                .addButton("OK", R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        dialog2.dismiss();
                                    }
                                });
                        dialog2.setCancelable(false);
                        dialog2.show();
                        return;
                    }

                    final PrettyDialog dialogs = new PrettyDialog(getContext());
                    dialogs.setTitle("Cancelar")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("Desea cancelar la venta número " + venta.getId())
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogs.dismiss();
                                }
                            })
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    final EmpleadoBean cancelaUsuario = AppBundle.getUserBean();
                                    VentasDao ventasDao = new VentasDao();
                                    venta.setEstado("CA");
                                    venta.setUsuario_cancelo(cancelaUsuario.getNombre());
                                    ventasDao.save(venta);

                                    mData = (List<VentasBean>) (List<?>) new VentasDao().getListVentasByDate(Utils.fechaActual());
                                    mAdapter.setVentas(mData);

                                    ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                    progressDialog.setMessage("Espere un momento");
                                    progressDialog.setCancelable(false);
                                    progressDialog.show();
                                    new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                                        progressDialog.dismiss();
                                        if (connected)  {
                                            syncCloudVenta(venta.getId());
                                        }

                                        if (venta.getCobranza() != null) {
                                            //Actualiza el documento de la cobranza
                                            final CobranzaDao cobranzaDao = new CobranzaDao();
                                            final CobranzaBean cobranzaBean = cobranzaDao.getByCobranza(venta.getCobranza());
                                            if (cobranzaBean != null) {
                                                cobranzaBean.setEstado("CA");
                                                cobranzaDao.save(cobranzaBean);
                                                final ClienteDao clienteDao = new ClienteDao();
                                                final ClienteBean clienteBean = clienteDao.getClienteByCuenta(venta.getCliente().getCuenta());
                                                if (clienteBean != null) {
                                                    clienteBean.setSaldo_credito(clienteBean.getSaldo_credito() - cobranzaBean.getImporte());
                                                    clienteDao.save(clienteBean);
                                                    testLoadClientes(String.valueOf(clienteBean.getId()));
                                                    new loadCobranza().execute();
                                                }
                                            }
                                            if (connected) {
                                                new loadCobranza().execute();
                                            }
                                        }


                                        final VentasBean ventasBean = ventasDao.getVentaByInventario(venta.getVenta());

                                        for (PartidasBean item : ventasBean.getListaPartidas()){
                                            final ProductoDao productoDao = new ProductoDao();
                                            final ProductoBean productoBean = productoDao.getProductoByArticulo(item.getArticulo().getArticulo());

                                            if (productoBean != null){

                                                productoBean.setExistencia(productoBean.getExistencia() + item.getCantidad());
                                                productoDao.save(productoBean);


                                                final InventarioHistorialDao inventarioHistorialDao = new InventarioHistorialDao();
                                                final InventarioHistorialBean inventarioHistorialBean = inventarioHistorialDao.getInvatarioPorArticulo(productoBean.getArticulo());

                                                if (inventarioHistorialBean != null){
                                                    inventarioHistorialBean.setCantidad(inventarioHistorialBean.getCantidad() - item.getCantidad());
                                                    inventarioHistorialDao.save(inventarioHistorialBean);

                                                }
                                            }
                                        }
                                        dialogs.dismiss();
                                    }, getActivity()).execute(), 100);
                                }
                            })
                            .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogs.dismiss();
                                }
                            });
                    dialogs.setCancelable(false);
                    dialogs.show();
                } else {

                    TicketVenta ticketVenta = new TicketVenta(getActivity());
                    ticketVenta.setVentasBean(venta);
                    ticketVenta.template();

                    if(mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write(ticketVenta.getDocumento());

                }
                dialog.dismiss();
            }
        });
        builderSingle.show();

    }


    public class loadCobranza extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            final CobranzaDao cobranzaDao = new CobranzaDao();
            List<CobranzaBean> cobranzaBeanList = new ArrayList<>();
            cobranzaBeanList = cobranzaDao.getCobranzaFechaActual(Utils.fechaActual());
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

            Call<CobranzaJson> loadCobranza = ApiServices.getClientRestrofit().create(PointApi.class).sendCobranza(cobranzaJson);

            loadCobranza.enqueue(new Callback<CobranzaJson>() {
                @Override
                public void onResponse(Call<CobranzaJson> call, Response<CobranzaJson> response) {
                    if (response.isSuccessful()) {
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

    private void syncCloudVenta(Long venta){

        try{
            final SincVentasByID sincVentasByID = new SincVentasByID(getActivity(), Long.parseLong(String.valueOf(venta)));

            sincVentasByID.setOnSuccess(new Servicio.ResponseOnSuccess() {
                @Override
                public void onSuccess(JSONArray response) throws JSONException {
                    //Toast.makeText(getContext(), "Venta sincronizada exitosamente", Toast.LENGTH_LONG).show();
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_lista_ventas, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.activaBluetooth:
                    if (isBluetoothEnabled()){
                        if (mBTSocket != null){
                            final PrettyDialog dialog = new PrettyDialog(getContext());
                            dialog.setTitle("Conexión")
                                    .setTitleColor(R.color.purple_500)
                                    .setMessage("El bluetooth ya esta habilitado...")
                                    .setMessageColor(R.color.purple_700)
                                    .setAnimationEnabled(false)
                                    .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                        @Override
                                        public void onClick() {
                                            dialog.dismiss();
                                        }
                                    })
                                    .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.purple_500, new PrettyDialogCallback() {
                                        @Override
                                        public void onClick() {
                                            dialog.dismiss();
                                        }
                                    });
                            dialog.setCancelable(false);
                            dialog.show();
                            return true;
                        }else {
                            if (isConfigPrinter()) {
                                if (!isBluetoothEnabled()) {
                                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                    startActivityForResult(enableBluetooth, 0);
                                }
                                initPrinter();
                            }
                        }
                    }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void initPrinter() {
        PrinterDao existeImpresora = new PrinterDao();
        int existe = existeImpresora.existeConfiguracionImpresora();

        if (existe > 0) {
            final PrinterBean establecida = existeImpresora.getImpresoraEstablecida();

            if (establecida != null) {

                if(!mBTAdapter.isEnabled()) {
                    Toast.makeText(getContext(), "Bluetooth no encendido", Toast.LENGTH_SHORT).show();
                    return;
                }
                //textViewStatus.setText("Conectado....");
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
                            Toast.makeText(getContext(), "Falló la creación de socket", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(getContext(), "Falló la creación de socket", Toast.LENGTH_SHORT).show();
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
    public void onDestroy() {
        super.onDestroy();
        if(mConnectedThread != null)
            mConnectedThread.cancel();
    }
}