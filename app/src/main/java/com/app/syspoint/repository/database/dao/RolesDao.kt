package com.app.syspoint.repository.database.dao

import com.app.syspoint.repository.database.bean.RolesBean
import com.app.syspoint.repository.database.bean.RolesBeanDao

class RolesDao: Dao("RolesBean") {

    fun getListaRolesByEmpleado(identificador: String?): List<RolesBean> {
        val roles =  dao.queryBuilder()
            .where(RolesBeanDao.Properties.Identificador.eq(identificador))
            .list() as List<RolesBean>
        return  roles
    }

    //Retorna el empleado por identificador
    fun getRolByEmpleado(identificador: String?, modulo: String?): RolesBean? {
        val clienteBeans = dao.queryBuilder()
            .where(
                RolesBeanDao.Properties.Identificador.eq(identificador),
                RolesBeanDao.Properties.Modulo.eq(modulo)
            )
            .list() as List<RolesBean>
        return if (clienteBeans.size > 0) clienteBeans[0] else null
    }

    fun getRolByModule(identificador: String?, modulo: String?): RolesBean? {
        val clienteBeans = dao.queryBuilder()
            .where(
                RolesBeanDao.Properties.Identificador.eq(identificador),
                RolesBeanDao.Properties.Modulo.eq(modulo)
            )
            .list() as List<RolesBean>
        return if (clienteBeans.size > 0) clienteBeans[0] else null
    }
}