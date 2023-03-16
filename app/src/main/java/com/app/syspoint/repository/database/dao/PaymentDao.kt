package com.app.syspoint.repository.database.dao

import android.database.Cursor
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.models.CloseCash
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.utils.Utils

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

    fun getAllConfirmedChargesToday(stockId: Int): List<CloseCash> {
        val lastUserSession = CacheInteractor().getSeller()
        val identificador = lastUserSession?.identificador?:""

        val lista_corte: MutableList<CloseCash> = ArrayList()
        val cobranzaBeanDao = daoSession.cobranzaBeanDao

        val cursor = cobranzaBeanDao.database.rawQuery(
            "SELECT cli.NOMBRE_COMERCIAL as nombre_comercial, c.VENTA as ticket, c.CLIENTE as cliente, c.ACUENTA as acuenta, c.UPDATED_AT as updated_at, c.STOCK_ID as stock_id, c.EMPLEADO as empleado, c.ESTADO as estado" +
                    " FROM cobranza AS c "+
                    " INNER JOIN clientes AS cli ON c.CLIENTE = cli.CUENTA " +
                    " WHERE c.UPDATED_AT BETWEEN '${Utils.fechaActualHMSStartDay()}' AND '${Utils.fechaActualHMSEndDay()}' " +
                    " AND c.STOCK_ID = $stockId AND c.ACUENTA > 0.0 "
            ,null
        )
        while (cursor.moveToNext()) {
            val closeCash = CloseCash()
            closeCash.comertialName = cursor.getString(cursor.getColumnIndex("nombre_comercial"))
            closeCash.abono = cursor.getDouble(cursor.getColumnIndex("acuenta"))
            closeCash.ticket = cursor.getString(cursor.getColumnIndex("ticket"))
            closeCash.updatedAt = cursor.getString(cursor.getColumnIndex("updated_at"))
            closeCash.employee = cursor.getString(cursor.getColumnIndex("empleado"))
            closeCash.status = cursor.getString(cursor.getColumnIndex("estado"))
            closeCash.stockId = cursor.getInt(cursor.getColumnIndex("stock_id"))

            lista_corte.add(closeCash)
        }
        return lista_corte
    }

}