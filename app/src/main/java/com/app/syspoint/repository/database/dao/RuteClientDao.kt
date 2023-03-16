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
            .where(ClientesRutaBeanDao.Properties.Status.eq(1))
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
            .where(
                when(day) {
                    1 -> ClientesRutaBeanDao.Properties.LunOrder.gt(0)
                    2 -> ClientesRutaBeanDao.Properties.MarOrder.gt(0)
                    3 -> ClientesRutaBeanDao.Properties.MieOrder.gt(0)
                    4 -> ClientesRutaBeanDao.Properties.JueOrder.gt(0)
                    5 -> ClientesRutaBeanDao.Properties.VieOrder.gt(0)
                    6 -> ClientesRutaBeanDao.Properties.SabOrder.gt(0)
                    else -> ClientesRutaBeanDao.Properties.DomOrder.gt(0)
                }
            )
            .orderAsc( when(day) {
                1 -> ClientesRutaBeanDao.Properties.LunOrder
                2 -> ClientesRutaBeanDao.Properties.MarOrder
                3 -> ClientesRutaBeanDao.Properties.MieOrder
                4 -> ClientesRutaBeanDao.Properties.JueOrder
                5 -> ClientesRutaBeanDao.Properties.VieOrder
                6 -> ClientesRutaBeanDao.Properties.SabOrder
                else -> ClientesRutaBeanDao.Properties.DomOrder
            })
            .list()
        return if (clients != null)
            clients as List<ClientesRutaBean>
        else null
    }

    //Retorna el empleado por identificador
    fun getClienteFirts(): ClientesRutaBean? {
        val clienteBeans = dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Id.eq(1))
            .list() as List<ClientesRutaBean>
        return if (clienteBeans.isNotEmpty()) clienteBeans[0] else null
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
        return if (clienteBeans.isNotEmpty()) clienteBeans[0] else null
    }

    //Retorna el empleado por identificador y ruta
    fun getClienteByCuentaClienteAndRute(cuenta: String?, rute: String?, day: Int): ClientesRutaBean? {
        val clienteBeans = dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Cuenta.eq(cuenta))
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
            )            .list() as List<ClientesRutaBean>
        return if (clienteBeans.isNotEmpty()) clienteBeans[0] else null
    }

    fun getClienteByCuentaCliente(cuenta: String?, day: Int, rute: String): ClientesRutaBean? {

        val clients = dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
            .where(ClientesRutaBeanDao.Properties.Rango.eq(rute))
            .where(ClientesRutaBeanDao.Properties.Cuenta.eq(cuenta))
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
            .list() as List<ClientesRutaBean>
        return if (clients.isNotEmpty()) clients[0] else null
    }

    private fun getLastInOrder(day: Int, rute: String): ClientesRutaBean? {
        val clienteBeans = dao.queryBuilder()
            .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
            .where(ClientesRutaBeanDao.Properties.Rango.eq(rute))
            .where(ClientesRutaBeanDao.Properties.Status.eq(1))
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
            .orderDesc( when(day) {
                1 -> ClientesRutaBeanDao.Properties.LunOrder
                2 -> ClientesRutaBeanDao.Properties.MarOrder
                3 -> ClientesRutaBeanDao.Properties.MieOrder
                4 -> ClientesRutaBeanDao.Properties.JueOrder
                5 -> ClientesRutaBeanDao.Properties.VieOrder
                6 -> ClientesRutaBeanDao.Properties.SabOrder
                else -> ClientesRutaBeanDao.Properties.DomOrder
            })
            .limit(1)
            .list() as List<ClientesRutaBean>
        return if (clienteBeans.isNotEmpty()) clienteBeans[0] else null
    }

    private fun getUltimoRegistro(): ClientesRutaBean? {
        val clienteBeans = dao.queryBuilder()
            .orderDesc(ClientesRutaBeanDao.Properties.Id)
            .limit(1)
            .list() as List<ClientesRutaBean>
        return if (clienteBeans.isNotEmpty()) clienteBeans[0] else null
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

    fun getLastClientInOrder(day: Int, rute: String): Int {
        var folio = 0
        val clienteBean = getLastInOrder(day, rute)
        if (clienteBean != null) {
            folio = when(day) {
                1 -> clienteBean.lunOrder
                2 -> clienteBean.marOrder
                3 -> clienteBean.mieOrder
                4 -> clienteBean.jueOrder
                5 -> clienteBean.vieOrder
                6 -> clienteBean.sabOrder
                else -> clienteBean.domOrder
            }
        }
        ++folio
        return folio
    }
}