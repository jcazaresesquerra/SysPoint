package com.app.syspoint.ui.stock;

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
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.bluetooth.ConnectedThread;
import com.app.syspoint.repository.cache.SharedPreferencesManager;
import com.app.syspoint.repository.objectBox.dao.PrinterDao;
import com.app.syspoint.repository.objectBox.dao.ProductDao;
import com.app.syspoint.repository.objectBox.dao.StockDao;
import com.app.syspoint.repository.objectBox.dao.StockHistoryDao;
import com.app.syspoint.repository.objectBox.entities.PrinterBox;
import com.app.syspoint.repository.objectBox.entities.ProductBox;
import com.app.syspoint.repository.objectBox.entities.StockBox;
import com.app.syspoint.ui.bluetooth.BluetoothActivity;
import com.app.syspoint.ui.stock.activities.CashCloseActivity;
import com.app.syspoint.ui.stock.activities.ConfirmaInventarioActivity;
import com.app.syspoint.ui.stock.activities.ListaProductosInventarioActivity;
import com.app.syspoint.ui.stock.adapter.AdapterInventario;
import com.app.syspoint.utils.Actividades;
import com.app.syspoint.utils.PrettyDialog;
import com.app.syspoint.utils.PrettyDialogCallback;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import timber.log.Timber;

public class StockFragment extends Fragment {

    private List<StockBox> mData;
    private AdapterInventario mAdapter;
    View root;

    public static final int CLOSE_INVENTORY = 1000;

    protected static final String TAG = "StockFragment";

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
    private boolean isConnected = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_stock, container, false);
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

        initRecyclerView();
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        setDataInventory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mConnectedThread != null)
            mConnectedThread.cancel();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_inventarios_opciones, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_menu_inventario_add:
                Timber.tag(TAG).d("add stock -> click");
                Actividades.getSingleton(getActivity(), ListaProductosInventarioActivity.class).muestraActividadForResult(Actividades.PARAM_INT_1);
                return true;

            case R.id.item_menu_inventario_finish:
                Timber.tag(TAG).d("finish stock -> click");
                if (mData.size() > 0){
                    if (isConnected) {
                        Actividades.getSingleton(getActivity(), ConfirmaInventarioActivity.class).muestraActividad();
                    } else {
                        Toast.makeText(getActivity(),
                                "Necesitas conectar la impresora para realizar esta operación",
                                Toast.LENGTH_LONG
                        ).show();
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

                    }
                }else {
                    final PrettyDialog dialogo = new PrettyDialog(getContext());
                    dialogo.setTitle("Sin inventario")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("No hay productos por inventariar")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            })
                            .addButton(getString(R.string.ok_dialog), R.color.black, R.color.quantum_orange, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            });
                    dialogo.setCancelable(false);
                    dialogo.show();
                    return false;
                }

                return true;

            case R.id.close_caja:
                Timber.tag(TAG).d("close stock -> click");
                if (mData.size() == 0){
                     final PrettyDialog dialogo = new PrettyDialog(getContext());
                    dialogo.setTitle("Sin inventario")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("No es posible cerrar no hay inventario previo")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            })
                            .addButton(getString(R.string.ok_dialog), R.color.black, R.color.quantum_orange, new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    dialogo.dismiss();
                                }
                            });
                    dialogo.setCancelable(false);
                    dialogo.show();
                    return false;
                }


                if (isConnected) {
                    final PrettyDialog dialogo = new PrettyDialog(getContext());
                    dialogo.setTitle("Cierre")
                            .setTitleColor(R.color.purple_500)
                            .setMessage("¿Desea cerrar la caja?")
                            .setMessageColor(R.color.purple_700)
                            .setAnimationEnabled(false)
                            .setIcon(R.drawable.ic_save_white, R.color.purple_500, () -> dialogo.dismiss())
                            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, () -> {
                                Intent intent = new Intent(getActivity(), CashCloseActivity.class);
                                startActivityForResult(intent, 0);
                                dialogo.dismiss();
                            })
                            .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, () -> dialogo.dismiss());
                    dialogo.setCancelable(false);
                    dialogo.show();
                } else {
                    Toast.makeText(getActivity(),
                            "Necesitas conectar la impresora para realizar esta operación",
                            Toast.LENGTH_LONG
                    ).show();
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
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CLOSE_INVENTORY) {
            closeInventory();
        }
    }

    private void closeInventory(){
        List<StockBox> mList =  new StockDao().list();
        final ProductDao productDao = new ProductDao();
        for (StockBox item : mList){

            final ProductBox productoBean = productDao.getProductoByArticulo(item.getArticulo().getTarget().getArticulo());
            if (productoBean != null){
                //Actualiza la existencia del articulo
                productoBean.setExistencia(0);
                productDao.insertBox(productoBean);
            }
            item.setTotalCantidad(0);
        }

        final StockDao stockDao = new StockDao();
        stockDao.clear();

        final StockHistoryDao historialDao = new StockHistoryDao();
        historialDao.clear();

        mData = new StockDao().list();
        mAdapter.setData(mData);

        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getActivity());
        int currentStockId = sharedPreferencesManager.getCurrentStockId() + 1;
        sharedPreferencesManager.saveCurrentStockId(currentStockId);

        ocultaLinearLayouth();
    }

    private void initRecyclerView(){
        mData = new ArrayList<>();
        mData = new StockDao().list();

        ocultaLinearLayouth();

        final RecyclerView recyclerView = root.findViewById(R.id.rv_inventario_pendiente);
        recyclerView.setHasFixedSize(true);

        final LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);

        mAdapter = new AdapterInventario(mData, position -> {
            StockBox inventarioBean = mData.get(position);

            if (inventarioBean.getEstado().compareToIgnoreCase("CO") == 0){

                final PrettyDialog dialog = new PrettyDialog(getContext());
                dialog.setTitle("Eliminar")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("No es posible eliminar el inventario ya fue confirmado")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();
                            }
                        })
                        .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.quantum_orange, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();
                            }
                        });
                dialog.setCancelable(false);
                dialog.show();
                return false;
            }else {
                StockDao stockDao = new StockDao();
                stockDao.delete(inventarioBean.getId());
                refreshList();
            }

            return false;
        });
        recyclerView.setAdapter(mAdapter);
    }

    private void refreshList(){
        setDataInventory();
    }

    private void setDataInventory(){
        mData = new StockDao().list();
        mAdapter.setData(mData);
        ocultaLinearLayouth();
    }

    private void ocultaLinearLayouth() {
        LinearLayout linearLayout = null;
        linearLayout = root.findViewById(R.id.empty_state_inventory);
        if (mData.size() == 0) {
            linearLayout.setVisibility(View.VISIBLE);
        } else {
            linearLayout.setVisibility(View.GONE);
        }
    }

    private void initPrinter() {
        PrinterDao existeImpresora = new PrinterDao();
        int existe = existeImpresora.existeConfiguracionImpresora();

        if (existe > 0) {
            final PrinterBox establecida = existeImpresora.getImpresoraEstablecida();

            if (establecida != null) {
                isConnected = true;
                if (establecida != null) {
                    if(!mBTAdapter.isEnabled()) {
                        Toast.makeText(getContext(), "Bluetooth no encendido", Toast.LENGTH_SHORT).show();
                        return;
                    }

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
                                Toast.makeText(getActivity(), "Falló la creación de socket", Toast.LENGTH_SHORT).show();
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
                                    Toast.makeText(getActivity(), "Falló la creación de socket", Toast.LENGTH_SHORT).show();
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
            Actividades.getSingleton(getActivity(), BluetoothActivity.class).muestraActividad();
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