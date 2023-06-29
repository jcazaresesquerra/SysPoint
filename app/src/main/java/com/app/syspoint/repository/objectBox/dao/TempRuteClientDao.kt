package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.TempRuteClientBox
import com.app.syspoint.repository.objectBox.entities.TempRuteClientBox_
import io.objectbox.query.QueryBuilder
import timber.log.Timber

private const val TAG = "TempRuteClientDao"

class TempRuteClientDao: AbstractDao<TempRuteClientBox>() {

    fun clear() {
        Timber.tag(TAG).d("clear")
        abstractBox<TempRuteClientBox>().removeAll()
    }

    fun insertBox(box: TempRuteClientBox) {
        Timber.tag(TAG).d("insertBox -> $box")
        insert(box)
    }

    fun removeBox(id: Long) {
        Timber.tag(TAG).d("removeBox -> $id")
        abstractBox<TempRuteClientBox>().remove(id)
    }

    fun getClienteByCuentaCliente(cuenta: String?): TempRuteClientBox? {
        val query = abstractBox<TempRuteClientBox>().query()
            .equal(TempRuteClientBox_.cuenta, cuenta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

}