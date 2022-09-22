package com.app.syspoint.interactor.roles

import com.app.syspoint.models.Role
import com.app.syspoint.repository.database.bean.RolesBean

abstract class RolInteractor {

    interface OnGetAllRolesListener {
        fun onGetAllRolesSuccess(roles: List<RolesBean>)
        fun onGetAllRolesError()
    }

    interface OnSaveRolesListener {
        fun onSaveRolesSuccess()
        fun onSaveRolesError()
    }

    open fun executeGetAllRoles(onGetAllRolesListener: OnGetAllRolesListener) {}
    open fun executeSaveRoles(roles: List<Role>, onSaveRolesListener: OnSaveRolesListener) {}
}