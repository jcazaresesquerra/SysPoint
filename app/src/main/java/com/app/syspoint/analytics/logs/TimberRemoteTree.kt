package com.app.syspoint.analytics.logs

import android.util.Log
import com.app.syspoint.BuildConfig
import com.app.syspoint.interactor.cache.CacheInteractor
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class TimberRemoteTree(private val deviceDetails: DeviceDetails) : Timber.DebugTree() {

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val timeFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss_SSS_a_zzz", Locale.getDefault())
    private val timeFormatHHmm = SimpleDateFormat("hh:mm", Locale.getDefault())
    private val date = dateFormat.format(Date(System.currentTimeMillis()))

    private var logRef = Firebase.database.getReference("logs/${BuildConfig.FLAVOR}/${deviceDetails.employeeId}/$date/${deviceDetails.deviceId}")

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (BuildConfig.REMOTE_LOG_ENABLED) {
            val timestamp = System.currentTimeMillis()
            val time = timeFormat.format(Date(timestamp)).replace(".", "").replace("#", "").replace("$", "")
            val remoteLog = RemoteLog(priorityAsString(priority), tag, message, t.toString(), time)
            val hour = timeFormatHHmm.format(Date(System.currentTimeMillis()))

            val seller = CacheInteractor().getSeller()
            if (seller != null) {
                logRef =
                    Firebase.database.getReference("logs/${BuildConfig.FLAVOR}/${seller.identificador}/$date/${deviceDetails.deviceId}")
            }

            with(logRef) {
                child("-DeviceDetails").setValue(deviceDetails)
                child(hour).child(time.toString()).setValue(remoteLog)
            }
            logEvent(priority, tag, message)
        } else super.log(priority, tag, message, t)
    }

    private fun priorityAsString(priority: Int): String = when (priority) {
        Log.VERBOSE -> "VERBOSE"
        Log.DEBUG -> "DEBUG"
        Log.INFO -> "INFO"
        Log.WARN -> "WARN"
        Log.ERROR -> "ERROR"
        Log.ASSERT -> "ASSERT"
        else -> priority.toString()
    }

    private fun logEvent(priority: Int, tag: String?, message: String) = when (priority) {
        Log.VERBOSE -> Log.v(tag, message)
        Log.DEBUG -> Log.d(tag, message)
        Log.INFO -> Log.i(tag, message)
        Log.WARN -> Log.w(tag, message)
        Log.ERROR -> Log.e(tag, message)
        Log.ASSERT -> Log.d(tag, message)
        else -> {Log.d(tag, message)}
    }
}