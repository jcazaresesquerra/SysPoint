package com.app.syspoint.db.dao;

import com.app.syspoint.db.bean.EmpleadoBeanDao;
import com.app.syspoint.db.bean.VisitasBean;
import com.app.syspoint.db.bean.VisitasBeanDao;

import java.util.List;

public class VisitasDao extends Dao{
    public VisitasDao() {
        super("VisitasBean");
    }

    final public List<VisitasBean> getVisitaID(String id) {
        return dao.queryBuilder()
                .where(EmpleadoBeanDao.Properties.Id.eq(id))
                .orderAsc(EmpleadoBeanDao.Properties.Id)
                .list();
    }


    final public List<VisitasBean> getAllVisitas() {
        return dao.queryBuilder()
                .orderAsc(VisitasBeanDao.Properties.Id)
                .list();
    }

    final public List<VisitasBean> getAllVisitasFechaActual(String fecha) {
        return dao.queryBuilder()
                .where(VisitasBeanDao.Properties.Fecha.eq(fecha))
                .orderAsc(VisitasBeanDao.Properties.Id)
                .list();
    }


}
