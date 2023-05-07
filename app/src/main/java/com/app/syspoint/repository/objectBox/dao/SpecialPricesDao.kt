package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.SpecialPricesBox
import com.app.syspoint.repository.objectBox.entities.SpecialPricesBox_
import io.objectbox.query.QueryBuilder

class SpecialPricesDao: AbstractDao<SpecialPricesBox>() {

    fun clear() {
        abstractBox<SpecialPricesBox>().removeAll()
    }

    fun inserBox(box: SpecialPricesBox) {
        insert(box)
    }

    fun getPrecioEspeciaPorCliente(productID: String?, clientID: String?): SpecialPricesBox? {

        val query = abstractBox<SpecialPricesBox>().query()
            .equal(SpecialPricesBox_.articulo, productID, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(SpecialPricesBox_.cliente, clientID, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(SpecialPricesBox_.active, true)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    //Retorna la listos especiales por ID
    fun getListaPrecioPorCliente(clienteID: String?): List<SpecialPricesBox> {
        val query = abstractBox<SpecialPricesBox>().query()
            .equal(SpecialPricesBox_.cliente, clienteID, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(SpecialPricesBox_.active, true)
            .build()

        val results = query.find()
        query.close()
        return results
    }

    //Retorna la listos especiales por ID
    fun getListaPrecioPorClienteUpdate(clienteID: String?): List<SpecialPricesBox> {
        val query = abstractBox<SpecialPricesBox>().query()
            .equal(SpecialPricesBox_.cliente, clienteID, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(SpecialPricesBox_.active, false)
            .build()

        val results = query.find()
        query.close()
        return results
    }


    fun getPreciosBydate(fecha: String?): List<SpecialPricesBox> {
        val query = abstractBox<SpecialPricesBox>().query()
            .equal(SpecialPricesBox_.fecha_sync, fecha, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(SpecialPricesBox_.active, false)
            .build()

        val results = query.find()
        query.close()
        return results
    }


    fun getPrecioEspecialPorIdentificador(
        cliente: String?,
        articulo: String?
    ): SpecialPricesBox? {
        val query = abstractBox<SpecialPricesBox>().query()
            .equal(SpecialPricesBox_.articulo, articulo, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(SpecialPricesBox_.cliente, cliente, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()

        val results = query.find()
        query.close()
        return if (results.isNotEmpty()) results[0] else null
    }
}