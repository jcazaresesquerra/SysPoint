package com.app.syspoint.db.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity(nameInDb = "log_sync")
public class LogSyncGetBean extends Bean{

    @Id
    private Long id;
    private String recurso;
    private Integer items;
    @Generated(hash = 532111330)
    public LogSyncGetBean(Long id, String recurso, Integer items) {
        this.id = id;
        this.recurso = recurso;
        this.items = items;
    }
    @Generated(hash = 1645211874)
    public LogSyncGetBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getRecurso() {
        return this.recurso;
    }
    public void setRecurso(String recurso) {
        this.recurso = recurso;
    }
    public Integer getItems() {
        return this.items;
    }
    public void setItems(Integer items) {
        this.items = items;
    }
}
