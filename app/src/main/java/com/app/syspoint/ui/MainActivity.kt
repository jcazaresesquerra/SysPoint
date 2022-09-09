package com.app.syspoint.ui

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.app.syspoint.BuildConfig
import com.app.syspoint.R
import com.app.syspoint.databinding.ActivityMainBinding
import com.app.syspoint.databinding.NavHeaderMainBinding
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.NetworkStateTask

class MainActivity: AppCompatActivity() {

    companion object {
        @JvmStatic
        var apikey: String? = null
        const val IS_ADMIN = "is_admin"
    }

    private var mAppBarConfiguration: AppBarConfiguration? = null
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        setUpLogo()

        val isAdmin = intent.getBooleanExtra(IS_ADMIN, false)

        mAppBarConfiguration =
            if (isAdmin) {
                AppBarConfiguration.Builder(
                    R.id.nav_home,
                    R.id.nav_ruta,
                    R.id.nav_empleado,
                    R.id.nav_producto,
                    R.id.nav_cliente,
                    R.id.nav_historial,
                    R.id.nav_inventario,
                    R.id.nav_cobranza
                )
            } else {
                AppBarConfiguration.Builder(
                    R.id.nav_home,
                    R.id.nav_ruta,
                    R.id.nav_empleado,
                    R.id.nav_producto,
                    R.id.nav_cliente,
                    R.id.nav_historial
                )
            }.setDrawerLayout(binding.drawerLayout)
                .build()

        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            val progressDialog = ProgressDialog(this@MainActivity)
            progressDialog.setMessage("Espere un momento")
            progressDialog.setCancelable(false)
            progressDialog.show()
            Handler().postDelayed({
                NetworkStateTask { connected: Boolean ->
                    progressDialog.dismiss()
                    if (!connected) showDialogNotInternet()
                }.execute()
            }, 100)
            if (destination.id == R.id.nav_ruta) {
                Constants.solictaRuta = true
            }
            if (destination.id == R.id.nav_home) {
                Constants.solictaRuta = false
            }
        }

        binding.navView.apply {
            menu.clear()
            if (isAdmin)
                inflateMenu(R.menu.activty_main_drawer_admin)
            else
                inflateMenu(R.menu.activity_main_drawer)
        }

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(binding.navView, navController)
        apikey = getString(R.string.google_maps_key)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        return NavigationUI.navigateUp(navController, mAppBarConfiguration!!)
                || super.onSupportNavigateUp()
    }

    private fun showDialogNotInternet() {
        val dialog = Dialog(this@MainActivity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // before
        dialog.setContentView(R.layout.no_internet_dialog_warning)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById<View>(R.id.bt_close) as AppCompatButton).setOnClickListener { dialog.dismiss() }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun setUpLogo() {
        val navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))

        when (BuildConfig.FLAVOR) {
            "donaqui" -> {
                navHeaderMainBinding.root.setBackgroundColor(resources.getColor(R.color.white))
                navHeaderMainBinding.imageView.setImageResource(R.drawable.logo_donaqui)
            }
            else -> {
                navHeaderMainBinding.imageView.setImageResource(R.drawable.logo)
            }
        }
    }
}