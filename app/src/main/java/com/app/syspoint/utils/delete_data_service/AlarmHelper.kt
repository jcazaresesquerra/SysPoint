package com.app.syspoint.utils.delete_data_service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar
import java.util.Date

object AlarmHelper {

    fun setMidnightAlarm(context: Context) {
        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Create an Intent for the BroadcastReceiver
        /*val intent = Intent(context, MidnightAlarmReceiver::class.java)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, flags)

        // Set the alarm to trigger at midnight
        val midnightTime = getMidnightTime()
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, midnightTime, AlarmManager.INTERVAL_DAY, pendingIntent)
*/

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val alarmIntent = Intent(
            context,
            MidnightAlarmReceiver::class.java
        )
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, alarmIntent, flags
        )

        // Set the alarm to start at 10:00 AM
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis, 86400000,
            pendingIntent
        ) // for repeating in every 24 hours

    }

    private fun getMidnightTime(): Long {
        val time: Long = Date().time
        val date = Date(time - time % (24 * 60 * 60 * 1000))
        return date.time
    }
}
