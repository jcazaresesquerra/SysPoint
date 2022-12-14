package com.app.syspoint.repository.database.bean;

import android.util.Log;

import com.app.syspoint.repository.database.dao.EmployeeDao;

public class AppBundle extends Bean{

    private static String TAG = "AppBundle";
    private static UserSession userSesion;

    final public static UserSession getUserSesion(){
        if (userSesion == null) {
            synchronized (AppBundle.class) {
                if (userSesion == null) {
                    userSesion = new UserSession();
                }
            }
        }
        return userSesion;
    }

    final public static EmpleadoBean getUserBean() {
        final UserSession userSesion_ = AppBundle.getUserSesion();
        if (userSesion_ == null) Log.e(TAG, "user session is null");
        final EmployeeDao vendedoresDao = new EmployeeDao();
        final EmpleadoBean vendedoresBean = vendedoresDao.getByEmail(userSesion_.getUsuario());
        if (vendedoresBean == null) {
            Log.e(TAG, "vendedoresBean is null");
        }
        return vendedoresBean;
    }

    final public static void setUserSession(final UserSession userSesion){
        AppBundle.userSesion = userSesion;
    }
}
