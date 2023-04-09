package com.app.syspoint.repository.database.bean;

import android.graphics.Bitmap;

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
    public String contrasenia;
    public String identificador;
    public boolean status;
    public String path_image;
    public String rute;
    public String updatedAt;

@Generated(hash = 138054522)
public EmpleadoBean(Long id, String nombre, String direccion, String email, String telefono,
        String fecha_nacimiento, String fecha_ingreso, String contrasenia, String identificador,
        boolean status, String path_image, String rute, String updatedAt) {
    this.id = id;
    this.nombre = nombre;
    this.direccion = direccion;
    this.email = email;
    this.telefono = telefono;
    this.fecha_nacimiento = fecha_nacimiento;
    this.fecha_ingreso = fecha_ingreso;
    this.contrasenia = contrasenia;
    this.identificador = identificador;
    this.status = status;
    this.path_image = path_image;
    this.rute = rute;
    this.updatedAt = updatedAt;
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
public String getPath_image() {
    return this.path_image;
}
public void setPath_image(String path_image) {
    this.path_image = path_image;
}


    public String getRute() {
        return rute;
    }

    public void setRute(String rute) {
        this.rute = rute;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
