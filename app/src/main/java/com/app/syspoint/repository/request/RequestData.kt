package com.app.syspoint.repository.request

import com.app.syspoint.repository.database.bean.*
import com.app.syspoint.repository.database.dao.*
import com.app.syspoint.repository.request.http.ApiServices
import com.app.syspoint.models.Data
import com.app.syspoint.repository.request.http.PointApi
import com.app.syspoint.interactor.data.GetAllDataInteractor
import retrofit2.Call
import retrofit2.Response
import java.io.IOException

class RequestData {
    companion object {
        fun requestAllData(onGetAllDataListener: GetAllDataInteractor.OnGetAllDataListener) {
            var response: Response<Data?>? = null
            val call: Call<Data> = ApiServices.getClientRestrofit().create(PointApi::class.java).allData

            try {
                response = call.execute()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (response?.body() == null) {
                onGetAllDataListener.onGetAllDataError()
            }


            if (response!!.isSuccessful) {
                if (response.code() == 200) {

                    //Contiene la lista de empledos
                    for (item in response.body()!!.data.empleados) {

                        //Instancia el DAO
                        val dao =
                            EmployeeDao()

                        //Validamos si existe el empleado en la base de datos en base al identificador
                        val employeeBean = dao.getEmployeeByIdentifier(item.identificador)

                        //NO existe entonces lo creamos
                        if (employeeBean == null) {
                            val employee = EmpleadoBean()
                            val employeeDao =
                                EmployeeDao()
                            employee.setNombre(item.nombre)
                            employee.setDireccion(item.direccion)
                            employee.setEmail(item.email)
                            employee.setTelefono(item.telefono)
                            employee.setFecha_nacimiento(item.fechaNacimiento)
                            employee.setFecha_ingreso(item.fechaIngreso)
                            employee.setFecha_egreso(item.fechaEgreso)
                            employee.setContrasenia(item.contrasenia)
                            employee.setIdentificador(item.identificador)
                            employee.setNss(item.nss)
                            employee.setRfc(item.rfc)
                            employee.setCurp(item.curp)
                            employee.setPuesto(item.puesto)
                            employee.setArea_depto(item.areaDepto)
                            employee.setTipo_contrato(item.tipoContrato)
                            employee.setRegion(item.region)
                            employee.setHora_entrada(item.horaEntrada)
                            employee.setHora_salida(item.horaSalida)
                            employee.setSalida_comer(item.salidaComer)
                            employee.setEntrada_comer(item.entradaComer)
                            employee.setSueldo_diario(item.sueldoDiario.toDouble())
                            employee.setTurno(item.turno)
                            employee.setPath_image(item.pathImage)
                            employeeDao.insert(employee)
                        } else {
                            employeeBean.setNombre(item.nombre)
                            employeeBean.setDireccion(item.direccion)
                            employeeBean.setEmail(item.email)
                            employeeBean.setTelefono(item.telefono)
                            employeeBean.setFecha_nacimiento(item.fechaNacimiento)
                            employeeBean.setFecha_ingreso(item.fechaIngreso)
                            employeeBean.setFecha_egreso(item.fechaEgreso)
                            employeeBean.setContrasenia(item.contrasenia)
                            employeeBean.setIdentificador(item.identificador)
                            employeeBean.setNss(item.nss)
                            employeeBean.setRfc(item.rfc)
                            employeeBean.setCurp(item.curp)
                            employeeBean.setPuesto(item.puesto)
                            employeeBean.setArea_depto(item.areaDepto)
                            employeeBean.setTipo_contrato(item.tipoContrato)
                            employeeBean.setRegion(item.region)
                            employeeBean.setHora_entrada(item.horaEntrada)
                            employeeBean.setHora_salida(item.horaSalida)
                            employeeBean.setSalida_comer(item.salidaComer)
                            employeeBean.setEntrada_comer(item.entradaComer)
                            employeeBean.setSueldo_diario(item.sueldoDiario.toDouble())
                            employeeBean.setTurno(item.turno)
                            employeeBean.setPath_image(item.pathImage)
                            dao.save(employeeBean)
                        }
                    }
                    //Contiene la lista de permidos
                    for (rol in response.body()!!.data.roles) {
                        val rolesDao =
                            RolesDao()
                        val rolesBean = rolesDao.getRolByModule(rol.empleado, rol.modulo)
                        if (rolesBean == null) {
                            val bean = RolesBean()
                            val dao =
                                RolesDao()
                            val employeeDao =
                                EmployeeDao()
                            val empleadoBean = employeeDao.getEmployeeByIdentifier(rol.empleado)
                            bean.empleado = empleadoBean
                            bean.modulo = rol.modulo
                            bean.active = rol.activo == 1
                            bean.identificador = rol.empleado
                            dao.insert(bean)
                        } else {
                            val employeeDao =
                                EmployeeDao()
                            val empleadoBean = employeeDao.getEmployeeByIdentifier(rol.empleado)
                            rolesBean.empleado = empleadoBean
                            rolesBean.modulo = rol.modulo
                            rolesBean.active = rol.activo == 1
                            rolesBean.identificador = rol.empleado
                            rolesDao.save(rolesBean)
                        }
                    }
                    //Contiene la lista de productos
                    for (items in response.body()!!.data.productos) {
                        val productDao =
                            ProductDao()
                        val productoBean = productDao.getProductoByArticulo(items.articulo)
                        if (productoBean == null) {
                            //Creamos el producto
                            val producto = ProductoBean()
                            val dao =
                                ProductDao()
                            producto.articulo = items.articulo
                            producto.descripcion = items.descripcion
                            producto.status = items.status
                            producto.unidad_medida = items.unidadMedida
                            producto.clave_sat = items.claveSat
                            producto.unidad_sat = items.unidadSat
                            producto.precio = items.precio
                            producto.costo = items.costo
                            producto.iva = items.iva
                            producto.ieps = items.ieps
                            producto.prioridad = items.prioridad
                            producto.region = items.region
                            producto.codigo_alfa = items.codigoAlfa
                            producto.codigo_barras = items.codigoBarras
                            producto.path_img = items.pathImage
                            dao.insert(producto)
                        } else {
                            productoBean.articulo = items.articulo
                            productoBean.descripcion = items.descripcion
                            productoBean.status = items.status
                            productoBean.unidad_medida = items.unidadMedida
                            productoBean.clave_sat = items.claveSat
                            productoBean.unidad_sat = items.unidadSat
                            productoBean.precio = items.precio
                            productoBean.costo = items.costo
                            productoBean.iva = items.iva
                            productoBean.ieps = items.ieps
                            productoBean.prioridad = items.prioridad
                            productoBean.region = items.region
                            productoBean.codigo_alfa = items.codigoAlfa
                            productoBean.codigo_barras = items.codigoBarras
                            productoBean.path_img = items.pathImage
                            productDao.save(productoBean)
                        }
                    }
                    for (item in response.body()!!.data.clientes) {

                        //Validamos si existe el cliente
                        val dao =
                            ClientDao()
                        val bean = dao.getClientByAccount(item.cuenta)
                        if (bean == null) {
                            val clienteBean = ClienteBean()
                            val clienteDao =
                                ClientDao()
                            clienteBean.nombre_comercial = item.nombreComercial
                            clienteBean.calle = item.calle
                            clienteBean.numero = item.numero
                            clienteBean.colonia = item.colonia
                            clienteBean.ciudad = item.ciudad
                            clienteBean.codigo_postal = item.codigoPostal
                            clienteBean.fecha_registro = item.fechaRegistro
                            clienteBean.fecha_baja = item.fechaBaja
                            clienteBean.cuenta = item.cuenta
                            clienteBean.grupo = item.grupo
                            clienteBean.categoria = item.categoria
                            clienteBean.status = item.status == 1
                            clienteBean.consec = item.consec
                            clienteBean.visitado = 0
                            clienteBean.region = item.region
                            clienteBean.sector = item.sector
                            clienteBean.rango = item.rango
                            clienteBean.secuencia = item.secuencia
                            clienteBean.periodo = item.periodo
                            clienteBean.ruta = item.ruta
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
                            clienteDao.insert(clienteBean)
                        } else {
                            bean.nombre_comercial = item.nombreComercial
                            bean.calle = item.calle
                            bean.numero = item.numero
                            bean.colonia = item.colonia
                            bean.ciudad = item.ciudad
                            bean.codigo_postal = item.codigoPostal
                            bean.fecha_registro = item.fechaRegistro
                            bean.fecha_baja = item.fechaBaja
                            bean.cuenta = item.cuenta
                            bean.grupo = item.grupo
                            bean.categoria = item.categoria
                            bean.status = item.status == 1
                            bean.consec = item.consec
                            bean.visitado = if (bean.visitado == 1) 1 else  0
                            bean.region = item.region
                            bean.sector = item.sector
                            bean.rango = item.rango
                            bean.secuencia = item.secuencia
                            bean.periodo = item.periodo
                            bean.ruta = item.ruta
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
                        val cobranzaDao =
                            PaymentDao()
                        val cobranzaBean = cobranzaDao.getByCobranza(item.cobranza)
                        if (cobranzaBean == null) {
                            val cobranzaBean1 = CobranzaBean()
                            val cobranzaDao1 =
                                PaymentDao()
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
                            cobranzaDao1.insert(cobranzaBean1)
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
                    }
                    for (item in response.body()!!.data.precios) {

                        //Para obtener los datos del cliente
                        val clientDao =
                            ClientDao()

                        val clientBean = clientDao.getClientByAccount(item.cliente)
                        if (clientBean == null) onGetAllDataListener.onGetAllDataError()

                        //Para obtener los datos del producto
                        val productDao =
                            ProductDao()
                        val productoBean = productDao.getProductoByArticulo(item.articulo)
                        if (productoBean == null) onGetAllDataListener.onGetAllDataError()

                        val specialPricesDao =
                            SpecialPricesDao()
                        val preciosEspecialesBean = specialPricesDao.getPrecioEspeciaPorCliente(
                            productoBean?.articulo,
                            clientBean?.cuenta
                        )

                        //Si no hay precios especiales entonces crea un precio
                        if (preciosEspecialesBean == null) {
                            val dao =
                                SpecialPricesDao()
                            val bean = PreciosEspecialesBean()
                            bean.cliente = clientBean?.cuenta
                            bean.articulo = productoBean?.articulo
                            bean.precio = item.precio
                            bean.active = item.active == 1
                            dao.insert(bean)
                        } else {
                            preciosEspecialesBean.cliente = clientBean?.cuenta
                            preciosEspecialesBean.articulo = productoBean?.articulo
                            preciosEspecialesBean.precio = item.precio
                            preciosEspecialesBean.active = item.active == 1
                            specialPricesDao.save(preciosEspecialesBean)
                        }
                    }
                }
                onGetAllDataListener.onGetAllDataSuccess()
            } else {
                onGetAllDataListener.onGetAllDataError()
            }
        }

        fun requestAllDataByDate(onGetAllDataByDateListener: GetAllDataInteractor.OnGetAllDataByDateListener) {

            onGetAllDataByDateListener.onGetAllDataByDateSuccess()
        }
    }
}