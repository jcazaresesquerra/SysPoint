package com.app.syspoint.ui.ventas

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.app.syspoint.R
import com.app.syspoint.databinding.ActivityCantidadBinding
import com.app.syspoint.repository.objectBox.dao.ProductDao
import com.app.syspoint.utils.Actividades
import com.app.syspoint.utils.PrettyDialog
import com.app.syspoint.utils.click
import com.app.syspoint.utils.setVisible

class CantidadActivity: AppCompatActivity() {

    private lateinit var binding: ActivityCantidadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCantidadBinding.inflate(layoutInflater)

        val barcode = intent.extras?.getString(Actividades.PARAM_1) ?: ""
        val productBox = ProductDao().getProductoByBarCode(barcode)

        productBox?.let {
            binding.textViewProduct.text = productBox.descripcion
            binding.textViewProduct.textSize = 24f // Tamaño de fuente en sp (puntos)
            binding.textViewProduct.setVisible()
        }

        setContentView(binding.root)
        initControls()
    }

    private fun initControls() {
        binding.buttonSeleccionarCantidadVenta click {
            val cantidad: String = binding.edittextCantidadVentaSeleccionada.text.toString()
            if (cantidad.isEmpty()) {
                val dialog = PrettyDialog(this@CantidadActivity)
                dialog.setTitle("Cantidad")
                    .setTitleColor(R.color.purple_500)
                    .setMessage("Debe ingresar la cantidad a vender")
                    .setMessageColor(R.color.purple_700)
                    .setAnimationEnabled(false)
                    .setIcon(
                        R.drawable.pdlg_icon_info,
                        R.color.purple_500
                    ) { dialog.dismiss() }
                    .addButton(
                        getString(R.string.confirmar_dialog),
                        R.color.pdlg_color_white,
                        R.color.light_blue_700
                    ) { dialog.dismiss() }
                dialog.setCancelable(false)
                dialog.show()
            } else {
                //Establece el resultado que debe de regresar
                val barCode = intent.extras?.getString(Actividades.PARAM_1) ?: ""

                val intent = Intent()
                intent.putExtra(Actividades.PARAM_1, cantidad)

                if (!barCode.isNullOrEmpty()) {
                    intent.putExtra(Actividades.PARAM_2, barCode)
                }

                setResult(RESULT_OK, intent)

                //Cierra la actividad
                finish()
            }
        }

        val imm: InputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.edittextCantidadVentaSeleccionada.windowToken, 0)
        binding.edittextCantidadVentaSeleccionada.requestFocus()
        showKeyboards(this)
    }


    fun showKeyboards(activity: Activity?) {
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }
}