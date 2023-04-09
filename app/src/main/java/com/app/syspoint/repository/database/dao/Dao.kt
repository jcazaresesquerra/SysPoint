package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.DBHelper.Companion.getSingleton
import com.app.syspoint.repository.database.bean.Bean
import com.app.syspoint.repository.database.bean.DaoSession
import org.greenrobot.greendao.AbstractDao


abstract class Dao() {
    protected lateinit var dao: AbstractDao<Bean, Long>
    protected lateinit var daoSession: DaoSession
    var daoExternalSession: DaoSession? = null

    constructor(daoName: String?): this() {
        daoSession = daoExternalSession ?: getSingleton().daoSession

        when (daoName) {
            "LogSyncGetBean" -> dao = daoSession.logSyncGetBeanDao as AbstractDao<Bean, Long>
            "EmpleadoBean" -> dao = daoSession.empleadoBeanDao as AbstractDao<Bean, Long>
            "ProductoBean" -> dao = daoSession.productoBeanDao as AbstractDao<Bean, Long>
            "ClienteBean" -> dao = daoSession.clienteBeanDao as AbstractDao<Bean, Long>
            "PartidasBean" -> dao = daoSession.partidasBeanDao as AbstractDao<Bean, Long>
            "VentasModelBean" -> dao = daoSession.ventasModelBeanDao as AbstractDao<Bean, Long>
            "VentasBean" -> dao = daoSession.ventasBeanDao as AbstractDao<Bean, Long>
            "PrinterBean" -> dao = daoSession.printerBeanDao as AbstractDao<Bean, Long>
            "RuteoBean" -> dao = daoSession.ruteoBeanDao as AbstractDao<Bean, Long>
            "SesionBean" -> dao = daoSession.sesionBeanDao as AbstractDao<Bean, Long>
            "PreciosEspecialesBean" -> dao = daoSession.preciosEspecialesBeanDao as AbstractDao<Bean, Long>
            "RolesBean" -> dao = daoSession.rolesBeanDao as AbstractDao<Bean, Long>
            "ClientesRutaBean" -> dao = daoSession.clientesRutaBeanDao as AbstractDao<Bean, Long>
            "InventarioBean" -> dao = daoSession.inventarioBeanDao as AbstractDao<Bean, Long>
            "InventarioHistorialBean" -> dao = daoSession.inventarioHistorialBeanDao as AbstractDao<Bean, Long>
            "CorteBean" -> dao = daoSession.corteBeanDao as AbstractDao<Bean, Long>
            "PersistenciaPrecioBean" -> dao = daoSession.persistenciaPrecioBeanDao as AbstractDao<Bean, Long>
            "VisitasBean" -> dao = daoSession.visitasBeanDao as AbstractDao<Bean, Long>
            "CobranzaBean" -> dao = daoSession.cobranzaBeanDao as AbstractDao<Bean, Long>
            "CobrosBean" -> dao = daoSession.cobrosBeanDao as AbstractDao<Bean, Long>
            "CobdetBean" -> dao = daoSession.cobdetBeanDao as AbstractDao<Bean, Long>
            "CobranzaModel" -> dao = daoSession.cobranzaModelDao as AbstractDao<Bean, Long>
            "TaskBean" -> dao = daoSession.taskBeanDao as AbstractDao<Bean, Long>
        }
    }

    fun list(): MutableList<Bean>? {
        return dao.loadAll()
    }

    fun insert(bean: Bean?) {
        dao.insert(bean)
    }

    fun delete(bean: Bean) {
        dao.delete(bean)
    }

    fun beginTransaction() {
        dao.database.beginTransaction()
    }

    fun beginExternalTransaction() {
        daoExternalSession = getSingleton().daoSession
        daoExternalSession!!.database.beginTransaction()
    }

    fun commitExternalTransaction() {
        daoExternalSession!!.database.setTransactionSuccessful()
        daoExternalSession!!.database.endTransaction()
        daoExternalSession = null
    }

    fun commmit() {
        dao.database.setTransactionSuccessful()
        dao.database.endTransaction()
    }

    fun session(): DaoSession {
        return daoSession
    }

    fun getByID(id: Long): Bean {
        return dao.loadByRowId(id) as Bean
    }

    fun clear() {
        dao.deleteAll()
    }

    fun insertAll(list: List<Bean?>) {
        list.map { bean ->
            dao.insert(bean)
        }
    }

    fun insertOrReplace(bean: Bean?) {
        dao.insertOrReplace(bean)
    }

    fun save(bean: Bean?) {
        dao.save(bean)
    }
}