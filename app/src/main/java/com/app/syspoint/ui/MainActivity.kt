package com.app.syspoint.ui

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.app.syspoint.BuildConfig
import com.app.syspoint.R
import com.app.syspoint.databinding.ActivityMainBinding
import com.app.syspoint.databinding.NavHeaderMainBinding
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.interactor.data.GetAllDataInteractor
import com.app.syspoint.interactor.data.GetAllDataInteractorImp
import com.app.syspoint.repository.database.bean.AppBundle
import com.app.syspoint.repository.database.dao.RolesDao
import com.app.syspoint.utils.Constants
import com.app.syspoint.utils.NetworkStateTask

class MainActivity: BaseActivity() {

    companion object {
        @JvmStatic
        var apikey: String? = null
        const val IS_ADMIN = "is_admin"
    }

    private var mAppBarConfiguration: AppBarConfiguration? = null
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)
        setUpLogo()

        val isAdmin = intent.getBooleanExtra(IS_ADMIN, false)

        var vendedoresBean = AppBundle.getUserBean()
        if (vendedoresBean == null) vendedoresBean = CacheInteractor().getSeller()
        val identificador = if (vendedoresBean != null) vendedoresBean.getIdentificador() else ""

        val rolesDao = RolesDao()
        val productsRolesBean = rolesDao.getRolByEmpleado(identificador, "Productos")
        val employeesRolesBean = rolesDao.getRolByEmpleado(identificador, "Empleados")

        val productsActive = productsRolesBean?.active ?: false
        val employeesActive = employeesRolesBean?.active ?: false

        binding.navView.apply {
            menu.clear()
            inflateMenu(R.menu.activity_main_drawer)
        }

        configureMenu(isAdmin, employeesActive, productsActive)

        mAppBarConfiguration =
            AppBarConfiguration.Builder(buildMenuSet(isAdmin, employeesActive, productsActive))
           .setDrawerLayout(binding.drawerLayout)
                .build()

        navController = findNavController(R.id.nav_host_fragment)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            /*val progressDialog = ProgressDialog(this@MainActivity)
            progressDialog.setMessage("Espere un momento")
            progressDialog.setCancelable(false)
            progressDialog.show()
            Handler().postDelayed({
                NetworkStateTask { connected: Boolean ->
                    progressDialog.dismiss()
                    if (!connected) showDialogNotInternet()
                }.execute()
            }, 100)*/
            if (destination.id == R.id.nav_ruta) {
                Constants.solictaRuta = true
            }
            if (destination.id == R.id.nav_home) {
                Constants.solictaRuta = false
            }
        }

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration!!)
        NavigationUI.setupWithNavController(binding.navView, navController)
        apikey = getString(R.string.google_maps_key)

        getUpdates()
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

    override fun getView(): View {
        return binding.root
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
                navHeaderMainBinding.imageView?.let { it.setImageResource(R.drawable.logo_donaqui) }
            }
            else -> {
                navHeaderMainBinding.imageView?.let { it.setImageResource(R.drawable.logo) }
            }
        }
    }

    private fun configureMenu(isAdmin: Boolean, employeesActive: Boolean, productsActive: Boolean) {
        val menu = binding.navView.menu
        menu.findItem(R.id.nav_home).isVisible = true
        menu.findItem(R.id.nav_producto).isVisible = true
        menu.findItem(R.id.nav_empleado).isVisible = employeesActive
        menu.findItem(R.id.nav_producto).isVisible = productsActive
        menu.findItem(R.id.nav_cliente).isVisible = true
        menu.findItem(R.id.nav_historial).isVisible = true
        menu.findItem(R.id.nav_inventario).isVisible = isAdmin
        menu.findItem(R.id.nav_cobranza).isVisible = isAdmin
    }

    private fun buildMenuSet(isAdmin: Boolean, employeesActive: Boolean, productsActive: Boolean): Set<Int> {
        //Obtiene el nombre del vendedor
        val menuSet = mutableSetOf(R.id.nav_home, R.id.nav_ruta)

        if (employeesActive) menuSet.add(R.id.nav_empleado)
        if (productsActive) menuSet.add(R.id.nav_producto)

        menuSet.add(R.id.nav_cliente)
        menuSet.add(R.id.nav_historial)

        if (isAdmin) {
            menuSet.add(R.id.nav_inventario)
            menuSet.add(R.id.nav_cobranza)
        }

        return menuSet
    }

    fun goHome() {
        if (::navController.isInitialized) {
            navController.navigate(R.id.nav_home)
        }
    }

    private fun getUpdates() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Espere un momento")
        progressDialog.setCancelable(false)
        progressDialog.show()

        Handler().postDelayed({
            NetworkStateTask { connected: Boolean ->
                progressDialog.dismiss()
                if (connected) {
                    progressDialog.setMessage("Obteniendo actualizaciones...");

                    progressDialog.show();
                    GetAllDataInteractorImp().executeGetAllDataByDate(object:  GetAllDataInteractor.OnGetAllDataByDateListener {
                        override fun onGetAllDataByDateSuccess() {
                            progressDialog.dismiss()
                        }

                        override fun onGetAllDataByDateError() {
                            progressDialog.dismiss()
                        }
                    })
                }
            }.execute()}
            ,100);
    }
}