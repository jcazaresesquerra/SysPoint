package com.app.syspoint.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Role {


    @Expose
    private Integer empleadosId;
    @SerializedName("modulo")
    @Expose
    private String modulo;
    @SerializedName("activo")
    @Expose
    private Integer activo;
    @SerializedName("empleado")
    @Expose
    private String empleado;
    @SerializedName("created_at")
    @Expose
    private Object createdAt;
    @SerializedName("updated_at")
    @Expose
    private Object updatedAt;


    public Integer getEmpleadosId() {
        return empleadosId;
    }

    public void setEmpleadosId(Integer empleadosId) {
        this.empleadosId = empleadosId;
    }

    public String getModulo() {
        return modulo;
    }

    public void setModulo(String modulo) {
        this.modulo = modulo;
    }

    public Integer getActivo() {
        return activo;
    }

    public void setActivo(Integer activo) {
        this.activo = activo;
    }

    public String getEmpleado() {
        return empleado;
    }

    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    public Object getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Object createdAt) {
        this.createdAt = createdAt;
    }

    public Object getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Object updatedAt) {
        this.updatedAt = updatedAt;
    }




}
