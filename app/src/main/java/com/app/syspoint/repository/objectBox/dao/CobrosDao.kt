package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.App
import com.app.syspoint.repository.objectBox.entities.CobdetBox
import com.app.syspoint.repository.objectBox.entities.CobdetBox_
import com.app.syspoint.repository.objectBox.entities.CobrosBox
import com.app.syspoint.repository.objectBox.entities.CobrosBox_
import com.app.syspoint.utils.Utils
import io.objectbox.query.QueryBuilder

class CobrosDao: AbstractDao<CobrosBox>() {

    fun clear() {
        abstractBox<CobrosBox>().removeAll()
    }

    fun createCharge(document: CobrosBox, lista: List<CobdetBox>) {

        insert(document)
        /**
         * Contiene las partidas de la venta y guardalas
         */
        val documentDetailBeanDao = App.mBoxStore!!.boxFor(CobdetBox::class.java)
        for (item in lista) {
            item.cobro = document.id
            documentDetailBeanDao.put(item)
        }
    }

    private fun getLastCharge(): CobrosBox? {
        val results = abstractBox<CobrosBox>().all
        return if (results.isNotEmpty()) results[0] else null
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


    private fun getTemporalPayment(): CobrosBox? {
        val query = abstractBox<CobrosBox>().query()
            .equal(CobrosBox_.temporal, 1)
            .build()

        val results = query.find()
        query.close()
        var cobrosBox: CobrosBox? = null
        val cobdetBox= App.mBoxStore!!.boxFor(CobdetBox::class.java)

        if (results.isNotEmpty()) {
            cobrosBox = results[0]
            val queryPartidas = cobdetBox.query().equal(CobdetBox_.cobro, cobrosBox.id!!).build()
            val partidaVentaBeanList = queryPartidas.find()
            queryPartidas.close()
            cobrosBox.listaPartidas!!.addAll(partidaVentaBeanList)
        }

        return cobrosBox
    }

    @Throws(Exception::class)
    fun deleteTemporalPayment() {

        val cobrosBox = getTemporalPayment()
        if (cobrosBox != null) {
            remove<CobrosBox>(cobrosBox.id!!)
            val partidaVentaBeanDao = App.mBoxStore!!.boxFor(CobdetBox::class.java)
            for (partidaVentaBean in cobrosBox.listaPartidas!!) {
                partidaVentaBeanDao.remove(partidaVentaBean.id!!)
            }
        }

    }


    fun getTotalCobrosRealizados(): Int {
        val query = abstractBox<CobrosBox>().query()
            .equal(CobrosBox_.estado, "CO", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        return results.count()
    }


    fun getVentasCliente(): List<CobrosBox> {
        val query = abstractBox<CobrosBox>().query()
            .equal(CobrosBox_.estado, "CO", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(CobrosBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results.groupBy { it.id }.map { it.value[0] }
    }

    fun getCobroConfirmadoById(cobro: Long): List<CobrosBox> {
        val query = abstractBox<CobrosBox>().query()
            .equal(CobrosBox_.estado, "CO", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(CobrosBox_.id, cobro)
            .equal(CobrosBox_.sinc, 0)
            .order(CobrosBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getByVentaId(venta: Long): CobrosBox? {
        val query = abstractBox<CobrosBox>().query()
            .equal(CobrosBox_.id, venta)
            .build()
        val results = query.find()
        query.close()

        return if (results.isNotEmpty()) results[0] else null
    }

    fun GetAllListaCobrosConfirmadas(): List<CobrosBox> {
        val query = abstractBox<CobrosBox>().query()
            .equal(CobrosBox_.estado, "CO", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(CobrosBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getAllConfirmedChargesToday(): List<CobrosBox> {
        val query = abstractBox<CobrosBox>().query()
            .equal(CobrosBox_.estado, "CO", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .greaterOrEqual(CobrosBox_.fecha, Utils.fechaActualHMSStartDay(), QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .lessOrEqual(CobrosBox_.fecha, Utils.fechaActualHMSEndDay(), QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .orderDesc(CobrosBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }
}