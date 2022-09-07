package com.app.syspoint.ui.templates

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.app.syspoint.R
import com.app.syspoint.databinding.ActivityDeviceListBinding
import com.app.syspoint.utils.setVisible

class DeviceListActivity: AppCompatActivity() {

    private lateinit var binding: ActivityDeviceListBinding

    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mPairedDevicesArrayAdapter: ArrayAdapter<String>? = null

    companion object {
        private const val TAG = "DeviceListActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeviceListBinding.inflate(layoutInflater)

        setContentView(binding.root)
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS)
        setResult(RESULT_CANCELED)

        setUpList()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter!!.cancelDiscovery()
        }
    }

    private fun setUpList() {
        mPairedDevicesArrayAdapter = ArrayAdapter(this, R.layout.device_name)

        binding.pairedDevices.adapter = mPairedDevicesArrayAdapter
        binding.pairedDevices.setOnItemClickListener { adapterView, view, i, l ->
            try {
                mBluetoothAdapter!!.cancelDiscovery()
                val mDeviceInfo = (view as TextView).text.toString()
                val mDeviceAddress = mDeviceInfo.substring(mDeviceInfo.length - 17)

                Log.d(TAG, "Device_Address $mDeviceAddress")

                //DeviceListActivity.EXTRA_DEVICE_ADDRESS = mDeviceAddress
                val mBundle = Bundle()
                mBundle.putString("DeviceAddress", mDeviceAddress)
                val mBackIntent = Intent()
                mBackIntent.putExtras(mBundle)
                setResult(RESULT_OK, mBackIntent)
                finish()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBluetoothAdapter?.let { bluetoothAdapter ->
            val mPairedDevices = bluetoothAdapter.bondedDevices

            mPairedDevicesArrayAdapter?.let { pairedDevicesArrayAdapter ->
                if (mPairedDevices.size > 0) {
                    binding.titlePairedDevices.setVisible()
                    for (mDevice in mPairedDevices) {
                        pairedDevicesArrayAdapter.add("${mDevice.name} \n ${mDevice.address}")
                    }
                } else {
                    val mNoDevices = "None Paired" //getResources().getText(R.string.none_paired).toString();
                    pairedDevicesArrayAdapter.add(mNoDevices)
                }
            }
        }
    }
}