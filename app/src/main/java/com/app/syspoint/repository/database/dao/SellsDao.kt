package com.app.syspoint.repository.database.dao

import android.database.Cursor
import com.app.syspoint.repository.database.bean.*
import org.greenrobot.greendao.query.CountQuery

class SellsDao: Dao("VentasBean") {

    fun creaVenta(venta: VentasBean, partidas: List<PartidasBean>) {

        //Transaccion
        beginTransaction()

        //Guarda la venta
        save(venta)

        //Vinculamos la venta con las partidas
        val detalle = daoSession.partidasBeanDao
        for (item: PartidasBean in partidas) {
            item.venta = venta.id
            detalle.insert(item)
        }

        //Termina la transaccion
        commmit()
    }

    private fun getUltimaVenta(): VentasBean? {
        val ventasBeans = dao.queryBuilder()
            .orderDesc(VentasBeanDao.Properties.Venta)
            .limit(1)
            .list() as List<VentasBean>
        return if (ventasBeans.size > 0) ventasBeans[0] else null
    }

    //Retorna el ultimo folio de la venta
    fun getUltimoFolio(): Int {
        var folio = 0
        val ventasBean = getUltimaVenta()
        if (ventasBean != null) {
            folio = ventasBean.venta
        }
        ++folio
        return folio
    }

    fun getSincVentaByID(id: Long?): List<VentasBean> {
        return dao.queryBuilder()
            .where(VentasBeanDao.Properties.Id.eq(id))
            .orderAsc(VentasBeanDao.Properties.Id)
            .list() as List<VentasBean>
    }

    fun getListVentasByDate(fecha: String?): List<VentasBean> {
        return dao.queryBuilder()
            .where(VentasBeanDao.Properties.Fecha.eq(fecha))
            .orderAsc(VentasBeanDao.Properties.Id)
            .list() as List<VentasBean>
    }


    fun getListVentasEstado(): List<VentasBean> {
        return dao.queryBuilder()
            .where(VentasBeanDao.Properties.Estado.eq("CO"))
            .orderAsc(VentasBeanDao.Properties.Id)
            .list() as List<VentasBean>
    }

    fun getListVentasParaInventario(fechaActual: String?): List<VentasBean> {
        return dao.queryBuilder()
            .where(
                VentasBeanDao.Properties.Estado.eq("CO"),
                VentasBeanDao.Properties.Fecha.eq(fechaActual)
            )
            .orderAsc(VentasBeanDao.Properties.Id)
            .list() as List<VentasBean>
    }


    fun getVentaByInventario(venta: Int): VentasBean? {
        val ventasBeans = dao.queryBuilder()
            .where(VentasBeanDao.Properties.Venta.eq(venta))
            .orderDesc(VentasBeanDao.Properties.Venta)
            .limit(1)
            .list() as List<VentasBean>
        return if (ventasBeans.size > 0) ventasBeans[0] else null
    }

    fun getAllPartsGroupedClient(): List<CorteBean> {
        val lista_corte: MutableList<CorteBean> = ArrayList()
        var cursor: Cursor? = null
        val productosDAO = ProductDao()
        val clientesDAO = ClientDao()
        val partidaVentaBeanDao = daoSession.partidasBeanDao
        cursor = partidaVentaBeanDao.database.rawQuery(
            "SELECT  " + ClienteBeanDao.TABLENAME + "." + ClienteBeanDao.Properties.Id.columnName + " AS idcliente," + ProductoBeanDao.TABLENAME + "." + ProductoBeanDao.Properties.Id.columnName + " AS idProducto, SUM(partidas.CANTIDAD) AS cantidad, SUM(partidas.PRECIO) AS precio, " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Descripcion.columnName + " AS descripcion, " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Impuesto.columnName + " AS iva  FROM " + PartidasBeanDao.TABLENAME +
                    " INNER JOIN " + ProductoBeanDao.TABLENAME + " ON " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.ArticuloId.columnName + " = " + ProductoBeanDao.TABLENAME + "." + ProductoBeanDao.Properties.Id.columnName +
                    " INNER JOIN " + VentasBeanDao.TABLENAME + " ON " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Venta.columnName + " = " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.Id.columnName +
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
            lista_corte.add(corteBean)
        }
        return lista_corte
    }

    @Throws(Exception::class)
    fun getTotalCountVentas(): Int {
        val query = dao.queryBuilder().buildCount() as CountQuery<VentasBean>
        return query.count().toInt()
    }
}