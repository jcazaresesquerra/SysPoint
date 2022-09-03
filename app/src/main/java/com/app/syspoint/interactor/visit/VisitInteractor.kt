package com.app.syspoint.interactor.visit

abstract class VisitInteractor {

    interface OnSaveVisitListener {
        fun onSaveVisitSuccess()
        fun onSaveVisitError()
    }

    open fun executeSaveVisit(onSaveVisitListener: OnSaveVisitListener) {}
}