package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.CobdetBean
import com.app.syspoint.repository.database.bean.CobdetBeanDao
import com.app.syspoint.repository.database.bean.CobrosBean
import com.app.syspoint.repository.database.bean.CobrosBeanDao
import com.app.syspoint.utils.Utils
import org.greenrobot.greendao.query.CountQuery
import org.greenrobot.greendao.query.QueryBuilder
import org.greenrobot.greendao.query.WhereCondition.StringCondition

class ChargesDao: Dao("CobrosBean") {

    fun createCharge(document: CobrosBean, lista: List<CobdetBean>) {
        /**
         * Inicia la transaccion
         */
        beginTransaction()
        /**
         * Guarda el documento
         */
        insert(document)
        /**
         * Contiene las partidas de la venta y guardalas
         */
        val documentDetailBeanDao = daoSession.cobdetBeanDao
        for (item in lista) {
            item.cobro = document.id
            documentDetailBeanDao.insert(item)
        }
        /**
         * Termina la transaccion
         */
        commmit()
    }

    private fun getLastCharge(): CobrosBean? {
        val ventasBeans = dao.queryBuilder()
            .orderDesc(CobrosBeanDao.Properties.Cobro)
            .limit(1)
            .list() as List<CobrosBean>
        return if (ventasBeans.size > 0) ventasBeans[0] else null
    }

    fun getUltimoFolio(): Int {
        var folio = 0
        val ventasBean = getLastCharge()
        if (ventasBean != null) {
            folio = ventasBean.cobro
        }
        ++folio
        return folio
    }


    private fun getTemporalPayment(): CobrosBean? {
        val queryBuilder = dao.queryBuilder() as QueryBuilder<CobrosBean>
        queryBuilder.where(CobrosBeanDao.Properties.Temporal.eq(1))
        val ventasBeanList = queryBuilder.list()
        var ventasBean: CobrosBean? = null
        if (ventasBeanList.size > 0) {
            ventasBean = ventasBeanList[0]
            val partidaVentaBeanList = daoSession.cobdetBeanDao.queryBuilder()
                .where(CobdetBeanDao.Properties.Cobro.eq(ventasBean.id)).list()
            ventasBean.listaPartidas = partidaVentaBeanList
        }
        return ventasBean
    }

    @Throws(Exception::class)
    fun deleteTemporalPayment() {

        /*
         *
         * Comienza la transacción
         *
         *
         * */
        beginTransaction()
        val paymentBean = getTemporalPayment()
        if (paymentBean != null) {

            /*
             *
             * Borra el cobro
             *
             * */
            delete(paymentBean)

            /*
             *
             * Borra las partidas
             *
             * */
            val partidaVentaBeanDao = daoSession.cobdetBeanDao
            for (partidaVentaBean in paymentBean.listaPartidas) {
                partidaVentaBeanDao.delete(partidaVentaBean)
            }
        }


        /*
         *
         * Termina la transacción
         *
         *
         * */commmit()
    }


    fun getTotalCobrosRealizados(): Int {
        val query = dao.queryBuilder().where(CobrosBeanDao.Properties.Estado.eq("CO"))
            .buildCount() as CountQuery<ChargesDao>
        return query.count().toInt()
    }


    fun getVentasCliente(): List<CobrosBean> {
        return dao.queryBuilder()
            .where(StringCondition(" ESTADO = 'CO' GROUP BY CLIENTE_ID "))
            .orderDesc(CobrosBeanDao.Properties.ClienteId)
            .list() as List<CobrosBean>
    }

    fun getCobroConfirmadoById(cobro: Long): List<CobrosBean> {
        return dao.queryBuilder()
            .where(
                CobrosBeanDao.Properties.Estado.eq("CO"),
                CobrosBeanDao.Properties.Id.eq(cobro),
                CobrosBeanDao.Properties.Sinc.eq(0)
            )
            .orderAsc(CobrosBeanDao.Properties.Id)
            .list() as List<CobrosBean>
    }

    fun getByVentaId(venta: Long): CobrosBean? {
        val ventasBeans = dao.queryBuilder()
            .where(CobrosBeanDao.Properties.Id.eq(venta))
            .list() as List<CobrosBean>
        return if (ventasBeans.size > 0) ventasBeans[0] else null
    }

    fun GetAllListaCobrosConfirmadas(): List<CobrosBean> {
        return dao.queryBuilder()
            .where(CobrosBeanDao.Properties.Estado.eq("CO"))
            .orderDesc(CobrosBeanDao.Properties.Id)
            .list() as List<CobrosBean>
    }

    fun getAllConfirmedChargesToday(): List<CobrosBean> {
        return dao.queryBuilder()
            .where(CobrosBeanDao.Properties.Estado.eq("CO"))
            .where(CobrosBeanDao.Properties.Fecha.ge(Utils.fechaActualHMSStartDay()))
            .where(CobrosBeanDao.Properties.Fecha.le(Utils.fechaActualHMSEndDay()))
            .orderDesc(CobrosBeanDao.Properties.Id)
            .list() as List<CobrosBean>
    }
}