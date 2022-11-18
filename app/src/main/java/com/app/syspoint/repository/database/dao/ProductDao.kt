package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.ProductoBean
import com.app.syspoint.repository.database.bean.ProductoBeanDao

class ProductDao: Dao("ProductoBean") {

    fun getActiveProducts(): List<ProductoBean> {
        return dao.queryBuilder()
            .where(ProductoBeanDao.Properties.Status.eq("Activo"))
            .orderAsc(ProductoBeanDao.Properties.Id)
            .list() as List<ProductoBean>
    }

    //Retorna el producto por identificador
    fun getProductoByArticulo(articulo: String?): ProductoBean? {
        val productoBeans = dao.queryBuilder()
            .where(ProductoBeanDao.Properties.Articulo.eq(articulo))
            .list() as List<ProductoBean>
        return if (productoBeans.size > 0) productoBeans[0] else null
    }


    fun getProductosInventario(): List<ProductoBean> {
        return dao.queryBuilder()
            .where(ProductoBeanDao.Properties.Existencia.ge(1))
            .orderAsc(ProductoBeanDao.Properties.Id)
            .list() as List<ProductoBean>
    }


    fun getProductos(): List<ProductoBean> {
        return dao.queryBuilder()
            .orderAsc(ProductoBeanDao.Properties.Id)
            .list() as List<ProductoBean>
    }


    fun getProductoByID(id: String?): List<ProductoBean> {
        return dao.queryBuilder()
            .where(ProductoBeanDao.Properties.Id.eq(id))
            .orderAsc(ProductoBeanDao.Properties.Id)
            .list() as List<ProductoBean>
    }
}