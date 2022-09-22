package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.ClienteBean
import com.app.syspoint.repository.database.bean.ClienteBeanDao

class ClientDao: Dao("ClienteBean") {

    //Retorna el empleado por identificador
    fun getClientByAccount(cuenta: String?): ClienteBean? {
        val clientBeans = dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Cuenta.eq(cuenta))
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
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.Id)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaMartes(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Mar.eq(dia))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.Id)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaMiercoles(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Mie.eq(dia))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.Id)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaJueves(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Jue.eq(dia))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.Id)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaViernes(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Vie.eq(dia))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.Id)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaSabado(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Sab.eq(dia))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.Id)
            .list() as List<ClienteBean>
    }

    fun getListaClientesRutaDomingo(ruta: String?, dia: Int): List<ClienteBean> {
        return dao.queryBuilder()
            .where(ClienteBeanDao.Properties.Rango.eq(ruta))
            .where(ClienteBeanDao.Properties.Dom.eq(dia))
            .where(ClienteBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClienteBeanDao.Properties.Id)
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