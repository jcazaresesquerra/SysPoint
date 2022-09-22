package com.app.syspoint.repository.database.dao

class ItemDao: Dao("PartidasBean") {
    fun getUnidadesVendidasPorArticulo(articulo: Long): Int {
        val cursor = dao.database.rawQuery(
            "SELECT SUM(cantidad) FROM partidas WHERE ARTICULO_ID =$articulo",
            null
        )
        cursor.moveToFirst()
        return cursor.getInt(0)
    }
}