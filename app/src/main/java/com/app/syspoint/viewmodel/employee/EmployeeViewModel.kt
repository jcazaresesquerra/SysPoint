package com.app.syspoint.viewmodel.employee

import androidx.core.util.Predicate
import androidx.lifecycle.MutableLiveData
import com.app.syspoint.interactor.employee.GetEmployeeInteractor
import com.app.syspoint.interactor.employee.GetEmployeesInteractorImp
import com.app.syspoint.models.sealed.EmployeeViewState
import com.app.syspoint.repository.database.bean.AppBundle
import com.app.syspoint.repository.database.bean.EmpleadoBean
import com.app.syspoint.repository.database.dao.EmployeeDao
import com.app.syspoint.repository.database.dao.RolesDao
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.viewmodel.BaseViewModel

class EmployeeViewModel: BaseViewModel() {

    val employeeViewState = MutableLiveData<EmployeeViewState>()

    fun checkConnectivity() {
        employeeViewState.postValue(EmployeeViewState.ShowProgressState)

        NetworkStateTask { connected: Boolean ->
            employeeViewState.value = EmployeeViewState.DismissProgressState
            if (!connected) {
                employeeViewState.value = EmployeeViewState.NetworkDisconnectedState
            } else {
                getData()
            }
        }.execute()
    }

    fun setUpEmployees() {
        val data = EmployeeDao().getActiveEmployees()
        employeeViewState.postValue(EmployeeViewState.SetUpEmployeesState(data))
    }

    fun refreshEmployees() {
        val data = EmployeeDao().getActiveEmployees()
        employeeViewState.postValue(EmployeeViewState.RefreshEmployeesState(data))
    }

    fun handleSelection(name: String?, employeeBean: EmpleadoBean) {
        var identificador = ""

        //Obtiene el nombre del vendedor
        var vendedoresBean = AppBundle.getUserBean()
        if (vendedoresBean == null) {
            vendedoresBean = CacheInteractor().getSeller()
        }
        if (vendedoresBean != null) {
            identificador = vendedoresBean.getIdentificador()
        }
        val rolesDao = RolesDao()
        val rolesBean = rolesDao.getRolByEmpleado(identificador, "Empleados")
        if (name == null || name.compareTo("Editar", ignoreCase = true) == 0) {
            if (rolesBean != null && rolesBean.active) {
                    employeeViewState.postValue(EmployeeViewState.ShowEditEmployeeState(employeeBean.identificador))
            } else {
                employeeViewState.postValue(EmployeeViewState.CanNotEditEmployeeState)
            }
        } else if (name.compareTo("Llamar", ignoreCase = true) == 0) {
            employeeViewState.postValue(EmployeeViewState.CallEmployeeState(employeeBean.telefono))
        } else if (name.compareTo("Enviar email", ignoreCase = true) == 0) {
            employeeViewState.postValue(EmployeeViewState.SendEmailState)
        }
    }

    fun getData() {
        employeeViewState.value = EmployeeViewState.LoadingStartState
        GetEmployeesInteractorImp().executeGetEmployees(object: GetEmployeeInteractor.GetEmployeesListener {
            override fun onGetEmployeesSuccess(employees: List<EmpleadoBean?>) {
                employees.toMutableList().removeIf { item -> !item!!.status }
                employeeViewState.value = EmployeeViewState.LoadingFinishState
                employeeViewState.value = EmployeeViewState.GetEmployeesState(employees)

            }

            override fun onGetEmployeesError() {
                employeeViewState.value = EmployeeViewState.LoadingFinishState

            }
        })
    }
}