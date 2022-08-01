package com.app.syspoint.db.bean;

import java.io.Serializable;

public class UserSesion implements Serializable {

    private String usuario;
    private String password;
    private boolean remember;


    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRemember() {
        return remember;
    }

    public void setRemember(boolean remember) {
        this.remember = remember;
    }
}
