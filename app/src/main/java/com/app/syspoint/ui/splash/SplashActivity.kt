package com.app.syspoint.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.app.syspoint.databinding.ActivitySplashBinding
import com.app.syspoint.ui.MainActivity
import com.app.syspoint.ui.login.LoginActivity
import com.app.syspoint.viewmodel.SplashViewModel


class SplashActivity: AppCompatActivity() {

    companion object {
        private const val TIME_DELAY : Long = 2000
    }

    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[SplashViewModel::class.java]

        setContentView(binding.root)

        Handler().postDelayed({
            var intent = Intent(this, LoginActivity::class.java)
            if (viewModel.checkSessionData()) {
                intent = Intent(this, MainActivity::class.java)
            }
            startActivity(intent)
            finish()
        }, TIME_DELAY)
    }
}