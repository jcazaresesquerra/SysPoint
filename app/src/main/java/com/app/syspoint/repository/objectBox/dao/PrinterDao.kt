package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.PrinterBox
import com.app.syspoint.repository.objectBox.entities.PrinterBox_

class PrinterDao: AbstractDao<PrinterBox>() {

    fun clear() {
        abstractBox<PrinterBox>().removeAll()
    }

    fun insertBox(box: PrinterBox) {
        insert(box)
    }

    fun existeConfiguracionImpresora(): Int {
        val results = abstractBox<PrinterBox>().all
        return results.count()
    }

    fun getImpresoraEstablecida(): PrinterBox? {
        val query = abstractBox<PrinterBox>().query()
            .equal(PrinterBox_.id, 1)
            .build()
        val results = query.find()
        query.close()
        return if (results.isNotEmpty()) results[0] else null
    }
}