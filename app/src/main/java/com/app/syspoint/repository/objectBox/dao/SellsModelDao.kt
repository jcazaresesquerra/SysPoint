package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.SellModelBox
import timber.log.Timber

private const val TAG = "SellsModelDao"

class SellsModelDao: AbstractDao<SellModelBox>() {

    fun clear() {
        Timber.tag(TAG).d("clear")
        abstractBox<SellModelBox>().removeAll()
    }

    fun delete(id: Long) {
        Timber.tag(TAG).d("delete -> id: %s", id)
        abstractBox<SellModelBox>().remove(id)
    }

    fun list(): List<SellModelBox> {
        Timber.tag(TAG).d("list")
        return abstractBox<SellModelBox>().all
    }
}