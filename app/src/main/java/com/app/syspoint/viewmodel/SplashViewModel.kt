package com.app.syspoint.viewmodel

import androidx.lifecycle.ViewModel
import com.app.syspoint.repository.objectBox.dao.SessionDao

class SplashViewModel: ViewModel() {

    fun checkSessionData(): Boolean {
        val sessionDao = SessionDao()
        val session = sessionDao.getUserSession()

        return session?.remember ?: false
    }
}