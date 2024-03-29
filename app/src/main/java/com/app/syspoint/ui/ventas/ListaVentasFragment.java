package com.app.syspoint.ui.ventas;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.app.syspoint.interactor.charge.ChargeInteractor;
import com.app.syspoint.interactor.charge.ChargeInteractorImp;
import com.app.syspoint.interactor.client.ClientInteractor;
import com.app.syspoint.interactor.client.ClientInteractorImp;
import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.R;
import com.app.syspoint.repository.objectBox.AppBundle;
import com.app.syspoint.repository.objectBox.dao.ChargeDao;
import com.app.syspoint.repository.objectBox.dao.ClientDao;
import com.app.syspoint.repository.objectBox.dao.EmployeeDao;
import com.app.syspoint.repository.objectBox.dao.PrinterDao;
import com.app.syspoint.repository.objectBox.dao.ProductDao;
import com.app.syspoint.repository.objectBox.dao.RolesDao;
import com.app.syspoint.repository.objectBox.dao.RuteClientDao;
import com.app.syspoint.repository.objectBox.dao.SellsDao;
import com.app.syspoint.repository.objectBox.dao.SessionDao;
import com.app.syspoint.repository.objectBox.dao.StockHistoryDao;
import com.app.syspoint.repository.objectBox.dao.TempRuteClientDao;
import com.app.syspoint.repository.objectBox.entities.ChargeBox;
import com.app.syspoint.repository.objectBox.entities.ClientBox;
import com.app.syspoint.repository.objectBox.entities.EmployeeBox;
import com.app.syspoint.repository.objectBox.entities.PlayingBox;
import com.app.syspoint.repository.objectBox.entities.PrinterBox;
import com.app.syspoint.repository.objectBox.entities.ProductBox;
import com.app.syspoint.repository.objectBox.entities.RolesBox;
import com.app.syspoint.repository.objectBox.entities.RuteClientBox;
import com.app.syspoint.repository.objectBox.entities.SellBox;
import com.app.syspoint.repository.objectBox.entities.SessionBox;
import com.app.syspoint.repository.objectBox.entities.StockHistoryBox;
import com.app.syspoint.repository.objectBox.entities.TempRuteClientBox;
import com.app.syspoint.ui.bluetooth.BluetoothActivity;
import com.app.syspoint.bluetooth.ConnectedThread;
import com.app.syspoint.documents.SellTicket;
import com.app.syspoint.repository.request.http.Servicio;
import com.app.syspoint.repository.request.http.SincVentasByID;
import com.app.syspoint.models.Client;
import com.app.syspoint.models.Payment;
import com.app.syspoint.ui.ventas.adapter.AdapterListaVentas;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.NetworkStateTask;
import com.app.syspoint.utils.PrettyDialog;
import com.app.syspoint.utils.PrettyDialogCallback;
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
import java.util.UUID;

public class ListaVentasFragment extends Fragment {

    protected static final String TAG = "ListaVentasFragment";

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

    private List<SellBox> mData;
    private AdapterListaVentas mAdapter;
    private LinearLayout lyt_empleados;

    private boolean cancelSellClicked = false;

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
        mData = new SellsDao().getListVentasByDate(Utils.fechaActual());

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

        final SellBox venta = mData.get(position);


        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
        builderSingle.setIcon(R.drawable.tenet_icon);
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
                EmployeeBox vendedoresBean = getEmployee();

                if (vendedoresBean != null){
                    identificador = vendedoresBean.getIdentificador();
                }
                final RolesDao rolesDao = new RolesDao();
                final RolesBox rolesBean = rolesDao.getRolByEmpleado(identificador, "Ventas");


