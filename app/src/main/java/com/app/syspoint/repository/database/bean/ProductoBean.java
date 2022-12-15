package com.app.syspoint.repository.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(nameInDb = "productos", indexes = { @Index(value = "articulo")})
public class ProductoBean extends Bean{

    @Id(autoincrement = true)
    private Long id;
    private String articulo;
    private String descripcion;
    private String status;
    private double precio;
    private int iva;
    private String codigo_barras;
    private String path_img;
    private int existencia = 0;
    @Generated(hash = 696399829)
    public ProductoBean(Long id, String articulo, String descripcion, String status, double precio,
            int iva, String codigo_barras, String path_img, int existencia) {
        this.id = id;
        this.articulo = articulo;
        this.descripcion = descripcion;
        this.status = status;
        this.precio = precio;
        this.iva = iva;
        this.codigo_barras = codigo_barras;
        this.path_img = path_img;
        this.existencia = existencia;
    }
    @Generated(hash = 2064312976)
    public ProductoBean() {
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
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public double getPrecio() {
        return this.precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public int getIva() {
        return this.iva;
    }
    public void setIva(int iva) {
        this.iva = iva;
    }
    public String getCodigo_barras() {
        return this.codigo_barras;
    }
    public void setCodigo_barras(String codigo_barras) {
        this.codigo_barras = codigo_barras;
    }
    public String getPath_img() {
        return this.path_img;
    }
    public void setPath_img(String path_img) {
        this.path_img = path_img;
    }
    public int getExistencia() {
        return this.existencia;
    }
    public void setExistencia(int existencia) {
        this.existencia = existencia;
    }
}
