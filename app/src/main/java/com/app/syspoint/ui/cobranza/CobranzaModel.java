package com.app.syspoint.ui.cobranza;


import com.app.syspoint.repository.database.bean.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity(nameInDb = "cobranza_model")
public class CobranzaModel extends Bean {
    @Id(autoincrement = true)
    private Long id;
    private int venta;
    private String cobranza;
    private double importe;
    private double saldo;
    private double acuenta;
    private String no_referen;
    @Generated(hash = 111939273)
    public CobranzaModel(Long id, int venta, String cobranza, double importe,
            double saldo, double acuenta, String no_referen) {
        this.id = id;
        this.venta = venta;
        this.cobranza = cobranza;
        this.importe = importe;
        this.saldo = saldo;
        this.acuenta = acuenta;
        this.no_referen = no_referen;
    }
    @Generated(hash = 1159363648)
    public CobranzaModel() {
    }
    public int getVenta() {
        return this.venta;
    }
    public void setVenta(int venta) {
        this.venta = venta;
    }
    public String getCobranza() {
        return this.cobranza;
    }
    public void setCobranza(String cobranza) {
        this.cobranza = cobranza;
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
    public double getAcuenta() {
        return this.acuenta;
    }
    public void setAcuenta(double acuenta) {
        this.acuenta = acuenta;
    }
    public String getNo_referen() {
        return this.no_referen;
    }
    public void setNo_referen(String no_referen) {
        this.no_referen = no_referen;
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }


}
