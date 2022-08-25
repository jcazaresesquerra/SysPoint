package com.app.syspoint.repository.database.dao;

import com.app.syspoint.repository.database.bean.InventarioBean;
import com.app.syspoint.repository.database.bean.InventarioBeanDao;

import java.util.List;

public class InventarioDao extends Dao {
    public InventarioDao() {
        super("InventarioBean");
    }

    //Retorna el producto por identificador
    public final InventarioBean getProductoByArticulo(String clave){
        final List<InventarioBean> productoBeans = dao.queryBuilder()
                .where(InventarioBeanDao.Properties.Articulo_clave.eq(clave))
                .list();
        return productoBeans.size()> 0 ? productoBeans.get(0) : null;
    }


    final public List<InventarioBean> getInventarioPendiente() {
        return dao.queryBuilder()
                .where(InventarioBeanDao.Properties.Estado.eq("CO"))
                .orderAsc(InventarioBeanDao.Properties.Id)
                .list();
    }


    public final InventarioBean getProductoByArticulo(int articulo){
        final List<InventarioBean> inventarioBeans = dao.queryBuilder()
                .where(InventarioBeanDao.Properties.ArticuloId.eq(articulo))
                .list();

        return inventarioBeans.size() > 0 ? inventarioBeans.get(0) : null;
    }



}
