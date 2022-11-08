package com.app.syspoint.repository.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(nameInDb = "empleados",  indexes = {
        @Index(value = "identificador")
})
public class EmpleadoBean extends Bean{

    @Id(autoincrement = true)
    private Long id;
    public String nombre;
    public String direccion;
    public String email;
    public String telefono;
    public String fecha_nacimiento;
    public String fecha_ingreso;
    public String fecha_egreso;
    public String contrasenia;
    public String identificador;
    public boolean status;
    public int edit_ruta;
    public String nss;
    public String rfc;
    public String curp;
    public String puesto;
    public String area_depto;
    public String tipo_contrato;
    public String region;
    public String hora_entrada;
    public String hora_salida;
    public String salida_comer;
    public String entrada_comer;
    public double sueldo_diario;
    public String turno;
    public String path_image;
    public int day;
    public String rute;

@Generated(hash = 1417760060)
public EmpleadoBean(Long id, String nombre, String direccion, String email,
        String telefono, String fecha_nacimiento, String fecha_ingreso,
        String fecha_egreso, String contrasenia, String identificador, boolean status,
        int edit_ruta, String nss, String rfc, String curp, String puesto,
        String area_depto, String tipo_contrato, String region, String hora_entrada,
        String hora_salida, String salida_comer, String entrada_comer,
        double sueldo_diario, String turno, String path_image, int day, String rute) {
    this.id = id;
    this.nombre = nombre;
    this.direccion = direccion;
    this.email = email;
    this.telefono = telefono;
    this.fecha_nacimiento = fecha_nacimiento;
    this.fecha_ingreso = fecha_ingreso;
    this.fecha_egreso = fecha_egreso;
    this.contrasenia = contrasenia;
    this.identificador = identificador;
    this.status = status;
    this.edit_ruta = edit_ruta;
    this.nss = nss;
    this.rfc = rfc;
    this.curp = curp;
    this.puesto = puesto;
    this.area_depto = area_depto;
    this.tipo_contrato = tipo_contrato;
    this.region = region;
    this.hora_entrada = hora_entrada;
    this.hora_salida = hora_salida;
    this.salida_comer = salida_comer;
    this.entrada_comer = entrada_comer;
    this.sueldo_diario = sueldo_diario;
    this.turno = turno;
    this.path_image = path_image;
    this.day = day;
    this.rute = rute;
}
@Generated(hash = 1983182120)
public EmpleadoBean() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getNombre() {
    return this.nombre;
}
public void setNombre(String nombre) {
    this.nombre = nombre;
}
public String getDireccion() {
    return this.direccion;
}
public void setDireccion(String direccion) {
    this.direccion = direccion;
}
public String getEmail() {
    return this.email;
}
public void setEmail(String email) {
    this.email = email;
}
public String getTelefono() {
    return this.telefono;
}
public void setTelefono(String telefono) {
    this.telefono = telefono;
}
public String getFecha_nacimiento() {
    return this.fecha_nacimiento;
}
public void setFecha_nacimiento(String fecha_nacimiento) {
    this.fecha_nacimiento = fecha_nacimiento;
}
public String getFecha_ingreso() {
    return this.fecha_ingreso;
}
public void setFecha_ingreso(String fecha_ingreso) {
    this.fecha_ingreso = fecha_ingreso;
}
public String getFecha_egreso() {
    return this.fecha_egreso;
}
public void setFecha_egreso(String fecha_egreso) {
    this.fecha_egreso = fecha_egreso;
}
public String getContrasenia() {
    return this.contrasenia;
}
public void setContrasenia(String contrasenia) {
    this.contrasenia = contrasenia;
}
public String getIdentificador() {
    return this.identificador;
}
public void setIdentificador(String identificador) {
    this.identificador = identificador;
}
public boolean getStatus() {
    return this.status;
}
public void setStatus(boolean status) {
    this.status = status;
}
public String getNss() {
    return this.nss;
}
public void setNss(String nss) {
    this.nss = nss;
}
public String getRfc() {
    return this.rfc;
}
public void setRfc(String rfc) {
    this.rfc = rfc;
}
public String getCurp() {
    return this.curp;
}
public void setCurp(String curp) {
    this.curp = curp;
}
public String getPuesto() {
    return this.puesto;
}
public void setPuesto(String puesto) {
    this.puesto = puesto;
}
public String getArea_depto() {
    return this.area_depto;
}
public void setArea_depto(String area_depto) {
    this.area_depto = area_depto;
}
public String getTipo_contrato() {
    return this.tipo_contrato;
}
public void setTipo_contrato(String tipo_contrato) {
    this.tipo_contrato = tipo_contrato;
}
public String getRegion() {
    return this.region;
}
public void setRegion(String region) {
    this.region = region;
}
public String getHora_entrada() {
    return this.hora_entrada;
}
public void setHora_entrada(String hora_entrada) {
    this.hora_entrada = hora_entrada;
}
public String getHora_salida() {
    return this.hora_salida;
}
public void setHora_salida(String hora_salida) {
    this.hora_salida = hora_salida;
}
public String getSalida_comer() {
    return this.salida_comer;
}
public void setSalida_comer(String salida_comer) {
    this.salida_comer = salida_comer;
}
public String getEntrada_comer() {
    return this.entrada_comer;
}
public void setEntrada_comer(String entrada_comer) {
    this.entrada_comer = entrada_comer;
}
public double getSueldo_diario() {
    return this.sueldo_diario;
}
public void setSueldo_diario(double sueldo_diario) {
    this.sueldo_diario = sueldo_diario;
}
public String getTurno() {
    return this.turno;
}
public void setTurno(String turno) {
    this.turno = turno;
}
public String getPath_image() {
    return this.path_image;
}
public void setPath_image(String path_image) {
    this.path_image = path_image;
}
public int getEdit_ruta() {
    return this.edit_ruta;
}
public void setEdit_ruta(int edit_ruta) {
    this.edit_ruta = edit_ruta;
}

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getRute() {
        return rute;
    }

    public void setRute(String rute) {
        this.rute = rute;
    }
}
