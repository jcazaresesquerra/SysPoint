package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.RoutingBox
import com.app.syspoint.repository.objectBox.entities.RoutingBox_
import io.objectbox.query.QueryBuilder

class RoutingDao: AbstractDao<RoutingBox>() {

    fun clear() {
        abstractBox<RoutingBox>().removeAll()
    }

    fun insertBox(box: RoutingBox) {
        insert(box)
    }

    fun getRutaEstablecida(): RoutingBox? {
        val query = abstractBox<RoutingBox>().query()
            .equal(RoutingBox_.id, 1)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }


    fun getRutaEstablecidaFechaActual(fecha: String?): RoutingBox? {
        val query = abstractBox<RoutingBox>().query()
            .equal(RoutingBox_.fecha, fecha, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isNotEmpty()) results[0] else null
    }
}