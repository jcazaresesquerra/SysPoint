package com.app.syspoint.models.json

import com.app.syspoint.models.Token
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class TokenJson(
    @SerializedName("Token") @Expose
    var tokens: List<Token?>? = null,
    @SerializedName("Error") @Expose
    var error: List<Token?>? = null
)