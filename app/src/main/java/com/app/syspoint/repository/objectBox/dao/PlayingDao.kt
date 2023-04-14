package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.PlayingBox

class PlayingDao: AbstractDao<PlayingBox>() {

    fun clear() {
        abstractBox<PlayingBox>().removeAll()
    }
}