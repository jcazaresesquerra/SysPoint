package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.InventarioBean
import com.app.syspoint.repository.database.bean.InventarioBeanDao

class StockDao: Dao("InventarioBean") {

    //Retorna el producto por identificador
    fun getProductoByArticulo(clave: String?): InventarioBean? {
        val productoBeans = dao.queryBuilder()
            .where(InventarioBeanDao.Properties.Articulo_clave.eq(clave))
            .list() as List<InventarioBean>
        return if (productoBeans.size > 0) productoBeans[0] else null
    }

    fun getInventarioPendiente(): List<InventarioBean> {
        return dao.queryBuilder()
            .where(InventarioBeanDao.Properties.Estado.eq("CO"))
            .orderAsc(InventarioBeanDao.Properties.Id)
            .list() as List<InventarioBean>
    }


    fun getProductoByArticulo(articulo: Int): InventarioBean? {
        val inventarioBeans = dao.queryBuilder()
            .where(InventarioBeanDao.Properties.ArticuloId.eq(articulo))
            .list() as List<InventarioBean>
        return if (inventarioBeans.size > 0) inventarioBeans[0] else null
    }

    fun getCurrentStockId(): Int {
        val inventarioBeans = dao.queryBuilder()
            .list() as List<InventarioBean>
        return if (inventarioBeans.isNotEmpty()) inventarioBeans[0].stockId else 0
    }
}