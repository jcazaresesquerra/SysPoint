package com.app.syspoint.ui.cobranza

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.syspoint.R
import com.app.syspoint.databinding.ActivityCobranzaBinding
import com.app.syspoint.databinding.EncabezadoCobranzaBinding
import com.app.syspoint.models.sealed.ChargeViewState
import com.app.syspoint.repository.objectBox.dao.ChargeModelDao
import com.app.syspoint.repository.objectBox.entities.ChargeModelBox
import com.app.syspoint.ui.cobranza.adapter.AdapterCobranza
import com.app.syspoint.utils.*
import com.app.syspoint.viewmodel.charge.ChargeViewModel

class CobranzaActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCobranzaBinding
    private lateinit var headerBinding: EncabezadoCobranzaBinding
    private lateinit var viewModel: ChargeViewModel
    private lateinit var adapter: AdapterCobranza

    private lateinit var progressDialog: ProgressDialog

    private lateinit var clientId: String

    private val TAG = "ChargeViewModel"

    private var endCharge = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCobranzaBinding.inflate(layoutInflater)
        headerBinding = EncabezadoCobranzaBinding.bind(binding.cobranzaHeader.root)
        viewModel = ViewModelProvider(this)[ChargeViewModel::class.java]
        viewModel.chargeViewState.observe(this, ::renderChargeViewState)

        setContentView(binding.root)

        initToolbar()
        setUpListeners()

        clientId = intent.getStringExtra(Actividades.PARAM_1) ?: ""
        viewModel.deletePartidas(clientId)
        viewModel.setUpCharge()
        viewModel.loadClientData(clientId)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) return

        super.onActivityResult(requestCode, resultCode, data);

        when (requestCode) {
            Actividades.PARAM_INT_1 -> {
                if (viewModel.validaDocumentoRepetido()) {
                    val dialog = PrettyDialog(this)
                    dialog.setTitle("Documento")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("El documento ya fue agregado")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) {
                            dialog.dismiss();
                        }.addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800) {
                            dialog.dismiss();
                        }
                    dialog.setCancelable(false)
                    dialog.show()
                    return
                }
            }
        }

        val cobranzaSeleccionada = data?.getStringExtra(Actividades.PARAM_1)
        val importeAcuenta = data?.getStringExtra(Actividades.PARAM_2)

        try {
            viewModel.createCharge(cobranzaSeleccionada, importeAcuenta, clientId)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        viewModel.setUpCharge()
        viewModel.getTaxes(clientId)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.deletePartidas(clientId)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cobranza, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                try {
                    //Eliminamos el cobro temporal para que no se guarde en memoria
                    val dao = ChargeModelDao()
                    dao.clear()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                true
            }
            R.id.terminarCobranza -> {
                viewModel.endCharge()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun renderChargeViewState(chargeViewState: ChargeViewState) {
        when(chargeViewState) {
            is ChargeViewState.ChargeListLoaded -> {
                initRecyclerViews(chargeViewState.charges)
            }
            is ChargeViewState.ChargeListRefresh -> {
                refreshRecyclerView(chargeViewState.charges)
            }
            is ChargeViewState.LoadingStart -> {
                progressDialog = ProgressDialog(this@CobranzaActivity)
                progressDialog.setMessage("Espere un momento")
                progressDialog.setCancelable(false)
                progressDialog.show()
            }
            is ChargeViewState.LoadingFinish -> {
                if (::progressDialog.isInitialized) {
                    progressDialog.dismiss()
                }
            }
            is ChargeViewState.ChargeLoaded -> {
                headerBinding.textViewSubtotalCobranzaView.text = Utils.FDinero(chargeViewState.saldoCiente)
                //this.textView_cliente_saldo_cobranza_view.setText(Formats.FDinero(saldoDocumentos));
                headerBinding.textViewClienteSaldoCobranzaView.text = Utils.FDinero(chargeViewState.saldoCiente)
                viewModel.deletePartidas(chargeViewState.clientId)
            }
            is ChargeViewState.ClientLoaded -> {
                val headerBinding = EncabezadoCobranzaBinding.bind(binding.cobranzaHeader.root)

                headerBinding.textViewClienteCobranzaView.text = chargeViewState.clientBox.cuenta
                headerBinding.textViewClienteNombreCobranzaView.text = chargeViewState.clientBox.nombre_comercial
                //this.id_cliente_seleccionado = chargeViewState.clienteBean.cuenta
                headerBinding.textViewSubtotalCobranzaView.text = Utils.FDinero(chargeViewState.clientBox.saldo_credito)
                //this.textView_cliente_saldo_cobranza_view.setText(Formats.FDinero(saldoDocumentos));
                headerBinding.textViewClienteSaldoCobranzaView.setText(Utils.FDinero(chargeViewState.clientBox.saldo_credito))
                //this.saldoCliente = chargeViewState.clienteBean.saldo_credito
            }
            is ChargeViewState.EndChargeWithDocument -> {
                endChargeWithDocument()
            }
            is ChargeViewState.EndChargeWithoutDocument -> {
                endChargeWithOutdocument()
            }
            is ChargeViewState.UserNotFound -> {
                userNotFoundDialog()
            }
            is ChargeViewState.SellerNotFound -> {
                sellerNotFoundDialog()
            }
            is ChargeViewState.NotInternetConnection -> {
                //showDialogNotConnectionInternet()
            }
            is ChargeViewState.ComputedTaxes -> {
                setTaxes(chargeViewState.totalAmount, chargeViewState.restAmount, chargeViewState.show)
            }
            is ChargeViewState.ClientSaved -> {
                goPintTicket(chargeViewState.ticket, chargeViewState.sellId, chargeViewState.clientId)
            }
        }
    }

    private fun initToolbar() {
        binding.toolbarCobranza.title = "Cobranza"
        setSupportActionBar(binding.toolbarCobranza)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(R.color.purple_700)
    }

    private fun refreshRecyclerView(charges: List<ChargeModelBox?>) {
        if (::adapter.isInitialized) {
            adapter.setData(charges)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initRecyclerViews(charges: List<ChargeModelBox?>) {

        binding.recyclerViewCobranza.setHasFixedSize(true)

        binding.recyclerViewCobranza.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.recyclerViewCobranza.itemAnimator = DefaultItemAnimator()

        adapter = AdapterCobranza(charges, object: AdapterCobranza.OnItemLongClickListener {
            override fun onItemLongClicked(position: Int): Boolean {
                val item: ChargeModelBox? = charges[position]
                if (item != null) {
                    val dialog = PrettyDialog(this@CobranzaActivity)
                    dialog.setTitle("Eliminar")
                        .setTitleColor(R.color.purple_500)
                        .setMessage("Desea eliminar el documento ${item.venta}")
                        .setMessageColor(R.color.purple_700)
                        .setAnimationEnabled(false)
                        .setIcon(R.drawable.pdlg_icon_close, R.color.purple_500) {
                            dialog.dismiss()
                        }.addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {
                            viewModel.deletePartida(item)
                            viewModel.getTaxes(clientId)
                            dialog.dismiss()
                        }.addButton(getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900) {
                            dialog.dismiss()
                        }
                    dialog.setCancelable(false)
                    dialog.show()
                }
                return true
            }
        })
        binding.recyclerViewCobranza.adapter = adapter
    }

    private fun setUpListeners() {
        headerBinding.fbAddDocumentos click {
            Log.d(TAG, "fbAddDocumentos clicked")
            val parametros = HashMap<String, String>()
            parametros[Actividades.PARAM_1] = clientId
            Actividades.getSingleton(this@CobranzaActivity, ListaDocumentosCobranzaActivity::class.java)
                .muestraActividadForResultAndParams(Actividades.PARAM_INT_1, parametros)
            Log.d(TAG, "fbAddDocumentos finish")
        }
    }

    private fun endChargeWithOutdocument() {
        val dialog = PrettyDialog(this@CobranzaActivity)
        dialog.setTitle("Sin documento")
            .setTitleColor(R.color.purple_500)
            .setMessage("No existen documentos por cobrar")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) {
                dialog.dismiss()
            }.addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800) {
                dialog.dismiss()
            }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun endChargeWithDocument() {
        val dialog = PrettyDialog(this@CobranzaActivity)
        dialog.setTitle("Terminar")
            .setTitleColor(R.color.purple_500)
            .setMessage("¿Desea terminal la cobranza?")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.ic_save_white, R.color.purple_500) { dialog.dismiss() }
            .addButton(getString(R.string.confirmar_dialog), R.color.pdlg_color_white, R.color.green_800) {
                if (!endCharge) {
                    endCharge = true
                    dialog.dismiss()
                    Utils.addActivity2Stack(this@CobranzaActivity)
                    val headerBinding = EncabezadoCobranzaBinding.bind(binding.cobranzaHeader.root)
                    val import = headerBinding.textViewImpuestoCobranzaView.text.toString()
                    viewModel.handleEndChargeWithDocument(clientId, import)
                    endCharge = false
                }
            }.addButton(
                getString(R.string.cancelar_dialog), R.color.pdlg_color_white, R.color.red_900
            ) { dialog.dismiss() }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun userNotFoundDialog() {
        val dialog = PrettyDialog(this@CobranzaActivity)
        dialog.setTitle("Cliente")
            .setTitleColor(R.color.purple_500)
            .setMessage("Cliente no encontrado")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) {
                dialog.dismiss()
            }.addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800) {
                dialog.dismiss()
            }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun sellerNotFoundDialog() {
        val dialog = PrettyDialog(this@CobranzaActivity)
        dialog.setTitle("Vendedor")
            .setTitleColor(R.color.purple_500)
            .setMessage("Vendedor no encontrado")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(R.drawable.pdlg_icon_info, R.color.purple_500) {
                dialog.dismiss()
            }
            .addButton(getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.green_800) {
                dialog.dismiss()
            }
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showDialogNotConnectionInternet() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.dialog_warning)
        dialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById<View>(R.id.bt_close) as AppCompatButton).setOnClickListener {
            viewModel.endCharge()
            dialog.dismiss()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun setTaxes(totalAmount: Double, restAmount: Double, show: Boolean) {
        val headerBinding = EncabezadoCobranzaBinding.bind(binding.cobranzaHeader.root)

        headerBinding.textViewImpuestoCobranzaView.text = Utils.FDinero(restAmount)
        headerBinding.textViewTotalCobranzaView.text = Utils.FDinero(totalAmount - restAmount)

        if (show) {
            binding.emptyStateContainer.setVisible()
        } else {
            binding.emptyStateContainer.setInvisible()
        }
    }

    private fun goPintTicket(ticket: String, ventaID: Long, clientId: String) {
        val intent = Intent(this@CobranzaActivity, ImprimeAbonoActivity::class.java)
        intent.putExtra("ticket", ticket)
        intent.putExtra("cobranza", ventaID)
        intent.putExtra("clienteID", clientId)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}