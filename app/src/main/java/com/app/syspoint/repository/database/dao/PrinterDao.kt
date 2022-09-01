package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.PrinterBean
import com.app.syspoint.repository.database.bean.PrinterBeanDao
import org.greenrobot.greendao.query.CountQuery

class PrinterDao: Dao("PrinterBean") {

    fun existeConfiguracionImpresora(): Int {
        val query = dao.queryBuilder().buildCount() as CountQuery<PrinterBeanDao>
        return query.count().toInt()
    }

    fun getImpresoraEstablecida(): PrinterBean? {
        val deviceBeanList = dao.queryBuilder()
            .where(PrinterBeanDao.Properties.Id.eq(1))
            .list() as List<PrinterBean>
        return if (deviceBeanList.size > 0) deviceBeanList[0] else null
    }
}