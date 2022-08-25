package com.app.syspoint.repository.database.dao;
import com.app.syspoint.repository.database.bean.RuteoBean;
import com.app.syspoint.repository.database.bean.RuteoBeanDao;

import java.util.List;

public class RuteoDao extends Dao{

    public RuteoDao() {
        super("RuteoBean");
    }

    final public RuteoBean getRutaEstablecida() {
        final List<RuteoBean> ruteoBeans = dao.queryBuilder()
                .where(RuteoBeanDao.Properties.Id.eq(1))
                .list();
        return ruteoBeans.size()>0?ruteoBeans.get(0):null;
    }


    final public RuteoBean getRutaEstablecidaFechaActual(String fecha) {
        final List<RuteoBean> ruteoBeans = dao.queryBuilder()
                .where(RuteoBeanDao.Properties.Fecha.eq(fecha))
                .list();
        return ruteoBeans.size()>0?ruteoBeans.get(0):null;
    }

}
