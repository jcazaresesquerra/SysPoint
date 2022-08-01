package com.app.syspoint.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity(nameInDb = "ruteo")
public class RuteoBean extends Bean{
    
    @Id(autoincrement = false)
    private Long id;
    private String region;
    private String ruta;
    private int dia;
    private String fecha;

    @Generated(hash = 1897451057)
    public RuteoBean(Long id, String region, String ruta, int dia, String fecha) {
        this.id = id;
        this.region = region;
        this.ruta = ruta;
        this.dia = dia;
        this.fecha = fecha;
    }
    @Generated(hash = 1594382308)
    public RuteoBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRegion() {
        return this.region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getRuta() {
        return this.ruta;
    }
    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
    public int getDia() {
        return this.dia;
    }
    public void setDia(int dia) {
        this.dia = dia;
    }
    public String getFecha() {
        return this.fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

}
