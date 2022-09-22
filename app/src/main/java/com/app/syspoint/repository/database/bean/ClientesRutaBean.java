package com.app.syspoint.repository.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;


@Entity(nameInDb = "clientes_ruta", indexes = {
        @Index(value = "cuenta")
})
public class ClientesRutaBean extends Bean {

    @Id()
    private Long id;
    private String nombre_comercial;
    private String calle;
    private String numero;
    private String colonia;
    private String cuenta;
    private String rango;
    private int lun;
    private int mar;
    private int mie;
    private int jue;
    private int vie;
    private int sab;
    private int dom;
    private int visitado;
    private String latitud;
    private String longitud;
    private String phone_contact;
@Generated(hash = 777198376)
public ClientesRutaBean(Long id, String nombre_comercial, String calle,
        String numero, String colonia, String cuenta, String rango, int lun,
        int mar, int mie, int jue, int vie, int sab, int dom, int visitado,
        String latitud, String longitud, String phone_contact) {
    this.id = id;
    this.nombre_comercial = nombre_comercial;
    this.calle = calle;
    this.numero = numero;
    this.colonia = colonia;
    this.cuenta = cuenta;
    this.rango = rango;
    this.lun = lun;
    this.mar = mar;
    this.mie = mie;
    this.jue = jue;
    this.vie = vie;
    this.sab = sab;
    this.dom = dom;
    this.visitado = visitado;
    this.latitud = latitud;
    this.longitud = longitud;
    this.phone_contact = phone_contact;
}
@Generated(hash = 2130296790)
public ClientesRutaBean() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getNombre_comercial() {
    return this.nombre_comercial;
}
public void setNombre_comercial(String nombre_comercial) {
    this.nombre_comercial = nombre_comercial;
}
public String getCalle() {
    return this.calle;
}
public void setCalle(String calle) {
    this.calle = calle;
}
public String getNumero() {
    return this.numero;
}
public void setNumero(String numero) {
    this.numero = numero;
}
public String getColonia() {
    return this.colonia;
}
public void setColonia(String colonia) {
    this.colonia = colonia;
}
public String getCuenta() {
    return this.cuenta;
}
public void setCuenta(String cuenta) {
    this.cuenta = cuenta;
}
public String getRango() {
    return this.rango;
}
public void setRango(String rango) {
    this.rango = rango;
}
public int getLun() {
    return this.lun;
}
public void setLun(int lun) {
    this.lun = lun;
}
public int getMar() {
    return this.mar;
}
public void setMar(int mar) {
    this.mar = mar;
}
public int getMie() {
    return this.mie;
}
public void setMie(int mie) {
    this.mie = mie;
}
public int getJue() {
    return this.jue;
}
public void setJue(int jue) {
    this.jue = jue;
}
public int getVie() {
    return this.vie;
}
public void setVie(int vie) {
    this.vie = vie;
}
public int getSab() {
    return this.sab;
}
public void setSab(int sab) {
    this.sab = sab;
}
public int getDom() {
    return this.dom;
}
public void setDom(int dom) {
    this.dom = dom;
}
public int getVisitado() {
    return this.visitado;
}
public void setVisitado(int visitado) {
    this.visitado = visitado;
}
public String getLatitud() {
    return this.latitud;
}
public void setLatitud(String latitud) {
    this.latitud = latitud;
}
public String getLongitud() {
    return this.longitud;
}
public void setLongitud(String longitud) {
    this.longitud = longitud;
}
public String getPhone_contact() {
    return this.phone_contact;
}
public void setPhone_contact(String phone_contact) {
    this.phone_contact = phone_contact;
}

 
}
