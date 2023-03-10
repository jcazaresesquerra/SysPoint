package com.app.syspoint.repository.database.dao

import android.database.Cursor
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

    fun getAllConfirmedChargesToday(stockId: Int): List<CobranzaBean> {
        return dao.queryBuilder()
            .where(CobranzaBeanDao.Properties.Estado.eq("CO"))
            .where(CobranzaBeanDao.Properties.UpdatedAt.between(Utils.fechaActualHMSStartDay(), Utils.fechaActualHMSEndDay()))
            .where(CobranzaBeanDao.Properties.StockId.eq(stockId))
            .orderDesc(CobranzaBeanDao.Properties.Id)
            .list() as List<CobranzaBean>

        /*val lista_corte: MutableList<CorteBean> = ArrayList()
        var cursor: Cursor? = null

        cursor = partidaVentaBeanDao.database.rawQuery(
            "SELECT  " + ClienteBeanDao.TABLENAME + "." + ClienteBeanDao.Properties.Id.columnName + " AS idcliente," + ProductoBeanDao.TABLENAME + "." + ProductoBeanDao.Properties.Id.columnName + " AS idProducto, SUM(partidas.CANTIDAD) AS cantidad, SUM(partidas.PRECIO) AS precio, " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Descripcion.columnName + " AS descripcion, " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Impuesto.columnName + " AS iva, "+ VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.Tipo_venta.columnName + " AS tipoVenta" +
                    " FROM " + PartidasBeanDao.TABLENAME +
                    " INNER JOIN " + ProductoBeanDao.TABLENAME + " ON " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.ArticuloId.columnName + " = " + ProductoBeanDao.TABLENAME + "." + ProductoBeanDao.Properties.Id.columnName +
                    " INNER JOIN " + VentasBeanDao.TABLENAME + " ON " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Venta.columnName + " = " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.Id.columnName + " AND " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.StockId.columnName + "=" + stockId +
                    " INNER JOIN " + ClienteBeanDao.TABLENAME + " ON " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.ClienteId.columnName + " = " + ClienteBeanDao.TABLENAME + "." + ClienteBeanDao.Properties.Id.columnName +
                    " WHERE " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.Estado.columnName + " == 'CO' " +
                    " GROUP BY " + ProductoBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Descripcion.columnName + ", " + ProductoBeanDao.TABLENAME + "." + ProductoBeanDao.Properties.Id.columnName + "," + ClienteBeanDao.TABLENAME + "." + ClienteBeanDao.Properties.Id.columnName + "," + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Descripcion.columnName + ", " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Impuesto.columnName + " ORDER BY clientes._id ",
            null
        )
        while (cursor.moveToNext()) {
            val productoBean =
                productosDAO.getByID(cursor.getLong(cursor.getColumnIndex("idProducto"))) as ProductoBean?
            val clienteBean = clientesDAO.getByID(cursor.getString(cursor.getColumnIndex("idcliente")).toLong()
            ) as ClienteBean?
            val corteBean = CorteBean()
            corteBean.clienteId = clienteBean!!.id
            corteBean.productoBean = productoBean
            corteBean.productoId = productoBean!!.id
            corteBean.clienteBean = clienteBean
            corteBean.cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"))
            corteBean.precio = cursor.getDouble(cursor.getColumnIndex("precio"))
            corteBean.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"))
            corteBean.impuesto = cursor.getDouble(cursor.getColumnIndex("iva"))
            corteBean.tipoVenta = cursor.getString(cursor.getColumnIndex("tipoVenta"))
            lista_corte.add(corteBean)
        }
        return lista_corte*/
    }

}