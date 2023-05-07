package com.app.syspoint.interactor.employee

import com.app.syspoint.models.Employee
import com.app.syspoint.repository.objectBox.entities.EmployeeBox

abstract class GetEmployeeInteractor {

    interface GetEmployeesListener {
        fun onGetEmployeesSuccess(employees: List<EmployeeBox?>)
        fun onGetEmployeesError()
    }

    interface GetEmployeeByIdListener {
        fun onGetEmployeeByIdSuccess(employees: EmployeeBox)
        fun onGetEmployeeByIdError()
    }

    interface SaveEmployeeListener {
        fun onSaveEmployeeSuccess()
        fun onSaveEmployeeError()
    }

    open fun executeGetEmployees(getEmployeesListener: GetEmployeesListener) {}
    open fun executeGetEmployeeById(getEmployeeByIdListener: GetEmployeeByIdListener) {}
    open fun executeSaveEmployees(employeeList: List<Employee>, saveEmployeeListener: SaveEmployeeListener) {}
}