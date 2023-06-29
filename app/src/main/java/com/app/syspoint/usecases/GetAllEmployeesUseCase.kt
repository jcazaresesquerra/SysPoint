package com.app.syspoint.usecases

import android.util.Log
import com.app.syspoint.models.Resource
import com.app.syspoint.repository.objectBox.dao.EmployeeDao
import com.app.syspoint.repository.objectBox.entities.EmployeeBox
import com.app.syspoint.repository.request.RequestEmployees
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.awaitResponse
import java.text.SimpleDateFormat

const val TAG = "GetAllEmployeesUseCase"
class GetAllEmployeesUseCase {

    suspend operator fun invoke(): Flow<Resource<Boolean>> = callbackFlow {
        trySend(Resource.Loading)

        val call = RequestEmployees.requestAllEmployees()

        val response = call.awaitResponse()

        if (response.isSuccessful) {
            val employees = ArrayList<EmployeeBox>()
            val employeeDao = EmployeeDao()
            response.body()!!.employees!!.map { item ->
                //Validamos si existe el empleado en la base de datos en base al identificador
                val employeeBox = employeeDao.getEmployeeByEmail(item!!.email)
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
                    employee.clientId = item.clientId
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
                        employeeBox.clientId = item.clientId
                        employeeDao.insert(employeeBox)
                    }
                    employees.add(employeeBox)
                }
            }
            trySend(Resource.Success(true))
        } else {
            val error = response.errorBody()!!.string()
            Log.e(TAG, error)
            trySend(Resource.Error(error))
        }

        awaitClose {
            call.cancel()
        }
    }
}