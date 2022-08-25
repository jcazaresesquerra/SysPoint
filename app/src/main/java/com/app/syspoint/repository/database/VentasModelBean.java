package com.app.syspoint.repository.database;

import com.app.syspoint.repository.database.bean.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;


@Entity(nameInDb = "ventas_model")
public class VentasModelBean extends Bean {

    @Id(autoincrement = true)
    private Long id;
    private String articulo;
    private String descripcion;
    private int cantidad;
    private double precio;
    private double costo;
    private double impuesto;
    private String observ;
    @Generated(hash = 195912614)
    public VentasModelBean(Long id, String articulo, String descripcion,
            int cantidad, double precio, double costo, double impuesto,
            String observ) {
        this.id = id;
        this.articulo = articulo;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.precio = precio;
        this.costo = costo;
        this.impuesto = impuesto;
        this.observ = observ;
    }
    @Generated(hash = 1690672154)
    public VentasModelBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getArticulo() {
        return this.articulo;
    }
    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }
    public String getDescripcion() {
        return this.descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public int getCantidad() {
        return this.cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public double getPrecio() {
        return this.precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public double getCosto() {
        return this.costo;
    }
    public void setCosto(double costo) {
        this.costo = costo;
    }
    public double getImpuesto() {
        return this.impuesto;
    }
    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }
    public String getObserv() {
        return this.observ;
    }
    public void setObserv(String observ) {
        this.observ = observ;
    }



}
