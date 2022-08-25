package com.app.syspoint.repository.database.dao;

import com.app.syspoint.repository.database.bean.EmpleadoBeanDao;
import com.app.syspoint.repository.database.bean.VisitasBean;
import com.app.syspoint.repository.database.bean.VisitasBeanDao;

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
