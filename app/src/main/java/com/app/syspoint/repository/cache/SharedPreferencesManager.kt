package com.app.syspoint.repository.cache

import android.content.Context

class SharedPreferencesManager(context: Context) {
    /*
     * tags to save data
     */
    private val SHARED_PREFERENCES_NAME : String = "local_shared_preferences"
    private val JSON_SELLERS : String = "json_sellers"
    private val JSON_SELLS : String = "json_sells"
    private val DATA_STATUS : String = "data_status"


    /*
     * Objects
     */
    private val mContext : Context = context

    /**
     * This method removes all sharedPreferences session data
     */
    fun removeSessionData() {
        mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }

    fun storeLocalSession(updated: Boolean) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(DATA_STATUS, updated)
        editor.apply()
    }

    fun isSessionUpdated(): Boolean {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(DATA_STATUS, false)
    }

    fun storeJsonSeller(json : String?) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_SELLERS, json)
        editor.apply()
    }

    fun removeSellerFromCache() {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.remove(JSON_SELLERS)
        editor.apply()
    }

    /**
     * @return
     *      A json string that contains user object
     */
    fun getJsonSeller() : String? {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_SELLERS, null)
    }

    fun storeJsonSells(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_SELLS, json)
        editor.apply()
    }

    /**
     * @return
     *      A json string that contains user object
     */
    fun getJsonSells() : String? {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_SELLS, null)
    }
}