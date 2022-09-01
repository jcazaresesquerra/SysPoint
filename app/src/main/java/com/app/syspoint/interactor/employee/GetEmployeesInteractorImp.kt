package com.app.syspoint.interactor.employee

import com.app.syspoint.repository.request.RequestEmployees
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GetEmployeesInteractorImp: GetEmployeeInteractor() {

    override fun executeGetEmployees(getEmployeesListener: GetEmployeesListener) {
        super.executeGetEmployees(getEmployeesListener)
        GlobalScope.launch {
            RequestEmployees.requestEmployees(getEmployeesListener)
        }
    }
}