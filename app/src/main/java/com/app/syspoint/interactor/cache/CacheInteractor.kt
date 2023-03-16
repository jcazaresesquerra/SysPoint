package com.app.syspoint.interactor.cache

import com.app.syspoint.App
import com.app.syspoint.models.json.TokenJson
import com.app.syspoint.repository.cache.SharedPreferencesManager
import com.app.syspoint.repository.database.bean.EmpleadoBean
import com.app.syspoint.utils.JsonParser

class CacheInteractor() {

    fun getToken(): String? {
        App.INSTANCE?.baseContext?.let {
            val token = SharedPreferencesManager(it).getToken() ?: return null
            return token
        }
        return null
    }

    fun saveToken(toke: String?) {
        App.INSTANCE?.baseContext?.let {
            SharedPreferencesManager(it).storeToken(toke)
        }
    }

    fun removeToken() {
        App.INSTANCE?.baseContext?.let {
            SharedPreferencesManager(it).removeToken()
        }
    }

    fun removeSellerFromCache() {
        App.INSTANCE?.baseContext?.let {
            SharedPreferencesManager(it).removeSellerFromCache()
        }
    }

    fun saveSeller(empleadoBean: EmpleadoBean?) {
        val json = if (empleadoBean != null) JsonParser.parceObjectToJson(empleadoBean) else null
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

    fun resetStockId() {
        App.INSTANCE?.baseContext?.let {
            SharedPreferencesManager(it).saveCurrentStockId(0)
        }
    }

}