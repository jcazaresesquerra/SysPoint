package com.app.syspoint.http;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.app.syspoint.models.Client;
import com.app.syspoint.models.Payment;
import com.app.syspoint.models.Employee;
import com.app.syspoint.models.Price;
import com.app.syspoint.models.Product;
import com.app.syspoint.models.Role;

import java.util.List;

public class Data_ {


    @SerializedName("Empleados")
    @Expose
    private List<Employee> empleados = null;

    @SerializedName("Roles")
    @Expose
    private List<Role> roles = null;

    @SerializedName("Productos")
    @Expose
    private List<Product> productos = null;

    @SerializedName("Clientes")
    @Expose
    private List<Client> clientes = null;

    @SerializedName("Precios")
    @Expose
    private List<Price> precios = null;

    @SerializedName("Cobranza")
    @Expose
    private List<Payment> cobranzas = null;

    public List<Client> getClientes() {
        return clientes;
    }

    public void setClientes(List<Client> clientes) {
        this.clientes = clientes;
    }

    public List<Payment> getCobranzas() {
        return cobranzas;
    }

    public void setCobranzas(List<Payment> cobranzas) {
        this.cobranzas = cobranzas;
    }

    public List<Employee> getEmpleados() {
        return empleados;
    }

    public void setEmpleados(List<Employee> empleados) {
        this.empleados = empleados;
    }

    public List<Price> getPrecios() {
        return precios;
    }

    public void setPrecios(List<Price> precios) {
        this.precios = precios;
    }

    public List<Product> getProductos() {
        return productos;
    }

    public void setProductos(List<Product> productos) {
        this.productos = productos;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
