package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.ClienteBeanDao
import com.app.syspoint.repository.database.bean.ClientesRutaBean
import com.app.syspoint.repository.database.bean.ClientesRutaBeanDao

class RuteClientDao: Dao("ClientesRutaBean") {

    fun getAllRutes(): List<String> {
        val cursor = dao.database.rawQuery(
            "SELECT DISTINCT rango FROM `clientes` ORDER BY rango;",
            null
        )
        val array = arrayListOf<String>()
        while (cursor.moveToNext()) {
            array.add(cursor.getString(0))
        }
        return array
    }

    fun getClientsByRute(rute: String): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(rute))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .orderAsc(ClienteBeanDao.Properties.Id)
            .list() as List<ClientesRutaBean>
    }

    fun getAllRutaClientes(rute: String, day: Int): List<ClientesRutaBean>? {
        val clients = dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
            .where(ClientesRutaBeanDao.Properties.Rango.eq(rute))
            .where(
                when(day) {
                    1 -> ClientesRutaBeanDao.Properties.Lun.eq(1)
                    2 -> ClientesRutaBeanDao.Properties.Mar.eq(1)
                    3 -> ClientesRutaBeanDao.Properties.Mie.eq(1)
                    4 -> ClientesRutaBeanDao.Properties.Jue.eq(1)
                    5 -> ClientesRutaBeanDao.Properties.Vie.eq(1)
                    6 -> ClientesRutaBeanDao.Properties.Sab.eq(1)
                    else -> ClientesRutaBeanDao.Properties.Dom.eq(1)
                }
            )
            .orderAsc(ClientesRutaBeanDao.Properties.Id)
            .list()
        return clients.ifEmpty { null } as List<ClientesRutaBean>
    }

    fun getListaClientesRutaLunes(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Lun.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.LunOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.LunOrder)
            .list() as List<ClientesRutaBean>
    }

    //Retorna el empleado por identificador
    fun getClienteFirts(): ClientesRutaBean? {
        val clienteBeans = dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Id.eq(1))
            .list() as List<ClientesRutaBean>
        return if (clienteBeans.size > 0) clienteBeans[0] else null
    }

    fun getListaClientesRutaMartes(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Mar.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.MarOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.MarOrder)
            .list() as List<ClientesRutaBean>
    }

    fun getListaClientesRutaMiercoles(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Mie.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.MieOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.MieOrder)
            .list() as List<ClientesRutaBean>
    }

    fun getListaClientesRutaJueves(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Jue.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.JueOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.JueOrder)
            .list() as List<ClientesRutaBean>
    }

    fun getListaClientesRutaViernes(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Vie.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.VieOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.VieOrder)
            .list() as List<ClientesRutaBean>
    }

    fun getListaClientesRutaSabado(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Sab.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.SabOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.SabOrder)
            .list() as List<ClientesRutaBean>
    }

    fun getListaClientesRutaDomingo(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Dom.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.DomOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.DomOrder)
            .list() as List<ClientesRutaBean>
    }

    //Actualizasmo si ya fue visitado el cliente
    fun updateVisitado() {
        val list = getAllVisitado()

        //Recorremos todos los clientes
        for (cliente in list) {
            val clienteDao =
                RuteClientDao()
            cliente.visitado = 0
            clienteDao.save(cliente)
        }
    }

    fun getAllVisitado(): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .list() as List<ClientesRutaBean>
    }


    //Retorna el empleado por identificador
    fun getClienteByCuentaCliente(cuenta: String?): ClientesRutaBean? {
        val clienteBeans = dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Cuenta.eq(cuenta))
            .list() as List<ClientesRutaBean>
        return if (clienteBeans.size > 0) clienteBeans[0] else null
    }

    private fun getUltimoRegistro(): ClientesRutaBean? {
        val clienteBeans = dao.queryBuilder()
            .orderDesc(ClientesRutaBeanDao.Properties.Id)
            .limit(1)
            .list() as List<ClientesRutaBean>
        return if (clienteBeans.size > 0) clienteBeans[0] else null
    }

    //TODO ULTIMO FOLIO
    fun getUltimoConsec(): Long {
        var folio: Long = 0
        val clienteBean = getUltimoRegistro()
        if (clienteBean != null) {
            folio = clienteBean.id
        }
        ++folio
        return folio
    }
}