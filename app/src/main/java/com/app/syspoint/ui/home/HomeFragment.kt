package com.app.syspoint.ui.home

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.syspoint.R
import com.app.syspoint.databinding.FragmentHomeBinding
import com.app.syspoint.models.sealed.HomeViewState
import com.app.syspoint.repository.database.bean.ClientesRutaBean
import com.app.syspoint.ui.customs.DialogoRuteo
import com.app.syspoint.ui.customs.DialogoRuteo.DialogListener
import com.app.syspoint.ui.home.activities.MapsRuteoActivity
import com.app.syspoint.ui.home.adapter.AdapterRutaClientes
import com.app.syspoint.ui.ventas.VentasActivity
import com.app.syspoint.utils.*
import com.app.syspoint.viewmodel.home.HomeViewModel
import libs.mjn.prettydialog.PrettyDialog

/*class HomeFragment: Fragment() {

    companion object {
        private const val TAG = "HomeFragment"
    }

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: AdapterRutaClientes

    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        viewModel.homeViewState.observe(viewLifecycleOwner, ::renderViewState)

        setHasOptionsMenu(true)

        if (Constants.solictaRuta) {
            viewModel.createSelectedRute()
        }

        viewModel.setUpClientRute()
        viewModel.updateCredits()

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home_fragment, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sinronizaAll -> {
                val progressDialog = ProgressDialog(activity)
                progressDialog.setMessage("Espere un momento")
                progressDialog.setCancelable(false)
                progressDialog.show()
                Handler().postDelayed({
                    NetworkStateTask { connected: Boolean ->
                        progressDialog.dismiss()
                        if (!connected) {
                            showDialogNotConnectionInternet()
                        } else {
                            viewModel.getData()
                        }
                    }.execute()
                }, 100)
                true
            }
            R.id.close_caja -> {
                closeBox()
                true
            }
            R.id.viewMap -> {
                Actividades.getSingleton(activity, MapsRuteoActivity::class.java).muestraActividad()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun renderViewState(homeViewState: HomeViewState) {
        when(homeViewState) {
            is HomeViewState.ClientRuteDefined -> {
                initRecyclerView(homeViewState.clientRute)
            }
            is HomeViewState.LoadingStart -> {
                progressDialog = ProgressDialog(requireActivity())
                progressDialog.setMessage("Espere un momento")
                progressDialog.setCancelable(false)
                progressDialog.show()
            }
            is HomeViewState.GettingUpdates -> {
                progressDialog = ProgressDialog(requireActivity())
                progressDialog.setMessage("Obteniendo actualizaciones...")
                progressDialog.show()
            }
            is HomeViewState.LoadingFinish -> {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
            }
            is HomeViewState.ErrorWhileGettingData -> {
                Toast.makeText(requireActivity(), "Ha ocurrido un error", Toast.LENGTH_LONG).show()
            }
            is HomeViewState.CreateRute -> {
                createRuteDialog()
            }
            is HomeViewState.UpdateRute -> {
                updateRuteDialog()
            }
            is HomeViewState.RuteLoaded -> {
                refreshRecyclerView(homeViewState.data)
            }
        }
    }

    private fun refreshRecyclerView(clientRute: List<ClientesRutaBean?>) {
        adapter.setData(clientRute)
        if (clientRute.isNotEmpty()) {
            binding.lytClientes.setInvisible()
        } else {
            binding.lytClientes.setVisible()
        }
    }

    private fun initRecyclerView(clientRute: List<ClientesRutaBean>) {
        binding.lytClientes.apply {
            if (clientRute.isNotEmpty()) {
                setInvisible()
            } else {
                setVisible()
            }
        }

        binding.rvListaClientes.setHasFixedSize(true)
        val manager = LinearLayoutManager(context)
        binding.rvListaClientes.layoutManager = manager

        adapter = AdapterRutaClientes(clientRute, object: AdapterRutaClientes.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val clientBean: ClientesRutaBean = clientRute[position]
                goSellActivity(clientBean.cuenta)
            }
        }, object: AdapterRutaClientes.OnItemLongClickListener {
            override fun onItemLongClicked(position: Int): Boolean {
                return false
            }
        })

        binding.rvListaClientes.adapter = adapter
    }

    private fun createRuteDialog() {
        val dialogoRuteo = DialogoRuteo(requireActivity(), object : DialogListener {
            override fun ready(day: String, rute: String) {

                //Preguntamos si queremos agregar un nuevo ruteo
                val dialog = PrettyDialog(context)
                dialog.setTitle("Establecer")
                    .setTitleColor(R.color.purple_500)
                    .setMessage("Desea establecer la ruta inicial")
                    .setMessageColor(R.color.purple_700)
                    .setAnimationEnabled(false)
                    .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) {
                        dialog.dismiss()
                    }
                    .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {
                        viewModel.confirmRute(day, rute)
                        Toast.makeText(activity, "La ruta se cargo con exito!", Toast.LENGTH_LONG)
                            .show()
                        dialog.dismiss()
                    }
                    .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900) {
                        dialog.dismiss()
                    }
                dialog.setCancelable(false)
                dialog.show()
            }

            override fun cancelled() {}
        })


        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogoRuteo.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        dialogoRuteo.show()
        dialogoRuteo.window?.attributes = lp
    }

    private fun updateRuteDialog() {
        val dialog = PrettyDialog(context)
        dialog.setTitle("Establecer")
            .setTitleColor(R.color.red_500)
            .setMessage("¡Ya existe una configuración inicial! \n¿Desea actualizar la ruta?")
            .setMessageColor(R.color.red_500)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.pdlg_icon_info, R.color.red_500) { dialog.dismiss() }
            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {
                createRuteDialog()
                dialog.dismiss()
            }
            .addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900) {
                dialog.dismiss()
            }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun closeBox() {
        val dialog = PrettyDialog(requireActivity())
        dialog.setTitle("Corte del día")
            .setTitleColor(R.color.purple_500)
            .setMessage("Desea realizar el corte del día")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) { dialog.dismiss() }
            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {
                dialog.dismiss()
            }.addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900) {
                dialog.dismiss()
            }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showDialogNotConnectionInternet() {
        val dialog = Dialog(requireActivity())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_warning)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById<View>(R.id.bt_close) as AppCompatButton) click {
            viewModel.getData()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun goSellActivity(account: String) {
        val params = HashMap<String, String>()
        params[Actividades.PARAM_1] = account
        Actividades.getSingleton(activity, VentasActivity::class.java).muestraActividad(params)
    }
}*/