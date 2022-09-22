package com.app.syspoint.models.json

import com.app.syspoint.models.Client
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.google.gson.internal.LinkedTreeMap

data class ClientJson(
    @SerializedName("Clientes") @Expose
    var clients: List<Client?>? = null
) {
    fun fromArray(items: List<LinkedTreeMap<String?, Any?>?>): List<Client>? {
        val result: MutableList<Client> = ArrayList()
        for (content in items) {
            result.add(Client())
        }
        return result
    }
}
