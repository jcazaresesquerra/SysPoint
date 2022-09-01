package com.app.syspoint.ui.products

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.syspoint.R
import com.app.syspoint.databinding.FragmentProductoBinding
import com.app.syspoint.models.sealed.ProductViewState
import com.app.syspoint.repository.database.bean.ProductoBean
import com.app.syspoint.ui.products.activities.ActualizaProductoActivity
import com.app.syspoint.ui.products.activities.RegistrarProductoActivity
import com.app.syspoint.ui.products.adapters.AdapterListaProductos
import com.app.syspoint.utils.*
import com.app.syspoint.viewmodel.produts.ProductViewModel

class ProductFragment: Fragment() {

    private lateinit var binding: FragmentProductoBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: AdapterListaProductos
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]
        viewModel.productViewState.observe(viewLifecycleOwner, ::renderViewState)
        viewModel.setUpProducts()
        setUpListeners()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshProducts()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_producto_fragment, menu)
        val searchMenuItem = menu.findItem(R.id.search)
        val searchView = searchMenuItem.actionView as SearchView

        searchView onFocusChange { _, hasFocus ->
            if (!hasFocus) {
                searchMenuItem.collapseActionView()
                searchView.setQuery("", false)
            }
        }

        searchView.onQueryText({ arg ->
            adapter.filter.filter(arg)
        },{ arg ->
            adapter.filter.filter(arg)
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.syncProductos -> {
                viewModel.checkConnectivity()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    private fun renderViewState(productViewState: ProductViewState) {
        when(productViewState) {
            is ProductViewState.SetUpProductsState -> {
                initRecyclerView(productViewState.data)
            }
            is ProductViewState.RefreshProductsState -> {
                refreshRecyclerView(productViewState.data)
            }
            is ProductViewState.ShowProgressState -> {
                progressDialog = ProgressDialog(activity)
                progressDialog.setMessage("Espere un momento")
                progressDialog.setCancelable(false)
                progressDialog.show()
            }
            is ProductViewState.DismissProgressState -> {
                if (::progressDialog.isInitialized)
                    progressDialog.dismiss()
            }
            is ProductViewState.NetworkDisconnectedState -> {
                showDialogNotConnectionInternet()
            }
            is ProductViewState.EditProductState -> {
                editProduct(productViewState.item)
            }
            is ProductViewState.CanNotEditProductState -> {
                Toast.makeText(
                    requireContext(),
                    "No tienes privilegios para esta area",
                    Toast.LENGTH_LONG
                ).show()
            }
            is ProductViewState.LoadingStartState -> {
                binding.rlprogressProductos.setVisible()
            }
            is ProductViewState.LoadingFinishState -> {
                binding.rlprogressProductos.setInvisible()
            }
            is ProductViewState.GetProductsSuccess -> {
                refreshRecyclerView(productViewState.products)
            }
            is ProductViewState.GetProductsError -> {
                Toast.makeText(
                    requireContext(),
                    "Ha ocurrido un problema, vuelve a intentarlo",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setUpListeners() {
        binding.fabAddProducto click {
            showRegisterProduct()
        }
    }

    private fun refreshRecyclerView(data: List<ProductoBean?>) {
        if (::adapter.isInitialized) {
            adapter.setProducts(data)
            if (data.isNotEmpty()) {
                binding.lytProductos.setInvisible()
            } else {
                binding.lytProductos.setVisible()
            }
        }
    }

    private fun initRecyclerView(data: List<ProductoBean?>) {
        if (data.isNotEmpty()) {
            binding.lytProductos.setInvisible()
        } else {
            binding.lytProductos.setVisible()
        }

        binding.rvListaProductos.setHasFixedSize(true)

        val manager = LinearLayoutManager(activity)
        binding.rvListaProductos.layoutManager = manager

        adapter = AdapterListaProductos(data) { position ->
            showSelectionFunction(position)
        }
        binding.rvListaProductos.adapter = adapter
    }

    private fun showSelectionFunction(productBean: ProductoBean) {

        val builderSingle = AlertDialog.Builder(requireContext())
        builderSingle.setIcon(R.drawable.logo)
        builderSingle.setTitle("Seleccionar opción")

        val arrayAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line)
        arrayAdapter.add("Editar")

        builderSingle.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }

        builderSingle.setAdapter(arrayAdapter) { dialog, which ->
            val name = arrayAdapter.getItem(which)
            viewModel.handleSelection(name, productBean)
            dialog.dismiss()
        }

        builderSingle.show()
    }

    private fun showRegisterProduct() {
        val intent = Intent(requireContext(), RegistrarProductoActivity::class.java)
        startActivity(intent)
    }

    private fun showDialogNotConnectionInternet() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_warning)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById<View>(R.id.bt_close) as AppCompatButton) click  {
            viewModel.getData()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun editProduct(item: String) {
        val params = HashMap<String, String>()
        params[Actividades.PARAM_1] = item
        Actividades.getSingleton(activity, ActualizaProductoActivity::class.java).muestraActividad(params)
    }
}