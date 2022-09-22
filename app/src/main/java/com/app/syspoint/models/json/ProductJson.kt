package com.app.syspoint.models.json

import com.app.syspoint.models.Product
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ProductJson(
    @SerializedName("Productos")
    @Expose
    var products: List<Product?>? = null
)
