package com.app.syspoint.interactor.visit

import com.app.syspoint.models.Visit
import com.app.syspoint.repository.request.RequestVisit
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class VisitInteractorImp: VisitInteractor() {

    override fun executeSaveVisit(visits: List<Visit>, onSaveVisitListener: OnSaveVisitListener) {
        super.executeSaveVisit(visits, onSaveVisitListener)
        GlobalScope.launch {
            RequestVisit.saveVisit(visits, onSaveVisitListener)
        }
    }
}