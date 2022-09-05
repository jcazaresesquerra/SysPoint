package com.app.syspoint.interactor.visit

import com.app.syspoint.models.Visit

abstract class VisitInteractor {

    interface OnSaveVisitListener {
        fun onSaveVisitSuccess()
        fun onSaveVisitError()
    }

    open fun executeSaveVisit(visits: List<Visit>, onSaveVisitListener: OnSaveVisitListener) {}
}