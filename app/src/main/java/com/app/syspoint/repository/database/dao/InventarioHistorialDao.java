package com.app.syspoint.repository.database.dao;

import com.app.syspoint.repository.database.bean.InventarioHistorialBean;
import com.app.syspoint.repository.database.bean.InventarioHistorialBeanDao;

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