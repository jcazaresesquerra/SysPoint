package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.PlayingBox
import timber.log.Timber

private const val TAG = "PlayingDao"

class PlayingDao: AbstractDao<PlayingBox>() {

    fun clear() {
        Timber.tag(TAG).d("clear")
        abstractBox<PlayingBox>().removeAll()
    }
}