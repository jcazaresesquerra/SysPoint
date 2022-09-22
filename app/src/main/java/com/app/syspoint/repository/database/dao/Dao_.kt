package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.DBHelper
import com.app.syspoint.repository.database.bean.Bean
import com.app.syspoint.repository.database.bean.DaoSession
import org.greenrobot.greendao.AbstractDao

abstract class Dao_(daoName: String) {
    protected var dao: AbstractDao<*, *>? = null
    protected var daoSession: DaoSession? = null

    companion object {
        var daoExternalSession: DaoSession? = null

        fun beginExternalTransaction() {
            daoExternalSession = DBHelper.getSingleton().daoSession
            daoExternalSession?.database?.beginTransaction()
        }

        fun commitExternalTransaction() {
            daoExternalSession?.database?.run {
                setTransactionSuccessful()
                endTransaction()
            }
            daoExternalSession = null
        }
    }

    init {
        daoSession = if (daoExternalSession == null) {
            DBHelper.getSingleton().daoSession
        } else {
            daoExternalSession
        }
        daoSession?.let {
            when (daoName) {
                "LogSyncGetBean" -> dao = it.logSyncGetBeanDao
                "EmpleadoBean" -> dao = it.empleadoBeanDao
                "ProductoBean" -> dao = it.productoBeanDao
                "ClienteBean" -> dao = it.clienteBeanDao
                "PartidasBean" -> dao = it.partidasBeanDao
                "VentasModelBean" -> dao = it.ventasModelBeanDao
                "VentasBean" -> dao = it.ventasBeanDao
                "PrinterBean" -> dao = it.printerBeanDao
                "RuteoBean" -> dao = it.ruteoBeanDao
                "SesionBean" -> dao = it.sesionBeanDao
                "PreciosEspecialesBean" -> dao = it.preciosEspecialesBeanDao
                "RolesBean" -> dao = it.rolesBeanDao
                "ClientesRutaBean" -> dao = it.clientesRutaBeanDao
                "InventarioBean" -> dao = it.inventarioBeanDao
                "InventarioHistorialBean" -> dao = it.inventarioHistorialBeanDao
                "CorteBean" -> dao = it.corteBeanDao
                "PersistenciaPrecioBean" -> dao = it.persistenciaPrecioBeanDao
                "VisitasBean" -> dao = it.visitasBeanDao
                "CobranzaBean" -> dao = it.cobranzaBeanDao
                "CobrosBean" -> dao = it.cobrosBeanDao
                "CobdetBean" -> dao = it.cobdetBeanDao
                "CobranzaModel" -> dao = it.cobranzaModelDao
                "TaskBean" -> dao = it.taskBeanDao
            }
        }
    }

    open fun list(): MutableList<out Any>? {
        return dao?.loadAll()
    }

    open fun insert(bean: Bean?) {
        //dao?.insert(bean)
    }

    open fun delete(bean: Bean?) {
        //dao?.delete(bean)
    }

    open fun beginTransaction() {
        dao?.database?.beginTransaction()
    }

    open fun commmit() {
        dao?.run {
            database.setTransactionSuccessful()
            database.endTransaction()
        }
    }

    open fun getByID(id: Long): Bean? {
        return dao?.loadByRowId(id) as Bean
    }

    open fun clear() {
        dao?.deleteAll()
    }

    open fun insertAll(list: List<Bean?>) {
        for (bean in list) {
            //dao?.insert(bean)
        }
    }

    open fun save(bean: Bean?) {
        //dao?.save(bean)
    }
}