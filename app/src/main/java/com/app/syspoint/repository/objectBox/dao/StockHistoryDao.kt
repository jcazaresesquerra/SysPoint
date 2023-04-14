package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.StockHistoryBox
import com.app.syspoint.repository.objectBox.entities.StockHistoryBox_
import io.objectbox.query.QueryBuilder


class StockHistoryDao: AbstractDao<StockHistoryBox>() {

    fun clear() {
        abstractBox<StockHistoryBox>().removeAll()
    }

    fun insertBox(box: StockHistoryBox) {
        insert(box)
    }

    fun getInvatarioPorArticulo(articulo: String?): StockHistoryBox? {
        val query = abstractBox<StockHistoryBox>().query()
            .equal(StockHistoryBox_.articulo_clave, articulo, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }
}