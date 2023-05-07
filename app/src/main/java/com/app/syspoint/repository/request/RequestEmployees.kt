package com.app.syspoint.repository.request

import android.util.Log
import com.app.syspoint.interactor.employee.GetEmployeeInteractor
import com.app.syspoint.models.Employee
import com.app.syspoint.models.json.EmployeeJson
import com.app.syspoint.repository.objectBox.dao.EmployeeDao
import com.app.syspoint.repository.objectBox.entities.EmployeeBox
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class RequestEmployees {
    companion object {

        fun requestEmployees(): Call<EmployeeJson> {
            return ApiServices.getClientRetrofit()
                .create(
                    PointApi::class.java
                ).getAllEmpleados()
        }

        fun requestEmployees(getEmployeesListener: GetEmployeeInteractor.GetEmployeesListener): Call<EmployeeJson> {
            val getEmployees: Call<EmployeeJson> = ApiServices.getClientRetrofit()
                .create(
                    PointApi::class.java
                ).getAllEmpleados()

            getEmployees.enqueue(object: Callback<EmployeeJson> {
                override fun onResponse(call: Call<EmployeeJson>, response: Response<EmployeeJson>) {
                    if (response.isSuccessful){
                        val employees = arrayListOf<EmployeeBox?>()
                        val employeeDao = EmployeeDao()

                        response.body()!!.employees!!.map { item ->
                            //Validamos si existe el empleado en la base de datos en base al identificador
                            val employeeBox = employeeDao.getEmployeeByIdentifier(item!!.identificador)
                            //NO existe entonces lo creamos
                            if (employeeBox == null) {
                                val employee = EmployeeBox()
                                employee.nombre = item.nombre
                                employee.direccion = item.direccion
                                employee.email = item.email
                                employee.telefono = item.telefono
                                employee.fecha_nacimiento = item.fechaNacimiento
                                employee.fecha_ingreso = item.fechaIngreso
                                employee.contrasenia = item.contrasenia
                                employee.identificador = item.identificador
                                employee.path_image = item.pathImage
                                employee.rute = item.rute
                                employee.status = item.status == 1
                                employee.updatedAt = item.updatedAt
                                employeeDao.insert(employee)
                                employees.add(employee)
                            } else {

                                val update = if (!employeeBox.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                    Log.d("SysPoint", item.updatedAt!! + "  --  " + item.id)
                                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                    val dateItem = formatter.parse(item.updatedAt)
                                    val dateBean = formatter.parse(employeeBox.updatedAt)
                                    dateItem?.compareTo(dateBean) ?: 1
                                } else 1

                                if (update > 0) {
                                    employeeBox.nombre = item.nombre
                                    employeeBox.direccion = item.direccion
                                    employeeBox.email = item.email
                                    employeeBox.telefono = item.telefono
                                    employeeBox.fecha_nacimiento = item.fechaNacimiento
                                    employeeBox.fecha_ingreso = item.fechaIngreso
                                    employeeBox.contrasenia = item.contrasenia
                                    employeeBox.identificador = item.identificador
                                    employeeBox.path_image = item.pathImage
                                    employeeBox.rute = item.rute
                                    employeeBox.status = item.status == 1
                                    employeeBox.updatedAt = item.updatedAt
                                    employeeDao.insert(employeeBox)
                                }
                                employees.add(employeeBox)
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
            return getEmployees
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
                        val error = response.errorBody()!!.string()
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