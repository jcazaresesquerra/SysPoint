package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.repository.objectBox.entities.EmployeeBox
import com.app.syspoint.repository.objectBox.entities.EmployeeBox_
import io.objectbox.query.QueryBuilder

class EmployeeDao: AbstractDao<EmployeeBox>() {

    fun clear() {
        abstractBox<EmployeeBox>().removeAll()
    }

    fun insertBox(box: EmployeeBox) {
        insert(box)
    }

    fun getEmployeeByIdentifier(identifier: String?): EmployeeBox? {
        if (identifier.isNullOrEmpty()) return null

        val query = abstractBox<EmployeeBox>().query()
            .equal(EmployeeBox_.identificador, identifier, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getEmployeeById(id: Long): List<EmployeeBox> {
        val query = abstractBox<EmployeeBox>().query()
            .equal(EmployeeBox_.id, id!!)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    suspend fun getActiveEmployees(): List<EmployeeBox> {
        val query = abstractBox<EmployeeBox>().query()
            .equal(EmployeeBox_.status, 1)
            .order(EmployeeBox_.id)
            .build()
        val results = query.find()
        query.close()

        return results
    }

    fun getByEmail(email: String?): EmployeeBox? {
        if (email.isNullOrEmpty()) return null

        val query = abstractBox<EmployeeBox>().query()
            .equal(EmployeeBox_.email, email, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]

    }

    fun validateLogin(email: String?, password: String?): EmployeeBox? {
        if (email.isNullOrEmpty()) return null

        val query = abstractBox<EmployeeBox>().query()
            .equal(EmployeeBox_.email, email, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .equal(EmployeeBox_.contrasenia, password, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
        val results = query.find()
        query.close()

        return if (results.isEmpty()) null else results[0]
    }

    fun getTotalEmployees(): Int {
        val query = abstractBox<EmployeeBox>().all
        return query.count()
    }

    fun getLastConsec(): Int {
        var folio = 0
        val empleadoBean = getLastRegister()
        if (empleadoBean != null) {
            folio = empleadoBean.identificador!!.replace("E","").toInt()
        }
        folio++
        return folio
    }

    private fun getLastRegister(): EmployeeBox? {
        val query = abstractBox<EmployeeBox>().query()
            .orderDesc(EmployeeBox_.id)
            .build()
        val results = query.find()
        query.close()

        return if (results.isNotEmpty()) results[0] else null
    }

}