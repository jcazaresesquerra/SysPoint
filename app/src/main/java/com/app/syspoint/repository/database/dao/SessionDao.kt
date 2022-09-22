package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.SesionBean
import org.greenrobot.greendao.query.CountQuery

class SessionDao: Dao("SesionBean") {

    fun getUserSession(): SesionBean? {
        val sessionBeanList = dao.loadAll() as List<SesionBean>
        return if (sessionBeanList.isNotEmpty()) sessionBeanList[0] else null
    }

    fun saveSession(sessionBean: SesionBean?) {
        this.clear()
        insert(sessionBean)
    }

    fun userExists(): Int {
        val query = dao.queryBuilder().buildCount() as CountQuery<SesionBean>
        return query.count().toInt()
    }
}