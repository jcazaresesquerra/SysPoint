package com.app.syspoint.ui.stock
/*
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
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.syspoint.R
import com.app.syspoint.bluetooth.ConnectedThread
import com.app.syspoint.databinding.FragmentStockBinding
import com.app.syspoint.doments.CloseTicket
import com.app.syspoint.models.sealed.StockViewState
import com.app.syspoint.repository.database.bean.InventarioBean
import com.app.syspoint.repository.database.dao.PrinterDao
import com.app.syspoint.repository.database.dao.StockDao
import com.app.syspoint.ui.bluetooth.BluetoothActivity
import com.app.syspoint.ui.stock.activities.ConfirmaInventarioActivity
import com.app.syspoint.ui.stock.activities.ListaProductosInventarioActivity
import com.app.syspoint.ui.stock.adapter.AdapterInventario
import com.app.syspoint.utils.Actividades
import com.app.syspoint.utils.setInvisible
import com.app.syspoint.utils.setVisible
import com.app.syspoint.viewmodel.stock.StockViewModel
import libs.mjn.prettydialog.PrettyDialog
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.util.*

class StockFragment: Fragment() {

    private lateinit var binding: FragmentStockBinding
    private lateinit var viewModel: StockViewModel
    private lateinit var adapter: AdapterInventario

    // Bluetooth
    private var mHandler: Handler? = null // Our main handler that will receive callback notifications
    private lateinit var BTAdapter: BluetoothAdapter
    private var mConnectedThread: ConnectedThread? = null // bluetooth background worker thread to send and receive data
    private var mBTSocket: BluetoothSocket? = null // bi-directional client-to-client data path

    //Connection bluetooth
    private val BT_MODULE_UUID =
        UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // "random" unique identifier


    // #defines for identifying shared types between calling functions
    private val REQUEST_ENABLE_BT = 1 // used to identify adding bluetooth names
    private val MESSAGE_READ = 2 // used in bluetooth handler to identify message update
    private val CONNECTING_STATUS = 3 // used in bluetooth handler to identify message status


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this)[StockViewModel::class.java]
        viewModel.stockViewState.observe(viewLifecycleOwner, ::renderViewState)

        BTAdapter = BluetoothAdapter.getDefaultAdapter()

        if (isConfigPrinter()) {
            if (!isBluetoothEnabled()) {
                //Pregunta si queremos activar el bluetooth
                val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBluetooth, 0)
            } else {
                viewModel.setUpPrinter()
            }
        } else {
            Actividades.getSingleton(activity, BluetoothActivity::class.java).muestraActividad()
        }

        // Ask for location permission if not already allowed
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1
        )

        val mHandler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_READ) {
                    var readMessage: String? = null
                    try {
                        readMessage = String((msg.obj as ByteArray)!!, "UTF-8")
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                    //textViewStatus.setText(readMessage);
                }
                if (msg.what == CONNECTING_STATUS) {
                    if (msg.arg1 == 1) {
                        //textViewStatus.setTextColor(Color.GREEN);
                        //textViewStatus.setText("Puede imprimir el documento dando click en la parte superior");
                    } else {
                        //textViewStatus.setTextColor(Color.RED);
                        //textViewStatus.setText("¡Dispositivo Bluetooth no encontrado!");
                        viewModel.setUpPrinter()
                    }
                }
            }
        }

        viewModel.setUpStock()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshStock()
    }

    override fun onDestroy() {
        super.onDestroy()
        mConnectedThread?.cancel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_inventarios_opciones, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.item_menu_inventario_add -> {
                Actividades.getSingleton(requireActivity(), ListaProductosInventarioActivity::class.java)
                    .muestraActividadForResult(Actividades.PARAM_INT_1)
                return true
            }
            R.id.item_menu_inventario_finish -> {
                viewModel.finishStock()
                return true
            }
            R.id.close_caja -> {
                viewModel.hanldeCloseStock()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun renderViewState(stockViewState: StockViewState) {
        when(stockViewState) {
            is StockViewState.EmptyStockState -> {
                showEmptyStockDialog()
            }
            is StockViewState.ConfirmStockState -> {
                Actividades.getSingleton(requireActivity(), ConfirmaInventarioActivity::class.java).muestraActividad()
            }
            is StockViewState.CannotCloseStockState -> {
                showCannotCloseCurrentStockDialog()
            }
            is StockViewState.CloseCurrentStockState -> {
                showCloseCurrentStockDialog()
            }
            is StockViewState.ClosedStockState -> {
                refreshRecyclerView(stockViewState.data)
            }
            is StockViewState.RefreshStockState -> {
                refreshRecyclerView(stockViewState.data)
            }
            is StockViewState.SetUpStockState -> {
                setUpRecyclerView(stockViewState.data)
            }
            is StockViewState.SetUpPrinterState -> {
                initPrinter()
            }
        }
    }

    private fun setUpRecyclerView(data: List<InventarioBean?>) {
        viewModel.refreshStock()

        binding.rvInventarioPendiente.setHasFixedSize(true)

        val manager = LinearLayoutManager(activity)
        binding.rvInventarioPendiente.layoutManager = manager

        adapter = AdapterInventario(data,
            AdapterInventario.OnItemLongClickListener { position ->
                val inventarioBean: InventarioBean? = data[position]
                if (inventarioBean != null && inventarioBean.estado.compareTo("CO", ignoreCase = true) == 0) {
                    val dialog = PrettyDialog(context)
                    dialog.setTitle("Eliminar")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("No es posible eliminar el inventario ya fue confirmado")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(
                            R.drawable.pdlg_icon_close, R.color.purple_500
                        ) { dialog.dismiss() }
                        .addButton(
                            getString(R.string.ok_dialog),
                            R.color.pdlg_color_white,
                            R.color.quantum_orange
                        ) { dialog.dismiss() }
                    dialog.setCancelable(false)
                    dialog.show()
                    return@OnItemLongClickListener false
                } else {
                    val stockDao = StockDao()
                    stockDao.delete(inventarioBean)
                    viewModel.refreshStock()
                }
                false
            })
        binding.rvInventarioPendiente.adapter = adapter
    }

    private fun refreshRecyclerView(data: List<InventarioBean?>) {
        adapter.setInventario(data)

        if (data.isNotEmpty()) {
            binding.emptyStateInventory.setInvisible()
        } else {
            binding.emptyStateInventory.setVisible()
        }
    }

    private fun showEmptyStockDialog() {
        val dialog = PrettyDialog(context)
        dialog.setTitle("Sin inventario")
            .setTitleColor(R.color.purple_500)
            .setMessage("No hay productos por inventariar")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(
                R.drawable.pdlg_icon_info, R.color.purple_500
            ) { dialog.dismiss() }
            .addButton(
                getString(R.string.ok_dialog), R.color.black, R.color.quantum_orange
            ) { dialog.dismiss() }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showCannotCloseCurrentStockDialog() {
        val dialog = PrettyDialog(context)
        dialog.setTitle("Sin inventario")
            .setTitleColor(R.color.purple_500)
            .setMessage("No es posible cerrar no hay inventario previo")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(
                R.drawable.pdlg_icon_info, R.color.purple_500
            ) { dialog.dismiss() }
            .addButton(
                getString(R.string.ok_dialog), R.color.black, R.color.quantum_orange
            ) { dialog.dismiss() }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showCloseCurrentStockDialog() {
        val dialogo = PrettyDialog(context)
        dialogo.setTitle("Cierre")
            .setTitleColor(R.color.purple_500)
            .setMessage("¿Desea cerrar la caja?")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(
                R.drawable.ic_save_white, R.color.purple_500
            ) { dialogo.dismiss() }
            .addButton(
                getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800
            ) { // final InventarioBean inventarioBean = new InventarioBean();
                // TicketCierre ticketInventario = new TicketCierre(getActivity());
                // ticketInventario.setInventarioBean(inventarioBean);
                // ticketInventario.template();
                // String ticket = ticketInventario.getDocumento();
                if (isConfigPrinter()) {
                    if (!isBluetoothEnabled()) {
                        val enableBluetooth = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        startActivityForResult(enableBluetooth, 0)
                    }
                    viewModel.setUpPrinter()
                }
                val inventarioBean = InventarioBean()
                val ticketInventario = CloseTicket(requireActivity())
                ticketInventario.bean = inventarioBean
                ticketInventario.template()
                val ticket = ticketInventario.document
                mConnectedThread?.write(ticket)
                viewModel.closeStock()
                dialogo.dismiss()
            }
            .addButton(
                getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900
            ) { dialogo.dismiss() }
        dialogo.setCancelable(false)
        dialogo.show()
    }

    private fun initPrinter() {
            val existeImpresora = PrinterDao()
            val existe = existeImpresora.existeConfiguracionImpresora()
            if (existe > 0) {
                val establecida = existeImpresora.getImpresoraEstablecida()
                if (establecida != null) {
                    if (establecida != null) {
                        if (!BTAdapter.isEnabled()) {
                            Toast.makeText(context, "Bluetooth no encendido", Toast.LENGTH_SHORT)
                                .show()
                            return
                        }

                        //textViewStatus.setText("Conectado....");

                        // Spawn a new thread to avoid blocking the GUI one
                        object : Thread() {
                            override fun run() {
                                var fail = false
                                val device: BluetoothDevice =
                                    BTAdapter.getRemoteDevice(establecida.address)
                                try {
                                    mBTSocket = createBluetoothSocket(device)
                                } catch (e: IOException) {
                                    fail = true
                                    Toast.makeText(
                                        activity,
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
                                        mHandler.obtainMessage(
                                            CONNECTING_STATUS,
                                            -1,
                                            -1
                                        )
                                            .sendToTarget()
                                    } catch (e2: IOException) {
                                        //insert code to deal with this
                                        Toast.makeText(
                                            activity,
                                            "Falló la creación de socket",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                if (!fail) {
                                    mConnectedThread = ConnectedThread(mBTSocket, mHandler)
                                    mConnectedThread.start()
                                    mHandler.obtainMessage(
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
                Actividades.getSingleton(activity, BluetoothActivity::class.java).muestraActividad()
            }
    }

}
*/