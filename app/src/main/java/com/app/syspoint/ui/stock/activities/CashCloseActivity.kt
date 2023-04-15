package com.app.syspoint.ui.stock.activities

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.app.syspoint.R
import com.app.syspoint.bluetooth.ConnectedThread
import com.app.syspoint.databinding.ActivityCashCloseBinding
import com.app.syspoint.documents.CloseTicket
import com.app.syspoint.repository.objectBox.dao.PrinterDao
import com.app.syspoint.repository.objectBox.entities.StockBox
import com.app.syspoint.ui.bluetooth.BluetoothActivity
import com.app.syspoint.ui.stock.StockFragment
import com.app.syspoint.utils.Actividades
import com.app.syspoint.utils.click
import com.app.syspoint.utils.setGone
import com.app.syspoint.utils.setVisible
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import java.util.*

class CashCloseActivity: AppCompatActivity() {
    private lateinit var binding: ActivityCashCloseBinding

    private val TAG = "CashCloseActivity"

    //Connection bluetooth
    private val BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // "random" unique identifier

    // #defines for identifying shared types between calling functions
    private val REQUEST_ENABLE_BT = 1 // used to identify adding bluetooth names
    val MESSAGE_READ = 2 // used in bluetooth handler to identify message update
    private val CONNECTING_STATUS = 3 // used in bluetooth handler to identify message status

    private var mBTAdapter: BluetoothAdapter? = null
    private val mPairedDevices: Set<BluetoothDevice>? = null
    private val mBTArrayAdapter: ArrayAdapter<String>? = null

    private var mHandler: Handler? = null // Our main handler that will receive callback notifications
    private var mConnectedThread: ConnectedThread? = null // bluetooth background worker thread to send and receive data
    private var mBTSocket: BluetoothSocket? = null // bi-directional client-to-client data path
    private var isConnected = false
    private var templateTicket = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCashCloseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpListeners()
        initToolBar()

        mBTAdapter = BluetoothAdapter.getDefaultAdapter()

        if (isConfigPrinter()) {
            if (!isBluetoothEnabled()) {
                //Pregunta si queremos activar el bluetooth
                val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetooth, 0)
            } else {
                initPrinter()
            }
        } else {
            Actividades.getSingleton(this, BluetoothActivity::class.java).muestraActividad()
        }

        // Ask for location permission if not already allowed
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), 1
        )

        mHandler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                if (msg.what == StockFragment.MESSAGE_READ) {
                    var readMessage: String? = null
                    try {
                        readMessage = String((msg.obj as ByteArray), Charset.forName("UTF-8"))
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                    //textViewStatus.setText(readMessage);
                }
                if (msg.what == CONNECTING_STATUS) {
                    if (msg.arg1 == 1) {
                    } else {
                        initPrinter()
                    }
                }
            }
        }
    }

    private fun initToolBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_finaliza_inventario)
        toolbar.title = "Corte de caja exitoso"
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.purple_700)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_pdf_view, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_print -> {
                if (!isConnected) {
                    initPrinter()
                    return false
                }

                val inventarioBean = StockBox()
                val ticketInventario = CloseTicket()
                ticketInventario.box = inventarioBean
                ticketInventario.template()
                val ticket = ticketInventario.document
                templateTicket = ticket
                Log.d(TAG, ticket)

                if (mConnectedThread != null) //First check to make sure thread created
                    mConnectedThread!!.write(ticket)

                return true
            }
            R.id.home -> {
                return true
            }
            R.id.action_settings -> {
                Actividades.getSingleton(this, BluetoothActivity::class.java)
                    .muestraActividad()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

    }

    private fun setUpListeners() {
        binding.btnFinishCashClose click {
            setResult(StockFragment.CLOSE_INVENTORY)
            finish()
        }
    }

    private fun initPrinter() {
        binding.tvConnect.text = "Connectando..."
        val existeImpresora = PrinterDao()
        val existe = existeImpresora.existeConfiguracionImpresora()
        if (existe > 0) {
            val establecida = existeImpresora.getImpresoraEstablecida()
            if (establecida != null) {
                isConnected = true
                if (establecida != null) {
                    if (!mBTAdapter!!.isEnabled) {
                        Toast.makeText(applicationContext, "Bluetooth no encendido", Toast.LENGTH_SHORT)
                            .show()
                        return
                    }

                    // Spawn a new thread to avoid blocking the GUI one
                    object : Thread() {
                        override fun run() {
                            var fail = false
                            val device = mBTAdapter!!.getRemoteDevice(establecida.address)
                            try {
                                mBTSocket = createBluetoothSocket(device)
                            } catch (e: IOException) {
                                fail = true
                                Toast.makeText(
                                    applicationContext,
                                    "Falló la creación de socket",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            // Establish the Bluetooth socket connection.
                            try {
                                mBTSocket!!.connect()
                            } catch (e: IOException) {
                                try {
                                    fail = true
                                    mBTSocket!!.close()
                                    mHandler!!.obtainMessage(
                                        CONNECTING_STATUS,
                                        -1,
                                        -1
                                    )
                                        .sendToTarget()
                                } catch (e2: IOException) {
                                    //insert code to deal with this
                                    Toast.makeText(
                                        applicationContext,
                                        "Falló la creación de socket",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                            if (!fail) {

                                binding.tvConnect.post {
                                    binding.tvConnect.setGone()
                                }
                                binding.tvPrintTicket.post {
                                    binding.tvPrintTicket.setVisible()
                                }

                                mConnectedThread = ConnectedThread(mBTSocket, mHandler)
                                mConnectedThread!!.start()
                                mHandler!!.obtainMessage(
                                    CONNECTING_STATUS,
                                    1,
                                    -1,
                                    establecida.name
                                )
                                    .sendToTarget()
                            }
                        }
                    }.start()
                }
            }
        } else {
            Actividades.getSingleton(this, BluetoothActivity::class.java).muestraActividad()
        }
    }

    @Throws(IOException::class)
    private fun createBluetoothSocket(device: BluetoothDevice): BluetoothSocket {
        try {
            val m = device.javaClass.getMethod(
                "createInsecureRfcommSocketToServiceRecord",
                UUID::class.java
            )
            return m.invoke(device, BT_MODULE_UUID) as BluetoothSocket
        } catch (e: Exception) {
            Log.e(TAG, "Could not create Insecure RFComm Connection", e)
        }
        return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID)
    }

    private fun isConfigPrinter(): Boolean {
        val existeImpresora = PrinterDao()
        val existe = existeImpresora.existeConfiguracionImpresora()
        return existe > 0
    }

    fun isBluetoothEnabled(): Boolean {
        return mBTAdapter != null && mBTAdapter!!.isEnabled
    }
}