package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.SellModelBox

class SellsModelDao: AbstractDao<SellModelBox>() {

    fun clear() {
        abstractBox<SellModelBox>().removeAll()
    }

    fun delete(id: Long) {
        abstractBox<SellModelBox>().remove(id)
    }

    fun list(): List<SellModelBox> {
        return abstractBox<SellModelBox>().all
    }
}