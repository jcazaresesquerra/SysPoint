package com.app.syspoint.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class ClienteJson {

    @SerializedName("Clientes")
    @Expose
    private List<Cliente> clientes = null;

    public List<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(List<Cliente> clientes) {
        this.clientes = clientes;
    }


    public static List<Cliente> fromArray(List<LinkedTreeMap<String, Object>> items){
        List<Cliente> result = new ArrayList<>();

        for(LinkedTreeMap<String, Object> content : items){
            result.add(new Cliente(

            ));
        }
        return result;
    }
}