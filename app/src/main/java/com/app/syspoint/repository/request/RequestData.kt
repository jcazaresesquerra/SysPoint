package com.app.syspoint.repository.request

import com.app.syspoint.interactor.data.GetAllDataInteractor
import com.app.syspoint.models.Data
import com.app.syspoint.models.json.BaseBodyJson
import com.app.syspoint.repository.objectBox.dao.ChargeDao
import com.app.syspoint.repository.objectBox.dao.ClientDao
import com.app.syspoint.repository.objectBox.dao.EmployeeDao
import com.app.syspoint.repository.objectBox.dao.ProductDao
import com.app.syspoint.repository.objectBox.dao.RolesDao
import com.app.syspoint.repository.objectBox.dao.SpecialPricesDao
import com.app.syspoint.repository.objectBox.entities.ChargeBox
import com.app.syspoint.repository.objectBox.entities.ClientBox
import com.app.syspoint.repository.objectBox.entities.EmployeeBox
import com.app.syspoint.repository.objectBox.entities.ProductBox
import com.app.syspoint.repository.objectBox.entities.RolesBox
import com.app.syspoint.repository.objectBox.entities.SpecialPricesBox
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class RequestData {
    companion object: BaseRequest() {
        fun requestAllData(onGetAllDataListener: GetAllDataInteractor.OnGetAllDataListener) {
            val employee = getEmployee()

            val baseBodyJson = BaseBodyJson()
            baseBodyJson.clientId = employee?.clientId?: "tenet"

            val call: Call<Data> = ApiServices.getClientRetrofit()
                .create(
                    PointApi::class.java
                ).getAllDataV2(baseBodyJson)

            call.enqueue(object: Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            var employeeDao = EmployeeDao()
                            var employee = EmployeeBox()

                            response.body()!!.data.empleados.map {
                                val employeeBean = employeeDao.getEmployeeByIdentifier(it.identificador)
                                if (employeeBean == null) {
                                    employee = EmployeeBox()
                                    employeeDao = EmployeeDao()
                                    employee.nombre = it.nombre!!
                                    employee.direccion = it.direccion!!
                                    employee.email = it.email!!
                                    employee.telefono = it.telefono!!
                                    employee.fecha_nacimiento = it.fechaNacimiento!!
                                    employee.fecha_ingreso = it.fechaIngreso!!
                                    employee.contrasenia = it.contrasenia!!
                                    employee.identificador = it.identificador!!
                                    employee.path_image = it.pathImage ?: ""
                                    employee.rute = it.rute!!
                                    employee.status = it.status == 1
                                    employeeDao.insert(employee)
                                } else {
                                    employeeBean.nombre = it.nombre!!
                                    employeeBean.direccion = it.direccion!!
                                    employeeBean.email = it.email!!
                                    employeeBean.telefono = it.telefono!!
                                    employeeBean.fecha_nacimiento = it.fechaNacimiento!!
                                    employeeBean.fecha_ingreso = it.fechaIngreso!!
                                    employeeBean.contrasenia = it.contrasenia!!
                                    employeeBean.identificador = it.identificador!!
                                    employeeBean.path_image = it.pathImage?: ""
                                    employeeBean.rute = it.rute!!
                                    employeeBean.status = it.status == 1
                                    employeeDao.insert(employeeBean)
                                }
                            }

                            var rolesDao = RolesDao()
                            var bean = RolesBox()
                            //Contiene la lista de permidos

                            response.body()!!.data.roles.map {
                                val rolesBean = rolesDao.getRolByModule(it.empleado, it.modulo)
                                val empleadoBean = employeeDao.getEmployeeByIdentifier(it.empleado)

                                if (rolesBean == null) {
                                    rolesDao = RolesDao()
                                    bean = RolesBox()
                                    bean.empleado.target = empleadoBean
                                    bean.modulo = it.modulo
                                    bean.active = it.activo == 1
                                    bean.identificador = it.empleado
                                    rolesDao.insert(bean)
                                } else {
                                    rolesBean.empleado.target = empleadoBean
                                    rolesBean.modulo = it.modulo
                                    rolesBean.active = it.activo == 1
                                    rolesBean.identificador = it.empleado
                                    rolesDao.insert(rolesBean)
                                }
                            }

                            var productDao = ProductDao()
                            var producto = ProductBox()
                            //Contiene la lista de productos

                            response.body()!!.data.productos.map {
                                val productoBean = productDao.getProductoByArticulo(it.articulo)
                                if (productoBean == null) {
                                    //Creamos el producto
                                    producto = ProductBox()
                                    productDao = ProductDao()
                                    producto.articulo = it.articulo
                                    producto.descripcion = it.descripcion
                                    producto.status = it.status
                                    producto.precio = it.precio
                                    producto.iva = it.iva
                                    producto.codigo_barras = it.codigoBarras
                                    producto.path_img = it.pathImage
                                    productDao.insert(producto)
                                } else {
                                    productoBean.articulo = it.articulo
                                    productoBean.descripcion = it.descripcion
                                    productoBean.status = it.status
                                    productoBean.precio = it.precio
                                    productoBean.iva = it.iva
                                    productoBean.codigo_barras = it.codigoBarras
                                    productoBean.path_img = it.pathImage
                                    productDao.insert(productoBean)
                                }
                            }

                            var clientDao = ClientDao()
                            var clienteBean = ClientBox()

                            response.body()!!.data.clientes.map {
                                val bean = clientDao.getClientByAccount(it.cuenta)
                                if (bean == null) {
                                    clienteBean = ClientBox()
                                    clientDao = ClientDao()
                                    clienteBean.nombre_comercial = it.nombreComercial
                                    clienteBean.calle = it.calle
                                    clienteBean.numero = it.numero
                                    clienteBean.colonia = it.colonia
                                    clienteBean.ciudad = it.ciudad
                                    clienteBean.codigo_postal = it.codigoPostal
                                    clienteBean.fecha_registro = it.fechaRegistro
                                    clienteBean.cuenta = it.cuenta
                                    clienteBean.status = it.status == 1
                                    clienteBean.consec = it.consec ?: "0"
                                    clienteBean.visitado = 0
                                    clienteBean.rango = it.rango
                                    clienteBean.lun = it.lun
                                    clienteBean.mar = it.mar
                                    clienteBean.mie = it.mie
                                    clienteBean.jue = it.jue
                                    clienteBean.vie = it.vie
                                    clienteBean.sab = it.sab
                                    clienteBean.dom = it.dom
                                    clienteBean.lunOrder = it.lunOrder
                                    clienteBean.marOrder = it.marOrder
                                    clienteBean.mieOrder = it.mieOrder
                                    clienteBean.jueOrder = it.jueOrder
                                    clienteBean.vieOrder = it.vieOrder
                                    clienteBean.sabOrder = it.sabOrder
                                    clienteBean.domOrder = it.domOrder
                                    clienteBean.latitud = it.latitud
                                    clienteBean.longitud = it.longitud
                                    clienteBean.contacto_phone = it.phone_contacto
                                    clienteBean.recordatorio = it.recordatorio
                                    clienteBean.visitasNoefectivas = it.visitas
                                    clienteBean.isCredito = it.isCredito == 1
                                    clienteBean.limite_credito = it.limite_credito
                                    clienteBean.saldo_credito = it.saldo_credito
                                    clienteBean.matriz = it.matriz
                                    clienteBean.updatedAt = it.updatedAt
                                    clientDao.insert(clienteBean)
                                } else {
                                    val update = if (!bean.updatedAt.isNullOrEmpty() && !it.updatedAt.isNullOrEmpty()) {
                                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val dateItem = formatter.parse(it.updatedAt)
                                        val dateBean = formatter.parse(bean.updatedAt)
                                        dateItem?.compareTo(dateBean) ?: 1
                                    } else 1

                                    if (update > 0) {
                                        bean.nombre_comercial = it.nombreComercial
                                        bean.calle = it.calle
                                        bean.numero = it.numero
                                        bean.colonia = it.colonia
                                        bean.ciudad = it.ciudad
                                        bean.codigo_postal = it.codigoPostal
                                        bean.fecha_registro = it.fechaRegistro
                                        bean.cuenta = it.cuenta
                                        bean.status = it.status == 1
                                        bean.consec = it.consec ?: "0"
                                        bean.visitado = if (bean.visitado == 1) 1 else 0
                                        bean.rango = it.rango
                                        bean.lun = it.lun
                                        bean.mar = it.mar
                                        bean.mie = it.mie
                                        bean.jue = it.jue
                                        bean.vie = it.vie
                                        bean.sab = it.sab
                                        bean.dom = it.dom
                                        bean.lunOrder = it.lunOrder
                                        bean.marOrder = it.marOrder
                                        bean.mieOrder = it.mieOrder
                                        bean.jueOrder = it.jueOrder
                                        bean.vieOrder = it.vieOrder
                                        bean.sabOrder = it.sabOrder
                                        bean.domOrder = it.domOrder
                                        bean.latitud = it.latitud
                                        bean.longitud = it.longitud
                                        bean.contacto_phone = it.phone_contacto
                                        bean.recordatorio = it.recordatorio
                                        bean.visitasNoefectivas = it.visitas
                                        bean.isCredito = it.isCredito == 1
                                        bean.limite_credito = it.limite_credito
                                        bean.saldo_credito = it.saldo_credito
                                        bean.matriz = it.matriz
                                        bean.updatedAt = it.updatedAt
                                        clientDao.insert(bean)
                                    }
                                }
                            }

                            val specialPricesDao = SpecialPricesDao()

                            response.body()!!.data.precios.map { item ->
                                val preciosEspecialesBean =
                                    specialPricesDao.getPrecioEspeciaPorCliente(
                                        item.articulo,
                                        item.cliente
                                    )

                                //Si no hay precios especiales entonces crea un precio
                                if (preciosEspecialesBean == null) {
                                    val bean = SpecialPricesBox()
                                    bean.cliente = item.cliente
                                    bean.articulo = item.articulo
                                    bean.precio = item.precio
                                    bean.active = item.active == 1
                                    specialPricesDao.insert(bean)
                                } else {
                                    preciosEspecialesBean.cliente = item.cliente
                                    preciosEspecialesBean.articulo = item.articulo
                                    preciosEspecialesBean.precio = item.precio
                                    preciosEspecialesBean.active = item.active == 1
                                    specialPricesDao.insert(preciosEspecialesBean)
                                }
                            }
                        }
                        onGetAllDataListener.onGetAllDataSuccess()
                    } else {
                        val error = response.errorBody()
                        onGetAllDataListener.onGetAllDataError()
                    }
                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                    onGetAllDataListener.onGetAllDataError()
                }
            })
        }

        suspend fun requestAllData2(onGetAllDataListener: GetAllDataInteractor.OnGetAllDataInServiceListener) {
            val employee = getEmployee()

            val baseBodyJson = BaseBodyJson()
            baseBodyJson.clientId = employee?.clientId?: "tenet"

            val call: Call<Data> = ApiServices.getClientRetrofit()
                .create(
                    PointApi::class.java
                ).getAllDataV2(baseBodyJson)

            call.enqueue(object: Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {

                            var dao = EmployeeDao()
                            var employee = EmployeeBox()
                            response.body()!!.data.empleados.map {
                                val employeeBean = dao.getEmployeeByIdentifier(it.identificador)
                                if (employeeBean == null) {
                                    employee = EmployeeBox()
                                    dao = EmployeeDao()
                                    employee.nombre = (it.nombre)
                                    employee.direccion = (it.direccion)
                                    employee.email = (it.email)
                                    employee.telefono = (it.telefono)
                                    employee.fecha_nacimiento = (it.fechaNacimiento)
                                    employee.fecha_ingreso = (it.fechaIngreso)
                                    employee.contrasenia = (it.contrasenia)
                                    employee.identificador = (it.identificador)
                                    employee.path_image = (it.pathImage)
                                    employee.rute = (it.rute)
                                    employee.status = (it.status == 1)
                                    dao.insert(employee)
                                } else {
                                    employeeBean.nombre = (it.nombre)
                                    employeeBean.direccion = (it.direccion)
                                    employeeBean.email = (it.email)
                                    employeeBean.telefono = (it.telefono)
                                    employeeBean.fecha_nacimiento = (it.fechaNacimiento)
                                    employeeBean.fecha_ingreso = (it.fechaIngreso)
                                    employeeBean.contrasenia = (it.contrasenia)
                                    employeeBean.identificador = (it.identificador)
                                    employeeBean.path_image = (it.pathImage)
                                    employeeBean.rute = (it.rute)
                                    employeeBean.status = (it.status == 1)
                                    dao.insert(employeeBean)
                                }
                            }

                            var rolesDao = RolesDao()
                            var bean = RolesBox()
                            val employeeDao = EmployeeDao()
                            //Contiene la lista de permidos
                            response.body()!!.data.roles.map {
                                val rolesBean = rolesDao.getRolByModule(it.empleado, it.modulo)
                                val empleadoBean = employeeDao.getEmployeeByIdentifier(it.empleado)

                                if (rolesBean == null) {
                                    rolesDao = RolesDao()
                                    bean = RolesBox()
                                    bean.empleado!!.target = empleadoBean
                                    bean.modulo = it.modulo
                                    bean.active = it.activo == 1
                                    bean.identificador = it.empleado
                                    rolesDao.insert(bean)
                                } else {
                                    rolesBean.empleado!!.target = empleadoBean
                                    rolesBean.modulo = it.modulo
                                    rolesBean.active = it.activo == 1
                                    rolesBean.identificador = it.empleado
                                    rolesDao.insert(rolesBean)
                                }
                            }

                            var productDao = ProductDao()
                            var producto = ProductBox()
                            //Contiene la lista de productos
                            response.body()!!.data.productos.map {
                                val productoBean = productDao.getProductoByArticulo(it.articulo)
                                if (productoBean == null) {
                                    //Creamos el producto
                                    producto = ProductBox()
                                    productDao = ProductDao()
                                    producto.articulo = it.articulo
                                    producto.descripcion = it.descripcion
                                    producto.status = it.status
                                    producto.precio = it.precio
                                    producto.iva = it.iva
                                    producto.codigo_barras = it.codigoBarras
                                    producto.path_img = it.pathImage
                                    productDao.insert(producto)
                                } else {
                                    productoBean.articulo = it.articulo
                                    productoBean.descripcion = it.descripcion
                                    productoBean.status = it.status
                                    productoBean.precio = it.precio
                                    productoBean.iva = it.iva
                                    productoBean.codigo_barras = it.codigoBarras
                                    productoBean.path_img = it.pathImage
                                    productDao.insert(productoBean)
                                }
                            }

                            var clientDao = ClientDao()
                            var clienteBean = ClientBox()

                            response.body()!!.data.clientes.map {
                                val bean = clientDao.getClientByAccount(it.cuenta)
                                if (bean == null) {
                                    clienteBean = ClientBox()
                                    clientDao = ClientDao()
                                    clienteBean.nombre_comercial = it.nombreComercial
                                    clienteBean.calle = it.calle
                                    clienteBean.numero = it.numero
                                    clienteBean.colonia = it.colonia
                                    clienteBean.ciudad = it.ciudad
                                    clienteBean.codigo_postal = it.codigoPostal
                                    clienteBean.fecha_registro = it.fechaRegistro
                                    clienteBean.cuenta = it.cuenta
                                    clienteBean.status = it.status == 1
                                    clienteBean.consec = it.consec ?: "0"
                                    clienteBean.visitado = 0
                                    clienteBean.rango = it.rango
                                    clienteBean.lun = it.lun
                                    clienteBean.mar = it.mar
                                    clienteBean.mie = it.mie
                                    clienteBean.jue = it.jue
                                    clienteBean.vie = it.vie
                                    clienteBean.sab = it.sab
                                    clienteBean.dom = it.dom
                                    clienteBean.lunOrder = it.lunOrder
                                    clienteBean.marOrder = it.marOrder
                                    clienteBean.mieOrder = it.mieOrder
                                    clienteBean.jueOrder = it.jueOrder
                                    clienteBean.vieOrder = it.vieOrder
                                    clienteBean.sabOrder = it.sabOrder
                                    clienteBean.domOrder = it.domOrder
                                    clienteBean.latitud = it.latitud
                                    clienteBean.longitud = it.longitud
                                    clienteBean.contacto_phone = it.phone_contacto
                                    clienteBean.recordatorio = it.recordatorio
                                    clienteBean.visitasNoefectivas = it.visitas
                                    clienteBean.isCredito = it.isCredito == 1
                                    clienteBean.limite_credito = it.limite_credito
                                    clienteBean.saldo_credito = it.saldo_credito
                                    clienteBean.matriz = it.matriz
                                    clienteBean.updatedAt = it.updatedAt
                                    clientDao.insert(clienteBean)
                                } else {
                                    val update = if (!bean.updatedAt.isNullOrEmpty() && !it.updatedAt.isNullOrEmpty()) {
                                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val dateItem = formatter.parse(it.updatedAt)
                                        val dateBean = formatter.parse(bean.updatedAt)
                                        dateItem?.compareTo(dateBean) ?: 1
                                    } else 1

                                    if (update > 0) {
                                        bean.nombre_comercial = it.nombreComercial
                                        bean.calle = it.calle
                                        bean.numero = it.numero
                                        bean.colonia = it.colonia
                                        bean.ciudad = it.ciudad
                                        bean.codigo_postal = it.codigoPostal
                                        bean.fecha_registro = it.fechaRegistro
                                        bean.cuenta = it.cuenta
                                        bean.status = it.status == 1
                                        bean.consec = it.consec ?: "0"
                                        bean.visitado = if (bean.visitado == 1) 1 else 0
                                        bean.rango = it.rango
                                        bean.lun = it.lun
                                        bean.mar = it.mar
                                        bean.mie = it.mie
                                        bean.jue = it.jue
                                        bean.vie = it.vie
                                        bean.sab = it.sab
                                        bean.dom = it.dom
                                        bean.lunOrder = it.lunOrder
                                        bean.marOrder = it.marOrder
                                        bean.mieOrder = it.mieOrder
                                        bean.jueOrder = it.jueOrder
                                        bean.vieOrder = it.vieOrder
                                        bean.sabOrder = it.sabOrder
                                        bean.domOrder = it.domOrder
                                        bean.latitud = it.latitud
                                        bean.longitud = it.longitud
                                        bean.contacto_phone = it.phone_contacto
                                        bean.recordatorio = it.recordatorio
                                        bean.visitasNoefectivas = it.visitas
                                        bean.isCredito = it.isCredito == 1
                                        bean.limite_credito = it.limite_credito
                                        bean.saldo_credito = it.saldo_credito
                                        bean.matriz = it.matriz
                                        bean.updatedAt = it.updatedAt
                                        clientDao.insert(bean)
                                    }
                                }
                            }

                            val specialPricesDao = SpecialPricesDao()
                            response.body()!!.data.precios.map { item ->
                                val preciosEspecialesBean =
                                    specialPricesDao.getPrecioEspeciaPorCliente(
                                        item.articulo,
                                        item.cliente
                                    )

                                //Si no hay precios especiales entonces crea un precio
                                if (preciosEspecialesBean == null) {
                                    val bean = SpecialPricesBox()
                                    bean.cliente = item.cliente
                                    bean.articulo = item.articulo
                                    bean.precio = item.precio
                                    bean.active = item.active == 1
                                    specialPricesDao.insert(bean)
                                } else {
                                    preciosEspecialesBean.cliente = item.cliente
                                    preciosEspecialesBean.articulo = item.articulo
                                    preciosEspecialesBean.precio = item.precio
                                    preciosEspecialesBean.active = item.active == 1
                                    specialPricesDao.insert(preciosEspecialesBean)
                                }
                            }
                        }
                        onGetAllDataListener.onGetAllDataSuccess()
                    } else {
                        val error = response.errorBody()
                        onGetAllDataListener.onGetAllDataError()
                    }
                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                    onGetAllDataListener.onGetAllDataError()
                }
            })
        }

        fun requestAllDataByDate(onGetAllDataByDateListener: GetAllDataInteractor.OnGetAllDataByDateListener) {
            val employee = getEmployee()
            val baseBodyJson = BaseBodyJson()
            baseBodyJson.clientId = employee?.clientId ?: "tenet"
            val getDataByDate = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getAllDataByDate(baseBodyJson)

            getDataByDate.enqueue(object : Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    if (response.isSuccessful) {

                        if (response.body() == null) {
                            onGetAllDataByDateListener.onGetAllDataByDateError()
                        }

                        if (response.code() == 200) {
                            val employeeDao = EmployeeDao()
                            response.body()!!.data.empleados.map {item ->
                                val empleadoBean = employeeDao.getEmployeeByIdentifier(item.identificador)

                                if (empleadoBean == null) {
                                    val empleado = EmployeeBox()
                                    empleado.nombre = (item.nombre)
                                    empleado.direccion = (item.direccion)
                                    empleado.email = (item.email)
                                    empleado.telefono = (item.telefono)
                                    empleado.fecha_nacimiento = (item.fechaNacimiento)
                                    empleado.fecha_ingreso = (item.fechaIngreso)
                                    empleado.contrasenia = (item.contrasenia)
                                    empleado.identificador = (item.identificador)
                                    empleado.path_image = (item.pathImage)
                                    empleado.rute = (item.rute)
                                    empleado.status = (item.status == 1)
                                    employeeDao.insert(empleado)
                                } else {
                                    empleadoBean.nombre = (item.nombre)
                                    empleadoBean.direccion = (item.direccion)
                                    empleadoBean.email = (item.email)
                                    empleadoBean.telefono = (item.telefono)
                                    empleadoBean.fecha_nacimiento = (item.fechaNacimiento)
                                    empleadoBean.fecha_ingreso = (item.fechaIngreso)
                                    empleadoBean.contrasenia = (item.contrasenia)
                                    empleadoBean.identificador = (item.identificador)
                                    empleadoBean.path_image = (item.pathImage)
                                    empleadoBean.rute = (item.rute)
                                    empleadoBean.status = (item.status == 1)
                                    employeeDao.insert(empleadoBean)
                                }
                            }


                            val rolesDao = RolesDao()
                            response.body()!!.data.roles.map {item ->
                                val rolesBean = rolesDao.getRolByModule(item.empleado, item.modulo)
                                if (rolesBean == null) {
                                    val bean = RolesBox()
                                    val empleadoBean = employeeDao.getEmployeeByIdentifier(item.empleado)
                                    bean.empleado!!.target = empleadoBean
                                    bean.modulo = item.modulo
                                    bean.active = item.activo == 1
                                    bean.identificador = item.empleado
                                    rolesDao.insert(bean)
                                } else {
                                    val empleadoBean = employeeDao.getEmployeeByIdentifier(item.empleado)
                                    rolesBean.empleado!!.target = empleadoBean
                                    rolesBean.modulo = item.modulo
                                    rolesBean.active = item.activo == 1
                                    rolesBean.identificador = item.empleado
                                    rolesDao.insert(rolesBean)
                                }
                            }

                            val productDao = ProductDao()
                            response.body()!!.data.productos.map {item ->
                                val productoBean = productDao.getProductoByArticulo(item.articulo)
                                if (productoBean == null) {
                                    val producto = ProductBox()
                                    producto.articulo = item.articulo
                                    producto.descripcion = item.descripcion
                                    producto.status = item.status
                                    producto.precio = item.precio
                                    producto.iva = item.iva
                                    producto.codigo_barras = item.codigoBarras
                                    producto.path_img = item.pathImage
                                    productDao.insert(producto)
                                } else {
                                    productoBean.articulo = item.articulo
                                    productoBean.descripcion = item.descripcion
                                    productoBean.status = item.status
                                    productoBean.precio = item.precio
                                    productoBean.iva = item.iva
                                    productoBean.codigo_barras = item.codigoBarras
                                    productoBean.path_img = item.pathImage
                                    productDao.insert(productoBean)
                                }
                            }

                            val clientDao = ClientDao()
                            response.body()!!.data.clientes.map {item ->
                                val bean = clientDao.getClientByAccount(item.cuenta)
                                if (bean == null) {
                                    val clienteBean = ClientBox()
                                    clienteBean.nombre_comercial = item.nombreComercial
                                    clienteBean.calle = item.calle
                                    clienteBean.numero = item.numero
                                    clienteBean.colonia = item.colonia
                                    clienteBean.ciudad = item.ciudad
                                    clienteBean.codigo_postal = item.codigoPostal
                                    clienteBean.fecha_registro = item.fechaRegistro
                                    clienteBean.cuenta = item.cuenta
                                    clienteBean.status = item.status == 1
                                    clienteBean.consec = item.consec ?: "0"
                                    clienteBean.visitado = 0
                                    clienteBean.rango = item.rango
                                    clienteBean.lun = item.lun
                                    clienteBean.mar = item.mar
                                    clienteBean.mie = item.mie
                                    clienteBean.jue = item.jue
                                    clienteBean.vie = item.vie
                                    clienteBean.sab = item.sab
                                    clienteBean.dom = item.dom
                                    clienteBean.latitud = item.latitud
                                    clienteBean.longitud = item.longitud
                                    clienteBean.contacto_phone = item.phone_contacto
                                    clienteBean.recordatorio = item.recordatorio
                                    clienteBean.visitasNoefectivas = item.visitas
                                    clienteBean.isCredito = item.isCredito == 1
                                    clienteBean.limite_credito = item.limite_credito
                                    clienteBean.saldo_credito = item.saldo_credito
                                    clienteBean.matriz = item.matriz
                                    clienteBean.updatedAt = item.updatedAt
                                    clientDao.insert(clienteBean)
                                } else {
                                    val update = if (!bean.updatedAt.isNullOrEmpty() && !item.updatedAt.isNullOrEmpty()) {
                                        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                        val dateItem = formatter.parse(item.updatedAt)
                                        val dateBean = formatter.parse(bean.updatedAt)
                                        dateItem?.compareTo(dateBean) ?: 1
                                    } else 1

                                    if (update > 0) {
                                        bean.nombre_comercial = item.nombreComercial
                                        bean.calle = item.calle
                                        bean.numero = item.numero
                                        bean.colonia = item.colonia
                                        bean.ciudad = item.ciudad
                                        bean.codigo_postal = item.codigoPostal
                                        bean.fecha_registro = item.fechaRegistro
                                        bean.cuenta = item.cuenta
                                        bean.status = item.status == 1
                                        bean.consec = item.consec ?: "0"
                                        bean.visitado = if (bean.visitado == 1) 1 else 0
                                        bean.rango = item.rango
                                        bean.lun = item.lun
                                        bean.mar = item.mar
                                        bean.mie = item.mie
                                        bean.jue = item.jue
                                        bean.vie = item.vie
                                        bean.sab = item.sab
                                        bean.dom = item.dom
                                        bean.latitud = item.latitud
                                        bean.longitud = item.longitud
                                        bean.contacto_phone = item.phone_contacto
                                        bean.recordatorio = item.recordatorio
                                        bean.visitasNoefectivas = item.visitas
                                        bean.isCredito = item.isCredito == 1
                                        bean.limite_credito = item.limite_credito
                                        bean.saldo_credito = item.saldo_credito
                                        bean.matriz = item.matriz
                                        bean.updatedAt = item.updatedAt
                                        clientDao.insert(bean)
                                    }
                                }
                            }


                            val chargeDao = ChargeDao()
                            response.body()!!.data.cobranzas.map {item ->
                                val chargeBox1 = chargeDao.getByCobranza(item.cobranza)
                                if (chargeBox1 == null) {
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
                                    chargeBox.isCheck = false
                                    chargeDao.insert(chargeBox)
                                } else {
                                    chargeBox1.cobranza = item.cobranza
                                    chargeBox1.cliente = item.cuenta
                                    chargeBox1.importe = item.importe
                                    chargeBox1.saldo = item.saldo
                                    chargeBox1.venta = item.venta
                                    chargeBox1.estado = item.estado
                                    chargeBox1.observaciones = item.observaciones
                                    chargeBox1.fecha = item.fecha
                                    chargeBox1.hora = item.hora
                                    chargeBox1.empleado = item.identificador
                                    chargeBox1.isCheck = false
                                    chargeDao.insert(chargeBox1)
                                }
                            }

                            val specialPricesDao = SpecialPricesDao()
                            response.body()!!.data.precios.map {item ->

                                val clienteBean = clientDao.getClientByAccount(item.cliente)
                                if (clienteBean != null) {
                                    val productoBean = productDao.getProductoByArticulo(item.articulo)
                                    if (productoBean != null) {
                                        val preciosEspecialesBean =
                                            specialPricesDao.getPrecioEspeciaPorCliente(
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
                                        } else {
                                            preciosEspecialesBean.cliente = clienteBean.cuenta
                                            preciosEspecialesBean.articulo = productoBean.articulo
                                            preciosEspecialesBean.precio = item.precio
                                            preciosEspecialesBean.active = item.active == 1
                                            specialPricesDao.insert(preciosEspecialesBean)
                                        }
                                    }
                                }
                            }

                            onGetAllDataByDateListener.onGetAllDataByDateSuccess()

                        } else {
                            onGetAllDataByDateListener.onGetAllDataByDateError()
                        }
                    } else {
                        val error = response.errorBody()
                        onGetAllDataByDateListener.onGetAllDataByDateError()
                    }
                }

                override fun onFailure(call: Call<Data>, t: Throwable) {
                    onGetAllDataByDateListener.onGetAllDataByDateError()
                }
            })
        }
    }
}