package com.app.syspoint.db.bean;

import com.app.syspoint.db.dao.EmpleadoDao;

public class AppBundle extends Bean{

    private static UserSesion userSesion;

    final public static UserSesion getUserSesion(){
        if(userSesion==null) {
            userSesion = new UserSesion();
        }
        return userSesion;
    }

    final public static EmpleadoBean getUserBean() {
        final UserSesion userSesion_ = AppBundle.getUserSesion();
        final EmpleadoDao vendedoresDao = new EmpleadoDao();
        final EmpleadoBean vendedoresBean = vendedoresDao.getByEmail(userSesion_.getUsuario());
        return vendedoresBean;
    }

    final public static void setUserSession(final UserSesion userSesion){
        AppBundle.userSesion = userSesion;
    }
}
