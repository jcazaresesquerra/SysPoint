package com.app.syspoint.models.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RequestCobranza {

    @SerializedName("cuenta")
    @Expose
    private String cuenta;

    @SerializedName("clientId")
    @Expose
    private String clientId;


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

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
