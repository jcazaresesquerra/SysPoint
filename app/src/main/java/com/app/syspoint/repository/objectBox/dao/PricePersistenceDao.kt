package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.PersistancePricesBox
import com.app.syspoint.repository.objectBox.entities.PersistancePricesBox_

class PricePersistenceDao: AbstractDao<PersistancePricesBox>() {

    fun inserBox(box: PersistancePricesBox) {
        insert(box)
    }

    fun getPersistence(): PersistancePricesBox? {
        val query = abstractBox<PersistancePricesBox>().query()
            .equal(PersistancePricesBox_.valor, 1)
            .build()
       val results = query.find()
        query.close()

        return if (results.isNotEmpty()) results[0] else null
    }

    fun existePersistencia(): Int {
        val query = abstractBox<PersistancePricesBox>().all
        return query.count()
    }
}