package com.app.syspoint.models.json

import com.app.syspoint.models.Price
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class SpecialPriceJson(
    @SerializedName("Precios")
    @Expose
    var prices: List<Price?>? = null
)
