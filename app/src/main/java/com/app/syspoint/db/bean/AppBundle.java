package com.app.syspoint.db.bean;

import android.util.Log;

import com.app.syspoint.db.dao.EmpleadoDao;

public class AppBundle extends Bean{

    private static String TAG = "AppBundle";
    private static UserSesion userSesion;

    final public static UserSesion getUserSesion(){
        if (userSesion == null) {
            synchronized (AppBundle.class) {
                if (userSesion == null) {
                    userSesion = new UserSesion();
                }
            }
        }
        return userSesion;
    }

    final public static EmpleadoBean getUserBean() {
        final UserSesion userSesion_ = AppBundle.getUserSesion();
        if (userSesion_ == null) Log.e(TAG, "user session is null");
        final EmpleadoDao vendedoresDao = new EmpleadoDao();
        final EmpleadoBean vendedoresBean = vendedoresDao.getByEmail(userSesion_.getUsuario());
        if (vendedoresBean == null) Log.e(TAG, "vendedoresBean is null");
        return vendedoresBean;
    }

    final public static void setUserSession(final UserSesion userSesion){
        AppBundle.userSesion = userSesion;
    }
}
