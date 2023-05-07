package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.ChargeModelBox

class ChargeModelDao: AbstractDao<ChargeModelBox>() {

    fun list(): List<ChargeModelBox> {
        return abstractBox<ChargeModelBox>().all
    }

    fun clear() {
        abstractBox<ChargeModelBox>().removeAll()
    }

    fun delete(id: Long) {
        abstractBox<ChargeModelBox>().remove(id)
    }

    fun insertBox(box: ChargeModelBox) {
        insert(box)
    }

}