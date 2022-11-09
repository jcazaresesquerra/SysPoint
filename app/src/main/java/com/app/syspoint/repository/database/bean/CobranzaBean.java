package com.app.syspoint.repository.database.bean;


import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;

@Entity(nameInDb = "cobranza",
        indexes = {
                @Index(value = "cobranza")
        })
public class CobranzaBean extends Bean {

    @Id(autoincrement = true)
    private Long id;
    private String cobranza;
    private String cliente;
    private double importe;
    private double saldo;
    private Long venta;
    private String estado;
    private String observaciones;
    private String fecha;
    private String hora;
    private String empleado;
    private boolean isCheck;
    private boolean abono;
@Generated(hash = 2034958666)
public CobranzaBean(Long id, String cobranza, String cliente, double importe,
        double saldo, Long venta, String estado, String observaciones,
        String fecha, String hora, String empleado, boolean isCheck,
        boolean abono) {
    this.id = id;
    this.cobranza = cobranza;
    this.cliente = cliente;
    this.importe = importe;
    this.saldo = saldo;
    this.venta = venta;
    this.estado = estado;
    this.observaciones = observaciones;
    this.fecha = fecha;
    this.hora = hora;
    this.empleado = empleado;
    this.isCheck = isCheck;
    this.abono = abono;
}
@Generated(hash = 258214784)
public CobranzaBean() {
}
public Long getId() {
    return this.id;
}
public void setId(Long id) {
    this.id = id;
}
public String getCobranza() {
    return this.cobranza;
}
public void setCobranza(String cobranza) {
    this.cobranza = cobranza;
}
public String getCliente() {
    return this.cliente;
}
public void setCliente(String cliente) {
    this.cliente = cliente;
}
public double getImporte() {
    return this.importe;
}
public void setImporte(double importe) {
    this.importe = importe;
}
public double getSaldo() {
    return this.saldo;
}
public void setSaldo(double saldo) {
    this.saldo = saldo;
}
public Long getVenta() {
    return this.venta;
}
public void setVenta(Long venta) {
    this.venta = venta;
}
public String getEstado() {
    return this.estado;
}
public void setEstado(String estado) {
    this.estado = estado;
}
public String getObservaciones() {
    return this.observaciones;
}
public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
}
public String getFecha() {
    return this.fecha;
}
public void setFecha(String fecha) {
    this.fecha = fecha;
}
public String getHora() {
    return this.hora;
}
public void setHora(String hora) {
    this.hora = hora;
}
public String getEmpleado() {
    return this.empleado;
}
public void setEmpleado(String empleado) {
    this.empleado = empleado;
}
public boolean getIsCheck() {
    return this.isCheck;
}
public void setIsCheck(boolean isCheck) {
    this.isCheck = isCheck;
}
public boolean getAbono() {
    return this.abono;
}
public void setAbono(boolean abono) {
    this.abono = abono;
}

  
}
