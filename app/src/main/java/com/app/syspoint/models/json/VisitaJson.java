package com.app.syspoint.models.json;

import com.app.syspoint.models.Visit;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VisitaJson {

    @SerializedName("Visitas")
    @Expose
    private List<Visit> visitas = null;

    public List<Visit> getVisitas() {
        return visitas;
    }

    public void setVisitas(List<Visit> visitas) {
        this.visitas = visitas;
    }
}


