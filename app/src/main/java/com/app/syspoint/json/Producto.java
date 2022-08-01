package com.app.syspoint.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Producto implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("articulo")
    @Expose
    private String articulo;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("unidad_medida")
    @Expose
    private String unidadMedida;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("clave_sat")
    @Expose
    private String claveSat;
    @SerializedName("unidad_sat")
    @Expose
    private String unidadSat;
    @SerializedName("precio")
    @Expose
    private double precio;
    @SerializedName("costo")
    @Expose
    private double costo;
    @SerializedName("iva")
    @Expose
    private Integer iva;
    @SerializedName("ieps")
    @Expose
    private Integer ieps;
    @SerializedName("prioridad")
    @Expose
    private Integer prioridad;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("codigo_alfa")
    @Expose
    private String codigoAlfa;
    @SerializedName("codigo_barras")
    @Expose
    private String codigoBarras;
    @SerializedName("created_at")
    @Expose
    private Object createdAt;
    @SerializedName("updated_at")
    @Expose
    private Object updatedAt;

    @SerializedName("path_image")
    @Expose
    private String pathImage;

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getClaveSat() {
        return claveSat;
    }

    public void setClaveSat(String claveSat) {
        this.claveSat = claveSat;
    }

    public String getUnidadSat() {
        return unidadSat;
    }

    public void setUnidadSat(String unidadSat) {
        this.unidadSat = unidadSat;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }

    public Integer getIva() {
        return iva;
    }

    public void setIva(Integer iva) {
        this.iva = iva;
    }

    public Integer getIeps() {
        return ieps;
    }

    public void setIeps(Integer ieps) {
        this.ieps = ieps;
    }

    public Integer getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(Integer prioridad) {
        this.prioridad = prioridad;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCodigoAlfa() {
        return codigoAlfa;
    }

    public void setCodigoAlfa(String codigoAlfa) {
        this.codigoAlfa = codigoAlfa;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
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
