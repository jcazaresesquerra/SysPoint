package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.repository.objectBox.entities.StockBox
import com.app.syspoint.repository.objectBox.entities.StockBox_
import io.objectbox.query.QueryBuilder

class StockDao: AbstractDao<StockBox>() {

    fun clear() {
        abstractBox<StockBox>().removeAll()
    }

    fun insertBox(box: StockBox) {
        insert(box)
    }

    fun delete(id: Long) {
        remove<StockBox>(id)
    }

    fun getProductoByArticulo(clave: String?): StockBox? {
        val query = abstractBox<StockBox>().query()
            .equal(StockBox_.articulo_clave, clave, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }


    fun getProductoByArticulo(articulo: Int): StockBox? {
        val query = abstractBox<StockBox>().query()
            .equal(StockBox_.articuloId, articulo.toLong())
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getCurrentStockId(): Int {
        val results = abstractBox<StockBox>().all
        return if (results.isEmpty()) 0 else results[0].stockId
    }

    fun list(): List<StockBox>  {
        val results = abstractBox<StockBox>().all
        return results
    }

    fun getInventarioPendiente(): List<StockBox> {
        val query = abstractBox<StockBox>().query()
            .equal(StockBox_.estado, "CO", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .order(StockBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getCurrentStock(): List<StockBox> {
        val stockId = CacheInteractor().getCurrentStockId()
        val loadId = CacheInteractor().getCurrentLoadId()

        val query = abstractBox<StockBox>().query()
            .equal(StockBox_.stockId, stockId.toLong())
            .equal(StockBox_.loadId, loadId.toLong())
            .order(StockBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }
}