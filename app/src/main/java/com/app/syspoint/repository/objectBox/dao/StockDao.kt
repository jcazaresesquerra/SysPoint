package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.repository.objectBox.entities.StockBox
import com.app.syspoint.repository.objectBox.entities.StockBox_
import io.objectbox.query.QueryBuilder
import timber.log.Timber


private const val TAG = "StockDao"

class StockDao: AbstractDao<StockBox>() {

    fun clear() {
        Timber.tag(TAG).d("clear")
        abstractBox<StockBox>().removeAll()
    }

    fun insertBox(box: StockBox) {
        Timber.tag(TAG).d("insertBox -> %s", box)
        insert(box)
    }

    fun delete(id: Long) {
        Timber.tag(TAG).d("insertBox -> %s", id)
        remove<StockBox>(id)
    }

    fun getProductoByArticulo(clave: String?): StockBox? {
        val query = abstractBox<StockBox>().query()
            .equal(StockBox_.articulo_clave, clave, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        Timber.tag(TAG).d("getProductoByArticulo -> articulo: %s -> result: %s",clave, results)
        return if (results.isEmpty()) null else results[0]
    }


    fun getProductoByArticulo(articulo: Int): StockBox? {
        val query = abstractBox<StockBox>().query()
            .equal(StockBox_.articuloId, articulo.toLong())
            .build()
        val results = query.find()
        query.close()

        Timber.tag(TAG).d("getProductoByArticulo -> articulo: %s -> result: %s",articulo, results)
        return if (results.isEmpty()) null else results[0]
    }

    fun getCurrentStockId(): Int {
        val results = abstractBox<StockBox>().all
        Timber.tag(TAG).d("getCurrentStockId -> result: %s", results)
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

        Timber.tag(TAG).d("getInventarioPendiente -> result: %s", results)
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

        Timber.tag(TAG).d("getCurrentStock -> result: %s", results)
        return results
    }
}