                if (strName == null ||  strName.compareToIgnoreCase("Subir comprobante") == 0 ) {
                    HashMap<String, String> parametros = new HashMap<>();
                    parametros.put(Actividades.PARAM_1, String.valueOf(venta.getTicket()));
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
                                    if (!cancelSellClicked) {
                                        cancelSellClicked = true;
                                        EmployeeBox cancelaUsuario = getEmployee();

                                        SellsDao sellsDao = new SellsDao();
                                        venta.setEstado("CA");
                                        venta.setUsuario_cancelo(cancelaUsuario.getNombre());
                                        sellsDao.insertBox(venta);

                                        mData = (List<SellBox>) new SellsDao().getListVentasByDate(Utils.fechaActual());
                                        mAdapter.setData(mData);

                                        ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                        progressDialog.setMessage("Espere un momento");
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();
                                        new Handler().postDelayed(() -> new NetworkStateTask(connected -> {
                                            progressDialog.dismiss();
                                            if (connected) {
                                                syncCloudVenta(venta.getId());
                                            }

                                            if (venta.getCobranza() != null) {
                                                //Actualiza el documento de la cobranza
                                                ChargeDao chargeDao = new ChargeDao();
                                                ChargeBox chargeBox = chargeDao.getByCobranza(venta.getCobranza());
                                                if (chargeBox != null) {
                                                    chargeBox.setEstado("CA");
                                                    chargeDao.insertBox(chargeBox);
                                                    final ClientDao clientDao = new ClientDao();
                                                    final ClientBox clientBox = clientDao.getClientByAccount(venta.getClient().getTarget().getCuenta());
                                                    if (clientBox != null) {
                                                        clientBox.setSaldo_credito(clientBox.getSaldo_credito() - chargeBox.getImporte());
                                                        clientDao.insertBox(clientBox);
                                                        testLoadClientes(clientBox.getId());
                                                        saveCharge();
                                                    }
                                                }
                                                if (connected) {
                                                    saveCharge();
                                                }
                                            }

                                            TempRuteClientBox tempRuteClientBox = new TempRuteClientDao().getClienteByCuentaCliente(venta.getClient().getTarget().getCuenta());
                                            if (tempRuteClientBox != null) {
                                                saveReturnRuteClient(tempRuteClientBox);
                                                new TempRuteClientDao().removeBox(tempRuteClientBox.getId());
                                            }


                                            final SellBox ventasBean = sellsDao.getVentaByInventario(venta.getVenta());

                                            for (PlayingBox item : ventasBean.getListaPartidas()) {
                                                final ProductDao productDao = new ProductDao();
                                                final ProductBox productoBean = productDao.getProductoByArticulo(item.getArticulo().getTarget().getArticulo());

                                                if (productoBean != null) {

                                                    productoBean.setExistencia(productoBean.getExistencia() + item.getCantidad());
                                                    productDao.insertBox(productoBean);


                                                    final StockHistoryDao stockHistoryDao = new StockHistoryDao();
                                                    final StockHistoryBox inventarioHistorialBean = stockHistoryDao.getInvatarioPorArticulo(productoBean.getArticulo());

                                                    if (inventarioHistorialBean != null) {
                                                        inventarioHistorialBean.setCantidad(inventarioHistorialBean.getCantidad() - item.getCantidad());
                                                        stockHistoryDao.insertBox(inventarioHistorialBean);

                                                    }
                                                }
                                            }
                                            dialogs.dismiss();
                                            cancelSellClicked = false;
                                        }).execute(), 100);
                                    }
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

                    SellTicket sellTicket = new SellTicket();
                    sellTicket.setBox(venta);
                    sellTicket.template();

                    String ticket = sellTicket.document;
                    Log.d("ListaVentasFragment", ticket);

                    if (mConnectedThread != null) //First check to make sure thread created
                        mConnectedThread.write(sellTicket.getDocument());

                }
                dialog.dismiss();
            }
        });
        builderSingle.show();

    }

    private void saveCharge() {

        ChargeDao chargeDao = new ChargeDao();
        List<ChargeBox> chargeBoxList = chargeDao.getCobranzaFechaActual(Utils.fechaActual());
        List<Payment> chargeList = new ArrayList<>();
        for (ChargeBox item : chargeBoxList) {
            Payment charge = new Payment();
            charge.setCobranza(item.getCobranza());
            charge.setCuenta(item.getCliente());
            charge.setImporte(item.getImporte());
            charge.setSaldo(item.getSaldo());
            charge.setVenta(item.getVenta());
            charge.setEstado(item.getEstado());
            charge.setObservaciones(item.getObservaciones());
            charge.setFecha(item.getFecha());
            charge.setHora(item.getHora());
            charge.setIdentificador(item.getEmpleado());
            chargeList.add(charge);
        }

        new ChargeInteractorImp().executeSaveCharge(chargeList, new ChargeInteractor.OnSaveChargeListener() {
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

    private void testLoadClientes(Long idCliente) {
        final ClientDao clientDao = new ClientDao();
        List<ClientBox> listaClientesDB = clientDao.getByIDClient(idCliente);

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
            cliente.setLatitud("" + item.getLatitud());
            cliente.setLongitud("" + item.getLongitud());
            cliente.setPhone_contacto("" + item.getContacto_phone());
            cliente.setRecordatorio("" + item.getRecordatorio());
            cliente.setVisitas(item.getVisitasNoefectivas());
            if (item.isCredito()) {
                cliente.setCredito(1);
            } else {
                cliente.setCredito(0);
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

        new ClientInteractorImp().executeSaveClient(listaClientes, new ClientInteractor.SaveClientListener() {
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

    private void syncCloudVenta(Long venta){

        try{
            final SincVentasByID sincVentasByID = new SincVentasByID(Long.parseLong(String.valueOf(venta)));

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
            final PrinterBox establecida = existeImpresora.getImpresoraEstablecida();

            if (establecida != null) {

                if(!mBTAdapter.isEnabled()) {
                    if (getContext() != null) Toast.makeText(getContext(), "Bluetooth no encendido", Toast.LENGTH_SHORT).show();
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

        return existe > 0;
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

    private EmployeeBox getEmployee() {
        EmployeeBox vendedoresBean = AppBundle.getUserBox();
        if (vendedoresBean == null) {
            SessionBox sessionBox = new SessionDao().getUserSession();
            if (sessionBox != null) {
                vendedoresBean = new EmployeeDao().getEmployeeByID(sessionBox.getEmpleadoId());
            } else {
                vendedoresBean = new CacheInteractor().getSeller();
            }
        }
        return vendedoresBean;
    }

    private void saveReturnRuteClient(TempRuteClientBox tempRuteClientBox) {
        RuteClientDao tempRuteClientDao = new RuteClientDao();
        RuteClientBox ruteClientBox = new RuteClientBox();
        ruteClientBox.setId(tempRuteClientBox.getId());
        ruteClientBox.setNombre_comercial(tempRuteClientBox.getNombre_comercial());
        ruteClientBox.setCalle(tempRuteClientBox.getCalle());
        ruteClientBox.setNumero(tempRuteClientBox.getNumero());
        ruteClientBox.setColonia(tempRuteClientBox.getColonia());
        ruteClientBox.setCuenta(tempRuteClientBox.getCuenta());
        ruteClientBox.setRango(tempRuteClientBox.getRango());
        ruteClientBox.setLun(tempRuteClientBox.getLun());
        ruteClientBox.setMar(tempRuteClientBox.getMar());
        ruteClientBox.setMie(tempRuteClientBox.getMie());
        ruteClientBox.setJue(tempRuteClientBox.getJue());
        ruteClientBox.setVie(tempRuteClientBox.getVie());
        ruteClientBox.setSab(tempRuteClientBox.getSab());
        ruteClientBox.setDom(tempRuteClientBox.getDom());
        ruteClientBox.setLunOrder(tempRuteClientBox.getLunOrder());
        ruteClientBox.setMarOrder(tempRuteClientBox.getMarOrder());
        ruteClientBox.setMieOrder(tempRuteClientBox.getMieOrder());
        ruteClientBox.setJueOrder(tempRuteClientBox.getJueOrder());
        ruteClientBox.setVieOrder(tempRuteClientBox.getVieOrder());
        ruteClientBox.setSabOrder(tempRuteClientBox.getSabOrder());
        ruteClientBox.setDomOrder(tempRuteClientBox.getDomOrder());
        ruteClientBox.setOrder(tempRuteClientBox.getOrder());
        ruteClientBox.setVisitado(0);
        ruteClientBox.setLatitud(tempRuteClientBox.getLatitud());
        ruteClientBox.setLongitud(tempRuteClientBox.getLongitud());
        ruteClientBox.setPhone_contact(tempRuteClientBox.getPhone_contact());
        ruteClientBox.setStatus(tempRuteClientBox.getStatus());
        ruteClientBox.setCredito(tempRuteClientBox.isCredito());
        ruteClientBox.setRecordatorio(tempRuteClientBox.getRecordatorio());
        ruteClientBox.setRecordatorio(tempRuteClientBox.isRecordatorio());
        ruteClientBox.setDate_sync(tempRuteClientBox.getDate_sync());
        ruteClientBox.setUpdatedAt(tempRuteClientBox.getUpdatedAt());
        ruteClientBox.setVentaClientId(tempRuteClientBox.getVentaClientId());
        ruteClientBox.setVentaFecha(tempRuteClientBox.getVentaFecha());
        ruteClientBox.setVentaCreatedAt(tempRuteClientBox.getVentaCreatedAt());
        ruteClientBox.setVentaUpdatedAt(tempRuteClientBox.getVentaUpdatedAt());

        tempRuteClientDao.insertBox(ruteClientBox);
    }
}