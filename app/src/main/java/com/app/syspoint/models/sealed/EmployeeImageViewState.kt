package com.app.syspoint.models.sealed

class EmployeeImageViewState {
    object EmployeeImageLoadingStartViewState: EmployeeLoadingViewState()
    object EmployeeImageLoadingFinishViewState: EmployeeLoadingViewState()
}