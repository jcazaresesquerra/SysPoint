package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.CobranzaBeanDao
import com.app.syspoint.ui.cobranza.CobranzaModel
import com.app.syspoint.utils.Utils

class PaymentModelDao: Dao("CobranzaModel") {
    fun getAllConfirmedChargesToday(): List<CobranzaModel> {
        return dao.queryBuilder()
            .where(CobranzaBeanDao.Properties.Estado.eq("CO"))
            .where(CobranzaBeanDao.Properties.Fecha.ge(Utils.fechaActualHMSStartDay()))
            .where(CobranzaBeanDao.Properties.Fecha.le(Utils.fechaActualHMSEndDay()))
            .orderDesc(CobranzaBeanDao.Properties.Id)
            .list() as List<CobranzaModel>
    }
}