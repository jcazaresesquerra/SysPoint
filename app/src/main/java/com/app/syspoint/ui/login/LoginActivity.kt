package com.app.syspoint.ui.login

import android.Manifest.permission
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.app.syspoint.BuildConfig
import com.app.syspoint.ui.MainActivity
import com.app.syspoint.R
import com.app.syspoint.databinding.ActivityLoginBinding
import com.app.syspoint.utils.click
import libs.mjn.prettydialog.PrettyDialog

class LoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        viewModel = LoginViewModel()

        setContentView(binding.root)

        binding.appVersion.text = getString(R.string.app_name) + "ver" + BuildConfig.VERSION_NAME
        setUpListeners()
        checkPermissions()
        viewModel.createUser()
        viewModel.validatePersistence()
        viewModel.sync()
    }

    private fun setUpListeners() {
        binding.btnSignIn click {
            val email: String = binding.etLoginEmail.text.toString()
            val password: String = binding.etLoginPassword.text.toString()

            if (viewModel.validateUser(email, password)) {
                showMainActivity()
            } else {
                showErrorDialog()
            }

        }
    }

    private fun checkPermissions(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true
        }
        if (checkSelfPermission(permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            return true
        }
        if (!(shouldShowRequestPermissionRationale(permission.CAMERA)
            || shouldShowRequestPermissionRationale(permission.WRITE_EXTERNAL_STORAGE)
            || shouldShowRequestPermissionRationale(permission.BLUETOOTH)
            || shouldShowRequestPermissionRationale(permission.READ_EXTERNAL_STORAGE)
            || shouldShowRequestPermissionRationale(permission.CALL_PHONE))) {
            requestPermissions(
                arrayOf(
                    permission.CAMERA,
                    permission.WRITE_EXTERNAL_STORAGE,
                    permission.READ_EXTERNAL_STORAGE,
                    permission.BLUETOOTH,
                    permission.CALL_PHONE
                ), 100
            )
        }
        return false
    }

    private fun showMainActivity() {
        val isAdmin = viewModel.isUserAdmin(applicationContext)

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra(MainActivity.IS_ADMIN,isAdmin)
        startActivity(intent)
        finish()
    }

    private fun showErrorDialog() {
        val dialog = PrettyDialog(this)
        dialog.setTitle("No encontrado")
            .setTitleColor(R.color.purple_500)
            .setMessage("Usuario no encontrado verifique los datos de acceso")
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
}