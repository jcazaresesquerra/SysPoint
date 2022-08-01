package com.app.syspoint.db.dao;

import android.database.Cursor;

public class PartidasDao extends Dao{
    public PartidasDao() {
        super("PartidasBean");
    }




    final public int getUnidadesVendidasPorArticulo(long articulo){
        final Cursor cursor = dao.getDatabase().rawQuery("SELECT SUM(cantidad) FROM partidas WHERE ARTICULO_ID ="  + articulo + "",null);
        cursor.moveToFirst();
        int result = cursor.getInt(0);
        return result;
    }
}
