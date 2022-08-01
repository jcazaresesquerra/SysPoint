package com.app.syspoint.http;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.app.syspoint.db.bean.ClienteBean;
import com.app.syspoint.db.bean.CobranzaBean;
import com.app.syspoint.db.bean.EmpleadoBean;
import com.app.syspoint.db.bean.PreciosEspecialesBean;
import com.app.syspoint.db.bean.ProductoBean;
import com.app.syspoint.db.bean.RolesBean;
import com.app.syspoint.db.dao.ClienteDao;
import com.app.syspoint.db.dao.CobranzaDao;
import com.app.syspoint.db.dao.EmpleadoDao;
import com.app.syspoint.db.dao.PreciosEspecialesDao;
import com.app.syspoint.db.dao.ProductoDao;
import com.app.syspoint.db.dao.RolesDao;
import com.app.syspoint.json.Cliente;
import com.app.syspoint.json.Cobranza;
import com.app.syspoint.json.Empleado;
import com.app.syspoint.json.Precio;
import com.app.syspoint.json.Producto;
import com.app.syspoint.json.Role;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DownloadGetAllDataLoader extends AsyncTaskLoader<Data> {

    public DownloadGetAllDataLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public Data loadInBackground() {

        final Data[] respuesta = {new Data()};
        Call<Data> getData = ApiServices.getClientRestrofit().create(PointApi.class).getAllData();

        getData.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (response.isSuccessful()){

                    if (response.code() == 200){
                        respuesta[0] = response.body();
                        //Contiene la lista de empledos
                        for (Empleado item : response.body().getData().getEmpleados()) {

                            //Instancia el DAO
                            final EmpleadoDao dao = new EmpleadoDao();

                            //Validamos si existe el empleado en la base de datos en base al identificador
                            final EmpleadoBean empleadoBean = dao.getEmpleadoByIdentificador(item.getIdentificador());

                            //NO existe entonces lo creamos
                            if (empleadoBean == null) {
                                EmpleadoBean empleado = new EmpleadoBean();
                                EmpleadoDao empleadoDao = new EmpleadoDao();
                                empleado.setNombre(item.getNombre());
                                empleado.setDireccion(item.getDireccion());
                                empleado.setEmail(item.getEmail());
                                empleado.setTelefono(item.getTelefono());
                                empleado.setFecha_nacimiento(item.getFechaNacimiento());
                                empleado.setFecha_ingreso(item.getFechaIngreso());
                                empleado.setFecha_egreso(item.getFechaEgreso());
                                empleado.setContrasenia(item.getContrasenia());
                                empleado.setIdentificador(item.getIdentificador());
                                empleado.setNss(item.getNss());
                                empleado.setRfc(item.getRfc());
                                empleado.setCurp(item.getCurp());
                                empleado.setPuesto(item.getPuesto());
                                empleado.setArea_depto(item.getAreaDepto());
                                empleado.setTipo_contrato(item.getTipoContrato());
                                empleado.setRegion(item.getRegion());
                                empleado.setHora_entrada(item.getHoraEntrada());
                                empleado.setHora_salida(item.getHoraSalida());
                                empleado.setSalida_comer(item.getSalidaComer());
                                empleado.setEntrada_comer(item.getEntradaComer());
                                empleado.setSueldo_diario(item.getSueldoDiario());
                                empleado.setTurno(item.getTurno());
                                empleado.setPath_image(item.getPathImage());
                                empleadoDao.insert(empleado);
                            }else {
                                empleadoBean.setNombre(item.getNombre());
                                empleadoBean.setDireccion(item.getDireccion());
                                empleadoBean.setEmail(item.getEmail());
                                empleadoBean.setTelefono(item.getTelefono());
                                empleadoBean.setFecha_nacimiento(item.getFechaNacimiento());
                                empleadoBean.setFecha_ingreso(item.getFechaIngreso());
                                empleadoBean.setFecha_egreso(item.getFechaEgreso());
                                empleadoBean.setContrasenia(item.getContrasenia());
                                empleadoBean.setIdentificador(item.getIdentificador());
                                empleadoBean.setNss(item.getNss());
                                empleadoBean.setRfc(item.getRfc());
                                empleadoBean.setCurp(item.getCurp());
                                empleadoBean.setPuesto(item.getPuesto());
                                empleadoBean.setArea_depto(item.getAreaDepto());
                                empleadoBean.setTipo_contrato(item.getTipoContrato());
                                empleadoBean.setRegion(item.getRegion());
                                empleadoBean.setHora_entrada(item.getHoraEntrada());
                                empleadoBean.setHora_salida(item.getHoraSalida());
                                empleadoBean.setSalida_comer(item.getSalidaComer());
                                empleadoBean.setEntrada_comer(item.getEntradaComer());
                                empleadoBean.setSueldo_diario(item.getSueldoDiario());
                                empleadoBean.setTurno(item.getTurno());
                                empleadoBean.setPath_image(item.getPathImage());
                                dao.save(empleadoBean);
                            }
                        }
                        //Contiene la lista de permidos
                        for (Role rol : response.body().getData().getRoles()){

                            final RolesDao rolesDao = new RolesDao();
                            final RolesBean rolesBean = rolesDao.getRolByModule(rol.getEmpleado(), rol.getModulo());

                            if (rolesBean == null){

                                final RolesBean bean = new RolesBean();
                                final RolesDao dao = new RolesDao();

                                final EmpleadoDao empleadoDao = new EmpleadoDao();
                                final EmpleadoBean empleadoBean = empleadoDao.getEmpleadoByIdentificador(rol.getEmpleado());

                                bean.setEmpleado(empleadoBean);
                                bean.setModulo(rol.getModulo());

                                if (rol.getActivo() == 1){
                                    bean.setActive(true);
                                }else {
                                    bean.setActive(false);
                                }
                                bean.setIdentificador(rol.getEmpleado());
                                dao.insert(bean);
                            }else {
                                final EmpleadoDao empleadoDao = new EmpleadoDao();
                                final EmpleadoBean empleadoBean = empleadoDao.getEmpleadoByIdentificador(rol.getEmpleado());

                                rolesBean.setEmpleado(empleadoBean);
                                rolesBean.setModulo(rol.getModulo());

                                if (rol.getActivo() == 1){
                                    rolesBean.setActive(true);
                                }else {
                                    rolesBean.setActive(false);
                                }
                                rolesBean.setIdentificador(rol.getEmpleado());
                                rolesDao.save(rolesBean);
                            }
                        }
                        //Contiene la lista de productos
                        for (Producto items : response.body().getData().getProductos()) {

                            final ProductoDao productoDao = new ProductoDao();
                            final ProductoBean productoBean = productoDao.getProductoByArticulo(items.getArticulo());

                            if (productoBean == null) {
                                //Creamos el producto
                                ProductoBean producto = new ProductoBean();
                                ProductoDao dao = new ProductoDao();
                                producto.setArticulo(items.getArticulo());
                                producto.setDescripcion(items.getDescripcion());
                                producto.setStatus(items.getStatus());
                                producto.setUnidad_medida(items.getUnidadMedida());
                                producto.setClave_sat(items.getClaveSat());
                                producto.setUnidad_sat(items.getUnidadSat());
                                producto.setPrecio(items.getPrecio());
                                producto.setCosto(items.getCosto());
                                producto.setIva(items.getIva());
                                producto.setIeps(items.getIeps());
                                producto.setPrioridad(items.getPrioridad());
                                producto.setRegion(items.getRegion());
                                producto.setCodigo_alfa(items.getCodigoAlfa());
                                producto.setCodigo_barras(items.getCodigoBarras());
                                producto.setPath_img(items.getPathImage());
                                dao.insert(producto);
                            }else {
                                productoBean.setArticulo(items.getArticulo());
                                productoBean.setDescripcion(items.getDescripcion());
                                productoBean.setStatus(items.getStatus());
                                productoBean.setUnidad_medida(items.getUnidadMedida());
                                productoBean.setClave_sat(items.getClaveSat());
                                productoBean.setUnidad_sat(items.getUnidadSat());
                                productoBean.setPrecio(items.getPrecio());
                                productoBean.setCosto(items.getCosto());
                                productoBean.setIva(items.getIva());
                                productoBean.setIeps(items.getIeps());
                                productoBean.setPrioridad(items.getPrioridad());
                                productoBean.setRegion(items.getRegion());
                                productoBean.setCodigo_alfa(items.getCodigoAlfa());
                                productoBean.setCodigo_barras(items.getCodigoBarras());
                                productoBean.setPath_img(items.getPathImage());
                                productoDao.save(productoBean);
                            }
                        }

                        for (Cliente item : response.body().getData().getClientes()) {

                            //Validamos si existe el cliente
                            final ClienteDao dao = new ClienteDao();
                            final ClienteBean bean = dao.getClienteByCuenta(item.getCuenta());

                            if (bean == null) {

                                final ClienteBean clienteBean = new ClienteBean();
                                final ClienteDao clienteDao = new ClienteDao();
                                clienteBean.setNombre_comercial(item.getNombreComercial());
                                clienteBean.setCalle(item.getCalle());
                                clienteBean.setNumero(item.getNumero());
                                clienteBean.setColonia(item.getColonia());
                                clienteBean.setCiudad(item.getCiudad());
                                clienteBean.setCodigo_postal(item.getCodigoPostal());
                                clienteBean.setFecha_registro(item.getFechaRegistro());
                                clienteBean.setFecha_baja(item.getFechaBaja());
                                clienteBean.setCuenta(item.getCuenta());
                                clienteBean.setGrupo(item.getGrupo());
                                clienteBean.setCategoria(item.getCategoria());
                                if (item.getStatus() == 1) {
                                    clienteBean.setStatus(true);
                                } else {
                                    clienteBean.setStatus(false);
                                }
                                clienteBean.setConsec(item.getConsec());
                                clienteBean.setVisitado(0);
                                clienteBean.setRegion(item.getRegion());
                                clienteBean.setSector(item.getSector());
                                clienteBean.setRango(item.getRango());
                                clienteBean.setSecuencia(item.getSecuencia());
                                clienteBean.setPeriodo(item.getPeriodo());
                                clienteBean.setRuta(item.getRuta());
                                clienteBean.setLun(item.getLun());
                                clienteBean.setMar(item.getMar());
                                clienteBean.setMie(item.getMie());
                                clienteBean.setJue(item.getJue());
                                clienteBean.setVie(item.getVie());
                                clienteBean.setSab(item.getSab());
                                clienteBean.setDom(item.getDom());
                                clienteBean.setLatitud(item.getLatitud());
                                clienteBean.setLongitud(item.getLongitud());
                                clienteBean.setContacto_phone(item.getPhone_contacto());
                                clienteBean.setRecordatorio(item.getRecordatorio());
                                clienteBean.setVisitasNoefectivas(item.getVisitas());

                                if (item.getIsCredito() == 1) {
                                    clienteBean.setIs_credito(true);
                                } else {
                                    clienteBean.setIs_credito(false);
                                }

                                clienteBean.setLimite_credito(item.getLimite_credito());
                                clienteBean.setSaldo_credito(item.getSaldo_credito());
                                clienteBean.setMatriz(item.getMatriz());
                                clienteDao.insert(clienteBean);
                            } else {
                                bean.setNombre_comercial(item.getNombreComercial());
                                bean.setCalle(item.getCalle());
                                bean.setNumero(item.getNumero());
                                bean.setColonia(item.getColonia());
                                bean.setCiudad(item.getCiudad());
                                bean.setCodigo_postal(item.getCodigoPostal());
                                bean.setFecha_registro(item.getFechaRegistro());
                                bean.setFecha_baja(item.getFechaBaja());
                                bean.setCuenta(item.getCuenta());
                                bean.setGrupo(item.getGrupo());
                                bean.setCategoria(item.getCategoria());
                                if (item.getStatus() == 1) {
                                    bean.setStatus(true);
                                } else {
                                    bean.setStatus(false);
                                }
                                bean.setConsec(item.getConsec());
                                if (bean.getVisitado() == 0) {
                                    bean.setVisitado(0);
                                } else if (bean.getVisitado() == 1) {
                                    bean.setVisitado(1);
                                }
                                bean.setRegion(item.getRegion());
                                bean.setSector(item.getSector());
                                bean.setRango(item.getRango());
                                bean.setSecuencia(item.getSecuencia());
                                bean.setPeriodo(item.getPeriodo());
                                bean.setRuta(item.getRuta());
                                bean.setLun(item.getLun());
                                bean.setMar(item.getMar());
                                bean.setMie(item.getMie());
                                bean.setJue(item.getJue());
                                bean.setVie(item.getVie());
                                bean.setSab(item.getSab());
                                bean.setDom(item.getDom());
                                bean.setLatitud(item.getLatitud());
                                bean.setLongitud(item.getLongitud());
                                bean.setContacto_phone(item.getPhone_contacto());
                                bean.setRecordatorio(item.getRecordatorio());
                                bean.setVisitasNoefectivas(item.getVisitas());
                                if (item.getIsCredito() == 1) {
                                    bean.setIs_credito(true);
                                } else {
                                    bean.setIs_credito(false);
                                }
                                bean.setLimite_credito(item.getLimite_credito());
                                bean.setSaldo_credito(item.getSaldo_credito());
                                bean.setMatriz(item.getMatriz());
                                dao.save(bean);
                            }
                        }

                        for (Cobranza item : response.body().getData().getCobranzas()) {
                            CobranzaDao cobranzaDao = new CobranzaDao();
                            CobranzaBean cobranzaBean = cobranzaDao.getByCobranza(item.getCobranza());
                            if (cobranzaBean == null) {
                                final CobranzaBean cobranzaBean1 = new CobranzaBean();
                                final CobranzaDao cobranzaDao1 = new CobranzaDao();
                                cobranzaBean1.setCobranza(item.getCobranza());
                                cobranzaBean1.setCliente(item.getCuenta());
                                cobranzaBean1.setImporte(item.getImporte());
                                cobranzaBean1.setSaldo(item.getSaldo());
                                cobranzaBean1.setVenta(item.getVenta());
                                cobranzaBean1.setEstado(item.getEstado());
                                cobranzaBean1.setObservaciones(item.getObservaciones());
                                cobranzaBean1.setFecha(item.getFecha());
                                cobranzaBean1.setHora(item.getHora());
                                cobranzaBean1.setEmpleado(item.getIdentificador());
                                cobranzaBean1.setIsCheck(false);
                                cobranzaDao1.insert(cobranzaBean1);
                            } else {
                                cobranzaBean.setCobranza(item.getCobranza());
                                cobranzaBean.setCliente(item.getCuenta());
                                cobranzaBean.setImporte(item.getImporte());
                                cobranzaBean.setSaldo(item.getSaldo());
                                cobranzaBean.setVenta(item.getVenta());
                                cobranzaBean.setEstado(item.getEstado());
                                cobranzaBean.setObservaciones(item.getObservaciones());
                                cobranzaBean.setFecha(item.getFecha());
                                cobranzaBean.setHora(item.getHora());
                                cobranzaBean.setEmpleado(item.getIdentificador());
                                cobranzaBean.setIsCheck(false);
                                cobranzaDao.save(cobranzaBean);
                            }
                        }

                        for (Precio item : response.body().getData().getPrecios()) {

                            //Para obtener los datos del cliente
                            final ClienteDao clienteDao = new ClienteDao();
                            final ClienteBean clienteBean = clienteDao.getClienteByCuenta(item.getCliente());
                            if (clienteBean == null) {
                                return;
                            }

                            //Para obtener los datos del producto
                            final ProductoDao productoDao = new ProductoDao();
                            final ProductoBean productoBean = productoDao.getProductoByArticulo(item.getArticulo());

                            if (productoBean == null) {
                                return;
                            }

                            final PreciosEspecialesDao preciosEspecialesDao = new PreciosEspecialesDao();
                            final PreciosEspecialesBean preciosEspecialesBean = preciosEspecialesDao.getPrecioEspeciaPorCliente(productoBean.getArticulo(), clienteBean.getCuenta());

                            //Si no hay precios especiales entonces crea un precio
                            if (preciosEspecialesBean == null) {

                                final PreciosEspecialesDao dao = new PreciosEspecialesDao();
                                final PreciosEspecialesBean bean = new PreciosEspecialesBean();
                                bean.setCliente(clienteBean.getCuenta());
                                bean.setArticulo(productoBean.getArticulo());
                                bean.setPrecio(item.getPrecio());
                                if (item.getActive() == 1){
                                    bean.setActive(true);
                                }else {
                                    bean.setActive(false);
                                }
                                dao.insert(bean);

                            } else {
                                preciosEspecialesBean.setCliente(clienteBean.getCuenta());
                                preciosEspecialesBean.setArticulo(productoBean.getArticulo());
                                preciosEspecialesBean.setPrecio(item.getPrecio());
                                if (item.getActive() == 1){
                                    preciosEspecialesBean.setActive(true);
                                }else {
                                    preciosEspecialesBean.setActive(false);
                                }
                                preciosEspecialesDao.save(preciosEspecialesBean);
                            }
                        }

                    }

                }

            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });


        return respuesta[0];
    }
}
