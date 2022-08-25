package com.app.syspoint.repository.database.dao;

import android.database.Cursor;

import com.app.syspoint.repository.database.bean.CobranzaBean;
import com.app.syspoint.repository.database.bean.CobranzaBeanDao;

import java.util.List;

public class CobranzaDao extends Dao{
    public CobranzaDao() {
        super("CobranzaBean");
    }




    final public double getSaldoByCliente(String cliente){
        final Cursor cursor = dao.getDatabase().rawQuery("SELECT SUM(saldo) FROM cobranza WHERE cliente ='"  + cliente + "' AND estado = 'PE' AND saldo > 0 ",null);
        cursor.moveToFirst();
        double result = cursor.getDouble(0);
        return result;
    }


    final public List<CobranzaBean> getDocumentsByCliente(String cliente) {
        return dao.queryBuilder()
                .where(CobranzaBeanDao.Properties.Cliente.eq(cliente))
                .orderAsc(CobranzaBeanDao.Properties.Id)
                .list();
    }


    final public List<CobranzaBean> getCobranzaFechaActual(String fecha) {
        return dao.queryBuilder()
                .where(CobranzaBeanDao.Properties.Fecha.eq(fecha), CobranzaBeanDao.Properties.Abono.eq(false))
                .orderAsc(CobranzaBeanDao.Properties.Id)
                .list();
    }


    final public List<CobranzaBean> getAbonosFechaActual(String fecha) {
        return dao.queryBuilder()
                .where(CobranzaBeanDao.Properties.Fecha.eq(fecha), CobranzaBeanDao.Properties.Abono.eq(true))
                .orderAsc(CobranzaBeanDao.Properties.Id)
                .list();
    }

    final public CobranzaBean getByCobranza(final String cobranza) {
        try {
            final List<CobranzaBean> productosBeans = dao.queryBuilder()
                    .where(CobranzaBeanDao.Properties.Cobranza.eq(cobranza))
                    .list();
            return productosBeans.size() > 0 ? productosBeans.get(0) : null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    final public double getTotalSaldoDocumentosCliente(final String cliente) throws Exception
    {
        final Cursor cursor = dao.getDatabase().rawQuery("SELECt SUM(saldo) FROM cobranza  WHERE saldo > 0 AND estado = 'PE' AND  CLIENTE = '"+ cliente + "'",null);
        cursor.moveToFirst();
        double result = cursor.getDouble(0);
        return result;
    }

    final public List<CobranzaBean> getByCobranzaByCliente(final String cuenta) {
        return dao.queryBuilder()
                .where(CobranzaBeanDao.Properties.Cliente.eq(cuenta), CobranzaBeanDao.Properties.Estado.eq("PE"), CobranzaBeanDao.Properties.Saldo.ge(1))
                .list();
    }


    final public List<CobranzaBean> getDocumentosSeleccionados(final String cuenta){
        return dao.queryBuilder()
                .where(CobranzaBeanDao.Properties.IsCheck.eq(true), CobranzaBeanDao.Properties.Cliente.eq(cuenta), CobranzaBeanDao.Properties.Estado.eq("PE"), CobranzaBeanDao.Properties.Saldo.ge(1))
                .list();
    }




}
