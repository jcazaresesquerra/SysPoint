package com.app.syspoint.repository.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.app.syspoint.R
import com.app.syspoint.interactor.data.GetAllDataInteractor
import com.app.syspoint.repository.request.RequestData.Companion.requestAllData2
import kotlinx.coroutines.*
import java.util.*

class UpdateDataService: Service() {

    companion object {
        private const val TIME : Long = 5000//1000 * 60 * 5
    }

    var binder: IBinder? = null
    var allowRebind = false

    override fun onCreate() {
        super.onCreate()

        val doAsynchronousTask: TimerTask = object : TimerTask() {
            override fun run() {
                GlobalScope.launch {
                    try {
                        requestAllData2(object : GetAllDataInteractor.OnGetAllDataInServiceListener {
                            override fun onGetAllDataSuccess() {
                                Log.d("SysPoint", "Updated")
                            }

                            override fun onGetAllDataError() {
                                Log.d("SysPoint", "Error when update")
                            }
                        })
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
        Timer().schedule(doAsynchronousTask, 0, TIME) //execute in every 50000 ms
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val CHANNELID = "Foreground Service ID"
        val channel = NotificationChannel(
            CHANNELID,
            CHANNELID,
            NotificationManager.IMPORTANCE_LOW
        )

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        val notification: NotificationCompat.Builder = NotificationCompat.Builder(this, CHANNELID)
            .setContentText("app is running in background")
            .setContentTitle("AppStatusControl")
            .setSmallIcon(R.drawable.logo)

        startForeground(1001, notification.build())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return allowRebind
    }

    override fun onRebind(intent: Intent?) {
        super.onRebind(intent)
    }
}