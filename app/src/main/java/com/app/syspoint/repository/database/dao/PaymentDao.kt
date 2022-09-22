package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.CobranzaBean
import com.app.syspoint.repository.database.bean.CobranzaBeanDao

class PaymentDao: Dao("CobranzaBean") {
    fun getSaldoByCliente(cliente: String): Double {
        val cursor = dao.database.rawQuery(
            "SELECT SUM(saldo) FROM cobranza WHERE cliente ='$cliente' AND estado = 'PE' AND saldo > 0 ",
            null
        )
        cursor.moveToFirst()
        return cursor.getDouble(0)
    }


    fun getDocumentsByCliente(cliente: String?): List<CobranzaBean> {
        return dao.queryBuilder()
            .where(CobranzaBeanDao.Properties.Cliente.eq(cliente))
            .orderAsc(CobranzaBeanDao.Properties.Id)
            .list() as List<CobranzaBean>
    }


    fun getCobranzaFechaActual(fecha: String?): List<CobranzaBean> {
        return dao.queryBuilder()
            .where(
                CobranzaBeanDao.Properties.Fecha.eq(fecha),
                CobranzaBeanDao.Properties.Abono.eq(false)
            )
            .orderAsc(CobranzaBeanDao.Properties.Id)
            .list() as List<CobranzaBean>
    }


    fun getAbonosFechaActual(fecha: String?): List<CobranzaBean> {
        return dao.queryBuilder()
            .where(
                CobranzaBeanDao.Properties.Fecha.eq(fecha),
                CobranzaBeanDao.Properties.Abono.eq(true)
            )
            .orderAsc(CobranzaBeanDao.Properties.Id)
            .list() as List<CobranzaBean>
    }

    fun getByCobranza(cobranza: String?): CobranzaBean? {
        try {
            val productosBeans = dao.queryBuilder()
                .where(CobranzaBeanDao.Properties.Cobranza.eq(cobranza))
                .list() as List<CobranzaBean>
            return if (productosBeans.size > 0) productosBeans[0] else null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(Exception::class)
    fun getTotalSaldoDocumentosCliente(cliente: String): Double {
        val cursor = dao.database.rawQuery(
            "SELECt SUM(saldo) FROM cobranza  WHERE saldo > 0 AND estado = 'PE' AND  CLIENTE = '$cliente'",
            null
        )
        cursor.moveToFirst()
        return cursor.getDouble(0)
    }

    fun getByCobranzaByCliente(cuenta: String?): List<CobranzaBean> {
        return dao.queryBuilder()
            .where(
                CobranzaBeanDao.Properties.Cliente.eq(cuenta),
                CobranzaBeanDao.Properties.Estado.eq("PE"),
                CobranzaBeanDao.Properties.Saldo.ge(1)
            )
            .list() as List<CobranzaBean>
    }


    fun getDocumentosSeleccionados(cuenta: String?): List<CobranzaBean> {
        return dao.queryBuilder()
            .where(
                CobranzaBeanDao.Properties.IsCheck.eq(true),
                CobranzaBeanDao.Properties.Cliente.eq(cuenta),
                CobranzaBeanDao.Properties.Estado.eq("PE"),
                CobranzaBeanDao.Properties.Saldo.ge(1)
            )
            .list() as List<CobranzaBean>
    }

}