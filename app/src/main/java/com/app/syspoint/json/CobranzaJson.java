package com.app.syspoint.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CobranzaJson {

    @SerializedName("Cobranza")
    @Expose
    private List<Cobranza> cobranzas = null;

    public List<Cobranza> getCobranzas() {
        return cobranzas;
    }

    public void setCobranzas(List<Cobranza> cobranzas) {
        this.cobranzas = cobranzas;
    }
}
