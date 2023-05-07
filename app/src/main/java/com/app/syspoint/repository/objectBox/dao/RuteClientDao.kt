package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.RuteClientBox
import com.app.syspoint.repository.objectBox.entities.RuteClientBox_
import io.objectbox.query.QueryBuilder
import timber.log.Timber

private const val TAG = "RuteClientDao"

class RuteClientDao: AbstractDao<RuteClientBox>() {

    fun clear() {
        Timber.tag(TAG).d("clear")
        abstractBox<RuteClientBox>().removeAll()
    }

    fun insertBox(box: RuteClientBox) {
        Timber.tag(TAG).d("insertBox -> $box")
        insert(box)
    }

    fun getClienteByCuentaCliente(cuenta: String?): RuteClientBox? {
        val query = abstractBox<RuteClientBox>().query()
            .equal(RuteClientBox_.cuenta, cuenta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getClienteFirts(): RuteClientBox? {
        val query = abstractBox<RuteClientBox>().query()
            .equal(RuteClientBox_.id, 1)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getUltimoConsec(): Long {
        var folio: Long = 0
        val clienteBean = getUltimoRegistro()
        if (clienteBean != null) {
            folio = clienteBean.id
        }
        ++folio
        return folio
    }

    private fun getUltimoRegistro(): RuteClientBox? {
        val query = abstractBox<RuteClientBox>().query()
            .orderDesc(RuteClientBox_.id)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    private fun getConsecAccount(): RuteClientBox? {
        val query = abstractBox<RuteClientBox>().query()
            .order(RuteClientBox_.id)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getLastClientInOrder(day: Int, rute: String): Int {
        var folio = 0
        val ruteClientBox = getLastInOrder(day, rute)
        if (ruteClientBox != null) {
            folio = when(day) {
                1 -> ruteClientBox.lunOrder
                2 -> ruteClientBox.marOrder
                3 -> ruteClientBox.mieOrder
                4 -> ruteClientBox.jueOrder
                5 -> ruteClientBox.vieOrder
                6 -> ruteClientBox.sabOrder
                else -> ruteClientBox.domOrder
            }
        }
        ++folio
        return folio
    }

    private fun getLastInOrder(day: Int, rute: String): RuteClientBox? {
        val query = abstractBox<RuteClientBox>().query()
            .equal(RuteClientBox_.visitado, 0)
            .equal(RuteClientBox_.rango, rute, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(RuteClientBox_.status, 1)
            .equal(when(day) {
                1 -> RuteClientBox_.lun
                2 -> RuteClientBox_.mar
                3 -> RuteClientBox_.mie
                4 -> RuteClientBox_.jue
                5 -> RuteClientBox_.vie
                6 -> RuteClientBox_.sab
                else -> RuteClientBox_.dom
            }, 1, )
            .order(when(day) {
                1 -> RuteClientBox_.lunOrder
                2 -> RuteClientBox_.marOrder
                3 -> RuteClientBox_.mieOrder
                4 -> RuteClientBox_.jueOrder
                5 -> RuteClientBox_.vieOrder
                6 -> RuteClientBox_.sabOrder
                else -> RuteClientBox_.domOrder
            })
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getClienteByCuentaClienteAndRute(cuenta: String?, rute: String?, day: Int): RuteClientBox? {
        val query = abstractBox<RuteClientBox>().query()
            .equal(RuteClientBox_.cuenta, cuenta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(RuteClientBox_.rango, rute, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(when(day) {
                1 -> RuteClientBox_.lun
                2 -> RuteClientBox_.mar
                3 -> RuteClientBox_.mie
                4 -> RuteClientBox_.jue
                5 -> RuteClientBox_.vie
                6 -> RuteClientBox_.sab
                else -> RuteClientBox_.dom
            }, 1, )
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }


    fun getClienteByCuentaCliente(cuenta: String?, day: Int, rute: String): RuteClientBox? {
        val query = abstractBox<RuteClientBox>().query()
            .equal(RuteClientBox_.cuenta, cuenta, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(RuteClientBox_.rango, rute, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(RuteClientBox_.visitado, 0)
            .equal(
                when (day) {
                    1 -> RuteClientBox_.lun
                    2 -> RuteClientBox_.mar
                    3 -> RuteClientBox_.mie
                    4 -> RuteClientBox_.jue
                    5 -> RuteClientBox_.vie
                    6 -> RuteClientBox_.sab
                    else -> RuteClientBox_.dom
                },
                1,
            )
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getAllRutaClientes(rute: String, day: Int): List<RuteClientBox>? {
        val query = abstractBox<RuteClientBox>().query()
            .equal(RuteClientBox_.visitado, 0)
            .equal(RuteClientBox_.rango, rute, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(RuteClientBox_.status, 1)
            .equal(when(day) {
                1 -> {RuteClientBox_.lun}
                2 -> {RuteClientBox_.mar}
                3 -> {RuteClientBox_.mie}
                4 -> {RuteClientBox_.jue}
                5 -> {RuteClientBox_.vie}
                6 -> {RuteClientBox_.sab}
                else ->RuteClientBox_.dom
                             }, 1)

            .greater(when(day) {
                1 -> {RuteClientBox_.lunOrder}
                2 -> {RuteClientBox_.marOrder}
                3 -> {RuteClientBox_.mieOrder}
                4 -> {RuteClientBox_.jueOrder}
                5 -> {RuteClientBox_.vieOrder}
                6 -> {RuteClientBox_.sabOrder}
                else ->RuteClientBox_.domOrder
            }, 0)
            .order(when(day) {
                1 -> {RuteClientBox_.lunOrder}
                2 -> {RuteClientBox_.marOrder}
                3 -> {RuteClientBox_.mieOrder}
                4 -> {RuteClientBox_.jueOrder}
                5 -> {RuteClientBox_.vieOrder}
                6 -> {RuteClientBox_.sabOrder}
                else ->RuteClientBox_.domOrder
            })
            .build()
        val results = query.find()
        query.close()

        return results
    }
}