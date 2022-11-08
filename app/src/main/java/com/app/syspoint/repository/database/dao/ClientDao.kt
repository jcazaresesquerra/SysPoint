package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.ClienteBean
import com.app.syspoint.repository.database.bean.ClienteBeanDao
import com.app.syspoint.repository.database.bean.ClientesRutaBean
import com.app.syspoint.repository.database.bean.ClientesRutaBeanDao

class ClientDao: Dao("ClienteBean") {

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

    fun getAllRutaClientes(rute: String, day: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .where(ClienteBeanDao.Properties.Rango.eq(rute))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(
                when(day) {
                    1 -> ClienteBeanDao.Properties.Lun.eq(1)
                    2 -> ClienteBeanDao.Properties.Mar.eq(1)
                    3 -> ClienteBeanDao.Properties.Mie.eq(1)
                    4 -> ClienteBeanDao.Properties.Jue.eq(1)
                    5 -> ClienteBeanDao.Properties.Vie.eq(1)
                    6 -> ClienteBeanDao.Properties.Sab.eq(1)
                    else -> ClienteBeanDao.Properties.Dom.eq(1)
                }
            )
            .orderAsc(
                when(day) {
                    1 -> ClienteBeanDao.Properties.LunOrder
                    2 -> ClienteBeanDao.Properties.MarOrder
                    3 -> ClienteBeanDao.Properties.MieOrder
                    4 -> ClienteBeanDao.Properties.JueOrder
                    5 -> ClienteBeanDao.Properties.VieOrder
                    6 -> ClienteBeanDao.Properties.SabOrder
                    else -> ClienteBeanDao.Properties.DomOrder
                })
            .list() as List<ClienteBean>
    }

    //Retorna el empleado por identificador
    fun getClientByAccount(cuenta: String?): ClienteBean? {
        val clientBeans = dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Cuenta.eq(cuenta))
            .list()
        return if (clientBeans.isNotEmpty()) clientBeans[0] as ClienteBean else null
    }

    fun getClientById(id: String?): ClienteBean? {
        val clientBeans = dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Id.eq(id))
            .list()
        return if (clientBeans.isNotEmpty()) clientBeans[0] as ClienteBean else null
    }

    fun getClient(account: String?): ClienteBean? {
        val clientBeans = dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Id.eq(account))
            .list()
        return if (clientBeans.isNotEmpty()) clientBeans[0] as ClienteBean else null
    }

    //TODO ULTIMA VENTA
    private fun getLastRegister(): ClienteBean? {
        val clientBeans = dao.queryBuilder()
            .orderDesc(ClienteBeanDao.Properties.Consec)
            .limit(1)
            .list()
        return if (clientBeans.isNotEmpty()) clientBeans[0] as ClienteBean else null
    }

    //TODO ULTIMO FOLIO
    fun getLastConsec(): Int {
        var folio = 0
        val clientBean = getLastRegister()
        if (clientBean != null) {
            folio = clientBean.consec
        }
        ++folio
        return folio
    }

    fun getClientsByMondayRute(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Lun.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.LunOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.LunOrder)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaMartes(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Mar.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.MarOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.MarOrder)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaMiercoles(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Mie.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.MieOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.MieOrder)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaJueves(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Jue.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.JueOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.JueOrder)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaViernes(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Vie.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.VieOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.VieOrder)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaSabado(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Sab.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.SabOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.SabOrder)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaDomingo(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Dom.eq(dia))
            .where(ClienteBeanDao.Properties.Status.eq(1))
            .where(ClienteBeanDao.Properties.DomOrder.gt(0))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.DomOrder)
            .list() as List<ClienteBean>
    }

    //Actualizasmo si ya fue visitado el cliente
    fun updateVisited() {
        val list = getAllVisited()

        //Recorremos todos los clientes
        for (cliente in list) {
            val clienteDao = ClientDao()
            cliente.visitado = 0
            clienteDao.save(cliente)
        }
    }

    private fun getAllVisited(): List<ClienteBean> {
        return dao.queryBuilder()
            .list() as List<ClienteBean>
    }

    fun getClients(): List<ClienteBean> {
        return dao.queryBuilder()
            .orderAsc(ClienteBeanDao.Properties.Id)
            .list() as List<ClienteBean>
    }


    fun getByIDClient(id: String?): List<ClienteBean> {
        return dao.queryBuilder()
            .orderAsc(ClienteBeanDao.Properties.Id)
            .where(ClienteBeanDao.Properties.Id.eq(id))
            .list() as List<ClienteBean>
    }

    fun getClientsByDay(fecha: String?): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Date_sync.eq(fecha))
            .orderAsc(ClienteBeanDao.Properties.Id)
            .list() as List<ClienteBean>
    }
}