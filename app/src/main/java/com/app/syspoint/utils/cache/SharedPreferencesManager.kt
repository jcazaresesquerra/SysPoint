package com.app.syspoint.utils.cache

import android.content.Context

class SharedPreferencesManager(context: Context) {
    /*
     * tags to save data
     */
    private val SHARED_PREFERENCES_NAME : String = "local_shared_preferences"
    private val JSON_SELLERS : String = "json_sellers"
    private val JSON_SELLS : String = "json_sells"

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

    fun storeJsonSeller(json : String) {
        val editor = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_SELLERS, json)
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