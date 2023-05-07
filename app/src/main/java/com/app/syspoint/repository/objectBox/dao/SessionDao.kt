package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.SessionBox

class SessionDao: AbstractDao<SessionBox>() {

    fun clear() {
        abstractBox<SessionBox>().removeAll()
    }

    fun getUserSession(): SessionBox? {
        val sessionBoxList = abstractBox<SessionBox>().all
        return if (sessionBoxList.isNotEmpty()) sessionBoxList[0] else null
    }

    fun saveSession(sessionBean: SessionBox) {
        abstractBox<SessionBox>().removeAll()
        insert(sessionBean)
    }

    fun userExists(): Int {
        val results = abstractBox<SessionBox>().all
        val count = results.count()

        return results.count()
    }
}