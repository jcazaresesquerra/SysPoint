package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.RuteoBean
import com.app.syspoint.repository.database.bean.RuteoBeanDao

class RoutingDao: Dao("RuteoBean") {

    fun getRutaEstablecida(): RuteoBean? {
        val ruteoBeans = dao.queryBuilder()
            .where(RuteoBeanDao.Properties.Id.eq(1))
            .list() as List<RuteoBean>
        return if (ruteoBeans.size > 0) ruteoBeans[0] else null
    }


    fun getRutaEstablecidaFechaActual(fecha: String?): RuteoBean? {
        val ruteoBeans = dao.queryBuilder()
            .where(RuteoBeanDao.Properties.Fecha.eq(fecha))
            .list() as List<RuteoBean>
        return if (ruteoBeans.size > 0) ruteoBeans[0] else null
    }
}