package com.app.syspoint.repository.objectBox.dao

import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.repository.objectBox.AppBundle
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

    fun removeUnnecessaryEmployees() {
        val currentEmployee = getEmployee()
        val employees = abstractBox<EmployeeBox>().all
        if (!currentEmployee?.clientId.isNullOrEmpty()) {
            employees.map { employee ->
                if (employee.clientId != currentEmployee?.clientId)
                    abstractBox<EmployeeBox>().remove(employee.id)
            }
        }

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

    fun getEmployeeByEmail(email: String?): EmployeeBox? {
        if (email.isNullOrEmpty()) return null

        val query = abstractBox<EmployeeBox>().query()
            .equal(EmployeeBox_.email, email, QueryBuilder.StringOrder.CASE_INSENSITIVE)
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

    fun getEmployeeByID(id: Long): EmployeeBox? {
        val query = abstractBox<EmployeeBox>().query()
            .equal(EmployeeBox_.id, id)
            .build()
        val results = query.find()
        query.close()

        return if (results.isNullOrEmpty()) null else results[0]
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
        val employee = abstractBox<EmployeeBox>().all

        val employees = employee.map { empleado ->
            val updatedIdentificador = empleado.identificador?.replace("E0", "")?.replace("E", "") ?: empleado.identificador
            empleado.copy(identificador = updatedIdentificador)
        }.sortedByDescending { it.identificador }

        return if (employees.isNotEmpty()) employees[0] else null
    }

    private fun getEmployee(): EmployeeBox? {
        var vendedoresBean = AppBundle.getUserBox()
        if (vendedoresBean == null) {
            val sessionBox = SessionDao().getUserSession()
            vendedoresBean = if (sessionBox != null) {
                EmployeeDao().getEmployeeByID(sessionBox.empleadoId)
            } else {
                CacheInteractor().getSeller()
            }
        }
        return vendedoresBean
    }

}