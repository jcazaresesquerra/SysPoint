package com.app.syspoint.models.json;

import com.app.syspoint.models.Product;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductoJson {

    @SerializedName("Productos")
    @Expose
    private List<Product> productos = null;

    public List<Product> getProductos() {
        return productos;
    }

    public void setProductos(List<Product> productos) {
        this.productos = productos;
    }
}
