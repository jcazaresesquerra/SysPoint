package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.App
import com.app.syspoint.repository.objectBox.entities.CashCloseBox
import com.app.syspoint.repository.objectBox.entities.PlayingBox
import com.app.syspoint.repository.objectBox.entities.SellBox
import com.app.syspoint.repository.objectBox.entities.SellBox_
import io.objectbox.query.QueryBuilder

class SellsDao: AbstractDao<SellBox>() {

    fun clear() {
        abstractBox<SellBox>().removeAll()
    }

    fun insertBox(box: SellBox) {
        insert(box)
    }

    fun creaVenta(venta: SellBox, partidas: List<PlayingBox>) {
        val stockId = StockDao().getCurrentStockId()
        venta.stockId = stockId

        abstractBox<SellBox>().put(venta)

        abstractBox<SellBox>()
        val detalle = App.mBoxStore!!.boxFor(PlayingBox::class.java)
        for (item: PlayingBox in partidas) {
            item.venta = venta.id
            detalle.put(item)
        }
    }

    //Retorna el ultimo folio de la venta
    fun getUltimoFolio(): Long {
        var folio = 0L
        val ventasBean = getUltimaVenta()
        if (ventasBean != null) {
            folio = ventasBean.venta
        }
        ++folio
        return folio
    }

    private fun getUltimaVenta(): SellBox? {
        val query = abstractBox<SellBox>().query()
            .order(SellBox_.id)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getSincVentaByID(id: Long): List<SellBox> {
        val query = abstractBox<SellBox>().query()
            .equal(SellBox_.id, id)
            .order(SellBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getListVentasByDate(fecha: String?): List<SellBox> {
        val query = abstractBox<SellBox>().query()
            .equal(SellBox_.fecha, fecha, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .order(SellBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }


    fun getListVentasEstado(): List<SellBox> {
        val query = abstractBox<SellBox>().query()
            .equal(SellBox_.estado, "CO", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .order(SellBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getListVentasParaInventario(fechaActual: String?): List<SellBox> {
        val query = abstractBox<SellBox>().query()
            .equal(SellBox_.estado, "CO", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(SellBox_.fecha, fechaActual, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .order(SellBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }


    fun getVentaByInventario(venta: Long): SellBox? {
        val query = abstractBox<SellBox>().query()
            .equal(SellBox_.venta, venta)
            .orderDesc(SellBox_.venta)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getAllPartsGroupedClient(): List<CashCloseBox> {
        val lista_corte: MutableList<CashCloseBox> = ArrayList()
        val productsDao = ProductDao()
        val clientsDao = ClientDao()
        val playingDao = PlayingDao()
        val stockId = StockDao().getCurrentStockId()

        val querySells = abstractBox<SellBox>().query()
            .equal(SellBox_.stockId, stockId.toLong())
            .equal(SellBox_.estado, "CO", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = querySells.find()
        querySells.close()

        results.map { sellItem ->
            val client = clientsDao.getByIDClient(sellItem.clienteId)
            sellItem.listaPartidas.map { playingBox ->
                val product = productsDao.getProductoByID(playingBox.articuloId)
                val corteBean = CashCloseBox()
                corteBean.clienteId = sellItem.clienteId
                corteBean.product.target = product[0]
                corteBean.productoId = playingBox.articuloId
                corteBean.client.target = client[0]
                corteBean.cantidad = playingBox.cantidad
                corteBean.precio = playingBox.precio
                corteBean.descripcion = playingBox.descripcion
                corteBean.impuesto = sellItem.impuesto
                corteBean.tipoVenta = sellItem.tipo_venta
                corteBean.estado = sellItem.estado
                lista_corte.add(corteBean)
            }
        }

        /*val partidaVentaBeanDao = daoSession.partidasBeanDao

        val cursor = partidaVentaBeanDao.database.rawQuery(
            "SELECT  " + ClienteBeanDao.TABLENAME + "." + ClienteBeanDao.Properties.Id.columnName + " AS idcliente," + ProductoBeanDao.TABLENAME + "." + ProductoBeanDao.Properties.Id.columnName + " AS idProducto, SUM(partidas.CANTIDAD) AS cantidad, partidas.PRECIO AS precio, " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Descripcion.columnName + " AS descripcion, " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Impuesto.columnName + " AS iva, "+ VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.Tipo_venta.columnName + " AS tipoVenta, "+ VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.Estado.columnName + " AS estado" +
                    " FROM " + PartidasBeanDao.TABLENAME +
                    " INNER JOIN " + ProductoBeanDao.TABLENAME + " ON " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.ArticuloId.columnName + " = " + ProductoBeanDao.TABLENAME + "." + ProductoBeanDao.Properties.Id.columnName +
                    " INNER JOIN " + VentasBeanDao.TABLENAME + " ON " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Venta.columnName + " = " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.Id.columnName + " AND " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.StockId.columnName + "=" + stockId +
                    " INNER JOIN " + ClienteBeanDao.TABLENAME + " ON " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.ClienteId.columnName + " = " + ClienteBeanDao.TABLENAME + "." + ClienteBeanDao.Properties.Id.columnName +
                    " WHERE " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.Estado.columnName + " == 'CO' " +
                    " GROUP BY " + ProductoBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Descripcion.columnName + ", " + ProductoBeanDao.TABLENAME + "." + ProductoBeanDao.Properties.Id.columnName + "," + ClienteBeanDao.TABLENAME + "." + ClienteBeanDao.Properties.Id.columnName + "," + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Descripcion.columnName + ", " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Impuesto.columnName + " ORDER BY " + VentasBeanDao.TABLENAME + "." +VentasBeanDao.Properties.Hora.columnName,
            null
        )
        while (cursor.moveToNext()) {
            val productoBean =
                productosDAO.getByID(cursor.getLong(cursor.getColumnIndex("idProducto"))) as ProductoBean?
            val clienteBean = clientesDAO.getByID(cursor.getString(cursor.getColumnIndex("idcliente")).toLong()
            ) as ClientBox?
            val corteBean = CashCloseBox()
            corteBean.clienteId = clienteBean!!.id
            corteBean.product.target = productoBean
            corteBean.productoId = productoBean!!.id
            corteBean.client.target = clienteBean
            corteBean.cantidad = cursor.getInt(cursor.getColumnIndex("cantidad"))
            corteBean.precio = cursor.getDouble(cursor.getColumnIndex("precio"))
            corteBean.descripcion = cursor.getString(cursor.getColumnIndex("descripcion"))
            corteBean.impuesto = cursor.getDouble(cursor.getColumnIndex("iva"))
            corteBean.tipoVenta = cursor.getString(cursor.getColumnIndex("tipoVenta"))
            corteBean.estado = cursor.getInt(cursor.getColumnIndex("estado"))
            lista_corte.add(corteBean)
        }
        return lista_corte*/
        return lista_corte
    }

    fun getTotalCountVentas(): Int {
        val results = abstractBox<SellBox>().all
        return results.count()
    }
}