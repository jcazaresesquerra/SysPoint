package com.app.syspoint.interactor.cache

import com.app.syspoint.App
import com.app.syspoint.repository.cache.SharedPreferencesManager
import com.app.syspoint.repository.database.bean.EmpleadoBean
import com.app.syspoint.utils.JsonParser

class CacheInteractor() {

    fun saveSeller(empleadoBean: EmpleadoBean) {
        val json = JsonParser.parceObjectToJson(empleadoBean)
        App.INSTANCE?.baseContext?.let {
            SharedPreferencesManager(it).storeJsonSeller(json)
        }
    }

    fun getSeller(): EmpleadoBean? {
        App.INSTANCE?.baseContext?.let {
            val json = SharedPreferencesManager(it).getJsonSeller() ?: return null
            return JsonParser.getObjectFromJson(json, EmpleadoBean::class.java) as EmpleadoBean
        }
        return null
    }
}