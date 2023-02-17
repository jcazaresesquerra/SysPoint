package com.app.syspoint.ui.cobranza;

import android.Manifest;
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
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.syspoint.R;
import com.app.syspoint.ui.bluetooth.BluetoothActivity;
import com.app.syspoint.bluetooth.ConnectedThread;
import com.app.syspoint.repository.database.bean.CobrosBean;
import com.app.syspoint.repository.database.bean.PrinterBean;
import com.app.syspoint.repository.database.dao.ChargesDao;
import com.app.syspoint.repository.database.dao.PrinterDao;
import com.app.syspoint.documents.DepositTicket;
import com.app.syspoint.ui.clientes.TaskClients;
import com.app.syspoint.ui.cobranza.adapter.AdapterListaCobranzas;
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

public class CobranzaFragment extends Fragment {

    protected static final String TAG = "TAG";

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


    private AdapterListaCobranzas mAdapter;
    private List<CobrosBean> partidas;
    private LinearLayout  lyt_cobranza;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_cobranza, container, false);

        setHasOptionsMenu(true);

        TaskClients.recalculated_data();

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

        lyt_cobranza = view.findViewById(R.id.lyt_cobranza);
        initRecyclerView(view);
        // Inflate the layout for this fragment
        return view;

    }

    private void initRecyclerView(View v){

        partidas = new ArrayList<>();
        partidas = (List<CobrosBean>)(List<?>) new ChargesDao().GetAllListaCobrosConfirmadas();

        if (partidas.size() > 0){
            lyt_cobranza.setVisibility(View.GONE);
        }else {
            lyt_cobranza.setVisibility(View.VISIBLE);
        }

        /*** ----- Obtiene el recyclador ------ ****/
        final RecyclerView recyclerView = v.findViewById(R.id.recyclerView_lista_cobranza);
        recyclerView.setHasFixedSize(true);

        /*** ----- Manejador ------ ****/
        final LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new AdapterListaCobranzas(partidas, position -> {

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
            builderSingle.setIcon(R.drawable.logo);
            builderSingle.setTitle("Seleccionar opción");

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line);
            arrayAdapter.add("Reimprimir");

            builderSingle.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
            builderSingle.setAdapter(arrayAdapter, (dialog, which) -> {
                CobrosBean cobrosBean = partidas.get(position);
                DepositTicket depositTicket = new DepositTicket();
                depositTicket.setBean(cobrosBean);
                depositTicket.template();
                Toast.makeText(getContext(), "Imprimiendo ticket", Toast.LENGTH_SHORT).show();
                if (mConnectedThread != null) //First check to make sure thread created
                    mConnectedThread.write(depositTicket.getDocument());
            });
            builderSingle.show();

        });
        recyclerView.setAdapter(mAdapter);
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_lista_cobranza, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.activaBluetoothCobranza:
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