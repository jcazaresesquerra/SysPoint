package com.app.syspoint

import android.app.Application
import android.provider.Settings
import com.app.syspoint.analytics.logs.DeviceDetails
import com.app.syspoint.analytics.logs.TimberRemoteTree
import com.app.syspoint.repository.objectBox.entities.MyObjectBox
import io.objectbox.BoxStore
import kotlinx.coroutines.IO_PARALLELISM_PROPERTY_NAME
import timber.log.Timber


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

        if (BuildConfig.DEBUG) {
            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val deviceDetails = DeviceDetails(deviceId)
            val remoteTree = TimberRemoteTree(deviceDetails)

            Timber.plant(remoteTree)
        } else {

        }
    }
}