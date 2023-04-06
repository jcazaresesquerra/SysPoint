package com.app.syspoint

import android.app.Application
import com.app.syspoint.repository.database.DBHelper
import kotlinx.coroutines.IO_PARALLELISM_PROPERTY_NAME


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

        System.setProperty(IO_PARALLELISM_PROPERTY_NAME, 1000.toString());
    }
}