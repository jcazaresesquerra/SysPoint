package com.app.syspoint.db.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity(nameInDb = "especiales")
public class PreciosEspecialesBean extends Bean {

    @Id(autoincrement = true)
    private Long id;

    private String cliente;
    private String articulo;
    private double precio;
    private boolean active;
    private String fecha_sync;
    @Generated(hash = 1255686053)
    public PreciosEspecialesBean(Long id, String cliente, String articulo,
            double precio, boolean active, String fecha_sync) {
        this.id = id;
        this.cliente = cliente;
        this.articulo = articulo;
        this.precio = precio;
        this.active = active;
        this.fecha_sync = fecha_sync;
    }
    @Generated(hash = 1135034048)
    public PreciosEspecialesBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCliente() {
        return this.cliente;
    }
    public void setCliente(String cliente) {
        this.cliente = cliente;
    }
    public String getArticulo() {
        return this.articulo;
    }
    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }
    public double getPrecio() {
        return this.precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public boolean getActive() {
        return this.active;
    }
    public void setActive(boolean active) {
        this.active = active;
    }
    public String getFecha_sync() {
        return this.fecha_sync;
    }
    public void setFecha_sync(String fecha_sync) {
        this.fecha_sync = fecha_sync;
    }


   
}
