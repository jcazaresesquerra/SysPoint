package com.app.syspoint.domentos;

import android.app.Activity;

public abstract class Documento {


    protected String documento;
    protected Activity activity;

    public Documento(Activity activity) {
        this.activity = activity;
    }


   public abstract void template();


    public String getDocumento() {
        return documento;
    }
}
