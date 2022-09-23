package com.app.syspoint.ui.home

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.syspoint.R
import com.app.syspoint.databinding.FragmentHomeBinding
import com.app.syspoint.models.sealed.HomeViewState
import com.app.syspoint.repository.database.bean.ClientesRutaBean
import com.app.syspoint.ui.customs.DialogoRuteo
import com.app.syspoint.ui.customs.DialogoRuteo.DialogListener
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

        adapter = AdapterRutaClientes(clientRute, { position: Int ->
            val clientBean: ClientesRutaBean = clientRute[position]
            goSellActivity(clientBean.cuenta)
        }) { position: Int -> false }

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

    private fun goSellActivity(account: String) {
        val params = HashMap<String, String>()
        params[Actividades.PARAM_1] = account
        Actividades.getSingleton(activity, VentasActivity::class.java).muestraActividad(params)
    }
}*/