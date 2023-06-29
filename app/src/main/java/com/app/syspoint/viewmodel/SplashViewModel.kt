package com.app.syspoint.viewmodel

import androidx.lifecycle.ViewModel
import com.app.syspoint.App
import com.app.syspoint.repository.cache.SharedPreferencesManager
import com.app.syspoint.repository.objectBox.dao.SessionDao
import com.app.syspoint.repository.objectBox.dao.TaskDao
import com.app.syspoint.utils.Utils

class SplashViewModel: ViewModel() {

    fun checkSessionData(): Boolean {
        val sessionDao = SessionDao()
        val session = sessionDao.getUserSession()

        return session?.remember ?: false
    }

    fun isSynced(): Boolean {
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