package com.app.syspoint.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Role(
    @Expose
    var empleadosId: Int? = null,
    
    @SerializedName("modulo")
    @Expose
    var modulo: String? = null,
    
    @SerializedName("activo")
    @Expose
    var activo: Int? = null,
    
    @SerializedName("empleado")
    @Expose
    var empleado: String? = null,
    
    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null,
    
    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
): Serializable
