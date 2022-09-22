package com.app.syspoint.ui

import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.app.syspoint.R
import com.google.android.material.snackbar.Snackbar

abstract class BaseActivity: AppCompatActivity() {
    /*
   * Attributes
   */
    private var doubleBackToExitPressedOnce : Boolean = false
    private val timeDelayExitBar: Int = 2000

    open fun getView(): View? { return null}
    /**
     * Override method, to shows [Snackbar] when user wants to exit
     */
    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        val view = getView()
        view?.let {
            Snackbar.make(view,
                R.string.snackbar_message_text,
                timeDelayExitBar)
                .show()
        }

        doubleBackToExitPressedOnce = true

        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)
    }
}