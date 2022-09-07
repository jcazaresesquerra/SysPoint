package com.app.syspoint.documents

import com.app.syspoint.repository.database.bean.Bean

abstract class BaseTicket {
    open lateinit var document: String
    open lateinit var bean: Bean

    open fun template() {}
}