package com.app.syspoint.utils.delete_data_service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Calendar

object AlarmHelper {


    fun setMidnightAlarm(context: Context) {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create an Intent for the BroadcastReceiver
        val intent = Intent(context, MidnightAlarmReceiver::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flags)

        // Set the alarm to trigger at midnight
        val midnightTime = getMidnightTime()
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, midnightTime, AlarmManager.INTERVAL_DAY, pendingIntent)
    }

    private fun getMidnightTime(): Long {
        // Get the current time
        val currentTimeMillis = System.currentTimeMillis()

        // Get the time for midnight (12:00 AM) of the next day
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = currentTimeMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        calendar.add(Calendar.DAY_OF_MONTH, 1)

        return calendar.timeInMillis
    }
}
