package com.app.syspoint.models.json;

import com.app.syspoint.models.Employee;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EmpleadoJson {

    @SerializedName("Empleados")
    @Expose
    private List<Employee> empleados = null;

    public List<Employee> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<Employee> empleados) {
        this.empleados = empleados;
    }
}
