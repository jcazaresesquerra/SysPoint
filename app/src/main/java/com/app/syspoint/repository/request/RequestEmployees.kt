package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.employee.GetEmployeeInteractor
import com.app.syspoint.models.Employee
import com.app.syspoint.models.json.EmployeeJson
import com.app.syspoint.repository.database.bean.EmpleadoBean
import com.app.syspoint.repository.database.dao.EmployeeDao
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestEmployees {
    companion object {
        fun requestEmployees(getEmployeesListener: GetEmployeeInteractor.GetEmployeesListener) {
            val getEmployees: Call<EmployeeJson> = ApiServices.getClientRetrofit()
                .create(
                    PointApi::class.java
                ).getAllEmpleados()

            getEmployees.enqueue(object: Callback<EmployeeJson> {
                override fun onResponse(call: Call<EmployeeJson>, response: Response<EmployeeJson>) {
                    if (response.isSuccessful){
                        val employees = arrayListOf<EmpleadoBean?>()
                        val dao = EmployeeDao()

                        for (item in response.body()!!.employees!!) {

                            //Validamos si existe el empleado en la base de datos en base al identificador
                            val employeeBean = dao.getEmployeeByIdentifier(item!!.identificador)

                            //NO existe entonces lo creamos
                            if (employeeBean == null) {
                                val employee = EmpleadoBean()
                                val employeeDao = EmployeeDao()
                                employee.setNombre(item.nombre)
                                employee.setDireccion(item.direccion)
                                employee.setEmail(item.email)
                                employee.setTelefono(item.telefono)
                                employee.setFecha_nacimiento(item.fechaNacimiento)
                                employee.setFecha_ingreso(item.fechaIngreso)
                                employee.setContrasenia(item.contrasenia)
                                employee.setIdentificador(item.identificador)
                                employee.setPath_image(item.pathImage)
                                employee.setRute(item.rute)
                                employee.setStatus(item.status == 1)
                                employeeDao.insert(employee)
                                employees.add(employee)
                            } else {
                                employeeBean.setNombre(item.nombre)
                                employeeBean.setDireccion(item.direccion)
                                employeeBean.setEmail(item.email)
                                employeeBean.setTelefono(item.telefono)
                                employeeBean.setFecha_nacimiento(item.fechaNacimiento)
                                employeeBean.setFecha_ingreso(item.fechaIngreso)
                                employeeBean.setContrasenia(item.contrasenia)
                                employeeBean.setIdentificador(item.identificador)
                                employeeBean.setPath_image(item.pathImage)
                                employeeBean.setRute(item.rute)
                                employeeBean.setStatus(item.status == 1)
                                dao.save(employeeBean)
                                employees.add(employeeBean)
                            }
                        }
                        getEmployeesListener.onGetEmployeesSuccess(employees)
                    } else {
                        getEmployeesListener.onGetEmployeesError()
                    }
                }

                override fun onFailure(call: Call<EmployeeJson>, t: Throwable) {
                    getEmployeesListener.onGetEmployeesError()
                }
            })
        }


        fun saveEmployee(employeeList: List<Employee>, onSaveEmployeeListener: GetEmployeeInteractor.SaveEmployeeListener) {

            val employeeJson = EmployeeJson()
            employeeJson.employees = employeeList
            val json = Gson().toJson(employeeJson)
            Log.d("SinEmpleados", json)

            val sendEmployees = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).sendEmpleado(employeeJson)

            sendEmployees.enqueue(object : Callback<EmployeeJson> {
                override fun onResponse(call: Call<EmployeeJson>, response: Response<EmployeeJson>) {
                    if(response.isSuccessful){
                        onSaveEmployeeListener.onSaveEmployeeSuccess()
                    } else {
                        onSaveEmployeeListener.onSaveEmployeeError()
                    }
                }

                override fun onFailure(call: Call<EmployeeJson>, t: Throwable) {
                    onSaveEmployeeListener.onSaveEmployeeError()
                }
            })

        }
    }
}