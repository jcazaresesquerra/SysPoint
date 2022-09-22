package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.ClientesRutaBean
import com.app.syspoint.repository.database.bean.ClientesRutaBeanDao

class RuteClientDao: Dao("ClientesRutaBean") {
    fun getAllRutaClientes(): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClientesRutaBeanDao.Properties.Id)
            .list() as List<ClientesRutaBean>
    }

    fun getListaClientesRutaLunes(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Lun.eq(dia))
            .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClientesRutaBeanDao.Properties.Id)
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
            .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClientesRutaBeanDao.Properties.Id)
            .list() as List<ClientesRutaBean>
    }

    fun getListaClientesRutaMiercoles(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Mie.eq(dia))
            .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClientesRutaBeanDao.Properties.Id)
            .list() as List<ClientesRutaBean>
    }

    fun getListaClientesRutaJueves(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Jue.eq(dia))
            .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClientesRutaBeanDao.Properties.Id)
            .list() as List<ClientesRutaBean>
    }

    fun getListaClientesRutaViernes(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Vie.eq(dia))
            .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClientesRutaBeanDao.Properties.Id)
            .list() as List<ClientesRutaBean>
    }

    fun getListaClientesRutaSabado(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Sab.eq(dia))
            .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClientesRutaBeanDao.Properties.Id)
            .list() as List<ClientesRutaBean>
    }

    fun getListaClientesRutaDomingo(ruta: String?, dia: Int): List<ClientesRutaBean> {
        return dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
            .where(ClientesRutaBeanDao.Properties.Dom.eq(dia))
            .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
            .orderAsc(ClientesRutaBeanDao.Properties.Id)
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