package com.app.syspoint.ui.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.syspoint.R;
import com.app.syspoint.repository.database.bean.PrinterBean;
import com.app.syspoint.repository.database.dao.PrinterDao;

import java.util.Set;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class ConfigPrinterActivity extends Fragment {

    protected static final String TAG = "TAG";
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> mPairedDevicesArrayAdapter;
    ListView mPairedListView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.activity_config_printer, container, false);
        setHasOptionsMenu(true);

        mPairedDevicesArrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.device_name);

        mPairedListView = root.findViewById(R.id.paired_devices);
        mPairedListView.setAdapter(mPairedDevicesArrayAdapter);
        mPairedListView.setOnItemClickListener(mDeviceClickListener);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();


        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                mPairedDevicesArrayAdapter.add(mDevice.getName() + "\n" + mDevice.getAddress());
            }
        } else {
            String mNoDevices = "No hay dispositivos vinculados";
            mPairedDevicesArrayAdapter.add(mNoDevices);
        }

        return root;
    }

    private void searchDevices(){

        mPairedListView.setAdapter(mPairedDevicesArrayAdapter);
        mPairedListView.setOnItemClickListener(mDeviceClickListener);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> mPairedDevices = mBluetoothAdapter.getBondedDevices();

        mPairedDevicesArrayAdapter.clear();
        if (mPairedDevices.size() > 0) {
            for (BluetoothDevice mDevice : mPairedDevices) {
                mPairedDevicesArrayAdapter.add(mDevice.getName() + "\n" + mDevice.getAddress());
            }
        } else {
            String mNoDevices = "No hay dispositivos vinculados";

            mPairedDevicesArrayAdapter.add(mNoDevices);
        }
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_impresoras, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    //Cuando el usuario da click en el icono save
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.buscarImpresoras:
                Toast.makeText(getContext(), "Buscando...", Toast.LENGTH_SHORT).show();
                if (mBluetoothAdapter.isEnabled()) {
                    searchDevices();
                } else {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBluetooth, 0);
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView<?> mAdapterView, View mView, int mPosition, long mLong) {

            try {
                mBluetoothAdapter.cancelDiscovery();
                String mDeviceInfo = ((TextView) mView).getText().toString();
                String mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length() - 17);


                final PrettyDialog dialog = new PrettyDialog(getContext());
                dialog.setTitle("Establecer")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Desea establecer la impresora " + mDeviceInfo + "?")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.ic_print_black, R.color.purple_500, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();
                            }
                        })
                        .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {

                                PrinterBean printerBean = new PrinterBean();
                                PrinterDao printerDao = new PrinterDao();
                                printerDao.clear();

                                printerBean.setId(Long.valueOf(1));
                                printerBean.setAddress(mDeviceAddress);
                                printerBean.setName(mDeviceInfo);
                                printerBean.setIdPrinter(Long.valueOf(1));
                                printerDao.insert(printerBean);

                                Toast.makeText(getContext(), "Impresora configurada exitosamente", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900, new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                dialog.dismiss();

                            }
                        });
                dialog.setCancelable(false);
                dialog.show();

            } catch (Exception ex) {

            }
        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
    }
}