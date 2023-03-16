package com.app.syspoint.models.json

import com.app.syspoint.models.Visit
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class VisitJson(
    @SerializedName("Visitas")
    @Expose
    var visits: List<Visit?>? = null
)
