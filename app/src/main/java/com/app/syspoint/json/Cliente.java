package com.app.syspoint.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Cliente implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("visitas_no_efectivas")
    @Expose
    private Integer visitas;

    @SerializedName("nombre_comercial")
    @Expose
    private String nombreComercial;

    @SerializedName("calle")
    @Expose
    private String calle;
    @SerializedName("numero")
    @Expose
    private String numero;
    @SerializedName("colonia")
    @Expose
    private String colonia;
    @SerializedName("ciudad")
    @Expose
    private String ciudad;
    @SerializedName("codigo_postal")
    @Expose
    private Integer codigoPostal;
    @SerializedName("fecha_registro")
    @Expose
    private String fechaRegistro;
    @SerializedName("fecha_baja")
    @Expose
    private String fechaBaja;
    @SerializedName("cuenta")
    @Expose
    private String cuenta;
    @SerializedName("grupo")
    @Expose
    private String grupo;
    @SerializedName("categoria")
    @Expose
    private String categoria;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("consec")
    @Expose
    private Integer consec;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("sector")
    @Expose
    private String sector;
    @SerializedName("rango")
    @Expose
    private String rango;
    @SerializedName("ruta")
    @Expose
    private String ruta;
    @SerializedName("secuencia")
    @Expose
    private Integer secuencia;
    @SerializedName("periodo")
    @Expose
    private Integer periodo;
    @SerializedName("lun")
    @Expose
    private Integer lun;
    @SerializedName("mar")
    @Expose
    private Integer mar;
    @SerializedName("mie")
    @Expose
    private Integer mie;
    @SerializedName("jue")
    @Expose
    private Integer jue;
    @SerializedName("vie")
    @Expose
    private Integer vie;
    @SerializedName("sab")
    @Expose
    private Integer sab;
    @SerializedName("dom")
    @Expose
    private Integer dom;
    @SerializedName("created_at")
    @Expose
    private Object createdAt;
    @SerializedName("updated_at")
    @Expose
    private Object updatedAt;


    @SerializedName("latitud")
    @Expose
    private String latitud;

    @SerializedName("longitud")
    @Expose
    private String longitud;

    @SerializedName("phone_contacto")
    @Expose
    private String phone_contacto;


    @SerializedName("comentarios")
    @Expose
    private String recordatorio;


    @SerializedName("saldo_credito")
    @Expose
    private double saldo_credito;

    @SerializedName("is_credito")
    @Expose
    private Integer isCredito;

    @SerializedName("limite_credito")
    @Expose
    private double limite_credito;

    @SerializedName("matriz")
    @Expose
    private String matriz;

    public Integer getVisitas() {
        return visitas;
    }

    public void setVisitas(Integer visitas) {
        this.visitas = visitas;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getColonia() {
        return colonia;
    }

    public void setColonia(String colonia) {
        this.colonia = colonia;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public Integer getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(Integer codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(String fechaBaja) {
        this.fechaBaja = fechaBaja;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getConsec() {
        return consec;
    }

    public void setConsec(Integer consec) {
        this.consec = consec;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getRango() {
        return rango;
    }

    public void setRango(String rango) {
        this.rango = rango;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public Integer getSecuencia() {
        return secuencia;
    }

    public void setSecuencia(Integer secuencia) {
        this.secuencia = secuencia;
    }

    public Integer getPeriodo() {
        return periodo;
    }

    public void setPeriodo(Integer periodo) {
        this.periodo = periodo;
    }

    public Integer getLun() {
        return lun;
    }

    public void setLun(Integer lun) {
        this.lun = lun;
    }

    public Integer getMar() {
        return mar;
    }

    public void setMar(Integer mar) {
        this.mar = mar;
    }

    public Integer getMie() {
        return mie;
    }

    public void setMie(Integer mie) {
        this.mie = mie;
    }

    public Integer getJue() {
        return jue;
    }

    public void setJue(Integer jue) {
        this.jue = jue;
    }

    public Integer getVie() {
        return vie;
    }

    public void setVie(Integer vie) {
        this.vie = vie;
    }

    public Integer getSab() {
        return sab;
    }

    public void setSab(Integer sab) {
        this.sab = sab;
    }

    public Integer getDom() {
        return dom;
    }

    public void setDom(Integer dom) {
        this.dom = dom;
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


    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getPhone_contacto() {
        return phone_contacto;
    }

    public void setPhone_contacto(String phone_contacto) {
        this.phone_contacto = phone_contacto;
    }

    public String getRecordatorio() {
        return recordatorio;
    }

    public void setRecordatorio(String recordatorio) {
        this.recordatorio = recordatorio;
    }


    public double getSaldo_credito() {
        return saldo_credito;
    }

    public void setSaldo_credito(double saldo_credito) {
        this.saldo_credito = saldo_credito;
    }


    public Integer getIsCredito() {
        return isCredito;
    }

    public void setIsCredito(Integer isCredito) {
        this.isCredito = isCredito;
    }

    public double getLimite_credito() {
        return limite_credito;
    }

    public void setLimite_credito(double limite_credito) {
        this.limite_credito = limite_credito;
    }

    public String getMatriz() {
        return matriz;
    }

    public void setMatriz(String matriz) {
        this.matriz = matriz;
    }

    public Cliente(Integer id, Integer visitas, String nombreComercial, String calle, String numero, String colonia, String ciudad, Integer codigoPostal, String fechaRegistro, String fechaBaja, String cuenta, String grupo, String categoria, Integer status, Integer consec, String region, String sector, String rango, String ruta, Integer secuencia, Integer periodo, Integer lun, Integer mar, Integer mie, Integer jue, Integer vie, Integer sab, Integer dom, Object createdAt, Object updatedAt, String latitud, String longitud, String phone_contacto, String recordatorio, double saldo_credito, Integer isCredito, double limite_credito, String matriz) {
        this.id = id;
        this.visitas = visitas;
        this.nombreComercial = nombreComercial;
        this.calle = calle;
        this.numero = numero;
        this.colonia = colonia;
        this.ciudad = ciudad;
        this.codigoPostal = codigoPostal;
        this.fechaRegistro = fechaRegistro;
        this.fechaBaja = fechaBaja;
        this.cuenta = cuenta;
        this.grupo = grupo;
        this.categoria = categoria;
        this.status = status;
        this.consec = consec;
        this.region = region;
        this.sector = sector;
        this.rango = rango;
        this.ruta = ruta;
        this.secuencia = secuencia;
        this.periodo = periodo;
        this.lun = lun;
        this.mar = mar;
        this.mie = mie;
        this.jue = jue;
        this.vie = vie;
        this.sab = sab;
        this.dom = dom;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.latitud = latitud;
        this.longitud = longitud;
        this.phone_contacto = phone_contacto;
        this.recordatorio = recordatorio;
        this.saldo_credito = saldo_credito;
        this.isCredito = isCredito;
        this.limite_credito = limite_credito;
        this.matriz = matriz;
    }

    public Cliente() {
    }
}
