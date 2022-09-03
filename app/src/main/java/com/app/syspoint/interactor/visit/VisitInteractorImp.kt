package com.app.syspoint.interactor.visit

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VisitInteractorImp: VisitInteractor() {

    override fun executeSaveVisit(onSaveVisitListener: OnSaveVisitListener) {
        super.executeSaveVisit(onSaveVisitListener)
        GlobalScope.launch {

        }
    }
}