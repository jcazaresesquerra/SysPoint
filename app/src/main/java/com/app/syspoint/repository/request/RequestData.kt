package com.app.syspoint.repository.request

import com.app.syspoint.interactor.data.GetAllDataInteractor
import com.app.syspoint.models.*
import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.database.dao.*
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.repository.request.http.PointApi
import kotlinx.coroutines.delay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class RequestData {
    companion object {
        suspend fun requestAllData(onGetAllDataListener: GetAllDataInteractor.OnGetAllDataListener) {
            val call: Call<Data> = ApiServices.getClientRetrofit()
                .create(
                    PointApi::class.java
                ).getAllDataV2()

            call.enqueue(object: Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    if (response.isSuccessful) {
                        if (response.code() == 200) {
                            var dao = EmployeeDao()
                            var employee = EmpleadoBean()
                            response.body()!!.data.empleados.map {
                                val employeeBean = dao.getEmployeeByIdentifier(it.identificador)
                                if (employeeBean == null) {
                                    employee = EmpleadoBean()
                                    dao = EmployeeDao()
                                    employee.setNombre(it.nombre)
                                    employee.setDireccion(it.direccion)
                                    employee.setEmail(it.email)
                                    employee.setTelefono(it.telefono)
                                    employee.setFecha_nacimiento(it.fechaNacimiento)
                                    employee.setFecha_ingreso(it.fechaIngreso)
                                    employee.setContrasenia(it.contrasenia)
                                    employee.setIdentificador(it.identificador)
                                    employee.setPath_image(it.pathImage)
                                    employee.setRute(it.rute)
                                    employee.setStatus(it.status == 1)
                                    dao.insert(employee)
                                } else {
                                    employeeBean.setNombre(it.nombre)
                                    employeeBean.setDireccion(it.direccion)
                                    employeeBean.setEmail(it.email)
                                    employeeBean.setTelefono(it.telefono)
                                    employeeBean.setFecha_nacimiento(it.fechaNacimiento)
                                    employeeBean.setFecha_ingreso(it.fechaIngreso)
                                    employeeBean.setContrasenia(it.contrasenia)
                                    employeeBean.setIdentificador(it.identificador)
                                    employeeBean.setPath_image(it.pathImage)
                                    employeeBean.setRute(it.rute)
                                    employeeBean.setStatus(it.status == 1)
                                    dao.save(employeeBean)
                                }
                            }

                            var rolesDao = RolesDao()
                            var bean = RolesBean()
                            val employeeDao = EmployeeDao()
                            //Contiene la lista de permidos

                            response.body()!!.data.roles.map {
                                val rolesBean = rolesDao.getRolByModule(it.empleado, it.modulo)
                                val empleadoBean = employeeDao.getEmployeeByIdentifier(it.empleado)

                                if (rolesBean == null) {
                                    rolesDao = RolesDao()
                                    bean = RolesBean()
                                    bean.empleado = empleadoBean
                                    bean.modulo = it.modulo
                                    bean.active = it.activo == 1
                                    bean.identificador = it.empleado
                                    rolesDao.insert(bean)
                                } else {
                                    rolesBean.empleado = empleadoBean
                                    rolesBean.modulo = it.modulo
                                    rolesBean.active = it.activo == 1
                                    rolesBean.identificador = it.empleado
                                    rolesDao.save(rolesBean)
                                }
                            }

                            var productDao = ProductDao()
                            var producto = ProductoBean()
                            //Contiene la lista de productos

                            response.body()!!.data.productos.map {
                                val productoBean = productDao.getProductoByArticulo(it.articulo)
                                if (productoBean == null) {
                                    //Creamos el producto
                                    producto = ProductoBean()
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
                                    productDao.save(productoBean)
                                }
                            }

                            var clientDao = ClientDao()
                            var clienteBean = ClienteBean()

                            response.body()!!.data.clientes.map {
                                val bean = clientDao.getClientByAccount(it.cuenta)
                                if (bean == null) {
                                    clienteBean = ClienteBean()
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
                                    clienteBean.consec = it.consec
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
                                    clienteBean.is_credito = it.isCredito == 1
                                    clienteBean.limite_credito = it.limite_credito
                                    clienteBean.saldo_credito = it.saldo_credito
                                    clienteBean.matriz = it.matriz
                                    clientDao.insert(clienteBean)
                                } else {
                                    bean.nombre_comercial = it.nombreComercial
                                    bean.calle = it.calle
                                    bean.numero = it.numero
                                    bean.colonia = it.colonia
                                    bean.ciudad = it.ciudad
                                    bean.codigo_postal = it.codigoPostal
                                    bean.fecha_registro = it.fechaRegistro
                                    bean.cuenta = it.cuenta
                                    bean.status = it.status == 1
                                    bean.consec = it.consec
                                    bean.visitado = if (bean.visitado == 1) 1 else  0
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
                                    bean.is_credito = it.isCredito == 1
                                    bean.limite_credito = it.limite_credito
                                    bean.saldo_credito = it.saldo_credito
                                    bean.matriz = it.matriz
                                    clientDao.save(bean)
                                }
                            }

                            val cobranzaDao = PaymentDao()
                            var cobranzaBean1 = CobranzaBean()

                            /*response.body()!!.data.cobranzas.map { item ->
                                val cobranzaBean = cobranzaDao.getByCobranza(item.cobranza)

                                if (cobranzaBean == null) {
                                    cobranzaBean1 = CobranzaBean()
                                    cobranzaBean1.cobranza = item.cobranza
                                    cobranzaBean1.cliente = item.cuenta
                                    cobranzaBean1.importe = item.importe
                                    cobranzaBean1.saldo = item.saldo
                                    cobranzaBean1.venta = item.venta
                                    cobranzaBean1.estado = item.estado
                                    cobranzaBean1.observaciones = item.observaciones
                                    cobranzaBean1.fecha = item.fecha
                                    cobranzaBean1.hora = item.hora
                                    cobranzaBean1.empleado = item.identificador
                                    cobranzaBean1.isCheck = false
                                    cobranzaDao.insert(cobranzaBean1)
                                } else {
                                    cobranzaBean.cobranza = item.cobranza
                                    cobranzaBean.cliente = item.cuenta
                                    cobranzaBean.importe = item.importe
                                    cobranzaBean.saldo = item.saldo
                                    cobranzaBean.venta = item.venta
                                    cobranzaBean.estado = item.estado
                                    cobranzaBean.observaciones = item.observaciones
                                    cobranzaBean.fecha = item.fecha
                                    cobranzaBean.hora = item.hora
                                    cobranzaBean.empleado = item.identificador
                                    cobranzaBean.isCheck = false
                                    cobranzaDao.save(cobranzaBean)
                                }
                            }*/

                            val specialPricesDao = SpecialPricesDao()

                            response.body()!!.data.precios.map { item ->
                                val preciosEspecialesBean =
                                    specialPricesDao.getPrecioEspeciaPorCliente(
                                        item.articulo,
                                        item.cliente
                                    )

                                //Si no hay precios especiales entonces crea un precio
                                if (preciosEspecialesBean == null) {
                                    val bean = PreciosEspecialesBean()
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
                                    specialPricesDao.save(preciosEspecialesBean)
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
            val getDataByDate = ApiServices.getClientRetrofit().create(
                PointApi::class.java
            ).getAllDataByDate()

            getDataByDate.enqueue(object : Callback<Data> {
                override fun onResponse(call: Call<Data>, response: Response<Data>) {
                    if (response.isSuccessful) {

                        if (response.body() == null) {
                            onGetAllDataByDateListener.onGetAllDataByDateError()
                        }

                        if (response.code() == 200) {
                            for (item in response.body()!!.data.empleados) {
                                val dao = EmployeeDao()
                                val empleadoBean = dao.getEmployeeByIdentifier(item.identificador)

                                if (empleadoBean == null) {
                                    val empleado = EmpleadoBean()
                                    val employeeDao = EmployeeDao()
                                    empleado.setNombre(item.nombre)
                                    empleado.setDireccion(item.direccion)
                                    empleado.setEmail(item.email)
                                    empleado.setTelefono(item.telefono)
                                    empleado.setFecha_nacimiento(item.fechaNacimiento)
                                    empleado.setFecha_ingreso(item.fechaIngreso)
                                    empleado.setContrasenia(item.contrasenia)
                                    empleado.setIdentificador(item.identificador)
                                    empleado.setPath_image(item.pathImage)
                                    empleado.setRute(item.rute)
                                    empleado.setStatus(item.status == 1)
                                    employeeDao.insert(empleado)
                                } else {
                                    empleadoBean.setNombre(item.nombre)
                                    empleadoBean.setDireccion(item.direccion)
                                    empleadoBean.setEmail(item.email)
                                    empleadoBean.setTelefono(item.telefono)
                                    empleadoBean.setFecha_nacimiento(item.fechaNacimiento)
                                    empleadoBean.setFecha_ingreso(item.fechaIngreso)
                                    empleadoBean.setContrasenia(item.contrasenia)
                                    empleadoBean.setIdentificador(item.identificador)
                                    empleadoBean.setPath_image(item.pathImage)
                                    empleadoBean.setRute(item.rute)
                                    empleadoBean.setStatus(item.status == 1)
                                    dao.save(empleadoBean)
                                }
                            }

                            //Contiene la lista de permidos
                            for (item in response.body()!!.data.roles) {
                                val rolesDao = RolesDao()
                                val rolesBean = rolesDao.getRolByModule(item.empleado, item.modulo)
                                if (rolesBean == null) {
                                    val bean = RolesBean()
                                    val dao = RolesDao()
                                    val employeeDao = EmployeeDao()
                                    val empleadoBean = employeeDao.getEmployeeByIdentifier(item.empleado)
                                    bean.empleado = empleadoBean
                                    bean.modulo = item.modulo
                                    bean.active = item.activo == 1
                                    bean.identificador = item.empleado
                                    dao.insert(bean)
                                } else {
                                    val employeeDao = EmployeeDao()
                                    val empleadoBean = employeeDao.getEmployeeByIdentifier(item.empleado)
                                    rolesBean.empleado = empleadoBean
                                    rolesBean.modulo = item.modulo
                                    rolesBean.active = item.activo == 1
                                    rolesBean.identificador = item.empleado
                                    rolesDao.save(rolesBean)
                                }
                            }

                            //Contiene la lista de productos
                            for (item in response.body()!!.data.productos) {
                                val productDao = ProductDao()
                                val productoBean = productDao.getProductoByArticulo(item.articulo)
                                if (productoBean == null) {
                                    val producto = ProductoBean()
                                    val dao = ProductDao()
                                    producto.articulo = item.articulo
                                    producto.descripcion = item.descripcion
                                    producto.status = item.status
                                    producto.precio = item.precio
                                    producto.iva = item.iva
                                    producto.codigo_barras = item.codigoBarras
                                    producto.path_img = item.pathImage
                                    dao.insert(producto)
                                } else {
                                    productoBean.articulo = item.articulo
                                    productoBean.descripcion = item.descripcion
                                    productoBean.status = item.status
                                    productoBean.precio = item.precio
                                    productoBean.iva = item.iva
                                    productoBean.codigo_barras = item.codigoBarras
                                    productoBean.path_img = item.pathImage
                                    productDao.save(productoBean)
                                }
                            }

                            for (item in response.body()!!.data.clientes) {

                                //Validamos si existe el cliente
                                val dao = ClientDao()
                                val bean = dao.getClientByAccount(item.cuenta)
                                if (bean == null) {
                                    val clienteBean = ClienteBean()
                                    val clientDao = ClientDao()
                                    clienteBean.nombre_comercial = item.nombreComercial
                                    clienteBean.calle = item.calle
                                    clienteBean.numero = item.numero
                                    clienteBean.colonia = item.colonia
                                    clienteBean.ciudad = item.ciudad
                                    clienteBean.codigo_postal = item.codigoPostal
                                    clienteBean.fecha_registro = item.fechaRegistro
                                    clienteBean.cuenta = item.cuenta
                                    clienteBean.status = item.status == 1
                                    clienteBean.consec = item.consec
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
                                    clienteBean.is_credito = item.isCredito == 1
                                    clienteBean.limite_credito = item.limite_credito
                                    clienteBean.saldo_credito = item.saldo_credito
                                    clienteBean.matriz = item.matriz
                                    clientDao.insert(clienteBean)
                                } else {
                                    bean.nombre_comercial = item.nombreComercial
                                    bean.calle = item.calle
                                    bean.numero = item.numero
                                    bean.colonia = item.colonia
                                    bean.ciudad = item.ciudad
                                    bean.codigo_postal = item.codigoPostal
                                    bean.fecha_registro = item.fechaRegistro
                                    bean.cuenta = item.cuenta
                                    bean.status = item.status == 1
                                    bean.consec = item.consec
                                    bean.visitado = if (bean.visitado == 1) 1 else  0
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
                                    bean.is_credito = item.isCredito == 1
                                    bean.limite_credito = item.limite_credito
                                    bean.saldo_credito = item.saldo_credito
                                    bean.matriz = item.matriz
                                    dao.save(bean)
                                }
                            }

                            for (item in response.body()!!.data.cobranzas) {
                                val paymentDao = PaymentDao()
                                val cobranzaBean = paymentDao.getByCobranza(item.cobranza)
                                if (cobranzaBean == null) {
                                    val cobranzaBean1 = CobranzaBean()
                                    val paymentDao1 = PaymentDao()
                                    cobranzaBean1.cobranza = item.cobranza
                                    cobranzaBean1.cliente = item.cuenta
                                    cobranzaBean1.importe = item.importe
                                    cobranzaBean1.saldo = item.saldo
                                    cobranzaBean1.venta = item.venta
                                    cobranzaBean1.estado = item.estado
                                    cobranzaBean1.observaciones = item.observaciones
                                    cobranzaBean1.fecha = item.fecha
                                    cobranzaBean1.hora = item.hora
                                    cobranzaBean1.empleado = item.identificador
                                    cobranzaBean1.isCheck = false
                                    paymentDao1.insert(cobranzaBean1)
                                } else {
                                    cobranzaBean.cobranza = item.cobranza
                                    cobranzaBean.cliente = item.cuenta
                                    cobranzaBean.importe = item.importe
                                    cobranzaBean.saldo = item.saldo
                                    cobranzaBean.venta = item.venta
                                    cobranzaBean.estado = item.estado
                                    cobranzaBean.observaciones = item.observaciones
                                    cobranzaBean.fecha = item.fecha
                                    cobranzaBean.hora = item.hora
                                    cobranzaBean.empleado = item.identificador
                                    cobranzaBean.isCheck = false
                                    paymentDao.save(cobranzaBean)
                                }
                            }

                            for (item in response.body()!!.data.precios) {

                                //Para obtener los datos del cliente
                                val clientDao = ClientDao()
                                val clienteBean = clientDao.getClientByAccount(item.cliente)

                                if (clienteBean != null) {
                                    //Para obtener los datos del producto
                                    val productDao = ProductDao()
                                    val productoBean =
                                        productDao.getProductoByArticulo(item.articulo)

                                    if (productoBean != null) {
                                        val specialPricesDao = SpecialPricesDao()
                                        val preciosEspecialesBean =
                                            specialPricesDao.getPrecioEspeciaPorCliente(
                                                productoBean.articulo,
                                                clienteBean.cuenta
                                            )

                                        //Si no hay precios especiales entonces crea un precio
                                        if (preciosEspecialesBean == null) {
                                            val dao = SpecialPricesDao()
                                            val bean = PreciosEspecialesBean()
                                            bean.cliente = clienteBean.cuenta
                                            bean.articulo = productoBean.articulo
                                            bean.precio = item.precio
                                            bean.active = item.active == 1
                                            dao.insert(bean)
                                            specialPricesDao.save(bean)
                                        } else {
                                            preciosEspecialesBean.cliente = clienteBean.cuenta
                                            preciosEspecialesBean.articulo = productoBean.articulo
                                            preciosEspecialesBean.precio = item.precio
                                            preciosEspecialesBean.active = item.active == 1
                                            specialPricesDao.save(preciosEspecialesBean)
                                        }
                                    }
                                }
                            }
                            onGetAllDataByDateListener.onGetAllDataByDateSuccess()

                        } else {
                            onGetAllDataByDateListener.onGetAllDataByDateError()
                        }
                    } else {
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