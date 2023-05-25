package com.app.syspoint.repository.objectBox;

import android.util.Log;

import com.app.syspoint.interactor.cache.CacheInteractor;
import com.app.syspoint.models.UserSession;
import com.app.syspoint.repository.objectBox.dao.EmployeeDao;
import com.app.syspoint.repository.objectBox.entities.EmployeeBox;

public class AppBundle {

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

    final public static EmployeeBox getUserBox() {
        final UserSession userSesion_ = AppBundle.getUserSesion();
        if (userSesion_ == null) Log.e(TAG, "user session is null");
        if (userSesion_ != null) {
            final EmployeeDao employeeDao = new EmployeeDao();
            final EmployeeBox vendedoresBean = employeeDao.getByEmail(userSesion_.getUsuario());
            if (vendedoresBean == null) {
                Log.e(TAG, "vendedoresBean is null");
            }

            return vendedoresBean;
        } else {
            EmployeeBox employeeBox = new CacheInteractor().getSeller();
            return employeeBox;
        }
    }

    final public static void setUserSession(final UserSession userSesion){
        AppBundle.userSesion = userSesion;
    }
}
