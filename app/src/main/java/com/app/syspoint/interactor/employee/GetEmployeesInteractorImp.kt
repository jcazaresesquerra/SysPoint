package com.app.syspoint.interactor.employee

import com.app.syspoint.models.Employee
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

    override fun executeGetEmployeeById(getEmployeeByIdListener: GetEmployeeByIdListener) {
        super.executeGetEmployeeById(getEmployeeByIdListener)
        GlobalScope.launch {

        }
    }

    override fun executeSaveEmployees(employeeList: List<Employee>, saveEmployeeListener: SaveEmployeeListener) {
        super.executeSaveEmployees(employeeList, saveEmployeeListener)
        GlobalScope.launch {
            RequestEmployees.saveEmployee(employeeList, saveEmployeeListener)
        }
    }
}