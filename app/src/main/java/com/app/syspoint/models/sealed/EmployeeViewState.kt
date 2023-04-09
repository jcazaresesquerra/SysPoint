package com.app.syspoint.models.sealed

import com.app.syspoint.repository.database.bean.EmpleadoBean

sealed class EmployeeViewState {
    object NetworkDisconnectedState: EmployeeViewState()
    object ShowProgressState: EmployeeViewState()
    object DismissProgressState: EmployeeViewState()
    data class SetUpEmployeesState(var data: List<EmpleadoBean?>): EmployeeViewState()
    data class RefreshEmployeesState(var data: List<EmpleadoBean?>): EmployeeViewState()
    object LoadingStartState: EmployeeViewState()
    object LoadingFinishState: EmployeeViewState()
    data class GetEmployeesState(var data: List<EmpleadoBean?>): EmployeeViewState()
    data class ShowEditEmployeeState(var employeeId: String): EmployeeViewState()
    object CanNotEditEmployeeState: EmployeeViewState()
    data class CallEmployeeState(var number: String): EmployeeViewState()
    object SendEmailState: EmployeeViewState()
}

sealed class EmployeeLoadingViewState {
    object LoadingStartState: EmployeeLoadingViewState()
    object LoadingFinishState: EmployeeLoadingViewState()
}