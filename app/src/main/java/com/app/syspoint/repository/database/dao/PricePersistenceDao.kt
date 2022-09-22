package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.PersistenciaPrecioBean
import com.app.syspoint.repository.database.bean.PersistenciaPrecioBeanDao
import com.app.syspoint.repository.database.bean.PrinterBeanDao
import org.greenrobot.greendao.query.CountQuery

class PricePersistenceDao: Dao("PersistenciaPrecioBean") {

    fun getPersistence(): PersistenciaPrecioBean? {
        val deviceBeanList = dao.queryBuilder()
            .where(PersistenciaPrecioBeanDao.Properties.Valor.eq(1))
            .list() as List<PersistenciaPrecioBean>
        return if (deviceBeanList.size > 0) deviceBeanList[0] else null
    }

    fun existePersistencia(): Int {
        val query = dao.queryBuilder().buildCount() as CountQuery<PrinterBeanDao>
        return query.count().toInt()
    }
}