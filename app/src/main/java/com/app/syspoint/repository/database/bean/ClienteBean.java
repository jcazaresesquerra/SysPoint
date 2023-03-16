package com.app.syspoint.repository.database.bean;

import androidx.annotation.Nullable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(nameInDb = "clientes", indexes = {
        @Index(value = "cuenta")
})
public class ClienteBean extends Bean {

    @Id(autoincrement = true)
    private Long id;
    private String nombre_comercial;
    private String calle;
    private String numero;
    private String colonia;
    private String ciudad;
    private int codigo_postal;
    private String fecha_registro;
    private String cuenta;
    private boolean status;
    private String consec;
    private String rango;
    private int lun;
    private int mar;
    private int mie;
    private int jue;
    private int vie;
    private int sab;
    private int dom;
    private int lunOrder;
    private int marOrder;
    private int mieOrder;
    private int jueOrder;
    private int vieOrder;
    private int sabOrder;
    private int domOrder;
    private int visitado;
    private String latitud;
    private String longitud;
    private String contacto_phone;
    @Nullable
    private String recordatorio;
    private boolean is_recordatorio;
    private int visitasNoefectivas = 0;
    private boolean is_credito;
    private double limite_credito = 0.00;
    private double saldo_credito = 0.00;
    private String matriz;
    private String date_sync;
    private String updatedAt;

@Generated(hash = 94399585)
public ClienteBean(Long id, String nombre_comercial, String calle, String numero, String colonia,
        String ciudad, int codigo_postal, String fecha_registro, String cuenta, boolean status, String consec,
        String rango, int lun, int mar, int mie, int jue, int vie, int sab, int dom, int lunOrder,
        int marOrder, int mieOrder, int jueOrder, int vieOrder, int sabOrder, int domOrder, int visitado,
        String latitud, String longitud, String contacto_phone, String recordatorio, boolean is_recordatorio,
        int visitasNoefectivas, boolean is_credito, double limite_credito, double saldo_credito,
        String matriz, String date_sync, String updatedAt) {
    this.id = id;
    this.nombre_comercial = nombre_comercial;
    this.calle = calle;
    this.numero = numero;
    this.colonia = colonia;
    this.ciudad = ciudad;
    this.codigo_postal = codigo_postal;
    this.fecha_registro = fecha_registro;
    this.cuenta = cuenta;
    this.status = status;
    this.consec = consec;
    this.rango = rango;
    this.lun = lun;
    this.mar = mar;
    this.mie = mie;
    this.jue = jue;
    this.vie = vie;
    this.sab = sab;
    this.dom = dom;
    this.lunOrder = lunOrder;
    this.marOrder = marOrder;
    this.mieOrder = mieOrder;
    this.jueOrder = jueOrder;
    this.vieOrder = vieOrder;
    this.sabOrder = sabOrder;
    this.domOrder = domOrder;
    this.visitado = visitado;
    this.latitud = latitud;
    this.longitud = longitud;
    this.contacto_phone = contacto_phone;
    this.recordatorio = recordatorio;
    this.is_recordatorio = is_recordatorio;
    this.visitasNoefectivas = visitasNoefectivas;
    this.is_credito = is_credito;
    this.limite_credito = limite_credito;
    this.saldo_credito = saldo_credito;
    this.matriz = matriz;
    this.date_sync = date_sync;
    this.updatedAt = updatedAt;
}
@Generated(hash = 1475481445)
public ClienteBean() {
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
public String getCiudad() {
    return this.ciudad;
}
public void setCiudad(String ciudad) {
    this.ciudad = ciudad;
}
public int getCodigo_postal() {
    return this.codigo_postal;
}
public void setCodigo_postal(int codigo_postal) {
    this.codigo_postal = codigo_postal;
}
public String getFecha_registro() {
    return this.fecha_registro;
}
public void setFecha_registro(String fecha_registro) {
    this.fecha_registro = fecha_registro;
}
public String getCuenta() {
    return this.cuenta;
}
public void setCuenta(String cuenta) {
    this.cuenta = cuenta;
}
public boolean getStatus() {
    return this.status;
}
public void setStatus(boolean status) {
    this.status = status;
}
public String getConsec() {
    return this.consec;
}
public void setConsec(String consec) {
    this.consec = consec;
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
    public int getLunOrder() {
        return this.lunOrder;
    }
    public void setLunOrder(int lunOrder) {
        this.lunOrder = lunOrder;
    }
    public int getMarOrder() {
        return this.marOrder;
    }
    public void setMarOrder(int marOrder) {
        this.marOrder = marOrder;
    }
    public int getMieOrder() {
        return this.mieOrder;
    }
    public void setMieOrder(int mieOrder) {
        this.mieOrder = mieOrder;
    }
    public int getJueOrder() {
        return this.jueOrder;
    }
    public void setJueOrder(int jueOrder) {
        this.jueOrder = jueOrder;
    }
    public int getVieOrder() {
        return this.vieOrder;
    }
    public void setVieOrder(int vieOrder) {
        this.vieOrder = vieOrder;
    }
    public int getSabOrder() {
        return this.sabOrder;
    }
    public void setSabOrder(int sabOrder) {
        this.sabOrder = sabOrder;
    }
    public int getDomOrder() {
        return this.domOrder;
    }
    public void setDomOrder(int domOrder) {
        this.domOrder = domOrder;
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
public String getContacto_phone() {
    return this.contacto_phone;
}
public void setContacto_phone(String contacto_phone) {
    this.contacto_phone = contacto_phone;
}
public String getRecordatorio() {
    return this.recordatorio;
}
public void setRecordatorio(String recordatorio) {
    this.recordatorio = recordatorio;
}
public boolean getIs_recordatorio() {
    return this.is_recordatorio;
}
public void setIs_recordatorio(boolean is_recordatorio) {
    this.is_recordatorio = is_recordatorio;
}
public int getVisitasNoefectivas() {
    return this.visitasNoefectivas;
}
public void setVisitasNoefectivas(int visitasNoefectivas) {
    this.visitasNoefectivas = visitasNoefectivas;
}
public boolean getIs_credito() {
    return this.is_credito;
}
public void setIs_credito(boolean is_credito) {
    this.is_credito = is_credito;
}
public double getLimite_credito() {
    return this.limite_credito;
}
public void setLimite_credito(double limite_credito) {
    this.limite_credito = limite_credito;
}
public double getSaldo_credito() {
    return this.saldo_credito;
}
public void setSaldo_credito(double saldo_credito) {
    this.saldo_credito = saldo_credito;
}
public String getMatriz() {
    return this.matriz;
}
public void setMatriz(String matriz) {
    this.matriz = matriz;
}
public String getDate_sync() {
    return this.date_sync;
}
public void setDate_sync(String date_sync) {
    this.date_sync = date_sync;
}

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
