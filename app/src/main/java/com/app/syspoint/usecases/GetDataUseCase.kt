package com.app.syspoint.usecases

import android.os.Build
import android.os.Looper
import android.util.Log
import com.app.syspoint.interactor.product.GetProductInteractor.OnGetProductsListener
import com.app.syspoint.interactor.roles.RolInteractor.OnGetAllRolesListener
import com.app.syspoint.models.Resource
import com.app.syspoint.repository.objectBox.dao.*
import com.app.syspoint.repository.objectBox.entities.*
import com.app.syspoint.repository.request.*
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.awaitResponse
import java.text.SimpleDateFormat

class GetDataUseCase {
    var getCharge = false
    var getEmployees = false
    var getProducts = false
    var getRoles = false
    var getPrices = false

    suspend operator fun invoke(): Flow<Resource<Boolean>> = callbackFlow {
        trySend(Resource.Loading)

        val chargeCallback = RequestCharge.requestGetCharge2()
        val requestCharge = async {
            val response = chargeCallback.awaitResponse()
            if (response.isSuccessful) {
                val chargeList = arrayListOf<ChargeBox>()
                val stockId = StockDao().getCurrentStockId()
                val chargeDao = ChargeDao()

                response.body()!!.payments!!.map {item ->
                    val charge = chargeDao.getByCobranza(item!!.cobranza)
                    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    if (charge == null) {
                        val chargeBox = ChargeBox()
                        chargeBox.cobranza = item.cobranza
                        chargeBox.cliente = item.cuenta
                        chargeBox.importe = item.importe
                        chargeBox.saldo = item.saldo
                        chargeBox.venta = item.venta
                        chargeBox.estado = item.estado
                        chargeBox.observaciones = item.observaciones
                        chargeBox.fecha = item.fecha
                        chargeBox.hora = item.hora
                        chargeBox.empleado = item.identificador
                        chargeBox.updatedAt = formatter.parse(item.updatedAt)
                        chargeBox.stockId = stockId
                        chargeDao.insert(chargeBox)
                        chargeList.add(chargeBox)
                    } else {

                        val update = if (charge.updatedAt != null && !formatter.format(charge.updatedAt).isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                            val dateItem = try {
                                formatter.parse(item.updatedAt)
                            } catch (e: Exception) {
                                formatter.parse(item.updatedAt + "00:00:00")
                            }
                            val dateBean = try {
                                charge.updatedAt
                            } catch (e: Exception) {
                                formatter.parse(formatter.format(charge.updatedAt) + "00:00:00")
                            }
                            dateItem?.compareTo(dateBean) ?: 1
                        } else 1

                        if (update > 0) {
                            charge.cobranza = item.cobranza
                            charge.cliente = item.cuenta
                            charge.importe = item.importe
                            charge.saldo = item.saldo
                            charge.venta = item.venta
                            charge.estado = item.estado
                            charge.observaciones = item.observaciones
                            charge.fecha = item.fecha
                            charge.hora = item.hora
                            charge.empleado = item.identificador
                            charge.updatedAt = formatter.parse(item.updatedAt)
                            charge.stockId = stockId
                            chargeDao.insert(charge)
                        }
                        chargeList.add(charge)
                    }
                }

            }

            getCharge = true
            if (getCharge && getEmployees && getProducts && getRoles) {
                trySend(Resource.Finished)
            } else {}

        }

        val employeesCallback = RequestEmployees.requestEmployees()
        val requestEmployees = async {

            val response = employeesCallback.awaitResponse()
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
                            employeeDao.insert(employeeBox)
                        }
                        employees.add(employeeBox)
                    }
                }
            }

            getEmployees = true
            if (getCharge && getEmployees && getProducts && getRoles)
                trySend(Resource.Finished)

        }

        val productCallback = RequestProducts.requestProducts()
        val requestProducts = async {
            val response = productCallback.awaitResponse()
            if (response.isSuccessful) {
                val products = arrayListOf<ProductBox>()
                val productDao = ProductDao()
                response.body()!!.products!!.map {items ->
                    val productBean = productDao.getProductoByArticulo(items!!.articulo)
                    if (productBean == null) {
                        //Creamos el producto
                        val producto = ProductBox()
                        val dao = ProductDao()
                        producto.articulo = items.articulo
                        producto.descripcion = items.descripcion
                        producto.status = items.status
                        producto.precio = items.precio
                        producto.iva = items.iva
                        producto.codigo_barras = items.codigoBarras
                        producto.path_img = items.pathImage
                        producto.updatedAt = items.updatedAt
                        dao.insert(producto)
                        products.add(producto)
                    } else {
                        val update = if (!productBean.updatedAt.isNullOrEmpty() && !items.updatedAt.isNullOrEmpty()) {
                            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val dateItem = formatter.parse(items.updatedAt)
                            val dateBean = formatter.parse(productBean.updatedAt)
                            dateItem?.compareTo(dateBean) ?: 1
                        } else 1

                        if (update > 0) {
                            productBean.articulo = items.articulo
                            productBean.descripcion = items.descripcion
                            productBean.status = items.status
                            productBean.precio = items.precio
                            productBean.iva = items.iva
                            productBean.codigo_barras = items.codigoBarras
                            productBean.path_img = items.pathImage
                            productBean.updatedAt = items.updatedAt
                            productDao.insert(productBean)
                        }
                        products.add(productBean)
                    }
                }
            }
            getProducts = true
            if (getCharge && getEmployees && getProducts && getRoles)
                trySend(Resource.Finished)
        }

        val rolesCallback = RequestRol.requestAllRoles()
        val requestRoles = async {
            val response = rolesCallback.awaitResponse()
            if (response.isSuccessful) {
                val roles = arrayListOf<RolesBox>()
                val rolesDao = RolesDao()
                val employeeDao = EmployeeDao()

                response.body()!!.roles!!.map { rol ->
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
                getRoles = true
                if (getCharge && getEmployees && getProducts && getRoles)
                    trySend(Resource.Finished)
            }
        }

        val pricesCallback = RequestPrice.requestAllPrices()
        val requestPrice = async {
            val response = pricesCallback.awaitResponse()

            if (response.isSuccessful) {
                val priceList = arrayListOf<SpecialPricesBox>()

                val clientDao = ClientDao()
                val productDao = ProductDao()
                val specialPricesDao = SpecialPricesDao()

                response.body()!!.prices!!.map {item ->
                    val clienteBean = clientDao.getClientByAccount(item!!.cliente) ?: return@map
                    val productoBean = productDao.getProductoByArticulo(item.articulo) ?: return@map
                    val preciosEspecialesBean = specialPricesDao.getPrecioEspeciaPorCliente(
                        productoBean.articulo,
                        clienteBean.cuenta
                    )

                    if (preciosEspecialesBean == null) {
                        val bean = SpecialPricesBox()
                        bean.cliente = clienteBean.cuenta
                        bean.articulo = productoBean.articulo
                        bean.precio = item.precio
                        bean.active = item.active == 1
                        specialPricesDao.insert(bean)
                        priceList.add(bean)
                    } else {
                        preciosEspecialesBean.cliente = clienteBean.cuenta
                        preciosEspecialesBean.articulo = productoBean.articulo
                        preciosEspecialesBean.precio = item.precio
                        preciosEspecialesBean.active = item.active == 1
                        specialPricesDao.insert(preciosEspecialesBean)
                        priceList.add(preciosEspecialesBean)
                    }
                }
            }

            getPrices = true
        }


        try {
            val awaitRequestCharge = requestCharge.await()
            val awaitRequestEmployees = requestEmployees.await()
            val awaitRequestProducts = requestProducts.await()
            val awaitRequestRoles = requestRoles.await()
            val awaitRequestPrice = requestPrice.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        awaitClose {
            chargeCallback.cancel()
            employeesCallback.cancel()
            productCallback.cancel()
            rolesCallback.cancel()
            pricesCallback.cancel()
        }
    }
}