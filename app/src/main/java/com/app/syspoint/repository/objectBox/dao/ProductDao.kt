package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.ProductBox
import com.app.syspoint.repository.objectBox.entities.ProductBox_
import io.objectbox.query.QueryBuilder

class ProductDao: AbstractDao<ProductBox>() {

    fun insertBox(box: ProductBox) {
        insert(box)
    }

    fun getActiveProducts(): List<ProductBox> {
        val query = abstractBox<ProductBox>().query()
            .equal(ProductBox_.status, "Activo", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .order(ProductBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    //Retorna el producto por identificador
    fun getProductoByArticulo(articulo: String?): ProductBox? {
        val query = abstractBox<ProductBox>().query()
            .equal(ProductBox_.articulo, articulo, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }


    fun getProductosInventario(): List<ProductBox> {
        val query = abstractBox<ProductBox>().query()
            .greaterOrEqual(ProductBox_.existencia, 1)
            .equal(ProductBox_.status, "Activo", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .order(ProductBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }


    fun getProductos(): List<ProductBox> {
        val results = abstractBox<ProductBox>().all
        return results
    }


    fun getProductoByID(id: Long): List<ProductBox> {
        val query = abstractBox<ProductBox>().query()
            .equal(ProductBox_.id, id)
            .order(ProductBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }
}