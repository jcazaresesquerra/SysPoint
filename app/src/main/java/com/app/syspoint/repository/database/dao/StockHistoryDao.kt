package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.InventarioHistorialBean
import com.app.syspoint.repository.database.bean.InventarioHistorialBeanDao

class StockHistoryDao: Dao("InventarioHistorialBean") {

    fun getInvatarioPorArticulo(articulo: String?): InventarioHistorialBean? {
        val clienteBeans = dao.queryBuilder()
            .where(InventarioHistorialBeanDao.Properties.Articulo_clave.eq(articulo))
            .list() as List<InventarioHistorialBean>
        return if (clienteBeans.size > 0) clienteBeans[0] else null
    }
}