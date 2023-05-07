package com.app.syspoint.viewmodel.employee

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.syspoint.interactor.employee.GetEmployeeInteractor
import com.app.syspoint.interactor.employee.GetEmployeesInteractorImp
import com.app.syspoint.models.sealed.EmployeeViewState
import com.app.syspoint.repository.objectBox.AppBundle
import com.app.syspoint.utils.NetworkStateTask
import com.app.syspoint.interactor.cache.CacheInteractor
import com.app.syspoint.models.sealed.EmployeeLoadingViewState
import com.app.syspoint.repository.objectBox.dao.EmployeeDao
import com.app.syspoint.repository.objectBox.dao.RolesDao
import com.app.syspoint.repository.objectBox.entities.EmployeeBox
import com.app.syspoint.viewmodel.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EmployeeViewModel: BaseViewModel() {

    val employeeViewState = MutableLiveData<EmployeeViewState>()
    val employeeProgressViewState = MutableLiveData<EmployeeLoadingViewState>()

    fun checkConnectivity() {
        viewModelScope.launch(Dispatchers.IO) {
            employeeProgressViewState.postValue(EmployeeLoadingViewState.LoadingStartState)

            NetworkStateTask { connected: Boolean ->
                employeeProgressViewState.postValue(EmployeeLoadingViewState.LoadingFinishState)
                if (!connected) {
                    employeeViewState.postValue(EmployeeViewState.NetworkDisconnectedState)
                } else {
                    getData()
                }
            }.execute()
        }
    }

    fun setUpEmployees() {
        employeeProgressViewState.value = EmployeeLoadingViewState.LoadingStartState
        viewModelScope.launch(Dispatchers.IO) {
            val data = EmployeeDao().getActiveEmployees()
            employeeViewState.postValue(EmployeeViewState.SetUpEmployeesState(data))
            employeeProgressViewState.postValue(EmployeeLoadingViewState.LoadingFinishState)
        }
    }

    fun refreshEmployees() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = EmployeeDao().getActiveEmployees()
            employeeViewState.postValue(EmployeeViewState.RefreshEmployeesState(data))
        }
    }

    fun handleSelection(name: String?, employeeBean: EmployeeBox) {
        viewModelScope.launch(Dispatchers.IO) {
            var identificador = ""

            //Obtiene el nombre del vendedor
            var vendedoresBean = AppBundle.getUserBox()
            if (vendedoresBean == null) {
                vendedoresBean = CacheInteractor().getSeller()
            }
            if (vendedoresBean != null) {
                identificador = vendedoresBean.identificador!!
            }
            val rolesDao = RolesDao()
            val rolesBean = rolesDao.getRolByEmpleado(identificador, "Empleados")
            if (name == null || name.compareTo("Editar", ignoreCase = true) == 0) {
                if (rolesBean != null && rolesBean.active) {
                    employeeViewState.postValue(EmployeeViewState.ShowEditEmployeeState(employeeBean.identificador!!))
                } else {
                    employeeViewState.postValue(EmployeeViewState.CanNotEditEmployeeState)
                }
            } else if (name.compareTo("Llamar", ignoreCase = true) == 0) {
                employeeViewState.postValue(EmployeeViewState.CallEmployeeState(employeeBean.telefono!!))
            } else if (name.compareTo("Enviar email", ignoreCase = true) == 0) {
                employeeViewState.postValue(EmployeeViewState.SendEmailState)
            }
        }
    }

    fun getData() {
        viewModelScope.launch(Dispatchers.IO) {
            employeeProgressViewState.postValue(EmployeeLoadingViewState.LoadingStartState)
            GetEmployeesInteractorImp().executeGetEmployees(object :
                GetEmployeeInteractor.GetEmployeesListener {
                override fun onGetEmployeesSuccess(employees: List<EmployeeBox?>) {
                    employees.toMutableList().removeIf { item -> !item!!.status }
                    employeeProgressViewState.postValue(EmployeeLoadingViewState.LoadingFinishState)
                    employeeViewState.postValue(EmployeeViewState.GetEmployeesState(employees))
                }

                override fun onGetEmployeesError() {
                    employeeProgressViewState.postValue(EmployeeLoadingViewState.LoadingStartState)
                }
            })
        }
    }
}