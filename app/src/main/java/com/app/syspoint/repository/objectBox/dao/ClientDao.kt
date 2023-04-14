package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.ClientBox
import com.app.syspoint.repository.objectBox.entities.ClientBox_
import io.objectbox.query.QueryBuilder

class ClientDao: AbstractDao<ClientBox>() {

    fun clear() {
        abstractBox<ClientBox>().removeAll()
    }

    fun insertBox(box: ClientBox) {
        insert(box)
    }

    private fun getLastRegister(): ClientBox? {
        val query = abstractBox<ClientBox>().query()
            .orderDesc(ClientBox_.consec)
            .build()
        val results = query.find()
        query.close()
        return if (results.isNotEmpty()) results[0] else null
    }

    //TODO ULTIMO FOLIO
    fun getLastConsec(): Int {
        var folio = 0
        val clientBean = getLastRegister()
        if (clientBean != null) {
            folio = Integer.valueOf(clientBean.consec)
        }
        ++folio
        return folio
    }

    fun getClientsByDay(fecha: String?): List<ClientBox> {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.date_sync, fecha, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .order(ClientBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getClientById(id: Long): ClientBox? {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.id, id)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getClientGeneralPublic(): ClientBox? {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.cuenta, "0001", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getClientsByRute(rute: String): List<ClientBox> {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.rango, rute, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ClientBox_.status, 1)
            .notEqual(ClientBox_.cuenta, "0001", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .order(ClientBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getClientByAccount(cuenta: String?): ClientBox? {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.cuenta, cuenta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getByIDClient(id: Long): List<ClientBox> {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.id, id)
            .order(ClientBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun updateVisited() {
        val list = getAllVisited()

        //Recorremos todos los clientes
        for (cliente in list) {
            cliente.visitado = 0
            insert(cliente)
        }
    }

    private fun getAllVisited(): List<ClientBox> {
        return abstractBox<ClientBox>().all
    }

    fun getClientsByMondayRute(ruta: String?, dia: Int): List<ClientBox> {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.rango, ruta!!, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ClientBox_.status, 1)
            .equal(ClientBox_.visitado, 0)
            .equal(ClientBox_.lun, 1)
            .greaterOrEqual(ClientBox_.lunOrder, 0)
            .order(ClientBox_.lunOrder)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getListaClientesRutaMartes(ruta: String?, dia: Int): List<ClientBox> {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.rango, ruta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ClientBox_.status, 1)
            .equal(ClientBox_.visitado, 0)
            .equal(ClientBox_.mar, 2)
            .greaterOrEqual(ClientBox_.marOrder, 0)
            .order(ClientBox_.marOrder)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getListaClientesRutaMiercoles(ruta: String?, dia: Int): List<ClientBox> {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.rango, ruta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ClientBox_.status, 1)
            .equal(ClientBox_.visitado, 0)
            .equal(ClientBox_.mie, 3)
            .greaterOrEqual(ClientBox_.mieOrder, 0)
            .order(ClientBox_.mieOrder)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getListaClientesRutaJueves(ruta: String?, dia: Int): List<ClientBox> {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.rango, ruta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ClientBox_.status, 1)
            .equal(ClientBox_.visitado, 0)
            .equal(ClientBox_.jue, 4)
            .greaterOrEqual(ClientBox_.jueOrder, 0)
            .order(ClientBox_.jueOrder)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getListaClientesRutaViernes(ruta: String?, dia: Int): List<ClientBox> {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.rango, ruta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ClientBox_.status, 1)
            .equal(ClientBox_.visitado, 0)
            .equal(ClientBox_.vie, 5)
            .greaterOrEqual(ClientBox_.vieOrder, 0)
            .order(ClientBox_.vieOrder)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getListaClientesRutaSabado(ruta: String?, dia: Int): List<ClientBox> {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.rango, ruta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ClientBox_.status, 1)
            .equal(ClientBox_.visitado, 0)
            .equal(ClientBox_.sab, 6)
            .greaterOrEqual(ClientBox_.sabOrder, 0)
            .order(ClientBox_.sabOrder)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getListaClientesRutaDomingo(ruta: String?, dia: Int): List<ClientBox> {
        val query = abstractBox<ClientBox>().query()
            .equal(ClientBox_.rango, ruta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ClientBox_.status, 1)
            .equal(ClientBox_.visitado, 0)
            .equal(ClientBox_.dom, 7)
            .greaterOrEqual(ClientBox_.domOrder, 0)
            .order(ClientBox_.domOrder)
            .build()
        val results = query.find()
        query.close()

        return results
    }
}