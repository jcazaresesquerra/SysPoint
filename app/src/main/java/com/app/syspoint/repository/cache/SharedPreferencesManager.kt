package com.app.syspoint.repository.cache

import android.content.Context
import java.time.LocalDate

class SharedPreferencesManager(context: Context) {
    /*
     * tags to save data
     */
    private val SHARED_PREFERENCES_NAME: String = "local_shared_preferences"
    private val JSON_SELLERS: String = "json_sellers"
    private val JSON_SELLS: String = "json_sells"
    private val DATA_STATUS: String = "data_status"
    private val APP_TOKEN: String = "app_token"
    private val STOCK_ID: String = "stock_id"
    private val LOAD_ID: String = "load_id"
    private val CURRENT_SAVED_DATE: String = "currentDate"


    /*
     * Objects
     */
    private val mContext: Context = context

    /**
     * This method removes all sharedPreferences session data
     */
    fun removeSessionData() {
        mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().clear()
            .apply()
    }

    fun storeLocalSession(updated: Boolean) {
        val editor =
            mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putBoolean(DATA_STATUS, updated)
        editor.apply()
    }

    fun isSessionUpdated(): Boolean {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(DATA_STATUS, false)
    }

    fun storeJsonSeller(json: String?) {
        val editor =
            mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_SELLERS, json)
        editor.apply()
    }

    fun removeSellerFromCache() {
        val editor =
            mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.remove(JSON_SELLERS)
        editor.apply()
    }

    /**
     * @return
     *      A json string that contains user object
     */
    fun getJsonSeller(): String? {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_SELLERS, null)
    }

    fun storeJsonSells(json: String) {
        val editor =
            mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(JSON_SELLS, json)
        editor.apply()
    }

    /**
     * @return
     *      A json string that contains user object
     */
    fun getJsonSells(): String? {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(JSON_SELLS, null)
    }

    fun getToken(): String? {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(APP_TOKEN, null)
    }

    fun storeToken(token: String?) {
        val editor =
            mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(APP_TOKEN, token)
        editor.apply()
    }

    fun removeToken() {
        val editor =
            mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.remove(APP_TOKEN)
        editor.apply()
    }

    fun saveCurrentStockId(stockId: Int) {
        val editor =
            mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt(STOCK_ID, stockId)
        editor.apply()
    }

    fun getCurrentStockId(): Int {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(STOCK_ID, 0)
    }


    fun saveCurrentLoadId(loadId: Int) {
        val editor =
            mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt(LOAD_ID, loadId)
        editor.apply()
    }

    fun getCurrentLoadId(): Int {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getInt(LOAD_ID, 0)
    }

    fun storeCurrentDate(currentDate: LocalDate) {
        val editor =
            mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(CURRENT_SAVED_DATE, currentDate.toString())
        editor.apply()
    }

    fun getCurrentDate(): String? {
        val prefs = mContext.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
        return prefs.getString(CURRENT_SAVED_DATE, null)
    }
}
