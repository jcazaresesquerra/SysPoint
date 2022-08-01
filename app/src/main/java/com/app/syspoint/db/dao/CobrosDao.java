package com.app.syspoint.db.dao;

import com.app.syspoint.db.bean.CobdetBean;
import com.app.syspoint.db.bean.CobdetBeanDao;
import com.app.syspoint.db.bean.CobrosBean;
import com.app.syspoint.db.bean.CobrosBeanDao;

import org.greenrobot.greendao.query.CountQuery;
import org.greenrobot.greendao.query.QueryBuilder;
import org.greenrobot.greendao.query.WhereCondition;

import java.util.List;

public class CobrosDao extends Dao {

    public CobrosDao() {
        super("CobrosBean");
    }


    public void CreaCobro(final CobrosBean documento, final List<CobdetBean> lista) {

        /**
         * Inicia la transaccion
         * **/
        this.beginTransaction();


        /**
         * Guarda el documento
         * **/
        this.dao.insert(documento);

        /**
         * Contiene las partidas de la venta y guardalas
         * **/
        final CobdetBeanDao documentosDetalleBeanDao = daoSession.getCobdetBeanDao();
        for (CobdetBean partida:  lista){
            partida.setCobro(documento.getId());
            documentosDetalleBeanDao.insert(partida);
        }

        /**
         * Termina la transaccion
         * **/
        this.commmit();

    }

    final public CobrosBean getUltimoCobro() {
        final List<CobrosBean> ventasBeans = dao.queryBuilder()
                .orderDesc(CobrosBeanDao.Properties.Cobro)
                .limit(1)
                .list();
        return ventasBeans.size()>0?ventasBeans.get(0):null;
    }

    final public int getUltimoFolio() {
        int folio = 0;
        final CobrosBean ventasBean = this.getUltimoCobro();
        if(ventasBean!=null){
            folio = ventasBean.getCobro();
        }
        ++folio;
        return folio;
    }


    final public CobrosBean getCobroTemporal() {

        final QueryBuilder<CobrosBean> queryBuilder = this.dao.queryBuilder();
        queryBuilder.where(CobrosBeanDao.Properties.Temporal.eq(1));

        final List<CobrosBean> ventasBeanList = queryBuilder.list();

        CobrosBean ventasBean = null;

        if(ventasBeanList.size()>0){
            ventasBean = ventasBeanList.get(0);
            final List<CobdetBean> partidaVentaBeanList = this.daoSession.getCobdetBeanDao().queryBuilder().where(CobdetBeanDao.Properties.Cobro.eq(ventasBean.getId())).list();
            ventasBean.setListaPartidas(partidaVentaBeanList);
        }

        return ventasBean;
    }

    final public void deleteTemporalCobro() throws Exception {

        /*
         *
         * Comienza la transacción
         *
         *
         * */
        this.beginTransaction();

        final CobrosBean cobrosBean = this.getCobroTemporal();
        if(cobrosBean!=null){

            /*
             *
             * Borra el cobro
             *
             * */
            this.dao.delete(cobrosBean);

            /*
             *
             * Borra las partidas
             *
             * */
            final CobdetBeanDao partidaVentaBeanDao = this.daoSession.getCobdetBeanDao();
            for(CobdetBean partidaVentaBean:cobrosBean.getListaPartidas()){
                partidaVentaBeanDao.delete(partidaVentaBean);
            }
        }


        /*
         *
         * Termina la transacción
         *
         *
         * */
        this.commmit();
    }


    final public int getTotalCobrosRealizados() {
        final CountQuery<CobrosDao> query = dao.queryBuilder().where(CobrosBeanDao.Properties.Estado.eq("CO")).buildCount();
        return (int)query.count();
    }





    final public List<CobrosBean> getVentasCliente() {

        return dao.queryBuilder()
                .where(new WhereCondition.StringCondition(" ESTADO = 'CO' GROUP BY CLIENTE_ID ")).orderDesc(CobrosBeanDao.Properties.ClienteId)
                .list();

    }

    final public List<CobrosBean> getCobroConfirmadoById(final long cobro) {
        return dao.queryBuilder()
                .where(CobrosBeanDao.Properties.Estado.eq("CO"),
                        CobrosBeanDao.Properties.Id.eq(cobro),
                        CobrosBeanDao.Properties.Sinc.eq(0))
                .orderAsc(CobrosBeanDao.Properties.Id)
                .list();
    }

    final public CobrosBean getByVentaId(final long venta) {
        final List<CobrosBean> ventasBeans = dao.queryBuilder()
                .where(CobrosBeanDao.Properties.Id.eq(venta))
                .list();
        return ventasBeans.size()>0?ventasBeans.get(0):null;
    }

    final public List<CobrosBean> GetAllListaCobrosConfirmadas() {
        return dao.queryBuilder()
                 .where(CobrosBeanDao.Properties.Estado.eq("CO"))
                .orderDesc(CobrosBeanDao.Properties.Id)
                .list();
    }



}
