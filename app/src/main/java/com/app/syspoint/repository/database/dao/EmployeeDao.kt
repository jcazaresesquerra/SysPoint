package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.ClienteBean
import com.app.syspoint.repository.database.bean.ClienteBeanDao
import com.app.syspoint.repository.database.bean.EmpleadoBean
import com.app.syspoint.repository.database.bean.EmpleadoBeanDao

class EmployeeDao: Dao("EmpleadoBean") {

    //Retorna el empleado por identificador
    fun getEmployeeByIdentifier(identifier: String?): EmpleadoBean? {
        val employeeBean = dao.queryBuilder()
            .where(EmpleadoBeanDao.Properties.Identificador.eq(identifier))
            .list()
        return if (employeeBean.isNotEmpty()) employeeBean[0] as EmpleadoBean? else null
    }

    fun getActiveEmployees(): List<EmpleadoBean> {
        return dao.queryBuilder()
            .where(EmpleadoBeanDao.Properties.Status.eq(1))
            .orderAsc(EmpleadoBeanDao.Properties.Id)
            .list() as List<EmpleadoBean>
    }

    //Retorna el empleado por identificador
    fun validateLogin(email: String?, password: String?): EmpleadoBean? {
        val employeeBeans = dao.queryBuilder()
            .where(
                EmpleadoBeanDao.Properties.Email.eq(email),
                EmpleadoBeanDao.Properties.Contrasenia.eq(password)
            )
            .list()
        return if (employeeBeans.size > 0) employeeBeans[0] as EmpleadoBean? else null
    }

    fun getTotalEmployees(): Int {
        val query = dao.queryBuilder().buildCount()
        return query.count().toInt()
    }

    fun getEmployees(): List<EmpleadoBean> {
        return dao.queryBuilder()
            .orderAsc(EmpleadoBeanDao.Properties.Id)
            .list() as List<EmpleadoBean>
    }


    fun getEmployeeById(id: String?): List<EmpleadoBean> {
        return dao.queryBuilder()
            .where(EmpleadoBeanDao.Properties.Id.eq(id))
            .orderAsc(EmpleadoBeanDao.Properties.Id)
            .list() as List<EmpleadoBean>
    }


    fun getByEmail(email: String?): EmpleadoBean? {
        if (email.isNullOrEmpty()) return null
        return try {
                val userBeanList = dao.queryBuilder()
                    .where(EmpleadoBeanDao.Properties.Email.eq(email))
                    .list()

                if (userBeanList.isNotEmpty()) userBeanList[0] as EmpleadoBean else null

            } catch (exception: Exception) {
                exception.printStackTrace()
                null
            }
    }

    fun getLastConsec(): Int {
        var folio = 0
        val empleadoBean = getLastRegister()
        if (empleadoBean != null) {
            folio = empleadoBean.identificador.replace("E","").toInt()
        }
        folio++
        return folio
    }

    private fun getLastRegister(): EmpleadoBean? {
        val empleadoBean = dao.queryBuilder()
            .orderDesc(EmpleadoBeanDao.Properties.Id)
            .limit(1)
            .list()
        return if (empleadoBean.isNotEmpty()) empleadoBean[0] as EmpleadoBean else null
    }
}