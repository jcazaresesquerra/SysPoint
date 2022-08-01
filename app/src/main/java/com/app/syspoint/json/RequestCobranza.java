package com.app.syspoint.json;

public class RequestCobranza {

    private String cuenta;

    public RequestCobranza() {
    }

    public RequestCobranza(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }
}
