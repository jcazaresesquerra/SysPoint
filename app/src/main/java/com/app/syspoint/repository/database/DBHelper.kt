package com.app.syspoint.repository.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.app.syspoint.repository.database.bean.DaoMaster
import com.app.syspoint.repository.database.bean.DaoSession

class DBHelper {
    lateinit var db: SQLiteDatabase
    lateinit var daoSession: DaoSession
    lateinit var helper: DaoMaster.DevOpenHelper
    lateinit var daoMaster: DaoMaster

    companion object {
        private var  daoHelperSingleton: DBHelper? = null

        fun getSingleton(): DBHelper {
            if (daoHelperSingleton == null) {
                daoHelperSingleton = DBHelper()
            }
            return daoHelperSingleton!!
        }
    }

    init {
        //Prevent form the reflection api.
        if (daoHelperSingleton != null) {
            throw RuntimeException("Use getSingleton() method to get the single instance of this class.")
        }
    }

    fun init(context: Context?, dbName: String?) {
        // do this once, for example in your Application class
        helper = DaoMaster.DevOpenHelper(context, dbName, null)
        db = helper.writableDatabase
        daoMaster = DaoMaster(db)
        daoSession = daoMaster.newSession()
    }
}