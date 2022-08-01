package com.app.syspoint.db.dao;

import com.app.syspoint.db.bean.InventarioHistorialBean;
import com.app.syspoint.db.bean.InventarioHistorialBeanDao;

import java.util.List;

public class InventarioHistorialDao extends Dao {

    public InventarioHistorialDao() {
        super("InventarioHistorialBean");
    }
    public final InventarioHistorialBean getInvatarioPorArticulo(String articulo){
        final List<InventarioHistorialBean> clienteBeans = dao.queryBuilder()
                .where(InventarioHistorialBeanDao.Properties.Articulo_clave.eq(articulo))
                .list();
        return clienteBeans.size()> 0? clienteBeans.get(0) : null;
    }


}