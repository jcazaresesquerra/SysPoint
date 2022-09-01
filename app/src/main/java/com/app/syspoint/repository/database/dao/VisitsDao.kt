package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.EmpleadoBeanDao
import com.app.syspoint.repository.database.bean.VisitasBean
import com.app.syspoint.repository.database.bean.VisitasBeanDao

class VisitsDao: Dao("VisitasBean") {

    fun getVisitID(id: String?): List<VisitasBean> {
        return dao.queryBuilder()
            .where(EmpleadoBeanDao.Properties.Id.eq(id))
            .orderAsc(EmpleadoBeanDao.Properties.Id)
            .list() as List<VisitasBean>
    }


    fun getAllVisits(): List<VisitasBean> {
        return dao.queryBuilder()
            .orderAsc(VisitasBeanDao.Properties.Id)
            .list() as List<VisitasBean>
    }

    fun getVisitsByCurrentDay(fecha: String?): List<VisitasBean> {
        return dao.queryBuilder()
            .where(VisitasBeanDao.Properties.Fecha.eq(fecha))
            .orderAsc(VisitasBeanDao.Properties.Id)
            .list() as List<VisitasBean>
    }
}