package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.RolesBox
import com.app.syspoint.repository.objectBox.entities.RolesBox_
import io.objectbox.query.QueryBuilder

class RolesDao: AbstractDao<RolesBox>() {
    fun clear() {
        abstractBox<RolesBox>().removeAll()
    }

    fun insertBox(box: RolesBox) {
        insert(box)
    }

    fun getListaRolesByEmpleado(identificador: String?): List<RolesBox> {
        val query = abstractBox<RolesBox>().query()
            .equal(RolesBox_.identificador, identificador, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    //Retorna el empleado por identificador
    fun getRolByEmpleado(identificador: String?, modulo: String?): RolesBox? {
        if (identificador.isNullOrEmpty()) return null

        val query = abstractBox<RolesBox>().query()
            .equal(RolesBox_.identificador, identificador, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(RolesBox_.modulo, modulo, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getRolByModule(identificador: String?, modulo: String?): RolesBox? {
        if (identificador.isNullOrEmpty()) return null

        val query = abstractBox<RolesBox>().query()
            .equal(RolesBox_.identificador, identificador, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(RolesBox_.modulo, modulo, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }
}