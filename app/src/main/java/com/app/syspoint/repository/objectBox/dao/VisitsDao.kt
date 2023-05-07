package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.VisitsBox
import com.app.syspoint.repository.objectBox.entities.VisitsBox_
import io.objectbox.query.QueryBuilder

class VisitsDao: AbstractDao<VisitsBox>() {

    fun clear() {
        abstractBox<VisitsBox>().removeAll()
    }

    fun getVisitID(id: Long): List<VisitsBox> {
        val query = abstractBox<VisitsBox>().query()
            .equal(VisitsBox_.id, id)
            .order(VisitsBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }


    fun getAllVisits(): List<VisitsBox> {
        val query = abstractBox<VisitsBox>().query()
            .order(VisitsBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getVisitsByCurrentDay(fecha: String?): List<VisitsBox> {
        val query = abstractBox<VisitsBox>().query()
            .equal(VisitsBox_.fecha, fecha, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .order(VisitsBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }
}