package com.app.syspoint.db.dao;

import com.app.syspoint.db.bean.PrinterBean;
import com.app.syspoint.db.bean.PrinterBeanDao;

import org.greenrobot.greendao.query.CountQuery;

import java.util.List;

public class PrinterDao extends Dao {
    public PrinterDao() {
        super("PrinterBean");
    }

    final public int existeConfiguracionImpresora() {
        final CountQuery<PrinterBeanDao> query = dao.queryBuilder().buildCount();
        return (int)query.count();
    }

    final public PrinterBean getImpresoraEstablecida() {
        final List<PrinterBean> deviceBeanList = dao.queryBuilder()
                .where(PrinterBeanDao.Properties.Id.eq(1))
                .list();
        return deviceBeanList.size()>0?deviceBeanList.get(0):null;
    }
}
