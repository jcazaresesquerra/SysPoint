package com.app.syspoint.doments

import com.app.syspoint.repository.database.bean.Bean

abstract class BaseTicket {
    open lateinit var document: String
    open lateinit var bean: Bean

    open fun template() {}
}