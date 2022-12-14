package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.roles.RolInteractor
import com.app.syspoint.models.Role
import com.app.syspoint.models.json.RolJson
import com.app.syspoint.repository.database.bean.RolesBean
import com.app.syspoint.repository.database.dao.EmployeeDao
import com.app.syspoint.repository.database.dao.RolesDao
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestRol {
    companion object {
        fun requestAllRoles(onGetAllRolesListener: RolInteractor.OnGetAllRolesListener) {
            val getRoles = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getAllRols()

            getRoles.enqueue(object: Callback<RolJson> {
                override fun onResponse(call: Call<RolJson>, response: Response<RolJson>) {
                    if (response.isSuccessful) {
                        val roles = arrayListOf<RolesBean>()
                        for (rol in response.body()!!.roles!!) {
                            val rolesDao = RolesDao()
                            val rolesBean = rolesDao.getRolByModule(rol!!.empleado, rol.modulo)
                            if (rolesBean == null) {
                                val bean = RolesBean()
                                val dao = RolesDao()
                                val employeeDao = EmployeeDao()
                                val empleadoBean =
                                    employeeDao.getEmployeeByIdentifier(rol.empleado)
                                bean.empleado = empleadoBean
                                bean.modulo = rol.modulo
                                bean.active = rol.activo == 1
                                bean.identificador = rol.empleado
                                dao.insert(bean)
                                roles.add(bean)
                            } else {
                                val employeeDao = EmployeeDao()
                                val empleadoBean =
                                    employeeDao.getEmployeeByIdentifier(rol.empleado)
                                rolesBean.empleado = empleadoBean
                                rolesBean.modulo = rol.modulo
                                rolesBean.active = rol.activo == 1
                                rolesBean.identificador = rol.empleado
                                rolesDao.save(rolesBean)
                                roles.add(rolesBean)
                            }
                        }
                        onGetAllRolesListener.onGetAllRolesSuccess(roles)
                    } else {
                        onGetAllRolesListener.onGetAllRolesError()
                    }
                }

                override fun onFailure(call: Call<RolJson>, t: Throwable) {
                    onGetAllRolesListener.onGetAllRolesError()
                }
            })

        }

        fun saveRoles(roles: List<Role>, onSaveRolesListener: RolInteractor.OnSaveRolesListener) {
            val rolesJson = RolJson()
            rolesJson.roles = roles

            val saveRoles = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).saveRoles(rolesJson)
            val json = Gson().toJson(rolesJson)
            Log.d("Roles", json)

            saveRoles.enqueue(object: Callback<RolJson> {
                override fun onResponse(call: Call<RolJson>, response: Response<RolJson>) {
                    if (response.isSuccessful) {
                        onSaveRolesListener.onSaveRolesSuccess()
                    } else {
                        onSaveRolesListener.onSaveRolesError()
                    }
                }

                override fun onFailure(call: Call<RolJson>, t: Throwable) {
                    onSaveRolesListener.onSaveRolesError()
                }
            })
        }
    }
}