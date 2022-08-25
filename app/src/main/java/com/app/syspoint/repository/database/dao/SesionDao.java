package com.app.syspoint.repository.database.dao;

import com.app.syspoint.repository.database.bean.SesionBean;

import org.greenrobot.greendao.query.CountQuery;

import java.util.List;

public class SesionDao extends Dao {


    public SesionDao(){
        super("SesionBean");
    }

    final public SesionBean getUsuarioSesion() {
        final List<SesionBean> sesionBeanList = dao.loadAll();
        return  sesionBeanList.size()>0?sesionBeanList.get(0):null;
    }

    final public void saveSesion(final SesionBean sesionBean) {
        this.clear();
        this.dao.insert(sesionBean);
    }

    final public int existeUsuario() {
        final CountQuery<SesionBean> query = dao.queryBuilder().buildCount();
        return (int)query.count();
    }
}
