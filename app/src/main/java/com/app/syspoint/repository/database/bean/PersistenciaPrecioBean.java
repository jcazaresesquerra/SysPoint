package com.app.syspoint.repository.database.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity(nameInDb = "persistencia")
public class PersistenciaPrecioBean extends Bean {
    @Id(autoincrement = true)
    private Long id;
    private String mostrar;
    private Long valor;
    @Generated(hash = 1988073472)
    public PersistenciaPrecioBean(Long id, String mostrar, Long valor) {
        this.id = id;
        this.mostrar = mostrar;
        this.valor = valor;
    }
    @Generated(hash = 348930828)
    public PersistenciaPrecioBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMostrar() {
        return this.mostrar;
    }
    public void setMostrar(String mostrar) {
        this.mostrar = mostrar;
    }
    public Long getValor() {
        return this.valor;
    }
    public void setValor(Long valor) {
        this.valor = valor;
    }

}
