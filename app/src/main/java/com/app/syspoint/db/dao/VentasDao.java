package com.app.syspoint.db.dao;

import android.database.Cursor;

import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.ClienteBeanDao;
import com.app.syspoint.db.bean.CorteBean;
import com.app.syspoint.db.bean.PartidasBean;
import com.app.syspoint.db.bean.PartidasBeanDao;
import com.app.syspoint.db.bean.ProductoBean;
import com.app.syspoint.db.bean.ProductoBeanDao;
import com.app.syspoint.db.bean.VentasBean;
import com.app.syspoint.db.bean.VentasBeanDao;

import org.greenrobot.greendao.query.CountQuery;

import java.util.ArrayList;
import java.util.List;

public class VentasDao extends Dao {

    public VentasDao() {
        super("VentasBean");
    }

    public void creaVenta(final VentasBean venta, final List<PartidasBean> partidas) {

        //Transaccion
        this.beginTransaction();

        //Guarda la venta
        this.dao.save(venta);

        //Vinculamos la venta con las partidas
        final PartidasBeanDao detalle = daoSession.getPartidasBeanDao();
        for (PartidasBean item : partidas) {
            item.setVenta(venta.getId());
            detalle.insert(item);
        }

        //Termina la transaccion
        this.commmit();

    }

    final private VentasBean getUltimaVenta() {
        final List<VentasBean> ventasBeans = dao.queryBuilder()
                .orderDesc(VentasBeanDao.Properties.Venta)
                .limit(1)
                .list();
        return ventasBeans.size() > 0 ? ventasBeans.get(0) : null;
    }

    //Retorna el ultimo folio de la venta
    final public int getUltimoFolio() {
        int folio = 0;
        final VentasBean ventasBean = this.getUltimaVenta();
        if (ventasBean != null) {
            folio = ventasBean.getVenta();
        }
        ++folio;
        return folio;
    }

    final public List<VentasBean> getSincVentaByID(Long id) {
        return dao.queryBuilder()
                .where(VentasBeanDao.Properties.Id.eq(id))
                .orderAsc(VentasBeanDao.Properties.Id)
                .list();
    }

    final public List<VentasBean> getListVentasByDate(String fecha) {

        return dao.queryBuilder()
                .where(VentasBeanDao.Properties.Fecha.eq(fecha))
                .orderAsc(VentasBeanDao.Properties.Id)
                .list();

    }


    final public List<VentasBean> getListVentasEstado() {
        return dao.queryBuilder()
                .where(VentasBeanDao.Properties.Estado.eq("CO"))
                .orderAsc(VentasBeanDao.Properties.Id)
                .list();
    }

    final public List<VentasBean> getListVentasParaInventario(String fechaActual) {
        return dao.queryBuilder()
                .where(VentasBeanDao.Properties.Estado.eq("CO"), VentasBeanDao.Properties.Fecha.eq(fechaActual))
                .orderAsc(VentasBeanDao.Properties.Id)
                .list();
    }


    final public VentasBean getVentaByInventario(int venta) {
        final List<VentasBean> ventasBeans = dao.queryBuilder()
                .where(VentasBeanDao.Properties.Venta.eq(venta))
                .orderDesc(VentasBeanDao.Properties.Venta)
                .limit(1)
                .list();
        return ventasBeans.size() > 0 ? ventasBeans.get(0) : null;
    }

    final public List<CorteBean> getAllPartsGroupedClient() {

        final List<CorteBean> lista_corte = new ArrayList<>();

        Cursor cursor = null;
        final ProductoDao productosDAO = new ProductoDao();
        final ClienteDao clientesDAO = new ClienteDao();

        final PartidasBeanDao partidaVentaBeanDao = daoSession.getPartidasBeanDao();
        cursor = partidaVentaBeanDao.getDatabase().rawQuery("SELECT  " + ClienteBeanDao.TABLENAME + "." + ClienteBeanDao.Properties.Id.columnName + " AS idcliente," + ProductoBeanDao.TABLENAME + "." + ProductoBeanDao.Properties.Id.columnName + " AS idProducto, SUM(partidas.CANTIDAD) AS cantidad, SUM(partidas.PRECIO) AS precio, " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Descripcion.columnName + " AS descripcion, " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Impuesto.columnName + " AS iva  FROM " + PartidasBeanDao.TABLENAME +
                " INNER JOIN " + ProductoBeanDao.TABLENAME + " ON " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.ArticuloId.columnName + " = " + ProductoBeanDao.TABLENAME + "." + ProductoBeanDao.Properties.Id.columnName +
                " INNER JOIN " + VentasBeanDao.TABLENAME + " ON " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Venta.columnName + " = " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.Id.columnName +
                " INNER JOIN " + ClienteBeanDao.TABLENAME + " ON " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.ClienteId.columnName + " = " + ClienteBeanDao.TABLENAME + "." + ClienteBeanDao.Properties.Id.columnName +
                " WHERE " + VentasBeanDao.TABLENAME + "." + VentasBeanDao.Properties.Estado.columnName + " == 'CO' " +
                " GROUP BY " + ProductoBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Descripcion.columnName + ", " + ProductoBeanDao.TABLENAME + "." + ProductoBeanDao.Properties.Id.columnName + "," + ClienteBeanDao.TABLENAME + "." + ClienteBeanDao.Properties.Id.columnName + "," + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Descripcion.columnName + ", " + PartidasBeanDao.TABLENAME + "." + PartidasBeanDao.Properties.Impuesto.columnName + " ORDER BY clientes._id ", null);


        String sQuery = "" + cursor;
        while (cursor.moveToNext()) {

            final ProductoBean productoBean = (ProductoBean) productosDAO.getByID(cursor.getLong(cursor.getColumnIndex("idProducto")));
            final ClienteBean clienteBean = (ClienteBean) clientesDAO.getByID(Long.parseLong(cursor.getString(cursor.getColumnIndex("idcliente"))));

            final CorteBean corteBean = new CorteBean();
            corteBean.setClienteId(clienteBean.getId());
            corteBean.setProductoBean(productoBean);
            corteBean.setProductoId(productoBean.getId());
            corteBean.setClienteBean(clienteBean);
            corteBean.setCantidad(cursor.getInt(cursor.getColumnIndex("cantidad")));
            corteBean.setPrecio(cursor.getDouble(cursor.getColumnIndex("precio")));
            corteBean.setDescripcion(cursor.getString(cursor.getColumnIndex("descripcion")));
            corteBean.setImpuesto(cursor.getDouble(cursor.getColumnIndex("iva")));

            lista_corte.add(corteBean);
        }

        return lista_corte;
    }


    final public int getTotalCountVentas() throws Exception {
        final CountQuery<VentasBean> query = dao.queryBuilder().buildCount();
        return (int) query.count();

    }
}
