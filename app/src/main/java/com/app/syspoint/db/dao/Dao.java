package com.app.syspoint.db.dao;

import com.app.syspoint.db.DBHelper;
import com.app.syspoint.db.DaoSession;
import com.app.syspoint.db.bean.Bean;

import org.greenrobot.greendao.AbstractDao;

import java.util.List;

public class Dao {

    protected AbstractDao dao;
    protected DaoSession daoSession;
    public static DaoSession daoExternalSession;

    public Dao(final String daoName) {

        if (daoExternalSession == null) {
            daoSession = DBHelper.getSingleton().getDaoSession();
        } else {
            daoSession = daoExternalSession;
        }

        switch (daoName) {

            case "LogSyncGetBean":
                dao = daoSession.getLogSyncGetBeanDao();
                break;


            case "EmpleadoBean":
                dao = daoSession.getEmpleadoBeanDao();
                break;

            case "ProductoBean":
                dao = daoSession.getProductoBeanDao();
                break;

            case "ClienteBean":
                dao = daoSession.getClienteBeanDao();
                break;

            case "PartidasBean":
                dao = daoSession.getPartidasBeanDao();
                break;

            case "VentasModelBean":
                dao = daoSession.getVentasModelBeanDao();
                break;

            case "VentasBean":
                dao = daoSession.getVentasBeanDao();
                break;

            case "PrinterBean":
                dao = daoSession.getPrinterBeanDao();
                break;

            case "RuteoBean":
                dao = daoSession.getRuteoBeanDao();
                break;

            case "SesionBean":
                dao = daoSession.getSesionBeanDao();
                break;

            case "PreciosEspecialesBean":
                dao = daoSession.getPreciosEspecialesBeanDao();
                break;

            case "RolesBean":
                dao = daoSession.getRolesBeanDao();
                break;

            case "ClientesRutaBean":
                dao = daoSession.getClientesRutaBeanDao();
                break;

            case "InventarioBean":
                dao = daoSession.getInventarioBeanDao();
                break;

            case "InventarioHistorialBean":
                dao = daoSession.getInventarioHistorialBeanDao();
                break;

            case "CorteBean":
                dao = daoSession.getCorteBeanDao();
                break;

            case "PersistenciaPrecioBean":
                dao = daoSession.getPersistenciaPrecioBeanDao();
                break;

            case "VisitasBean":
                dao = daoSession.getVisitasBeanDao();
                break;

            case "CobranzaBean":
                dao = daoSession.getCobranzaBeanDao();
                break;

            case "CobrosBean":
                dao = daoSession.getCobrosBeanDao();
                break;

            case "CobdetBean":
                dao = daoSession.getCobdetBeanDao();
                break;

            case "CobranzaModel":
                dao =daoSession.getCobranzaModelDao();
                break;

            case "TaskBean":
                dao = daoSession.getTaskBeanDao();
                break;
        }
    }

    public List<Bean> list() {
        return dao.loadAll();
    }

    public void insert(Bean bean) {
        dao.insert(bean);
    }

    public void delete(Bean bean) {
        dao.delete(bean);
    }

    public void beginTransaction() {
        dao.getDatabase().beginTransaction();
    }

    final public static void beginExternalTransaction() {
        daoExternalSession = DBHelper.getSingleton().getDaoSession();
        daoExternalSession.getDatabase().beginTransaction();
    }

    final public static void commitExternalTransaction() {
        daoExternalSession.getDatabase().setTransactionSuccessful();
        daoExternalSession.getDatabase().endTransaction();
        daoExternalSession = null;
    }

    public void commmit() {
        dao.getDatabase().setTransactionSuccessful();
        dao.getDatabase().endTransaction();
    }

    public Bean getByID(final long id) {
        return (Bean) dao.loadByRowId(id);
    }

    public void clear() {
        dao.deleteAll();
    }

    public void insertAll(List<Bean> list) {
        for (Bean bean : list) {
            this.dao.insert(bean);
        }
    }

    public void save(Bean bean) {
        dao.save(bean);
    }


}
