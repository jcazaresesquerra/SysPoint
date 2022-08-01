package com.app.syspoint.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity(nameInDb = "impresoras")
public class PrinterBean extends Bean {

    @Id(autoincrement = true)
    private Long id;
    private String name;
    private String address;
    private Long idPrinter;
    @Generated(hash = 1927572325)
    public PrinterBean(Long id, String name, String address, Long idPrinter) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.idPrinter = idPrinter;
    }
    @Generated(hash = 1585630953)
    public PrinterBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return this.address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Long getIdPrinter() {
        return this.idPrinter;
    }
    public void setIdPrinter(Long idPrinter) {
        this.idPrinter = idPrinter;
    }
    
}
