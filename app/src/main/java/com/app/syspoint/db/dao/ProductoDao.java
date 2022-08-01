package com.app.syspoint.db.dao;

import com.app.syspoint.db.bean.ProductoBean;
import com.app.syspoint.db.bean.ProductoBeanDao;

import java.util.List;

public class ProductoDao extends Dao{
    public ProductoDao() {
        super("ProductoBean");
    }


    //Retorna el producto por identificador
    public final ProductoBean getProductoByArticulo(String articulo){
        final List<ProductoBean> productoBeans = dao.queryBuilder()
                .where(ProductoBeanDao.Properties.Articulo.eq(articulo))
                .list();
        return productoBeans.size()> 0 ? productoBeans.get(0) : null;
    }


    final public List<ProductoBean> getProductosInventario() {
        return dao.queryBuilder()
                .where(ProductoBeanDao.Properties.Existencia.ge(1))
                .orderAsc(ProductoBeanDao.Properties.Id)
                .list();
    }


    final public List<ProductoBean> getProductos() {
        return dao.queryBuilder()
                .orderAsc(ProductoBeanDao.Properties.Id)
                .list();
    }


    final public List<ProductoBean> getProductoByID(String id) {
        return dao.queryBuilder()
                .where(ProductoBeanDao.Properties.Id.eq(id))
                .orderAsc(ProductoBeanDao.Properties.Id)
                .list();
    }

}
