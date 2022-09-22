package com.app.syspoint.models.json

import com.app.syspoint.models.Payment
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class PaymentJson(
    @SerializedName("Cobranza")
    @Expose
    var payments: List<Payment?>? = null
)
