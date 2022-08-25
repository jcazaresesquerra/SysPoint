package com.app.syspoint.repository.database.dao;

import com.app.syspoint.repository.database.bean.PreciosEspecialesBean;
import com.app.syspoint.repository.database.bean.PreciosEspecialesBeanDao;

import java.util.List;

public class PreciosEspecialesDao extends Dao {
    public PreciosEspecialesDao() {
        super("PreciosEspecialesBean");
    }

    //Retorna el precio por cliente y por articulo
    final public PreciosEspecialesBean getPrecioEspeciaPorCliente(String productoID, String clienteID) {
        try {
            final List<PreciosEspecialesBean> ruteoBeans = dao.queryBuilder()
                    .where(PreciosEspecialesBeanDao.Properties.Articulo.eq(productoID), PreciosEspecialesBeanDao.Properties.Cliente.eq(clienteID), PreciosEspecialesBeanDao.Properties.Active.eq(true))
                    .list();
            return ruteoBeans.size() > 0 ? ruteoBeans.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //Retorna la listos especiales por ID
    final public List<PreciosEspecialesBean> getListaPrecioPorCliente(String clienteID){
        return dao.queryBuilder()
                .where(PreciosEspecialesBeanDao.Properties.Cliente.eq(clienteID), PreciosEspecialesBeanDao.Properties.Active.eq((true)))
                .list();
    }


    //Retorna la listos especiales por ID
    final public List<PreciosEspecialesBean> getListaPrecioPorClienteUpdate(String clienteID){
        return dao.queryBuilder()
                .where(PreciosEspecialesBeanDao.Properties.Cliente.eq(clienteID), PreciosEspecialesBeanDao.Properties.Active.eq((false)))
                .list();
    }



    final public List<PreciosEspecialesBean> getPreciosBydate(String fecha){
        return dao.queryBuilder()
                .where(PreciosEspecialesBeanDao.Properties.Fecha_sync.eq(fecha), PreciosEspecialesBeanDao.Properties.Active.eq((false)))
                .list();
    }



    final public  PreciosEspecialesBean getPrecioEspecialPorIdentificador(String cliente, String articulo){
        final List<PreciosEspecialesBean> ruteoBeans = dao.queryBuilder()
                .where(PreciosEspecialesBeanDao.Properties.Articulo.eq(articulo), PreciosEspecialesBeanDao.Properties.Cliente.eq(cliente) )
                .list();
        return ruteoBeans.size()>0?ruteoBeans.get(0):null;
    }
}
