package com.app.syspoint.db.dao;


import com.app.syspoint.db.bean.ClientesRutaBean;
import com.app.syspoint.db.bean.ClientesRutaBeanDao;

import java.util.List;

public class ClientesRutaDao extends Dao {

    public ClientesRutaDao() {
        super("ClientesRutaBean");
    }


    final public List<ClientesRutaBean> getAllRutaClientes() {
        return dao.queryBuilder()
                .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClientesRutaBeanDao.Properties.Id)
                .list();
    }

    final public List<ClientesRutaBean> getListaClientesRutaLunes(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
                .where(ClientesRutaBeanDao.Properties.Lun.eq(dia))
                .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClientesRutaBeanDao.Properties.Id)
                .list();
    }

    //Retorna el empleado por identificador
    public final ClientesRutaBean getClienteFirts() {
        final List<ClientesRutaBean> clienteBeans = dao.queryBuilder()
                .where(ClientesRutaBeanDao.Properties.Id.eq(1))
                .list();
        return clienteBeans.size() > 0 ? clienteBeans.get(0) : null;
    }

    final public List<ClientesRutaBean> getListaClientesRutaMartes(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
                .where(ClientesRutaBeanDao.Properties.Mar.eq(dia))
                .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClientesRutaBeanDao.Properties.Id)
                .list();
    }

    final public List<ClientesRutaBean> getListaClientesRutaMiercoles(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
                .where(ClientesRutaBeanDao.Properties.Mie.eq(dia))
                .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClientesRutaBeanDao.Properties.Id)
                .list();
    }

    final public List<ClientesRutaBean> getListaClientesRutaJueves(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
                .where(ClientesRutaBeanDao.Properties.Jue.eq(dia))
                .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClientesRutaBeanDao.Properties.Id)
                .list();
    }

    final public List<ClientesRutaBean> getListaClientesRutaViernes(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
                .where(ClientesRutaBeanDao.Properties.Vie.eq(dia))
                .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClientesRutaBeanDao.Properties.Id)
                .list();
    }

    final public List<ClientesRutaBean> getListaClientesRutaSabado(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
                .where(ClientesRutaBeanDao.Properties.Sab.eq(dia))
                .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClientesRutaBeanDao.Properties.Id)
                .list();
    }

    final public List<ClientesRutaBean> getListaClientesRutaDomingo(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClientesRutaBeanDao.Properties.Rango.eq(ruta))
                .where(ClientesRutaBeanDao.Properties.Dom.eq(dia))
                .where(ClientesRutaBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClientesRutaBeanDao.Properties.Id)
                .list();
    }

    //Actualizasmo si ya fue visitado el cliente
    final public void updateVisitado() {
        final List<ClientesRutaBean> list = this.getAllVisitado();

        //Recorremos todos los clientes
        for (ClientesRutaBean cliente : list) {
            ClientesRutaDao clienteDao = new ClientesRutaDao();
            cliente.setVisitado(0);
            clienteDao.save(cliente);
        }
    }

    final public List<ClientesRutaBean> getAllVisitado() {
        return dao.queryBuilder()
                .list();
    }


    //Retorna el empleado por identificador
    public final ClientesRutaBean getClienteByCuentaCliente(String cuenta) {
        final List<ClientesRutaBean> clienteBeans = dao.queryBuilder()
                .where(ClientesRutaBeanDao.Properties.Cuenta.eq(cuenta))
                .list();
        return clienteBeans.size() > 0 ? clienteBeans.get(0) : null;
    }

    final private ClientesRutaBean getUltimoRegistro() {
        final List<ClientesRutaBean> clienteBeans = dao.queryBuilder()
                .orderDesc(ClientesRutaBeanDao.Properties.Id)
                .limit(1)
                .list();
        return clienteBeans.size()>0?clienteBeans.get(0):null;
    }

    //TODO ULTIMO FOLIO
    final public long getUltimoConsec() {
        long folio = 0;
        final ClientesRutaBean clienteBean = this.getUltimoRegistro();
        if(clienteBean!=null){
            folio = clienteBean.getId();
        }
        ++folio;
        return folio;
    }

}
