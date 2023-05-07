package com.app.syspoint.documents

import com.app.syspoint.repository.objectBox.entities.BaseBox

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
}