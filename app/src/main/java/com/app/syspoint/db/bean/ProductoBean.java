package com.app.syspoint.db.bean;

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
    private String unidad_medida;
    private String status;
    private String clave_sat;
    private String unidad_sat;
    private double precio;
    private double costo;
    private int iva;
    private int ieps;
    private int prioridad;
    private String region;
    private String codigo_alfa;
    private String codigo_barras;
    private String path_img;
    private int existencia = 0;
    @Generated(hash = 687083199)
    public ProductoBean(Long id, String articulo, String descripcion,
            String unidad_medida, String status, String clave_sat,
            String unidad_sat, double precio, double costo, int iva, int ieps,
            int prioridad, String region, String codigo_alfa, String codigo_barras,
            String path_img, int existencia) {
        this.id = id;
        this.articulo = articulo;
        this.descripcion = descripcion;
        this.unidad_medida = unidad_medida;
        this.status = status;
        this.clave_sat = clave_sat;
        this.unidad_sat = unidad_sat;
        this.precio = precio;
        this.costo = costo;
        this.iva = iva;
        this.ieps = ieps;
        this.prioridad = prioridad;
        this.region = region;
        this.codigo_alfa = codigo_alfa;
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
    public String getUnidad_medida() {
        return this.unidad_medida;
    }
    public void setUnidad_medida(String unidad_medida) {
        this.unidad_medida = unidad_medida;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getClave_sat() {
        return this.clave_sat;
    }
    public void setClave_sat(String clave_sat) {
        this.clave_sat = clave_sat;
    }
    public String getUnidad_sat() {
        return this.unidad_sat;
    }
    public void setUnidad_sat(String unidad_sat) {
        this.unidad_sat = unidad_sat;
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
    public int getIva() {
        return this.iva;
    }
    public void setIva(int iva) {
        this.iva = iva;
    }
    public int getIeps() {
        return this.ieps;
    }
    public void setIeps(int ieps) {
        this.ieps = ieps;
    }
    public int getPrioridad() {
        return this.prioridad;
    }
    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }
    public String getRegion() {
        return this.region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getCodigo_alfa() {
        return this.codigo_alfa;
    }
    public void setCodigo_alfa(String codigo_alfa) {
        this.codigo_alfa = codigo_alfa;
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
