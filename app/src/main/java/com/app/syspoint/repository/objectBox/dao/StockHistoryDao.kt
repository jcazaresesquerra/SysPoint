package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.StockHistoryBox
import com.app.syspoint.repository.objectBox.entities.StockHistoryBox_
import io.objectbox.query.QueryBuilder
import timber.log.Timber

private const val TAG = "StockHistoryDao"

class StockHistoryDao: AbstractDao<StockHistoryBox>() {

    fun clear() {
        Timber.tag(TAG).d("clear")
        abstractBox<StockHistoryBox>().removeAll()
    }

    fun insertBox(box: StockHistoryBox) {
        Timber.tag(TAG).d("insertBox -> %s",box)
        insert(box)
    }

    fun getInvatarioPorArticulo(articulo: String?): StockHistoryBox? {
        val query = abstractBox<StockHistoryBox>().query()
            .equal(StockHistoryBox_.articulo_clave, articulo, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        Timber.tag(TAG).d("getInvatarioPorArticulo -> articulo: %s -> result: %s",articulo, results)

        return if (results.isEmpty()) null else results[0]
    }
}