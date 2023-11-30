package com.app.syspoint.repository.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import com.app.syspoint.App
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.repository.cache.SharedPreferencesManager
import com.app.syspoint.repository.objectBox.dao.ChargeDao
import com.app.syspoint.repository.objectBox.dao.ClientDao
import com.app.syspoint.repository.objectBox.dao.CobrosDao
import com.app.syspoint.repository.objectBox.dao.EmployeeDao
import com.app.syspoint.repository.objectBox.dao.PlayingDao
import com.app.syspoint.repository.objectBox.dao.RolesDao
import com.app.syspoint.repository.objectBox.dao.RoutingDao
import com.app.syspoint.repository.objectBox.dao.RuteClientDao
import com.app.syspoint.repository.objectBox.dao.SellsDao
import com.app.syspoint.repository.objectBox.dao.SessionDao
import com.app.syspoint.repository.objectBox.dao.SpecialPricesDao
import com.app.syspoint.repository.objectBox.dao.StockDao
import com.app.syspoint.repository.objectBox.dao.StockHistoryDao
import com.app.syspoint.repository.objectBox.dao.TaskDao
import com.app.syspoint.repository.objectBox.dao.VisitsDao
import com.app.syspoint.repository.objectBox.entities.TaskBox
import com.app.syspoint.ui.MainActivity
import com.app.syspoint.usecases.GetAllEmployeesUseCase
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.utils.Utils
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate

class RemoveDataService: Service() {

    companion object {
        private const val TAG = "RemoveDataService"
        var isServiceRunning = false
    }
    private val mHandler: Handler = Handler()
    private val interval: Long = 1 * 5 * 1000 // 5 minutes in milliseconds

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        mHandler.postDelayed(mRunnable, interval)
        isServiceRunning = true
        return START_STICKY
    }

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            isServiceRunning = true
            val savedDateStr = SharedPreferencesManager(applicationContext)
                .getCurrentDate()

            // Check if savedDateStr exists and is not null
            if (!savedDateStr.isNullOrEmpty()) {

                val savedDate = LocalDate.parse(savedDateStr)

                val currentDate = LocalDate.now()

                // Compare current date with saved date
                if (currentDate > savedDate) {
                    forceUpdate(true)
                    saveCurrentDate()
                    sync()
                    try {
                        val finishIntent = Intent(MainActivity.ACTION_FINISH_ACTIVITY)
                        sendBroadcast(finishIntent)
                        Timber.tag(TAG).d( "finishing activity")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Timber.tag(TAG).d( "Exception finishing activity ${e.message}")
                    }
                } else {
                    Timber.tag(TAG).d("currentDate($currentDate) <= savedDate($savedDate)")

                }
            } else {
                Timber.tag(TAG).d("savedDateStr is null")
            }

            mHandler.postDelayed(this, interval)
        }
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
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

    private fun saveCurrentDate() {
        App.INSTANCE?.baseContext?.let {
            val currentDate = LocalDate.now()
            SharedPreferencesManager(it).storeCurrentDate(currentDate)
        }
    }

    fun sync() {
        Timber.tag(TAG).d("sync")
        if (!isSync()) {

            Handler().postDelayed({
                NetworkStateTask { connected ->
                    Timber.tag(TAG).d("sync -> %s", connected)
                    if (connected) {
                        GlobalScope.launch {
                            GetAllEmployeesUseCase().invoke().onEach { response ->
                                Timber.tag(TAG).d("GetAllEmployeesUseCase success")
                            }.launchIn(this)
                        }
                    }
                }.execute()
            }, 100)
        }
    }

    private fun isSync(): Boolean {
        val taskDao = TaskDao()
        val taskBean = taskDao.getTask(Utils.fechaActual())
        val exist: Boolean
        val isUpdated = isSessionUpdated()

        exist = !(taskBean == null || (taskBean.date != Utils.fechaActual()) || !isUpdated)

        return exist
    }

    private fun isSessionUpdated(): Boolean {
        App.INSTANCE?.baseContext?.let {
            return SharedPreferencesManager(it).isSessionUpdated()
        }
        return false
    }
}