package com.app.syspoint.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.app.syspoint.json.Cliente;
import com.app.syspoint.json.Cobranza;
import com.app.syspoint.json.Empleado;
import com.app.syspoint.json.Precio;
import com.app.syspoint.json.Producto;
import com.app.syspoint.json.Role;

import java.util.List;

public class Data_ {


    @SerializedName("Empleados")
    @Expose
    private List<Empleado> empleados = null;

    @SerializedName("Roles")
    @Expose
    private List<Role> roles = null;

    @SerializedName("Productos")
    @Expose
    private List<Producto> productos = null;

    @SerializedName("Clientes")
    @Expose
    private List<Cliente> clientes = null;

    @SerializedName("Precios")
    @Expose
    private List<Precio> precios = null;

    @SerializedName("Cobranza")
    @Expose
    private List<Cobranza> cobranzas = null;

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }

    public List<Cobranza> getCobranzas() {
        return cobranzas;
    }

    public void setCobranzas(List<Cobranza> cobranzas) {
        this.cobranzas = cobranzas;
    }

    public List<Empleado> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<Empleado> empleados) {
        this.empleados = empleados;
    }

    public List<Precio> getPrecios() {
        return precios;
    }

    public void setPrecios(List<Precio> precios) {
        this.precios = precios;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
