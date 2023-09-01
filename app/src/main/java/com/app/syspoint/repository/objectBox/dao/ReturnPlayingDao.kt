package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.ReturnPlayingBox
import timber.log.Timber

private const val TAG = "ReturnPlayingDao"

class ReturnPlayingDao: AbstractDao<ReturnPlayingBox>() {
    fun clear() {
        Timber.tag(TAG).d("clear")
        abstractBox<ReturnPlayingBox>().removeAll()
    }
}