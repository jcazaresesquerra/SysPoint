package com.app.syspoint.models.json

import com.app.syspoint.models.Employee
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class EmployeeJson(
    @SerializedName("Empleados")
    @Expose
    var employees: List<Employee?>? = null
)
