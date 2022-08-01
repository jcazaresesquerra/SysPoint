package com.app.syspoint.db.dao;

import com.app.syspoint.db.bean.RolesBean;
import com.app.syspoint.db.bean.RolesBeanDao;

import java.util.List;

public class RolesDao extends Dao {
    public RolesDao() {
        super("RolesBean");
    }

    final public List<RolesBean> getListaRolesByEmpleado(String identificador) {
        return dao.queryBuilder()
                .where(RolesBeanDao.Properties.Identificador.eq(identificador))

                .list();
    }




    //Retorna el empleado por identificador
    public final RolesBean getRolByEmpleado(String identificador, String modulo){
        final List<RolesBean> clienteBeans = dao.queryBuilder()
                .where(RolesBeanDao.Properties.Identificador.eq(identificador), RolesBeanDao.Properties.Modulo.eq(modulo))
                .list();
        return clienteBeans.size()> 0? clienteBeans.get(0) : null;
    }


    public final RolesBean getRolByModule(String identificador, String modulo){
        final List<RolesBean> clienteBeans = dao.queryBuilder()
                .where(RolesBeanDao.Properties.Identificador.eq(identificador), RolesBeanDao.Properties.Modulo.eq(modulo))
                .list();
        return clienteBeans.size()> 0? clienteBeans.get(0) : null;
    }

}
