package com.app.syspoint.utils.cache

import android.content.Context
import com.app.syspoint.repository.database.bean.EmpleadoBean
import com.app.syspoint.utils.JsonParser

class CacheInteractor(context: Context) {
    private var mContext = context

    fun saveSeller(empleadoBean: EmpleadoBean) {
        val json = JsonParser.parceObjectToJson(empleadoBean)
        SharedPreferencesManager(mContext).storeJsonSeller(json)
    }

    fun getSeller(): EmpleadoBean? {
        val json = SharedPreferencesManager(mContext).getJsonSeller() ?: return null
        return JsonParser.getObjectFromJson(json, EmpleadoBean::class.java) as EmpleadoBean
    }
}