package com.app.syspoint.models.json;

import com.app.syspoint.models.Client;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.List;

public class ClienteJson {

    @SerializedName("Clientes")
    @Expose
    private List<Client> clientes = null;

    public List<Client> getClientes() {
        return clientes;
    }

    public void setClientes(List<Client> clientes) {
        this.clientes = clientes;
    }


    public static List<Client> fromArray(List<LinkedTreeMap<String, Object>> items){
        List<Client> result = new ArrayList<>();

        for(LinkedTreeMap<String, Object> content : items){
            result.add(new Client(

            ));
        }
        return result;
    }
}