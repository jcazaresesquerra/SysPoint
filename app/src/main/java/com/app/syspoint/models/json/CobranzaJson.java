package com.app.syspoint.models.json;

import com.app.syspoint.models.Payment;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CobranzaJson {

    @SerializedName("Cobranza")
    @Expose
    private List<Payment> cobranzas = null;

    public List<Payment> getCobranzas() {
        return cobranzas;
    }

    public void setCobranzas(List<Payment> cobranzas) {
        this.cobranzas = cobranzas;
    }
}
