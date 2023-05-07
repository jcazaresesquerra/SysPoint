package com.app.syspoint.usecases

import com.app.syspoint.models.Resource
import com.app.syspoint.repository.objectBox.dao.EmployeeDao
import com.app.syspoint.repository.objectBox.dao.RolesDao
import com.app.syspoint.repository.objectBox.entities.RolesBox
import com.app.syspoint.repository.request.RequestRol
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.awaitResponse

class GetRolesUseCase {

    suspend operator fun invoke(): Flow<Resource<List<RolesBox>>> = callbackFlow {
        trySend(Resource.Loading)

        val call = RequestRol.requestAllRoles()

        val response = call.awaitResponse()

        if (response.isSuccessful) {
            val roles = arrayListOf<RolesBox>()
            val rolesDao = RolesDao()
            val employeeDao = EmployeeDao()

            response.body()!!.roles!!.map {rol ->
                val rolesBean = rolesDao.getRolByModule(rol!!.empleado, rol.modulo)
                if (rolesBean == null) {
                    val bean = RolesBox()
                    val empleadoBean = employeeDao.getEmployeeByIdentifier(rol.empleado)
                    bean.empleado!!.target = empleadoBean
                    bean.modulo = rol.modulo
                    bean.active = rol.activo == 1
                    bean.identificador = rol.empleado
                    rolesDao.insert(bean)
                    roles.add(bean)
                } else {
                    val empleadoBean = employeeDao.getEmployeeByIdentifier(rol.empleado)
                    rolesBean.empleado!!.target = empleadoBean
                    rolesBean.modulo = rol.modulo
                    rolesBean.active = rol.activo == 1
                    rolesBean.identificador = rol.empleado
                    rolesDao.insert(rolesBean)
                    roles.add(rolesBean)
                }
            }

            trySend(Resource.Success(roles))
        } else {
            trySend(Resource.Error("Ha ocurrido un error al obtener cobranzas"))
        }

        awaitClose {
            call.cancel()
        }
    }
}