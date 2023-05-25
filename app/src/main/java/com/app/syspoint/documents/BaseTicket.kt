package com.app.syspoint.documents

import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.repository.objectBox.AppBundle
import com.app.syspoint.repository.objectBox.dao.EmployeeDao
import com.app.syspoint.repository.objectBox.dao.SessionDao
import com.app.syspoint.repository.objectBox.entities.BaseBox
import com.app.syspoint.repository.objectBox.entities.EmployeeBox


abstract class BaseTicket {
    open lateinit var document: String
    open lateinit var box: BaseBox

    open fun template() {}

    open fun buildSyspointHeader(): String {
       return ""
    }

    open fun buildDonAquiHeader(): String {
        return ""
    }

    open fun getEmployee(): EmployeeBox? {
        var vendedoresBean = AppBundle.getUserBox()
        if (vendedoresBean == null) {
            val sessionBox = SessionDao().getUserSession()
            vendedoresBean = if (sessionBox != null) {
                EmployeeDao().getEmployeeByID(sessionBox.empleadoId)
            } else {
                CacheInteractor().getSeller()
            }
        }
        return vendedoresBean
    }
}