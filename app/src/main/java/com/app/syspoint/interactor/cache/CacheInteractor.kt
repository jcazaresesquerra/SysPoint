package com.app.syspoint.interactor.cache

import com.app.syspoint.App
import com.app.syspoint.repository.cache.SharedPreferencesManager
import com.app.syspoint.repository.objectBox.entities.EmployeeBox
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

    fun saveSeller(empleadoBean: EmployeeBox?) {
        val json = if (empleadoBean != null) JsonParser.parceObjectToJson(empleadoBean) else null
        App.INSTANCE?.baseContext?.let {
            SharedPreferencesManager(it).storeJsonSeller(json)
        }
    }

    fun getSeller(): EmployeeBox? {
        App.INSTANCE?.baseContext?.let {
            val json = SharedPreferencesManager(it).getJsonSeller() ?: return null
            return JsonParser.getObjectFromJson(json, EmployeeBox::class.java) as EmployeeBox
        }
        return null
    }

    fun resetStockId() {
        App.INSTANCE?.baseContext?.let {
            SharedPreferencesManager(it).saveCurrentStockId(0)
        }
    }

    fun getCurrentStockId(): Int {
        App.INSTANCE?.baseContext?.let {
            return SharedPreferencesManager(it).getCurrentStockId()
        }
        return 0
    }

    fun resetLoadId() {
        App.INSTANCE?.baseContext?.let {
            SharedPreferencesManager(it).saveCurrentLoadId(0)
        }
    }


    fun setLoadId(loadId: Int) {
        App.INSTANCE?.baseContext?.let {
            return SharedPreferencesManager(it).saveCurrentLoadId(loadId)
        }
    }

    fun getCurrentLoadId(): Int {
        App.INSTANCE?.baseContext?.let {
            return SharedPreferencesManager(it).getCurrentLoadId()
        }
        return 0
    }

}