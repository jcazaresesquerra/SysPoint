package com.app.syspoint;

import android.app.Application;

import com.app.syspoint.db.DBHelper;

public class App extends Application {

    private DBHelper dbHelper;
    private static App _INSTANCE = null;
    @Override
    public void onCreate() {
        super.onCreate();
        _INSTANCE = this;
        dbHelper=  DBHelper.getSingleton();
        dbHelper.init(_INSTANCE, "point3_db");
    }

}
