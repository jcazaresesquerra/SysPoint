package com.app.syspoint.ui.printer

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.syspoint.R
import com.app.syspoint.databinding.ActivityConfigPrinterBinding
import com.app.syspoint.models.sealed.ConfigurePrinterViewState
import com.app.syspoint.viewmodel.printer.ConfigurePrinterViewModel
import libs.mjn.prettydialog.PrettyDialog

class ConfigurePrinterFragment: Fragment() {

    private lateinit var binding: ActivityConfigPrinterBinding
    private lateinit var viewModel: ConfigurePrinterViewModel

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mPairedDevicesArrayAdapter: ArrayAdapter<String>? = null

    private var findPrinterClicked = false
    private var confirmPrinterClicked = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = ActivityConfigPrinterBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ConfigurePrinterViewModel::class.java]

        viewModel.configurePrinterViewState.observe(viewLifecycleOwner, ::renderViewState)

        setHasOptionsMenu(true)

        setUpBluetoothAdapter()

        return binding.root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter!!.cancelDiscovery()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_impresoras, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    //Cuando el usuario da click en el icono save
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.buscarImpresoras -> {
                if (!findPrinterClicked) {
                    findPrinterClicked = true
                    Toast.makeText(context, "Buscando...", Toast.LENGTH_SHORT).show()
                    if (mBluetoothAdapter!!.isEnabled) {
                        setUpBluetoothAdapter()
                    } else {
                        val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableBluetooth, 0)
                    }
                    findPrinterClicked = false
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun renderViewState(configurePrinterViewState: ConfigurePrinterViewState) {
        when(configurePrinterViewState) {
            is ConfigurePrinterViewState.PrinterConfigured -> {
                Toast.makeText(requireActivity(), "Impresora configurada exitosamente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setUpBluetoothAdapter() {
        mPairedDevicesArrayAdapter = ArrayAdapter(requireActivity(), R.layout.device_name)

        binding.pairedDevices.adapter = mPairedDevicesArrayAdapter
        binding.pairedDevices.setOnItemClickListener { adapterView, view, i, l ->
            binding.pairedDevices.isEnabled = false
            try {
                mBluetoothAdapter!!.cancelDiscovery()
                val mDeviceInfo = (view as TextView).text.toString()
                val mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length - 17)
                val dialog = PrettyDialog(context)
                dialog.setTitle("Establecer")
                    .setTitleColor(R.color.purple_500)
                    .setMessage("Desea establecer la impresora $mDeviceInfo?")
                    .setMessageColor(R.color.purple_700)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.ic_print_black, R.color.purple_500) {
                        dialog.dismiss()
                        binding.pairedDevices.isEnabled = true
                    }
                    .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {
                        if (!confirmPrinterClicked) {
                            confirmPrinterClicked = true
                            viewModel.configurePrinter(mDeviceAddress, mDeviceInfo)
                            dialog.dismiss()
                            confirmPrinterClicked = false
                        }
                        binding.pairedDevices.isEnabled = true
                    }
                    .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900) {
                        dialog.dismiss()
                        binding.pairedDevices.isEnabled = true
                    }
                dialog.setCancelable(false)
                dialog.show()
            } catch (ex: Exception) {
                ex.printStackTrace()
                binding.pairedDevices.isEnabled = true
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothAdapter?.let {bluetoothAdapter ->
            val mPairedDevices = bluetoothAdapter.bondedDevices

            mPairedDevicesArrayAdapter?.let { pairedDevicesArrayAdapter ->
                pairedDevicesArrayAdapter.clear()

                if (mPairedDevices.size > 0) {
                    for (mDevice in mPairedDevices) {
                        pairedDevicesArrayAdapter.add("${mDevice.name} \n ${mDevice.address}")
                    }
                } else {
                    val mNoDevices = "No hay dispositivos vinculados"
                    pairedDevicesArrayAdapter.add(mNoDevices)
                }
            }

        }

    }
}