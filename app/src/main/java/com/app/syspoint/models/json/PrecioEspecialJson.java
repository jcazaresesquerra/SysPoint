package com.app.syspoint.models.json;

import com.app.syspoint.models.Price;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PrecioEspecialJson {

    @SerializedName("Precios")
    @Expose
    private List<Price> precios = null;

    public List<Price> getPrecios() {
        return precios;
    }

    public void setPrecios(List<Price> precios) {
        this.precios = precios;
    }
}
