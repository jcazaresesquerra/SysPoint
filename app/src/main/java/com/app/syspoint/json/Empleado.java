package com.app.syspoint.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Empleado implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("direccion")
    @Expose
    private String direccion;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("telefono")
    @Expose
    private String telefono;
    @SerializedName("fecha_nacimiento")
    @Expose
    private String fechaNacimiento;
    @SerializedName("fecha_ingreso")
    @Expose
    private String fechaIngreso;
    @SerializedName("fecha_egreso")
    @Expose
    private String fechaEgreso;
    @SerializedName("contrasenia")
    @Expose
    private String contrasenia;
    @SerializedName("identificador")
    @Expose
    private String identificador;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("nss")
    @Expose
    private String nss;
    @SerializedName("rfc")
    @Expose
    private String rfc;
    @SerializedName("curp")
    @Expose
    private String curp;
    @SerializedName("puesto")
    @Expose
    private String puesto;
    @SerializedName("area_depto")
    @Expose
    private String areaDepto;
    @SerializedName("tipo_contrato")
    @Expose
    private String tipoContrato;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("hora_entrada")
    @Expose
    private String horaEntrada;
    @SerializedName("hora_salida")
    @Expose
    private String horaSalida;
    @SerializedName("salida_comer")
    @Expose
    private String salidaComer;
    @SerializedName("entrada_comer")
    @Expose
    private String entradaComer;
    @SerializedName("sueldo_diario")
    @Expose
    private Integer sueldoDiario;
    @SerializedName("turno")
    @Expose
    private String turno;
    @SerializedName("path_image")
    @Expose
    private String pathImage;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(String fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getFechaEgreso() {
        return fechaEgreso;
    }

    public void setFechaEgreso(String fechaEgreso) {
        this.fechaEgreso = fechaEgreso;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getNss() {
        return nss;
    }

    public void setNss(String nss) {
        this.nss = nss;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public String getCurp() {
        return curp;
    }

    public void setCurp(String curp) {
        this.curp = curp;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getAreaDepto() {
        return areaDepto;
    }

    public void setAreaDepto(String areaDepto) {
        this.areaDepto = areaDepto;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getHoraEntrada() {
        return horaEntrada;
    }

    public void setHoraEntrada(String horaEntrada) {
        this.horaEntrada = horaEntrada;
    }

    public String getHoraSalida() {
        return horaSalida;
    }

    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    public String getSalidaComer() {
        return salidaComer;
    }

    public void setSalidaComer(String salidaComer) {
        this.salidaComer = salidaComer;
    }

    public String getEntradaComer() {
        return entradaComer;
    }

    public void setEntradaComer(String entradaComer) {
        this.entradaComer = entradaComer;
    }

    public Integer getSueldoDiario() {
        return sueldoDiario;
    }

    public void setSueldoDiario(Integer sueldoDiario) {
        this.sueldoDiario = sueldoDiario;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
