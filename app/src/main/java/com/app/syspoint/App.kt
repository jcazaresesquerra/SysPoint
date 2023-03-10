package com.app.syspoint

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.app.syspoint.repository.database.DBHelper


class App: Application() {
    private lateinit var dbHelper: DBHelper

    companion object {
        var INSTANCE: App? = null
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        dbHelper = DBHelper.getSingleton()
        dbHelper.init(INSTANCE, "point3_db")
    }
}