package com.app.syspoint.repository.database.dao;

import com.app.syspoint.repository.database.bean.EmpleadoBean;
import com.app.syspoint.repository.database.bean.EmpleadoBeanDao;

import org.greenrobot.greendao.query.CountQuery;

import java.util.List;

public class EmpleadoDao extends Dao {

    public EmpleadoDao() {
        super("EmpleadoBean");
    }

    //Retorna el empleado por identificador
    public final EmpleadoBean getEmpleadoByIdentificador(String identificador){
        final List<EmpleadoBean> empleadoBeans = dao.queryBuilder()
                .where(EmpleadoBeanDao.Properties.Identificador.eq(identificador))
                .list();
        return empleadoBeans.size()> 0? empleadoBeans.get(0) : null;
    }


    //Retorna el empleado por identificador
    public final EmpleadoBean getValidaLogin(String correo, String contrasenia){
        final List<EmpleadoBean> empleadoBeans = dao.queryBuilder()
                .where(EmpleadoBeanDao.Properties.Email.eq(correo), EmpleadoBeanDao.Properties.Contrasenia.eq(contrasenia))
                .list();
        return empleadoBeans.size()> 0? empleadoBeans.get(0) : null;
    }


    final public int getTotalEmpleados() {
        final CountQuery<EmpleadoBean> query = dao.queryBuilder().buildCount();
        return (int)query.count();
    }

    final public List<EmpleadoBean> getEmpleados() {
        return dao.queryBuilder()
                .orderAsc(EmpleadoBeanDao.Properties.Id)
                .list();
    }



    final public List<EmpleadoBean> getEmpleadoByID(String id) {
        return dao.queryBuilder()
                .where(EmpleadoBeanDao.Properties.Id.eq(id))
                .orderAsc(EmpleadoBeanDao.Properties.Id)
                .list();
    }


    final public EmpleadoBean getByEmail(final String email) {
        if (email != null && !email.isEmpty()) {
            try {
                final List<EmpleadoBean> usuarioBeanList = dao.queryBuilder()
                        .where(EmpleadoBeanDao.Properties.Email.eq(email))
                        .list();
                return usuarioBeanList.size() > 0 ? usuarioBeanList.get(0) : null;
            } catch (Exception exception) {
                exception.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }


}
