package com.app.syspoint.db.dao;

import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.ClienteBeanDao;

import java.util.List;

public class ClienteDao extends Dao {

    public ClienteDao() {
        super("ClienteBean");
    }

    //Retorna el empleado por identificador
    public final ClienteBean getClienteByCuenta(String cuenta){
        final List<ClienteBean> clienteBeans = dao.queryBuilder()
                .where(ClienteBeanDao.Properties.Cuenta.eq(cuenta))
                .list();
        return clienteBeans.size()> 0? clienteBeans.get(0) : null;
    }


    public final ClienteBean getCliente(String cuenta){
        final List<ClienteBean> clienteBeans = dao.queryBuilder()
                .where(ClienteBeanDao.Properties.Id.eq(cuenta))
                .list();
        return clienteBeans.size()> 0? clienteBeans.get(0) : null;
    }

    //TODO ULTIMA VENTA
    final private ClienteBean getUltimoRegistro() {
        final List<ClienteBean> clienteBeans = dao.queryBuilder()
                .orderDesc(ClienteBeanDao.Properties.Consec)
                .limit(1)
                .list();
        return clienteBeans.size()>0?clienteBeans.get(0):null;
    }

    //TODO ULTIMO FOLIO
    final public int getUltimoConsec() {
        int folio = 0;
        final ClienteBean clienteBean = this.getUltimoRegistro();
        if(clienteBean!=null){
            folio = clienteBean.getConsec();
        }
        ++folio;
        return folio;
    }


    final public List<ClienteBean> getListaClientesRutaLunes(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClienteBeanDao.Properties.Rango.eq(ruta))
                .where(ClienteBeanDao.Properties.Lun.eq(dia))
                .where(ClienteBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClienteBeanDao.Properties.Id)
                .list();
    }
    final public List<ClienteBean> getListaClientesRutaMartes(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClienteBeanDao.Properties.Rango.eq(ruta))
                .where(ClienteBeanDao.Properties.Mar.eq(dia))
                .where(ClienteBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClienteBeanDao.Properties.Id)
                .list();
    }

    final public List<ClienteBean> getListaClientesRutaMiercoles(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClienteBeanDao.Properties.Rango.eq(ruta))
                .where(ClienteBeanDao.Properties.Mie.eq(dia))
                .where(ClienteBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClienteBeanDao.Properties.Id)
                .list();
    }

    final public List<ClienteBean> getListaClientesRutaJueves(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClienteBeanDao.Properties.Rango.eq(ruta))
                .where(ClienteBeanDao.Properties.Jue.eq(dia))
                .where(ClienteBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClienteBeanDao.Properties.Id)
                .list();
    }

    final public List<ClienteBean> getListaClientesRutaViernes(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClienteBeanDao.Properties.Rango.eq(ruta))
                .where(ClienteBeanDao.Properties.Vie.eq(dia))
                .where(ClienteBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClienteBeanDao.Properties.Id)
                .list();
    }
    final public List<ClienteBean> getListaClientesRutaSabado(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClienteBeanDao.Properties.Rango.eq(ruta))
                .where(ClienteBeanDao.Properties.Sab.eq(dia))
                .where(ClienteBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClienteBeanDao.Properties.Id)
                .list();
    }
    final public List<ClienteBean> getListaClientesRutaDomingo(String ruta, int dia) {
        return dao.queryBuilder()
                .where(ClienteBeanDao.Properties.Rango.eq(ruta))
                .where(ClienteBeanDao.Properties.Dom.eq(dia))
                .where(ClienteBeanDao.Properties.Visitado.eq(0))
                .orderAsc(ClienteBeanDao.Properties.Id)
                .list();
    }


    //Actualizasmo si ya fue visitado el cliente
    final public void updateVisitado(){
        final List<ClienteBean> list = this.getAllVisitado();

        //Recorremos todos los clientes
        for (ClienteBean cliente : list){
            ClienteDao clienteDao = new ClienteDao();
            cliente.setVisitado(0);
            clienteDao.save(cliente);
        }
    }

    final public List<ClienteBean> getAllVisitado(){
        return dao.queryBuilder()
                .list();
    }

    final public List<ClienteBean> getClientes() {
        return dao.queryBuilder()
                .orderAsc(ClienteBeanDao.Properties.Id)
                .list();
    }


    final public List<ClienteBean> getByIDCliente(String id) {
        return dao.queryBuilder()
                .orderAsc(ClienteBeanDao.Properties.Id)
                .where(ClienteBeanDao.Properties.Id.eq(id))
                .list();
    }


    final public List<ClienteBean> getClientsByDay(String fecha){

        return dao.queryBuilder()
                .where(ClienteBeanDao.Properties.Date_sync.eq(fecha))
                .orderAsc(ClienteBeanDao.Properties.Id)
                .list();

    }

}
