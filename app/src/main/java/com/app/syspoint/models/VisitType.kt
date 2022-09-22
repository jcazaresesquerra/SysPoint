package com.app.syspoint.models

data class VisitType(
    var id: Int = 0,
    var name: String? = null,
    var isSelected: Boolean = false
) {
    override fun toString(): String {
        return "TipoVisitaModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}'
    }
}
