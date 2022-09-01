package com.app.syspoint.interactor.employee

import com.app.syspoint.repository.database.bean.EmpleadoBean

abstract class GetEmployeeInteractor {
    interface GetEmployeesListener {
        fun onGetEmployeesSuccess(employees: List<EmpleadoBean?>)
        fun onGetEmployeesError()
    }

    open fun executeGetEmployees(getEmployeesListener: GetEmployeesListener) {}
}