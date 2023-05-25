package com.app.syspoint.ui.login

import android.Manifest
import android.Manifest.permission
import android.app.DownloadManager
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.app.syspoint.App
import com.app.syspoint.BuildConfig
import com.app.syspoint.R
import com.app.syspoint.databinding.ActivityLoginBinding
import com.app.syspoint.interactor.installer.ApkInstaller
import com.app.syspoint.models.sealed.DownloadApkViewState
import com.app.syspoint.models.sealed.DownloadingViewState
import com.app.syspoint.models.sealed.LoginViewState
import com.app.syspoint.models.sealed.LoginViewState.*
import com.app.syspoint.repository.cache.SharedPreferencesManager
import com.app.syspoint.ui.MainActivity
import com.app.syspoint.utils.*
import com.app.syspoint.viewmodel.login.LoginViewModel


class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    // receiver
    private lateinit var mNetworkChangeReceiver: NetworkChangeReceiver

    private var isConnected = false
    private var isOldApkVersionDialogShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        setContentView(binding.root)

        binding.appVersion.text = getString(R.string.app_name) + " Ver. " + BuildConfig.VERSION_NAME

        setUpLogo()
        setUpListeners()
        checkPermissions()
        registerNetworkBroadcastForNougat()

        viewModel.loginViewState.observe(this, ::loginViewState)
        viewModel.downloadApkViewState.observe(this, ::downloadApkViewState)
    }

    override fun onDestroy() {
        if (viewModel.downloadingViewState.value is DownloadingViewState.StartDownloadViewState)
            SharedPreferencesManager(this).storeLocalSession(false)
        super.onDestroy()
    }

    private fun loginViewState(viewState: LoginViewState) {
        when(viewState) {
            is LoggedIn -> {
                App.INSTANCE?.plantTimber()
                showMainActivity()
            }
            is LoginError -> showErrorDialog(viewState.error)
            is LoginVersionError -> showVersionErrorDialog(viewState.error)
            is LoadingDataStart -> binding.rlprogressLogin.setVisible()
            is LoadingDataFinish -> {
                binding.rlprogressLogin.setInvisible()
                if (isConnected) {
                    hideNotInternetConnectionError()
                } else {
                    showNotInternetConnectionError()
                }
            }
            is ConnectedToInternet -> {
                isConnected = true
                hideNotInternetConnectionError()
            }
            is NotInternetConnection -> {
                isConnected = false
                binding.rlprogressLogin.setInvisible()
                showNotInternetConnectionError()
            }
            is NoSessionExists -> {
                showNotDataInApp()
            }
        }
    }

    private fun downloadApkViewState(viewState: DownloadApkViewState) {
        when (viewState) {
            is DownloadApkViewState.ApkOldVersion -> showAppOldVersion(viewState.baseUpdateUrl, viewState.versionToDownload)
            is DownloadApkViewState.DownloadApkSuccess -> {
                showAppOldVersion("", viewState.versionToDownload)
                val installer = ApkInstaller()
                installer.installApplicationFromFireBase(applicationContext, viewState.file)
            }
            is DownloadApkViewState.DownloadApkError -> {
                showAppOldVersion("", viewState.versionToDownload)
                showErrorDialog("Ocurrio un error al descargar la ultima actualización")
            }
        }
    }

    private fun setUpListeners() {
        binding.btnSignIn click {
            binding.btnSignIn.isEnabled = false
            val email: String = binding.etLoginEmail.text.toString()
            val password: String = binding.etLoginPassword.text.toString()
            val rememberSession = binding.cbRememberSession.isChecked
            viewModel.login(email, password, rememberSession)
        }
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permission.INSTALL_PACKAGES) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permission.REQUEST_INSTALL_PACKAGES) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (!(shouldShowRequestPermissionRationale(permission.CAMERA)
            || shouldShowRequestPermissionRationale(permission.WRITE_EXTERNAL_STORAGE)
            || shouldShowRequestPermissionRationale(permission.BLUETOOTH)
            || shouldShowRequestPermissionRationale(permission.READ_EXTERNAL_STORAGE)
            || shouldShowRequestPermissionRationale(permission.CALL_PHONE)
            || shouldShowRequestPermissionRationale(permission.INSTALL_PACKAGES)
            || shouldShowRequestPermissionRationale(permission.REQUEST_INSTALL_PACKAGES)
            || shouldShowRequestPermissionRationale(permission.ACCESS_FINE_LOCATION))) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestPermissions(
                    arrayOf(
                        permission.CAMERA,
                        permission.WRITE_EXTERNAL_STORAGE,
                        permission.READ_EXTERNAL_STORAGE,
                        permission.BLUETOOTH_SCAN,
                        permission.BLUETOOTH_CONNECT,
                        permission.CALL_PHONE,
                        permission.ACCESS_FINE_LOCATION,
                        permission.INSTALL_PACKAGES,
                        permission.REQUEST_INSTALL_PACKAGES
                    ), 100
                )
            } else {
                requestPermissions(
                    arrayOf(
                        permission.CAMERA,
                        permission.WRITE_EXTERNAL_STORAGE,
                        permission.READ_EXTERNAL_STORAGE,
                        permission.BLUETOOTH,
                        permission.CALL_PHONE,
                        permission.ACCESS_FINE_LOCATION,
                        permission.INSTALL_PACKAGES,
                        permission.REQUEST_INSTALL_PACKAGES
                    ), 100
                )
            }


        }
        return false
    }

    private fun showMainActivity() {
        binding.btnSignIn.isEnabled = true

        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showErrorDialog(message: String) {
        binding.btnSignIn.isEnabled = true
        val dialog = PrettyDialog(this)
        dialog.setTitle("Error")
            .setTitleColor(R.color.purple_500)
            .setMessage(message)
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(
                R.drawable.pdlg_icon_info, R.color.purple_500
            ) { dialog.dismiss() }
            .addButton(
                getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.light_blue_800
            ) { dialog.dismiss() }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showNeedsWifiErrorDialog(baseUpdateUrl: String, versionToDownload: String) {
        binding.btnSignIn.isEnabled = true
        val dialog = PrettyDialog(this)
        dialog.setTitle("Error")
            .setTitleColor(R.color.purple_500)
            .setMessage("Necesitas conectarte a una red WIFI")
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(
                R.drawable.pdlg_icon_info, R.color.purple_500
            ) {
                dialog.dismiss()
                showAppOldVersion(baseUpdateUrl, versionToDownload)
            }
            .addButton(
                getString(R.string.ok_dialog), R.color.pdlg_color_white, R.color.light_blue_800
            ) {
                dialog.dismiss()
            showAppOldVersion(baseUpdateUrl, versionToDownload)
            }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showVersionErrorDialog(message: String) {
        binding.btnSignIn.isEnabled = true
        val dialog = PrettyDialog(this)
        dialog.setTitle("Error")
            .setTitleColor(R.color.purple_500)
            .setMessage(message)
            .setMessageColor(R.color.purple_700)
            .setAnimationEnabled(false)
            .setIcon(
                R.drawable.pdlg_icon_info, R.color.purple_500
            ) {  }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun showAppOldVersion(baseUpdateUrl: String, versionToDownload: String) {
        binding.btnSignIn.isEnabled = false
        binding.etLoginEmail.isEnabled = false
        binding.etLoginPassword.isEnabled = false

        if (!isOldApkVersionDialogShowing) {
            isOldApkVersionDialogShowing = true
            val oldApkVersionDialog = OldApkVersionDialog(this)
            oldApkVersionDialog.setTitle("Error")
                .setTitleColor(R.color.purple_500)
                .setMessage("Su versión no esta soportada, por favor, actualice su aplicación")
                .setMessageColor(R.color.purple_700)
                .setAnimationEnabled(false)
                .addButton(
                    getString(R.string.download_dialog),
                    R.color.pdlg_color_white,
                    R.color.green_800
                ) {

                    if (versionToDownload.isNullOrEmpty()) {
                        showErrorDialog("Ha ocurrido un error, vuelve a intentarlo")
                    } else {
                        val connManager =
                            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                        val mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)

                        if (mWifi?.isConnected == true) {
                            // Do whatever

                            isOldApkVersionDialogShowing = false
                            oldApkVersionDialog.dismiss()

                            binding.rlprogressLogin.setVisible()

                            val downloadManager =
                                getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                            val request = DownloadManager.Request(
                                Uri.parse(
                                    Utils.getUpdateURL(baseUpdateUrl, versionToDownload)
                                )
                            )
                            val id = downloadManager.enqueue(request)

                            val downloadReceiver = DownloadReceiver(id, object : DownloadListener {
                                override fun onDownloadSuccess(uri: Uri) {
                                    binding.rlprogressLogin.setInvisible()
                                    showAppOldVersion(baseUpdateUrl, versionToDownload)
                                    ApkInstaller().installApplicationFromCpanel(
                                        applicationContext,
                                        uri
                                    )
                                    viewModel.forceUpdate(true)
                                }

                                override fun onDownloadError(error: String) {
                                    binding.rlprogressLogin.setInvisible()
                                    showAppOldVersion(baseUpdateUrl, versionToDownload)
                                    showErrorDialog(error)
                                }

                            })
                            registerReceiver(
                                downloadReceiver,
                                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
                            )


                            // just uncomment when firebase is working and paid
                            //viewModel.checkAppVersionInFirebaseStore(versionToDownload)
                        } else {
                            oldApkVersionDialog.dismiss()
                            isOldApkVersionDialogShowing = false
                            showNeedsWifiErrorDialog(baseUpdateUrl, versionToDownload)
                        }
                    }

                }
                .setIcon(
                    R.drawable.pdlg_icon_info, R.color.purple_500
                ) { }

            oldApkVersionDialog.setCancelable(false)
            oldApkVersionDialog.show()
        }

    }

    private fun setUpLogo() {
        when (BuildConfig.FLAVOR) {
            "donaqui" -> {
                binding.imageView.setImageResource(R.drawable.logo_donaqui)
            }
            else -> {
                binding.imageView.setImageResource(R.drawable.tenet_land)
            }
        }
    }

    private fun showNotInternetConnectionError() {
        showError("No tiene conexión a internet")
        binding.etLoginEmail.isEnabled = true
        binding.etLoginPassword.isEnabled = true
        binding.btnSignIn.isEnabled = true
        binding.errorLogin.setVisible()
        //binding.etLoginEmail.inputType = InputType.TYPE_NULL
        //binding.etLoginPassword.inputType = InputType.TYPE_NULL
    }

    private fun showNotDataInApp() {
        showError("No hay regitros en la base de datos, verifique su conexion a internet y vuelva a intentar.")
        binding.etLoginEmail.isEnabled = false
        binding.etLoginPassword.isEnabled = false
        binding.btnSignIn.isEnabled = false
        binding.errorLogin.setVisible()
        //binding.etLoginEmail.inputType = InputType.TYPE_NULL
        //binding.etLoginPassword.inputType = InputType.TYPE_NULL
    }

    private fun hideNotInternetConnectionError() {
        showError("Conectado")
        binding.etLoginEmail.isEnabled = true
        binding.etLoginPassword.isEnabled = true
        binding.btnSignIn.isEnabled = true
        //binding.etLoginEmail.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        //binding.etLoginPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        binding.errorLogin.setGone()
    }

    private fun showError(error: String) {
        binding.errorLogin.text = error
    }

    private fun registerNetworkBroadcastForNougat() {
        mNetworkChangeReceiver = NetworkChangeReceiver(object : ConnectionNetworkListener {
            override fun onConnected() {
                isConnected = true
                hideNotInternetConnectionError()
                viewModel.validateToken()
            }
            override fun onDisconnected() {
                isConnected = false
            }
        })
        registerReceiver(
            mNetworkChangeReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    /**
     * Connection Listener
     */
    interface ConnectionNetworkListener {
        fun onConnected()
        fun onDisconnected()
    }

    /**
     * Connection BroadcastReceiver
     */
    class NetworkChangeReceiver(): BroadcastReceiver() {
        private lateinit var mConnectionNetworkListener: ConnectionNetworkListener

        constructor(connectionNetworkListener: ConnectionNetworkListener): this() {
            mConnectionNetworkListener = connectionNetworkListener
        }

        override fun onReceive(context: Context?, p1: Intent?) {
            try {
                val cm = context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = cm.activeNetworkInfo
                val isConnected = netInfo != null && netInfo.isConnected
                if (isConnected) {
                    mConnectionNetworkListener.onConnected()
                } else {
                    mConnectionNetworkListener.onDisconnected()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mConnectionNetworkListener.onDisconnected()
            }
        }
    }

    /**
     * Connection Listener
     */
    interface DownloadListener {
        fun onDownloadSuccess(uri: Uri)
        fun onDownloadError(error: String)
    }

    class DownloadReceiver(): BroadcastReceiver() {
        private var id: Long = 0
        private lateinit var downloadListener: DownloadListener

        constructor(id: Long, downloadListener: DownloadListener): this() {
            this.downloadListener = downloadListener
            this.id = id
        }

        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                val downloadManager = context!!.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor = downloadManager.query(DownloadManager.Query().setFilterById(id))
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            downloadListener.onDownloadSuccess(downloadManager.getUriForDownloadedFile(id))
                        } else {
                            downloadListener.onDownloadError("Error al descargar la aplicación")
                        }
                    }
                } catch (e: Exception) {
                    downloadListener.onDownloadError("Error al descargar la aplicación")
                    e.printStackTrace()
                } finally {
                    cursor?.close()
                }
            }
        }
    }
}

