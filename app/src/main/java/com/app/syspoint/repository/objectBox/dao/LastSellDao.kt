package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.LastSellBox
import com.app.syspoint.repository.objectBox.entities.LastSellBox_
import timber.log.Timber

private const val TAG = "LastSellDao"

class LastSellDao : AbstractDao<LastSellBox>() {

    fun clear() {
        Timber.tag(TAG).d("clear")
        abstractBox<LastSellBox>().removeAll()
    }

    fun insertBox(box: LastSellBox) {
        Timber.tag(TAG).d("insertBox -> $box")
        insert(box)
    }

    fun getLastSellByClient(clientId: Long): LastSellBox? {
        val query = abstractBox<LastSellBox>().query()
            .equal(LastSellBox_.ventaClientId, clientId)
            .order(LastSellBox_.ventaCreatedAt)
            .build()
        val results = query.find()
        query.close()

        return if (results.isNullOrEmpty()) null else results[0]
    }
}