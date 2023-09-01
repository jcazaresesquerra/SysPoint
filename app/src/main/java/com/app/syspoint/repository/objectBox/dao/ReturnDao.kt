package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.ReturnBox
import timber.log.Timber

private const val TAG = "ReturnDao"

class ReturnDao: AbstractDao<ReturnBox>() {

    fun insertBox(returnBox: ReturnBox) {
        abstractBox<ReturnBox>().put(returnBox)
    }

    fun insertAll(returnBoxes: List<ReturnBox>) {
        abstractBox<ReturnBox>().put(returnBoxes)
    }

    fun delete(id: Long) {
        abstractBox<ReturnBox>().remove(id)
    }

    fun clear() {
        Timber.tag(TAG).d("clear")
        abstractBox<ReturnBox>().removeAll()
    }

    fun list(): List<ReturnBox> {
        Timber.tag(TAG).d("list")
        return abstractBox<ReturnBox>().all
    }
}