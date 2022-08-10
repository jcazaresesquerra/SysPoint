package com.app.syspoint.utils

import com.google.gson.Gson

class JsonParser {
    companion object {

        /**
         * @return
         *      A json string taht contains an object
         */
        fun parceObjectToJson(obj: Any): String {
            return Gson().toJson(obj)
        }

        /**
         * @return
         *      An object
         */
        fun getObjectFromJson(json: String, clase: Class<*>): Any {
            return Gson().fromJson<Any>(json, clase)
        }
    }
}