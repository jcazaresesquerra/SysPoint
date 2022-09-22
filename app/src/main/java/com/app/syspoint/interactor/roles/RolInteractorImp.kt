package com.app.syspoint.interactor.roles

import com.app.syspoint.models.Role
import com.app.syspoint.repository.request.RequestRol
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class RolInteractorImp: RolInteractor() {

    override fun executeGetAllRoles(onGetAllRolesListener: OnGetAllRolesListener) {
        super.executeGetAllRoles(onGetAllRolesListener)
        GlobalScope.launch {
            RequestRol.requestAllRoles(onGetAllRolesListener)
        }
    }

    override fun executeSaveRoles(roles: List<Role>, onSaveRolesListener: OnSaveRolesListener) {
        super.executeSaveRoles(roles, onSaveRolesListener)
        GlobalScope.launch {
            RequestRol.saveRoles(roles, onSaveRolesListener)
        }
    }
}