package com.app.syspoint

import android.app.Application
import com.app.syspoint.repository.objectBox.entities.MyObjectBox
import io.objectbox.BoxStore
import kotlinx.coroutines.IO_PARALLELISM_PROPERTY_NAME


class App: Application() {

    companion object {
        var INSTANCE: App? = null
        var mBoxStore: BoxStore? = null
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        System.setProperty(IO_PARALLELISM_PROPERTY_NAME, 1000.toString())

        mBoxStore = MyObjectBox.builder().androidContext(this).build()
    }
}