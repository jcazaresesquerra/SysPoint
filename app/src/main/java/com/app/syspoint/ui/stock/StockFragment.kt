package com.app.syspoint.ui.stock

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.app.syspoint.R
import com.app.syspoint.bluetooth.ConnectedThread
import com.app.syspoint.databinding.FragmentStockBinding
import com.app.syspoint.models.sealed.StockViewState
import com.app.syspoint.repository.database.bean.InventarioBean
import com.app.syspoint.ui.stock.activities.ListaProductosInventarioActivity
import com.app.syspoint.ui.stock.adapter.AdapterInventario
import com.app.syspoint.utils.Actividades
import com.app.syspoint.utils.setInvisible
import com.app.syspoint.utils.setVisible
import com.app.syspoint.viewmodel.stock.StockViewModel

class StockFragment: Fragment() {

    private lateinit var binding: FragmentStockBinding
    private lateinit var viewModel: StockViewModel
    private lateinit var adapter: AdapterInventario

    // Bluetooth
    private lateinit var BTAdapter: BluetoothAdapter
    private val mConnectedThread: ConnectedThread? = null // bluetooth background worker thread to send and receive data
    private val mBTSocket: BluetoothSocket? = null // bi-directional client-to-client data path


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStockBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this)[StockViewModel::class.java]
        viewModel.stockViewState.observe(viewLifecycleOwner, ::renderViewState)

        BTAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    override fun onResume() {
        super.onResume()
        refreshRecyclerView()
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
                Actividades.getSingleton(activity, ListaProductosInventarioActivity::class.java)
                    .muestraActividadForResult(Actividades.PARAM_INT_1)
            }
            R.id.item_menu_inventario_finish -> {
                return true
            }
            R.id.close_caja -> {
                return true
            }else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun renderViewState(stockViewState: StockViewState) {

    }

    private fun setUpRecyclerView(data: List<InventarioBean?>) {

    }

    private fun refreshRecyclerView(data: List<InventarioBean?>) {
        adapter.setInventario(data)

        if (data.isNotEmpty()) {
            binding.emptyStateInventory.setInvisible()
        } else {
            binding.emptyStateInventory.setVisible()
        }
    }
}