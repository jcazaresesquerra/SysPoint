package com.app.syspoint.ui.stock.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.syspoint.bluetooth.ConnectedThread
import com.app.syspoint.databinding.ActivityCashCloseBinding
import com.app.syspoint.documents.CloseTicket
import com.app.syspoint.repository.database.bean.InventarioBean
import com.app.syspoint.repository.database.dao.PrinterDao
import com.app.syspoint.repository.database.dao.ProductDao
import com.app.syspoint.repository.database.dao.StockDao
import com.app.syspoint.repository.database.dao.StockHistoryDao
import com.app.syspoint.ui.bluetooth.BluetoothActivity
import com.app.syspoint.utils.Actividades
import com.app.syspoint.utils.click
import java.io.IOException
import java.util.*

class CashCloseActivity: AppCompatActivity() {
    private lateinit var binding: ActivityCashCloseBinding

    //Connection bluetooth
    private val BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private val REQUEST_ENABLE_BT = 1 // used to identify adding bluetooth names
    val MESSAGE_READ = 2 // used in bluetooth handler to identify message update
    private val CONNECTING_STATUS = 3 // used in bluetooth handler to identify message status

    private val mBTAdapter: BluetoothAdapter? = null
    private val mPairedDevices: Set<BluetoothDevice>? = null
    private val mBTArrayAdapter: ArrayAdapter<String>? = null

    private val mHandler: Handler? = null // Our main handler that will receive callback notifications
    private var mConnectedThread: ConnectedThread? = null // bluetooth background worker thread to send and receive data
    private var mBTSocket: BluetoothSocket? = null // bi-directional client-to-client data path


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashCloseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.btnFinishCashClose click {
            finish()
        }
    }
}