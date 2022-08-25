package com.app.syspoint.repository.database.dao;

import com.app.syspoint.repository.database.bean.PersistenciaPrecioBean;
import com.app.syspoint.repository.database.bean.PersistenciaPrecioBeanDao;
import com.app.syspoint.repository.database.bean.PrinterBeanDao;

import org.greenrobot.greendao.query.CountQuery;

import java.util.List;

public class PersistenciaPrecioDao extends Dao{

    public PersistenciaPrecioDao() {
        super("PersistenciaPrecioBean");
    }

    final public PersistenciaPrecioBean getPersistencia() {
        final List<PersistenciaPrecioBean> deviceBeanList = dao.queryBuilder()
                .where(PersistenciaPrecioBeanDao.Properties.Valor.eq(1))
                .list();
        return deviceBeanList.size()>0?deviceBeanList.get(0):null;
    }

    final public int existePersistencia() {
        final CountQuery<PrinterBeanDao> query = dao.queryBuilder().buildCount();
        return (int)query.count();
    }
}