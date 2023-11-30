package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.models.CloseCash
import com.app.syspoint.repository.objectBox.entities.ChargeBox
import com.app.syspoint.repository.objectBox.entities.ChargeBox_
import com.app.syspoint.utils.Utils
import io.objectbox.query.QueryBuilder

class ChargeDao: AbstractDao<ChargeBox>() {

    fun clear() {
        removeAll<ChargeBox>()
    }

    fun insertBox(chargeBox: ChargeBox) {
        insert(chargeBox)
    }

    fun removeBox(id: Long) {
        remove<ChargeBox>(id)
    }

    fun getSaldoByCliente(cliente: String): Double {
        val query = abstractBox<ChargeBox>().query()
            .equal(ChargeBox_.cliente, cliente, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ChargeBox_.estado, "PE", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .greater(ChargeBox_.saldo, 0)
            .build()
        val results = query.find().sumOf { it.saldo!! }
        query.close()
        return results
    }

    fun getByCobranzaByCliente(cuenta: String?): List<ChargeBox> {
        val query = abstractBox<ChargeBox>().query()
            .equal(ChargeBox_.cliente, cuenta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ChargeBox_.estado, "PE", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .greater(ChargeBox_.saldo, 1)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getDocumentsByCliente(cliente: String?): List<ChargeBox> {
        val query = abstractBox<ChargeBox>().query()
            .equal(ChargeBox_.cliente, cliente, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .order(ChargeBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getByCobranza(cobranza: String?): ChargeBox? {
        val query = abstractBox<ChargeBox>().query()
            .equal(ChargeBox_.cobranza, cobranza, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }


    fun getDocumentosSeleccionados(cuenta: String?): List<ChargeBox> {
        val query = abstractBox<ChargeBox>().query()
            .equal(ChargeBox_.isCheck, true)
            .equal(ChargeBox_.cliente, cuenta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ChargeBox_.estado, "PE", QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .greater(ChargeBox_.saldo, 1)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getCobranzaFechaActual(fecha: String?): List<ChargeBox> {
        val query = abstractBox<ChargeBox>().query()
            .equal(ChargeBox_.fecha, fecha, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ChargeBox_.abono, false)
            .order(ChargeBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getAbonosFechaActual(fecha: String?): List<ChargeBox> {
        val query = abstractBox<ChargeBox>().query()
            .equal(ChargeBox_.fecha, fecha, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(ChargeBox_.abono, true)
            .order(ChargeBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getAllConfirmedChargesToday(stockId: Int): List<CloseCash> {
        val lastUserSession = CacheInteractor().getSeller()
        val identificador = lastUserSession?.identificador?:""
        val lista_corte: MutableList<CloseCash> = ArrayList()

        val queryCharges = abstractBox<ChargeBox>().query()
            .equal(ChargeBox_.stockId, stockId.toLong())
            .between(ChargeBox_.updatedAt, Utils.fechaActualHMSStartDay_(),  Utils.fechaActualHMSEndDay_())
            .greater(ChargeBox_.acuenta, 0.0)
            .build()

        val resultCharges = queryCharges.find()
        queryCharges.close()

        val clientDao = ClientDao()
        resultCharges.map {cursor ->
            val client = clientDao.getClientByAccount(cursor.cliente)
            val closeCash = CloseCash()
            closeCash.comertialName = client!!.nombre_comercial!!
            closeCash.abono = cursor.acuenta!!
            closeCash.ticket = cursor.venta.toString()
            closeCash.updatedAt = cursor.updatedAt!!
            closeCash.employee = cursor.empleado!!
            closeCash.status = cursor.estado!!
            closeCash.stockId = cursor.stockId
            lista_corte.add(closeCash)
        }

        return lista_corte
    }

}