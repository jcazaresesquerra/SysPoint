package com.app.syspoint.utils.delete_data_service

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.syspoint.App
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.repository.objectBox.dao.*
import com.app.syspoint.repository.objectBox.entities.TaskBox
import com.app.syspoint.ui.MainActivity.Companion.ACTION_FINISH_ACTIVITY
import com.app.syspoint.ui.login.LoginActivity
import com.app.syspoint.utils.Utils
import timber.log.Timber
import java.util.Calendar

class MidnightAlarmReceiver : BroadcastReceiver() {

    companion object {
        const val TAG = "MidnightAlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val calendar: Calendar = Calendar.getInstance()
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0
            && calendar.get(Calendar.MINUTE) == 0) {
            forceUpdate(true)
            try {
                val finishIntent = Intent(ACTION_FINISH_ACTIVITY)
                context.sendBroadcast(finishIntent)
                //exitApplication(context as Activity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun forceUpdate(removeTask: Boolean) {
        Timber.tag(TAG).d("sync -> forceUpdate")

        App.mBoxStore?.removeAllObjects()

        val sessionDao = SessionDao()
        sessionDao.clear()
        val stockDao = StockDao()
        stockDao.clear()
        val stockHistoryDao = StockHistoryDao()
        stockHistoryDao.clear()
        val sellDao = SellsDao()
        sellDao.clear()
        val playingDao = PlayingDao()
        playingDao.clear()
        val visitasDao = VisitsDao()
        visitasDao.clear()
        val cobranzaDao = CobrosDao()
        cobranzaDao.clear()
        val chargesDao = ChargeDao()
        chargesDao.clear()
        val routingDao = RoutingDao()
        routingDao.clear()
        val employeeDao = EmployeeDao()
        employeeDao.clear()
        val rolesDao = RolesDao()
        rolesDao.clear()
        val clientesRutaDao = RuteClientDao()
        clientesRutaDao.clear()
        val clientDao = ClientDao()
        clientDao.clear()
        val specialPricesDao = SpecialPricesDao()
        specialPricesDao.clear()
        val dao = TaskDao()
        dao.clear()
        val bean = TaskBox()

        bean.date = if (removeTask) "" else Utils.fechaActual()
        bean.task = "Deleted"

        CacheInteractor().removeSellerFromCache()
        CacheInteractor().resetStockId()
        CacheInteractor().resetLoadId()

        dao.insert(bean)
    }

    fun exitApplication(activity: Activity) {
        activity.finish()

        // Start the login activity
        val loginIntent = Intent(activity, LoginActivity::class.java)
        loginIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        activity.startActivity(loginIntent)
    }
}
